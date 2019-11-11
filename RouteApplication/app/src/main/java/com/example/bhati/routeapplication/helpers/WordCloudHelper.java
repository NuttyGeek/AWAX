package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WordCloudHelper {

    Context context;

    public WordCloudHelper(Context context){
        this.context = context;
    }

    /**
     * this fxn adds textview to the flexbox object given
     * @param layout flexbox layout
     * @param list list of textviews
     */
    public void addTextViewToFlexbox(FlexboxLayout layout, ArrayList<TextView> list){
        // only add textview if the flexbox layout is empty
        TextView tx = (TextView) layout.getChildAt(0);
        if(tx==null){
            Log.v("nuttygeek_layout", "list: "+list.toString());
            for(TextView textView: list){
                layout.addView(textView);
            }
        }else{
            Log.v("nuttygeek_text", "textview are already added, no need to add more");
        }
    }

    /**
     * this fxn returns the list of textviews
     * @param map hashmap
     */
    public ArrayList<TextView> getTextviewListFromHashMap(HashMap<String, JSONArray> map,ArrayList<String> colorList, View.OnClickListener listener) throws JSONException {
        Log.v("nuttygeek_list", map.toString());
        ArrayList<TextView> textViewList = new ArrayList<>();
        // get list of keys
        Set keysSet = map.keySet();
        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            JSONArray arr = entry.getValue();
            String chunkName = entry.getKey();
            int chunkNo = Integer.parseInt(chunkName.replace("chunk", ""));
            for(int i=0; i<arr.length(); i++){
                TextView textView = new TextView(context);
                String keyStr = arr.get(i).toString();
//                ViewGroup.LayoutParams params = textView.getLayoutParams();
//                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                textView.setText(keyStr);
                textView.setClickable(true);
                float size = getAccurateSize(keyStr);
                textView.setTextSize(size);
                textView.setTextColor(Color.parseColor(colorList.get(chunkNo)));
                textView.setOnClickListener(listener);
                textViewList.add(textView);
            }
            Log.v("nuttygeek_text", textViewList.get(0).getText().toString());
        }
        return textViewList;
    }

    /**
     * this fxn returns the string list of keywords from hashmap
     * @return strign list of keywords seperated by comma ( , )
     */
    public String getStringOfKeywords(HashMap<String, JSONArray> map) throws JSONException {
        String keywordsStr = "";
        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            JSONArray keywordArr = entry.getValue();
            for(int i=0; i<keywordArr.length();i++){
                // if it is last item don't add comma
                if(i==keywordArr.length()-1){
                    String tempStr = keywordArr.get(i).toString();
                    keywordsStr += tempStr;
                }else{
                    String tempStr = keywordArr.get(i).toString()+",";
                    keywordsStr += tempStr;
                }
            }
        }
        return keywordsStr;
    }

    /**
     * this fxn returns  a string with list of values
     * @return string
     */
    public String getStringOfImportanceValues(String keywordsString){
        String valuesStr = "";
        int normalValue = 1;
        int labelValue = 5;
        String[] keywords = keywordsString.split(",");
        for(int i=0; i<keywords.length; i++){
            String keyword = keywords[i];
            int value = 0;
            if(isValidLabel(keyword)){
                value = labelValue;
            }else{
                // get no of occurances in the string
                int noOfOccurances = countOccurences(keywordsString,keyword);
                value = noOfOccurances;
            }
            // if it is last item don't add comma
            if(i==(keywords.length-1)){
                valuesStr += String.valueOf(value);
            }else{
                valuesStr += (String.valueOf(value)+",");
            }
        }
        return valuesStr;
    }

    /**
     * this fxn gets the keywords from shared pref
     * @param videoName name of video
     */
    public HashMap<String, JSONArray> getKeywordsFromSharedPref(String videoName){
        HashMap<String, JSONArray> keywordsMap = new HashMap<>();
        SharedPreferences pref = context.getSharedPreferences("keywords", Context.MODE_PRIVATE);
        String objStr = pref.getString(videoName, null);
        if(objStr!=null){
            try{
                JSONObject obj = new JSONObject(objStr);
                Log.v("nuttygeek_json", obj.toString());
                JSONArray arr = obj.names();
                for(int i=0; i<arr.length(); i++){
                    JSONArray tempArray = obj.getJSONArray(arr.get(i).toString());
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
    public float getAccurateSize(String keyStr){
        int[] sizes = {18,32,64,128};
        // if the keyword is server label size should be 128
        boolean isValidLabel = isValidLabel(keyStr);
        if(isValidLabel){
            return 80;
        }else{
            double random = Math.random();
            Log.v("nuttygeek_random", "random: "+random);
            float value = (float) random * 10;
            int intVal = (int) value;
            Log.v("nuttygeek_random", "value: "+value);
            int index = intVal % sizes.length;
            Log.v("nuttygeek_random", "index: "+index);
            Log.v("nuttygeek_random", "random size: "+sizes[index]);
            return 40;
        }
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


    /**
     * this fxn returns boolean result telling if the keyword passed is one of keywords on server
     * @param keyword keyword to be checked
     * @return boolean result
     */
    public boolean isValidLabel(String keyword){
        String[] serverKeywords = {"road", " sidewalk", "building", "wall",
        "fence", "pole",
        "traffic light",
        "traffic sign",
        "vegetation", "terrain",
        "sky", "person",
        "rider", "car",
        "truck", "bus train",
        "motocycle"};
        boolean valid = Arrays.asList(serverKeywords).contains(keyword);
        return valid;
    }

    /**
     * this fucntion count occurances of a keywords in the keywords String
     * @param str string of keywords with commas
     * @param word word for which we we need to calculate the occurances
     * @return int no of ocuurances
     */
    public int countOccurences(String str, String word) {
        // split the string by spaces in a
        String a[] = str.split(",");
        // search for pattern in a
        int count = 0;
        for (int i = 0; i < a.length; i++)
        {
            String item = a[i];
            // if match found increase count
            if (word.equals(item)){
                count++;
            }
        }
        return count;
    }

    /**
     * this fxn returns the index in which the keyword belongs to
     * @param keyword keywords we are searching for
     * @param map full map
     * @return index of the keyword
     */
    public int getAudioChunkIndexFromKeyword(String keyword, HashMap<String, JSONArray> map) throws JSONException {
        for(HashMap.Entry<String, JSONArray> entry: map.entrySet()){
            String chunkName = entry.getKey();
            int indexFromChunkName = Integer.parseInt(chunkName.replace("chunk", ""));
            JSONArray keywordsArr= entry.getValue();
            for(int i=0; i<keywordsArr.length(); i++){
                if(keyword.equals(keywordsArr.get(i).toString())){
                    return indexFromChunkName;
                }
            }
        }
        return 0;
    }
}
