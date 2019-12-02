package com.example.bhati.routeapplication.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bhati.routeapplication.Interface.OnFrameExtracted;
import com.example.bhati.routeapplication.helpers.FramesHelper;
import com.example.bhati.routeapplication.helpers.FramesRestApiHelper;
import com.example.bhati.routeapplication.helpers.SharedPrefHelper;

import java.util.ArrayList;


/**
 * This service will receive an intent when the video file is being saved,
 * and then this service will need the video uri
 * this service will do the following tasks :
 * 1. Get Video Uri from intent
 * 2. Extract the frames and save images in a folder
 * 3. Upload Frames one by one
 * 4. Make a call to server to fetch the analysis , if the server is giving an error code retry again after 5 seconds
 * 5. After getting the results from server save it in Shared Pref
 */


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FrameUploadService extends IntentService {

    // shared pref helper object
    SharedPrefHelper prefHelper;
    public static FramesHelper helper;
    public static FramesRestApiHelper restHelper;
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_START_EXTRACTING = "com.example.bhati.routeapplication.start_extracting";
    private static final String ACTION_START_UPLOADING = "com.example.bhati.routeapplication.start_uploading";
    private static final String ACTION_START_GET_RESULTS = "com.example.bhati.routeapplication.get_results";
    // TODO: Rename parameters
    private static final String VIDEO_URI = "com.nuttygeek.backgroundservice.extra.VIDEO_URI";

    // constructor
    public FrameUploadService() {
        super("FrameUploadService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionExtracting(Context context, Uri videoUri) {
        // init rest helper
        helper = new FramesHelper(context);
        restHelper = new FramesRestApiHelper(context);
        Intent intent = new Intent(context, FrameUploadService.class);
        intent.setAction(ACTION_START_EXTRACTING);
        intent.putExtra(VIDEO_URI, videoUri.toString());
        context.startService(intent);
    }

    /**
     * passes the intent to service for starting uploading
     * @param context context
     * @param videoUri uri of video
     */
    public static void startActionUploading(Context context, Uri videoUri){
        restHelper = new FramesRestApiHelper(context);
        Intent intent = new Intent(context, FrameUploadService.class);
        intent.setAction(ACTION_START_UPLOADING);
        intent.putExtra(VIDEO_URI, videoUri.toString());
        context.startService(intent);
    }

    /**
     * pasess the intent to service for starting get results operation
     * @param context context
     * @param videoUri uri of video
     */
    public static void startActionGetResults(Context context, Uri videoUri){
        restHelper = new FramesRestApiHelper(context);
        Intent intent = new Intent(context, FrameUploadService.class);
        intent.setAction(ACTION_START_GET_RESULTS);
        intent.putExtra(VIDEO_URI, videoUri.toString());
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START_EXTRACTING.equals(action)) {
                String filePath = intent.getStringExtra(VIDEO_URI);
                handleActionExtracting(filePath);
            }
            if(ACTION_START_UPLOADING.equals(action)){
                String filePath = intent.getStringExtra(VIDEO_URI);
                handleActionUploading(filePath);
            }
            if(ACTION_START_GET_RESULTS.equals(action)){
                String filePath = intent.getStringExtra(VIDEO_URI);
                handleActionGetResults(filePath);
            }
        }
    }

    /**
     * Handle action Extracting in the provided background thread with the provided
     * parameters.
     */
    private void handleActionExtracting(String videoUriString) {
        //TODO: do eveyrthing here
        Log.v("nuttygeek_service", "video_uri: "+videoUriString);
        Toast.makeText(this, "Uri: "+videoUriString, Toast.LENGTH_SHORT).show();
        // TODO: extract the frames using FramesHelper class
        restHelper.extractFrames(videoUriString);
        // getting video name from a helper method
        String videoName = helper.getVideoName(videoUriString);
        // saving boolean value in shared pref
        prefHelper = new SharedPrefHelper(getApplicationContext(),videoName);
        prefHelper.saveBoolean(SharedPrefHelper.ARE_FRAMES_EXTRACTED_KEY, true);
        Log.v("nuttygeek", "Saved frames extracted boolean");
    }

    // TODO: Call the server for getting response json response, if server is busy wait for 5 seconds and try again
    // TODO: save the response in Shared Pref

    /**
     * handle action Uploading in the provided background thread with the params
     * @param videoUriString video uri
     */
    private void handleActionUploading(String videoUriString){
        Log.v("nuttygeek_uploading", "uri: "+videoUriString);
        // get list of files to upload
        helper = new FramesHelper(this);
        helper.setVideoPath(videoUriString);
        String videoName = helper.getVideoName(videoUriString);
        prefHelper = new SharedPrefHelper(getApplicationContext(), videoName);
        // get list of images in a folder
        ArrayList<Uri> imagesUri = helper.getAllImageUrisFromVideoFolder();
        Log.v("nuttygeek_im_uris",imagesUri.toString());
        for(Uri uri: imagesUri){
            // TODO: Upload Frames one by one using Retrofit
            Log.v("nuttygeek_service", "Uri to upload: "+uri.toString());
            //TODO: use firebase method here
            restHelper.uploadFrame(videoName, uri, imagesUri.indexOf(uri) == imagesUri.size()-1);
        }
    }

    /**
     * this fxn calls server to get the results of the frames analysis
     * @param videoUri uri of video
     */
    private void handleActionGetResults(String videoUri){
        helper = new FramesHelper(this);
        helper.setVideoPath(videoUri);
        String videoName = helper.getVideoName(videoUri);
        prefHelper = new SharedPrefHelper(getApplicationContext(), helper.getVideoName(videoUri));
        restHelper.getResults(videoName);
    }


}


