package com.example.bhati.routeapplication.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.bhati.routeapplication.Interface.OnFrameExtracted;
import com.google.android.gms.vision.Frame;
import com.squareup.okhttp.internal.framed.FrameReader;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// convert it into singleton class
public class FramesRestApiHelper {

    public Context context;
    private FramesHelper helper;
    private SharedPrefHelper prefHelper;
    private String videoName;
    private Retrofit retrofit;

    String baseUrl = "http://35.245.189.30:5000/";

    /**
     * private constructore for singleton class
     */
    public FramesRestApiHelper(Context context){
        this.context = context;
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());
        retrofit = builder.build();
    }

    /**
     * this function is for extracting the Frames and putting the images in folder
     * @param videoUriString uri of the video file
     */
    public void extractFrames(String videoUriString){
        helper = new FramesHelper(this.context);
        helper.setVideoPath(videoUriString);
        prefHelper = new SharedPrefHelper(this.context, helper.getVideoName());
        helper.extractAllFrames(new OnFrameExtracted() {
            @Override
            public void onFrameExtractionCompleted() {
                // all frames are extracted, do the subsequent tasks
                Log.v("nuttygeek_service", "All Frames are extracted");
                // saving are farmes extracted in shared pref
                prefHelper.saveBoolean(SharedPrefHelper.ARE_FRAMES_EXTRACTED_KEY, true);
            }
            @Override
            public void getExtractedFrameCount(int count) {
                Log.v("nuttygeek_service", "Frames Extracted: "+count);
            }
            @Override
            public void getTotalFramesCount(int count) {
                Log.v("nuttygeek_service","Total Frames: "+count);
                // saving the total no of frames in shared pref
                prefHelper.saveInt(SharedPrefHelper.TOTAL_FRAMES_KEY, count);
            }
        });
    }

    /**
     * this fxn is for uploading the frames to the server
     * @param videoName name fo video
     * @param uri list of uri
     */
    public void uploadFrame(String videoName, Uri uri, boolean last){
        Log.v("nuttygeek_service", "videoName in upload Frame: "+videoName);
        if(videoName!=null && uri!=null){
            // create the videoName form data
            FrameUploadApiClient client = retrofit.create(FrameUploadApiClient.class);
            RequestBody video_namePart = RequestBody.create(MultipartBody.FORM, videoName);
            File originalFile = new File(uri.getPath());
            String mediaType = context.getContentResolver().getType(getContentUri(uri));
            // create image file request body
            RequestBody imagePart  = RequestBody.create(
                    MediaType.parse(mediaType),
                    originalFile
            );
            MultipartBody.Part file = MultipartBody.Part.createFormData("file", originalFile.getName(), imagePart);
            // call the rest endpoint
            Call<ResponseBody> call = client.uploadFrame(video_namePart, file);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.v("nuttygeek_res", String.valueOf(response.code()));
                    if(last){
                        Log.v("nuttygeek_service" ,"last item");
                        // update in shared pref that all frames are uploaded
                        SharedPrefHelper prefHelper = new SharedPrefHelper(context, videoName);
                        prefHelper.saveBoolean(SharedPrefHelper.ARE_FRAMES_UPLOADED_KEY, true);
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.v("nuttygeek_res", t.getMessage());
                    Log.v("nuttygeek_service", "Frames was not uploaded, coz, : "+t.getMessage());
                }
            });
        }
    }

    /**
     * this fxn get results from server about a video
     * @param videoName name of video sent to server
     */
    public void getResults(String videoName){
        Log.v("nuttygeek_service", "videoName in get results: "+videoName);
        Map<String, String> map = new HashMap<>();
//      map.put("video_name", videoName);
        map.put("video_name", "test2");
        FrameUploadApiClient client = retrofit.create(FrameUploadApiClient.class);
        Call<ResponseBody> resultCall = client.getResult(map);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("nuttygeek_code", "Res Code: "+response.code());
                if(response.code() == 200){
                    try{
                        String result = response.body().string();
                        Log.v("nuttygeek_res", "Response Code: "+response.code());
                        Log.v("nuttygeek_res", "Res: \n"+result);
                        SharedPrefHelper prefHelper = new SharedPrefHelper(context, videoName);
                        prefHelper.saveResult(result);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    // if error code is 429 , sleep the thread for 5 seconds
                }else if(response.code() == 429){
                    try {
                        Log.v("nuttygeek_res", "response code is: 200");
                        Thread.sleep(5000);
                        Log.v("nuttygeek_res", "fetching the result again");
                        //calling this function again
                        getResults(videoName);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.v("nuttygeek_service", "Response got from server is other than 429");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("nuttygeek_error", t.getMessage());
            }
        });
    }

//    // don't know if it will be used or not
//    public void getPath(){
//        public String getPath(Uri uri) {
//            String[] projection = { MediaStore.Images.Media.DATA };
//            Cursor cursor = Cursor.managedQuery(uri, projection, null, null, null);
//            startManagingCursor(cursor);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }
//    }

    public Uri getContentUri(Uri uri){
        File file = new File(uri.getPath());
        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = null;
        try {
            uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Uri.parse(uriString);
    }
}
