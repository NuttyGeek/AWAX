package com.example.bhati.routeapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.example.bhati.routeapplication.R;
import com.google.android.flexbox.FlexboxLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WordCloudActivity extends AppCompatActivity implements View.OnClickListener {

    HashMap<String, JSONArray> keyMap;
    ProgressBar progressBar;
    ArrayList<TextView> textViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);

        Intent i = getIntent();
        String videoName = i.getStringExtra("videoName");
        keyMap = getKeywordsFromSharedPref(videoName);
        progressBar = findViewById(R.id.progress);
        FlexboxLayout flexboxLayout = (FlexboxLayout) findViewById(R.id.flexbox);
        // getting the data from shared pref
        HashMap<String, JSONArray> map = getKeywordsFromSharedPref(videoName);
        try{
            textViewList = getTextviewListFromHashMap(map);
            Log.v("nuttygeek_textview_list", textViewList.toString());

        }catch(JSONException e){
            e.printStackTrace();
        }
        addTextViewToFlexbox(flexboxLayout, textViewList);

    }

    /**
     * this fxn adds textview to the flexbox object given
     * @param layout flexbox layout
     * @param list list of textviews
     */
    public void addTextViewToFlexbox(FlexboxLayout layout, ArrayList<TextView> list){
        Log.v("nuttygeek_layout", "list: "+list.toString());
        for(TextView textView: list){
            layout.addView(textView);
        }
    }

    /**
     * this fxn returns the list of textviews
     * @param map hashmap
     */
    public ArrayList<TextView> getTextviewListFromHashMap(HashMap<String, JSONArray> map) throws JSONException {
        Log.v("nuttygeek_list", map.toString());
        ArrayList<TextView> textViewList = new ArrayList<>();
        // get list of keys
        Set keysSet = map.keySet();


        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            JSONArray arr = entry.getValue();
            for(int i=0; i<arr.length(); i++){
                TextView textView = new TextView(this);
                textView.setText(arr.get(i).toString());
                textView.setTextSize(getRandomSize());
                textView.setTextColor(Color.parseColor("#008577"));
                textView.setOnClickListener(this);
                textViewList.add(textView);
            }
        }
        return textViewList;
    }

    @Override
    public void onClick(View view) {
        // whenever any textview is clicked you get here
        TextView textview = (TextView)view;
        textview.setTextColor(Color.BLACK);
        String keywordText = textview.getText().toString();
        Log.v("nuttygeek_click", "clicked on: "+keywordText);
        // get the audioChunk from keyword name
        try{
            String audioChunkName = getChunkNameFromKeyword(keywordText, keyMap);
            if(audioChunkName!=null){
                Log.v("nuttygeek_intent", "chunk: "+audioChunkName+ " keyword: "+keywordText);
                Intent i = new Intent();
                i.putExtra("chunkName", audioChunkName);
                i.putExtra("keyword", keywordText);
                setResult(Activity.RESULT_OK, i);
                finish();
            }else{
                Log.v("nuttygeek_intent", "audio chunk name is null");
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

    }

    /**
     * this fxn gets the keywords from shared pref
     * @param videoName name of video
     */
    public HashMap<String, JSONArray> getKeywordsFromSharedPref(String videoName){
        HashMap<String, JSONArray> keywordsMap = new HashMap<>();
        SharedPreferences pref = getSharedPreferences("keywords", MODE_PRIVATE);
        String objStr = pref.getString(videoName, null);
        if(objStr!=null){
            try{
                JSONObject obj = new JSONObject(objStr);
                Log.v("nuttygeek_json", obj.toString());
                JSONArray arr = obj.names();
                for(int i=0; i<arr.length(); i++){
                     JSONArray tempArray = obj.getJSONArray(arr.get(0).toString());
                     Log.v("nuttygeek_json_arr", tempArray.toString());
                     keywordsMap.put("chunk"+i,tempArray);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }else{
            Log.v("nuttygeek_shared", "Shared pref is null");
        }
        return keywordsMap;
    }

    /**
     * this fxn converts the hashmap into arraylits of keywords
     * @param map keywords hashmap
     * @return list of keywords
     */
    public ArrayList<String> getKeywordsListFromHashMap(HashMap<String, JSONArray> map){
        ArrayList<String> keywords = new ArrayList<>();
        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            JSONArray arr = entry.getValue();
            for(int i=0; i<arr.length(); i++){
                try {
                    keywords.add(arr.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return keywords;
    }

    /**
     * this fxn returns the data entry list
     * @return data entry list
     */
    public List<DataEntry> getDataEntryList(HashMap<String, JSONArray> map){
        List<DataEntry> dataEntryList = new ArrayList<>();
        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            String chunkName = entry.getKey();
            JSONArray arr = map.get(chunkName);
           for(int i=0; i<arr.length();i++ ){
               try {
                   String keyword = arr.get(i).toString();
                   if(keyword!="``"){
                       dataEntryList.add(new CategoryValueDataEntry(keyword,chunkName, 123));
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }
        return dataEntryList;
    }

    /**
     * this fxn returns a random size
     * @return
     */
    public float getRandomSize(){
        int[] sizes = {18,32,64,128};
        double random = Math.random();
        Log.v("nuttygeek_random", "random: "+random);
        float value = (float) random * 10;
        int intVal = (int) value;
        Log.v("nuttygeek_random", "value: "+value);
        int index = intVal % sizes.length;
        Log.v("nuttygeek_random", "index: "+index);
        Log.v("nuttygeek_random", "random size: "+sizes[index]);
        return sizes[index];
    }

    /**
     * this fxn returns he chunk name in which the keyword is present
     * @param keyword name of keyword
     * @param map chunk - keywords map
     * @return null or chunk name
     * @throws JSONException
     */
    public String getChunkNameFromKeyword(String keyword, HashMap<String, JSONArray> map) throws JSONException{
        String chunkName = null;
        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            chunkName = entry.getKey();
            JSONArray tempArr = entry.getValue();
            for(int i=0; i<tempArr.length(); i++){
                if(keyword.equals(tempArr.get(i).toString())){
                    return chunkName;
                }
            }
        }
        return chunkName;
    }

}
