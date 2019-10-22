package com.example.bhati.routeapplication.Activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhati.routeapplication.Interface.GotLabels;
import com.example.bhati.routeapplication.Interface.OnFrameExtracted;
import com.example.bhati.routeapplication.Model.LabelPOJO;
import com.example.bhati.routeapplication.Pojo.FramesResult;
import com.example.bhati.routeapplication.Pojo.ImageDetectionResult;
import com.example.bhati.routeapplication.Pojo.ImageLabel;
import com.example.bhati.routeapplication.Pojo.UniqueLabelData;
import com.example.bhati.routeapplication.R;
import com.example.bhati.routeapplication.Services.FrameUploadService;
import com.example.bhati.routeapplication.helpers.FramesHelper;
import com.example.bhati.routeapplication.helpers.SharedPrefHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FrameTest extends AppCompatActivity implements OnMapReadyCallback {


    BarChart chart, frameChart;
    String videoUri;
    FramesHelper helper;
    ImageView image;
    Button overAllButton;
    ProgressBar loading, progress;
    TextView answerText;
    String[] labelsStringArray;
    // firebase vision image from uri
    SharedPrefHelper prefHelp;
    LinearLayout loadingView;
    MapView mapView;
    MapboxMap map;
    String videoName;
    Polyline polyline;
    ImageView frameImage;
    TextView loadingText;
    // list of points in main polyline
    ArrayList<LatLng> mainPolylinePoints;
    int progressValue;
    // ImageDetectionResult imageDetectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_test);
        //TODO: prefHelp = new SharedPrefHelper(getApplicationContext());
        // init UI
        loadingText = findViewById(R.id.loading_text);
        image = findViewById(R.id.image);
        loading = findViewById(R.id.loading);
        answerText = findViewById(R.id.answer);
        chart = findViewById(R.id.chart);
        frameChart = findViewById(R.id.frame_chart);
        progress = findViewById(R.id.progress);
        mapView = findViewById(R.id.map);
        loadingView  = findViewById(R.id.loading_view);
        overAllButton = findViewById(R.id.overall_button);
        frameImage = findViewById(R.id.frame_image);

        overAllButton.setVisibility(View.GONE);
        frameImage.setVisibility(View.GONE);

        // getting map data
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // init helpers
        helper = new FramesHelper(this);
        // imageDetectionResult = null;

        // getting values from intent
        Intent i = getIntent();
        videoUri = i.getStringExtra("videoUri");
        // set the video path in helper
        helper.setVideoPath(videoUri);
        // right, after setting video path, get the video name
        videoName = helper.getVideoName(videoUri);
        //region frames service code
        if(isResultStored()){
            // hiding loading view
            loadingView.setVisibility(View.GONE);
            // show the graph & other stuff
            Log.v("nuttygeek_service", "Result is stored in shared pref");
            helper.selectNPointsFromArrayList(10,2);
            // get result from shared pref
            String result = getresultFromSharedPref();
            HashMap<String, Double> map = helper.getAggregateResultFromResultString(result);
            // draw graph from aggregate result
            drawOverAllChart(map);
            // extract individual data also
            helper.extractIndividualFrameData(result);
        }else{
            // check if images are uploaded
            if(areFramesUploaded()){
                // hiding loading view
                loadingView.setVisibility(View.GONE);
                Log.v("nuttygeek_service", "Frames are already uploaded, do you work & show the graph");
                // now we just have to get results from server
                FrameUploadService.startActionGetResults(getApplicationContext(), Uri.parse(this.videoUri));
            }else{
                // check if frames are extracted or not
                if(areFramesExtracted()){
                    loadingView.setVisibility(View.VISIBLE);
                    loadingText.setText("Frames are extracted & are being uploaded to server, please come back later");
                    Toast.makeText(this, "Frames are already extracted !", Toast.LENGTH_SHORT).show();
                    Log.v("nuttygeek_service", "Frames are already extracted !");
                    // check if frames are uploaded
                    Log.v("nuttygeek_service", "Frames are not uploaded ");
                    // upload frames
                    FrameUploadService.startActionUploading(getApplicationContext(),Uri.parse(this.videoUri));
                }else{
                    loadingText.setText("Frames are not extracted ");
                    // frames are not extracted nor uploaded, neither got the result from server
                    Toast.makeText(this, "Frames are not extracted from the video ", Toast.LENGTH_SHORT).show();
                    Log.v("nuttygeek_service", "frames are not extracted yet");
                }
            }
        }
        //endregion

        mainPolylinePoints = (ArrayList<LatLng>) i.getSerializableExtra("list");
        Log.v("nuttygeek_poly", "polylines coordinates: "+mainPolylinePoints.toString());
        Log.v("nuttygeek", videoUri);
        Log.v("nuttygeek_vid", "Video Name: "+videoName);
        answerText.setText("Please wait.. Analyzing the Video !");

        chart.setDrawBarShadow(false);
        chart.setMaxVisibleValueCount(100);
        frameChart.setDrawBarShadow(false);
        frameChart.setMaxVisibleValueCount(100);

        //endregion

        // calling helper method
        //helper.getFrameFromVideo(videoUri, 10000, image);
        //Toast.makeText(this, "Length: "+helper.getLengthOfVideo(videoUri), Toast.LENGTH_SHORT).show();
        overAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide the single frame graph
                frameChart.setVisibility(View.GONE);
                // show the over all graph
                chart.setVisibility(View.VISIBLE);
                // hide the button itself
                overAllButton.setVisibility(View.GONE);
                // hide the image
                frameImage.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * extract the frames and detect objects
     */
/*    public void extractButtonClickAction(){
        ArrayList<String> ansStrs = new ArrayList<>();
        // extract all images
        helper.extractAllFrames(new OnFrameExtracted() {
            @Override
            public void onFrameExtractionCompleted() {
                Toast.makeText(FrameTest.this, "All Frames Extracted !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void getExtractedFrameCount(int count) {
                Log.v("nuttygeek_count", count+ "th frame extracted !");
                incrementProgress();
            }

            @Override
            public void getTotalFramesCount(int count) {
                Log.v("nuttygeek_count", "Total no of frames to be extracted: "+count);
                setMaxValueForProgressBar(count*2);
            }
        });
        // process all images
        helper.processAllImagesForLabeling(new GotLabels(){
            @Override
            public void gotLabelsSuccess(String videoName, String frameName, ArrayList<ImageLabel> labels) {
                ArrayList<ImageLabel> desiredLabels = new ArrayList<>();
                // filter the labels here, only add those labels which we want to add

                // init the ImageDetectionResult Object
                if(imageDetectionResult == null){
                    imageDetectionResult = new ImageDetectionResult(videoName);
                    Log.v("nuttygeek_result", "image Detection result no initialized , intialized it !");
                }
                // hiding the loading view
                loadingView.setVisibility(View.GONE);
                // adding the labels to the image detection result object
                imageDetectionResult.appendImageLabels(frameName, labels);
                // increment the progress
                incrementProgress();
                Log.v("nuttygeek_od", "ImageDetection Obj: "+new Gson().toJson(imageDetectionResult));
            }

            @Override
            public void gotLabelsFailure(String error) {
                Toast.makeText(FrameTest.this, "Error Processing Frames!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void gotLabelsCompleted(String videoName) {
                // save the whole object in Shared Pref
                Toast.makeText(FrameTest.this, "Got All the images processed !", Toast.LENGTH_SHORT).show();
                Log.v("nuttygeek_completed", "Image Detection Obj after completion: "+new Gson().toJson(imageDetectionResult));
                //TODO: prefHelp.saveObjectDetectionData(videoName, imageDetectionResult);
                // now try to get the value from Shared Pref
                //TODO: imageDetectionResult = prefHelp.getObjectDetectionData(videoName);
                Log.v("nuttygeek_sp", "\nShared Pref: "+new Gson().toJson(imageDetectionResult));
                // creating time stamp map
                helper.createTimestampsFromImageDetectionResult(mainPolylinePoints,imageDetectionResult);
                // getting coordinates
                ArrayList<LatLng> framePoints = helper.getCoordinatesFromTimeLocationMap();
                // drawing markers on map
                drawMarkersOnMap(framePoints);
            }

            @Override
            public void getProcessedFramesCount(int count) {
                Log.v("nuttygeek_count", count+"th frame processed");
                incrementProgress();
            }
        });

    }
*/


    /**
     * increments the progress value by 1
     */
    public void incrementProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int old = progress.getProgress();
                progress.setProgress(old+5);
                Log.v("nuttygeek_count", "Progress: "+old+1);
            }
        });
    }

    /**
     * set te max value for progress bar
     * @param value max value for progress bar
     */
    public void setMaxValueForProgressBar(int value){
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setMax(value);
        Log.v("nuttygeek_count", "Setting Progress Bar Max Value: "+value);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        // drawing polyline
        drawPolyline(mainPolylinePoints);
        // move the camera to show the polyline
        setMapCamera(mainPolylinePoints.get(0));
        // if we have the result for image detection draw the markers on the map
        // draw it anyways
        ArrayList<LatLng> framePoints = helper.getCoordinatesList(mainPolylinePoints, getresultFromSharedPref());
        Log.v("nuttygeek_points", "size: "+framePoints.size());
        drawMarkersOnMap(framePoints);
    }


    /**
     * this fxn draws the polyline on map with the passed list of latitude and longitudes
     * @param points arraylist lat lng points
     */
    public void drawPolyline(ArrayList<LatLng> points){
        polyline = map.addPolyline(new PolylineOptions()
                .width(20f)
                .color(Color.GREEN)
                .alpha(1f)
                .addAll(points));
        // Toast.makeText(this, "Drawn the polyline !", Toast.LENGTH_SHORT).show();
    }

    /**
     * this fxn sets the camera on the given point
     * @param point LatLng on which we want to set camera
     */
    public void setMapCamera(LatLng point){
        CameraPosition position = new CameraPosition.Builder()
                .target(point)
                .zoom(17)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    /**
     * this fxn drawa the list of markers on map
     */
    public void drawMarkersOnMap(ArrayList<LatLng> points){
        for(LatLng point: points){
            // if it is the first point
            map.addMarker(new MarkerOptions()
                    .position(point)
                    .title("Single Frame Analysis")
            );
        }
        // now draw the starting of polyline
        IconFactory iconFactory = IconFactory.getInstance(this);
        map.addMarker(new MarkerOptions()
        .position(mainPolylinePoints.get(0))
        .title("Starting Point")
        .setIcon(iconFactory.fromResource(R.drawable.marker_red))
        );
        // now draw the last point of polyline
        map.addMarker(new MarkerOptions()
        .position(mainPolylinePoints.get(mainPolylinePoints.size()-1))
        .title("Last Point")
        .setIcon(iconFactory.fromResource(R.drawable.marker_blue))
        );
        //TODO: draw chart data based on the new result
        //drawChartData();
        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // show the over all button
                overAllButton.setVisibility(View.VISIBLE);
                // get the frame no on click
                Log.v("nuttygeek_marker", "Position: "+marker.getPosition().toString());
                // get frame name from position
                ArrayList<LatLng> selectedList = helper.getCoordinatesList(mainPolylinePoints, getresultFromSharedPref());
                // now get frame name from position
                String frameName = helper.getFrameNameFromLocation(marker.getPosition(), selectedList);
                Log.v("nuttygeek_framename", "Frame: "+frameName);
                // get lableObject from position
                // hide the overall chart
                chart.setVisibility(View.GONE);
                //TODO: show single frame chart according to new result
                showSingleFrameGraph(frameName);
                // now show the image related to this marker
                String absPath = helper.getAbsolutePathOfImageFromFrameName(frameName.split("\\.(?=[^\\.]+$)")[0]);
                if(new File(absPath).exists()){
                    Log.v("nuttygeek_file", "Image named: "+absPath+ " exists !");
                    frameImage.setImageBitmap(BitmapFactory.decodeFile(absPath));
                    frameImage.setVisibility(View.VISIBLE);
                    ViewCompat.setTranslationZ(frameImage, 5);
                }else{
                    frameImage.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    /*
     * this fxn draw the chart data
     *
//    public void drawChartData(){
//        //HashMap<String, UniqueLabelData> chartData = helper.getChartData(imageDetectionResult);
//        // just for now
//        HashMap<String, UniqueLabelData> chartData = helper.getChartData(null);
//        ArrayList<BarEntry> entries = new ArrayList<>();
//        ArrayList<String> labels = new ArrayList<>();
//        ArrayList<Float> valueList = new ArrayList<>();
//
//        // get labels from chartData
//        for(HashMap.Entry<String, UniqueLabelData> entry: chartData.entrySet()){
//            labels.add(entry.getKey());
//        }
//
//        // get values from hashmap
//        for(HashMap.Entry<String, UniqueLabelData> entry: chartData.entrySet()){
//            valueList.add(entry.getValue().getAverage());
//        }
//        // adding into entries
//        for(Float value: valueList){
//            entries.add(new BarEntry(valueList.indexOf(value), value));
//        }
//        //
//        BarDataSet set = new BarDataSet(entries,"Labels" );
//        set.setColors(ColorTemplate.COLORFUL_COLORS);
//        XAxis xaxis = chart.getXAxis();
//        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xaxis.setDrawGridLines(false);
//        xaxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                int index = (int) value;
//                return labels.get(index);
//            }
//        });
//        BarData data = new BarData(set);
//        data.setBarWidth(0.9f);
//        chart.setData(data);
//        chart.setFitBars(true);
//        chart.invalidate();
//    }*/


    /**
     * this fxn draws the aggreate chart data
     * @param map Hashmap containing all the values
     */
    public void drawOverAllChart(HashMap<String, Double> map){
        // hide the single frame chart
        frameChart.setVisibility(View.GONE);
        // entries for bar chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        // creating list of lables & values
        ArrayList<String> labelsList = new ArrayList<>();
        ArrayList<Double> valuesList = new ArrayList<>();
        for(HashMap.Entry<String, Double> entry: map.entrySet()){
            labelsList.add(entry.getKey());
            valuesList.add(entry.getValue());
        }
        // updating entries array
       for(int i=0; i<valuesList.size(); i++){
           entries.add(new BarEntry(i,valuesList.get(i).floatValue()));
       }
        BarDataSet set = new BarDataSet(entries,"Labels" );
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        XAxis xaxis = chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        xaxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return labelsList.get(index);
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        chart.setData(data);
        chart.setFitBars(true);
        chart.invalidate();
    }

    /**
     * this fxn hides the overall and shows the single frame map according to the marker clicked on
     * @param frameName of the marker clicked on
     */
    public void showSingleFrameGraph(String frameName){
        // hide the overall frame
        frameChart.setVisibility(View.VISIBLE);
        // get label pojo object from coordinate
        LabelPOJO pojoObj = helper.getLabelPojoFromFrameName(frameName);
        // then get hashmap from label pojo object
        HashMap<String, Double> resultHashMap = pojoObj.getFullHashMap();
        // LabelPOJO labelObj =
        // show it on map with iteration over labels
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labelsList = new ArrayList<>();
        ArrayList<Double> valuesList = new ArrayList<>();
//        for(ImageLabel label: labelList){
//            Log.v("nuttygeek_single_label", label.getName()+" : "+label.getScore());
//            labels.add(label.getName());
//            entries.add(new BarEntry(labelList.indexOf(label), label.getScore()));
//        }
        // get list of labels from hashmap
        for(HashMap.Entry<String, Double> entry: resultHashMap.entrySet()){
            labelsList.add(entry.getKey());
            valuesList.add(entry.getValue());
        }
        // create bar entry list
        for(int i=0; i<labelsList.size(); i++){
            entries.add(new BarEntry(i, valuesList.get(i).floatValue()));
        }
        BarDataSet set = new BarDataSet(entries,"Labels" );
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        XAxis xaxis = frameChart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        xaxis.setGranularity(1);
        xaxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(value<0){
                    return "";
                }else{
                    int index = (int) value;
                    return labelsList.get(index);
                }
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        frameChart.setData(data);
        frameChart.setFitBars(true);
        frameChart.invalidate();

    }

    /**
     * this fxn returns the value from shared pref
     * @return true/false
     */
    public boolean areFramesExtracted(){
        SharedPrefHelper prefHelper = new SharedPrefHelper(this, this.videoName);
        boolean result = prefHelper.getBoolean(SharedPrefHelper.ARE_FRAMES_EXTRACTED_KEY);
        return result;
    }

    /**
     * this fxn tells if frames are uploaded to server or not
     * @return
     */
    public boolean areFramesUploaded(){
        SharedPrefHelper prefHelper = new SharedPrefHelper(this, this.videoName);
        boolean result = prefHelper.getBoolean(SharedPrefHelper.ARE_FRAMES_UPLOADED_KEY);
        return result;
    }

    /**
     * this fxn tells if result is available in shared pref or not
     * @return true/false
     */
    public boolean isResultStored(){
        SharedPrefHelper prefHelper = new SharedPrefHelper(this, this.videoName);
        boolean result = prefHelper.getBoolean(SharedPrefHelper.IS_RESULT_AVAILABLE_KEY);
        return result;
    }

    /**
     * this fxn returns the result string from shared pref
     * @return result string
     */
    public String getresultFromSharedPref(){
        SharedPrefHelper prefHelper = new SharedPrefHelper(this, this.videoName);
        String result = prefHelper.getResult();
        return result;
    }

}

