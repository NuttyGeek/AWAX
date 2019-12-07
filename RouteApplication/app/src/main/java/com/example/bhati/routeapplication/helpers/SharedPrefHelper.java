package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.bhati.routeapplication.Pojo.ImageDetectionResult;
import com.example.bhati.routeapplication.Pojo.ImageLabel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Image;

public class SharedPrefHelper {

    private Context context;
    //String sharedPrefFileName = "object_detection_json_data";
    private SharedPreferences prefs;
    public static String ARE_FRAMES_EXTRACTED_KEY = "frames_extracted";
    public static String TOTAL_FRAMES_KEY = "total_frames";
    public static String ARE_FRAMES_UPLOADED_KEY = "are_frames_uploaded";
    public static String IS_RESULT_AVAILABLE_KEY = "is_result_available";
    public static String RESULT = "object_detection_result";

    public static String AUDIO_SYNC = "audio_sync_json";


    // constructor
    public SharedPrefHelper(Context context, String videoName){
        this.context = context;
        prefs = context.getSharedPreferences(videoName, Context.MODE_PRIVATE);
    }


    /**
     * this fxn saves the key value pair in Shared Pref
     * @param key key to be used while retreval
     * @param value value to be saved
     */
    public void saveString(String key, String value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
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
     * this function saves int value in the shared pref
     * @param key key of the value ssaved
     * @param value int value saved in shared pref
     */
    public void saveInt(String key, int value){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     * this fxn returns a int value from shared pref file
     * @param key key of the value
     * @return int value
     */
    public int getInt(String key){
        int defaultValue = -1;
        return prefs.getInt(key, defaultValue);
    }

    /**
     * this fxn saves result as a string
     * @param resultString result string from server
     */
    public void saveResult(String resultString){
        saveString("result", resultString);
        saveBoolean(SharedPrefHelper.IS_RESULT_AVAILABLE_KEY, true);
    }

    /**
     * this fxn returns the result as a string
     */
    public String getResult(){
        return getString("result");
    }


    /**
     * this fxn saves the json object in shared pref of that video file
     * @param object object
     */
    public void saveAudioSyncJSONObject(JSONObject object){
        // convert this object to string and save it in shared pref
        String objStr = object.toString();
        saveString(AUDIO_SYNC, objStr);
        JSONObject res = getAudioSyncJSONObject();
        Log.v("ng_json_saved", res.toString());
    }

    /**
     * this fxn returns the audiosync json object
     * @return JSOn Object
     */
    public JSONObject getAudioSyncJSONObject(){
        String jsonStr = getString(AUDIO_SYNC);
        try{
            JSONObject obj = new JSONObject(jsonStr);
            return obj;
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }


}
