package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bhati.routeapplication.Activities.properties;
import com.example.bhati.routeapplication.Pojo.FramesResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * this class is a helper for extracting frames from video
 */
public class FramesHelper {

    private Context context;
    private MediaMetadataRetriever metadataRetriever;
    // frame interval
    private String videoPath;
    private int REGULAR_FRAME_INTERVAL;
    private ArrayList<Integer> timestamps;
    private int lengthOfVideo;

    public FramesHelper(Context context){
        timestamps = new ArrayList<>();
        this.context = context;
        metadataRetriever = new MediaMetadataRetriever();
        REGULAR_FRAME_INTERVAL = properties.REGULAR_FRAME_INTERVAL_MILLIS;
    }


    public void setVideoPath(String path){
        String[] paths =  path.split("file://");
        Log.v("nuttygeek", "[Splited String]: "+paths[1]);
        // setting path of video file
        this.videoPath = paths[1];
        metadataRetriever.setDataSource(videoPath);
        String length = this.getLengthOfVideo();
        this.lengthOfVideo = Integer.parseInt(length);
        Log.v("nuttygeek", "[Length]: "+length);
        // GENERATE TIME STAMPS and add it to a public property of this class
        generateTimestamps();
    }

    public void getFrameFromVideo(int time){
        // getting image from video at particular image
        Bitmap image = metadataRetriever.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST);
        String dirPath = "/RouteApp/"+getFileNameFromUriWithoutExtensions(this.videoPath);
        String fileName = String.valueOf(time)+".jpg";
        saveBitmapToStorage(image, dirPath, fileName);
    }




    /**
     * this fxn converts the bitmap into JPEg and save it to the given path
     * @param image  bitmap image
     * @param dirPath absolute path to save it on
     * @param fileName name of the file to create
     */
    public void saveBitmapToStorage(Bitmap image,String dirPath, String fileName){
        // root path of external storage
        String root = Environment.getExternalStorageDirectory().toString();
        // creating file
        File dirFile = new File(root+dirPath);
        // create the directory
        dirFile.mkdirs();
        // creating file
        File file = new File(dirFile,fileName);
        // converting bitmap to jpeg
        try {
            if(file.exists()){
                Log.v("nuttygeek", "File already present ! Deleting File");
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(context, "File Created !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("nuttygeek", e.toString());
            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this function returns the file name from the video Uri
     * @param fileUri file URI
     * @return filename
     */
    public String getFileNameFromUriWithExtension(String fileUri){
        return fileUri.split("/RouteApp/")[1];
    }

    public String getFileNameFromUriWithoutExtensions(String fileUri){
        return fileUri.split("/RouteApp/")[1].replace(".mp4", "");
    }

    public String getLengthOfVideo(){
        String length = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return length;
    }

    public void generateTimestamps(){
        Log.v("nuttygeek_time", "inside the time stamp fxn ");
        int i=0;
        while(i<this.lengthOfVideo){
            Log.v("nuttygeek_time", "[val]: "+i);
            timestamps.add(i);
            i=i+REGULAR_FRAME_INTERVAL;
        }
    }

    /**
     * this fxn extracts all the frames intended from teh video
     */
    public void extractAllFrames(){
        Toast.makeText(context, "Extracting Frames from Video !", Toast.LENGTH_SHORT).show();
        for (int i=0; i<timestamps.size();i++){
            getFrameFromVideo(i);
        }
        Toast.makeText(context, "Extracted All Frames",Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "Extracted All the Frames", Toast.LENGTH_SHORT).show();
    }


    public FramesResult getFramesData(){
        FramesResult res = new FramesResult();
        res.setCar(String.valueOf(getValue()));
        res.setVegetation(String.valueOf(getValue()));
        res.setSnpashot(String.valueOf(getValue()));
        res.setStreet(String.valueOf(getValue()));
        res.setPerson(String.valueOf(getValue()));
        return res;
    }

    public double getValue(){
        return Math.random();
    }




}
