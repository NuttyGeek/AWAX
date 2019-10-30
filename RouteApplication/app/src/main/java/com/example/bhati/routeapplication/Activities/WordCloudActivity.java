package com.example.bhati.routeapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.TagCloud;
import com.anychart.core.annotations.Line;
import com.anychart.data.Mapping;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.anychart.scales.OrdinalColor;
import com.example.bhati.routeapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordCloudActivity extends AppCompatActivity {

    HashMap<String, JSONArray> keyMap;
    ProgressBar progressBar;
    List<DataEntry> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_cloud);

        Intent i = getIntent();
        String videoName = i.getStringExtra("videoName");
        keyMap = getKeywordsFromSharedPref(videoName);
        progressBar = findViewById(R.id.progress);
        AnyChartView chartView = findViewById(R.id.chart);
        ArrayList<String> keywordsList = getKeywordsListFromHashMap(keyMap);

        //copied code
        TagCloud tagCloud = AnyChart.tagCloud();
        tagCloud.title("All Keywords");
        OrdinalColor ordinalColor = OrdinalColor.instantiate();
        ordinalColor.colors(new String[] {
                "#26959f", "#f18126", "#3b8ad8", "#60727b", "#e24b26"
        });
        tagCloud.colorScale(ordinalColor);
        tagCloud.angles(new Double[] {-90d, 0d, 90d});
        tagCloud.colorRange().enabled(true);
        tagCloud.colorRange().colorLineSize(15d);
        data = new ArrayList<>();
        data = getDataEntryList(keyMap);
        tagCloud.data(data);
        chartView.setChart(tagCloud);
        // hiding the progress bar
        progressBar.setVisibility(View.GONE);
        tagCloud.setOnClickListener(new ListenersInterface.OnClickListener() {
            @Override
            public void onClick(Event event) {
                Log.v("nuttygeek_click", "you clicked on word cloud ");
            }
        });
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

}
