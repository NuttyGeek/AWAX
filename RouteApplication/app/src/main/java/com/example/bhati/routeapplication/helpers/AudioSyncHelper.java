package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * this helper is create dot sync audio with the related sentences
 *
 * 1. get the result from the server
 * 2. convert it to json object
 * 3. save json object in shared rpef
 *
 * and when user clicks on wordcloud
 * 1. get sentences from keywords
 * 2. get audio paths from sentences using shared pref value
 * 3. show them in bottom sheet
 */
public class AudioSyncHelper {

    Context context;
    String videoName;

    public AudioSyncHelper(Context context, String videoName){
        this.context  = context;
        this.videoName = videoName;
    }


    /**
     * this fxn saves the result in shared pref after converting it to a json object
     * @param res result from server
     */
    public void saveResultInSharedPref(String res){
        // log it here and see
        Log.v("ng_asyn", "result from server : "+res);
        String simplifiedResult = simplifyResult(res);
        // now convert this result into json object
        try{
            JSONObject obj = convertToJsonObject(simplifiedResult);
            // now save it in shared pref
            SharedPrefHelper helper = new SharedPrefHelper(context, videoName);
            helper.saveAudioSyncJSONObject(obj);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    /**
     * this fxn takes the server result and convert it to a json object with correct mapping
     * @param res result got from server
     * @return json object
     */
    public JSONObject convertToJsonObject(String res) throws JSONException {
        String[] parts = res.split("@");
        String[] simplifiedParts = new String[parts.length];
        // simplify parts remove colon (:) from last of both parts
        for(int i=0; i<parts.length; i++){
            String part = parts[i];
            simplifiedParts[i] = part.replaceAll(":$", "");
        }
        // now split these simplified parts further
        String[] sentences = simplifiedParts[0].split(":");
        String[] audioPaths = simplifiedParts[1].split(":");
        // create a json object now
        JSONObject newObj = new JSONObject();
        for(int i=0; i<sentences.length; i++){
            newObj.put(sentences[i], audioPaths[i]);
        }
        Log.v("ng_res_json",newObj.toString());
        return newObj;
    }

    /**
     * this fxn simplifies the result
     * @param res result
     * @return simplified result
     */
    public String simplifyResult(String res){
        return res.replace("\n", "").replace("\"", "");
    }


    /**
     * This fxn is
     */
    public ArrayList<String> getSentencesFromKeyword(String keyword) throws JSONException{
        ArrayList<String> sentences = new ArrayList<>();
        // get json res from shared pref
        SharedPrefHelper helper = new SharedPrefHelper(context, videoName);
        JSONObject obj = helper.getAudioSyncJSONObject();
        JSONArray keys = obj.names();
        for(int i=0; i<keys.length(); i++){
            String key = keys.get(i).toString();
            if(key.contains(keyword)){
                sentences.add(key);
            }
        }
        return sentences;
    }


    /**
     * this fxn takes a list of sentences and returns the list of audio files associated with it
     * @param sentences list of sentnecs
     * @return list of audio paths
     */
    public ArrayList<String> getAudioPathsFromSentences(ArrayList<String> sentences){
        ArrayList<String> result = new ArrayList<>();
        SharedPrefHelper helper = new SharedPrefHelper(context, videoName);
        JSONObject obj = helper.getAudioSyncJSONObject();
        for(String sentence: sentences){
            Log.v("ng_sentence", "sen: "+sentence);
            try{
                String audioPath = obj.get(sentence).toString();
                result.add(audioPath);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }
}
