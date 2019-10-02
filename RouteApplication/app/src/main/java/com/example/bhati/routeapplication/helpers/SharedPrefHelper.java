package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bhati.routeapplication.Pojo.ImageDetectionResult;
import com.example.bhati.routeapplication.Pojo.ImageLabel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Image;

public class SharedPrefHelper {

    Context context;
    String sharedPrefFileName = "object_detection_json_data";
    SharedPreferences prefs;
    public String FRAMES_EXTRACTED = "frames_extracted";
    public String FRAMES_PROCESSED = "frames_processed";

    public SharedPrefHelper(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
    }

    /**
     * this fxn create shared pref with the name given
     * @param videoName video name
     * @return
     */
    public SharedPreferences createSharedPref(String videoName){
        return context.getSharedPreferences(videoName, Context.MODE_PRIVATE);
    }


    /**
     * this fxn saves the key value pair in Shared Pref
     * @param key key to be used while retreval
     * @param value value to be saved
     */
    public void saveString(String key, String value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * this fxn returms the key from the shared pref
     * @param key key of the data to be extracted
     * @return respective value of the key passed as param
     */
    public String getString(String key){
        String defaultValue = "null";
        return prefs.getString(key, defaultValue);
    }

    /**
     * saves boolean value in shared pref
     * @param key key of data
     * @param value value of the data
     */
    public void saveBoolean(String key, boolean value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * returns the boolean value according to the key
     * @param key key of the data
     * @return boolean value
     */
    public boolean getBoolean(String key){
        boolean defaultValue = false;
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * this fxn tells if the frames are processed or not
     * @return
     */
    public boolean areFramesProcessed(){
        return getBoolean(FRAMES_PROCESSED);
    }

    /**
     * save the object detection result in the shared pref using the key as video name
     * @param videoName video name
     * @param result ImageDetecttionResult object
     */
    public void saveObjectDetectionData(String videoName, ImageDetectionResult result){
        // here we have to add correct labels beofre saving it to local storage
//        HashMap<String, ArrayList<ImageLabel>> map = result.getFrameDataMap();
//        for(Map.Entry<String, ArrayList<ImageLabel>> entry: map.entrySet()){
//            entry.
//        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(result);
        Log.v("nuttygeeek_json", "Saving Json response in db: "+result.getFrameDataMap().toString());
        SharedPreferences videoPref = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = videoPref.edit();
        editor.putString(videoName, jsonString);
        editor.commit();
    }


    /**
     * this fxn returns the Object Detecttion Data from the shared pref
     * @param videoName video name
     * @return Image Detecttion Results
     */
    public ImageDetectionResult getObjectDetectionData(String videoName){
        SharedPreferences videoPref = context.getSharedPreferences(sharedPrefFileName, Context.MODE_PRIVATE);
        String jsonString = videoPref.getString(videoName, null);
        if(jsonString!=null){
            Gson gson = new Gson();
            ImageDetectionResult result = gson.fromJson(jsonString, ImageDetectionResult.class);
            return result;
        }else{
            return  null;
        }

    }

}
