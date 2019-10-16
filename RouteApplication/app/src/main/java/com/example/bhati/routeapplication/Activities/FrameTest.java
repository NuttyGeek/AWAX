package com.example.bhati.routeapplication.Activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.example.bhati.routeapplication.Pojo.FramesResult;
import com.example.bhati.routeapplication.Pojo.ImageDetectionResult;
import com.example.bhati.routeapplication.Pojo.ImageLabel;
import com.example.bhati.routeapplication.Pojo.UniqueLabelData;
import com.example.bhati.routeapplication.R;
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
import java.util.ArrayList;
import java.util.HashMap;
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
    // list of points in main polyline
    ArrayList<LatLng> mainPolylinePoints;
    int progressValue;
    ImageDetectionResult imageDetectionResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_test);

        prefHelp = new SharedPrefHelper(this);

        // init UI
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
        imageDetectionResult = null;
        // getting values from intent
        Intent i = getIntent();
        videoUri = i.getStringExtra("videoUri");
        mainPolylinePoints = (ArrayList<LatLng>) i.getSerializableExtra("list");
        Log.v("log_poly", "polylines coordinates: "+mainPolylinePoints.toString());
        Log.v("log", videoUri);
        helper.setVideoPath(videoUri);
        // right, after setting video path, get the video name
        videoName = helper.getVideoName();
        Log.v("log_vid", "Video Name: "+videoName);
        answerText.setText("Please wait.. Analyzing the Video !");
        // doing config for the chart
        chart.setDrawBarShadow(false);
        chart.setMaxVisibleValueCount(100);
        frameChart.setDrawBarShadow(false);
        frameChart.setMaxVisibleValueCount(100);

        //region testing
//        ImageLabel imgLabel = helper.getDesiredLabelObjectFromSimpleImageLabel(new ImageLabel("asd", "Pony car", 0.56f));
//        if(imgLabel!=null){
//            Log.v("allowed_label", imgLabel.getName());
//        }else{
//            Log.v("allowed_label", "IMage Label is not allowed, we don'' need to add it in the calculation ");
//        }
        //endregion

        // if data is already present in shared pref don't do any processing
        imageDetectionResult = prefHelp.getObjectDetectionData(videoName);
        Log.v("oncreate", "Image Detection Object: "+new Gson().toJson(imageDetectionResult));
        if(imageDetectionResult != null){
            Log.v("oncreate", "Shared Pref Already have the data no need to do anything ");
            // trying to test if the new res with new labels have some values 
            ImageDetectionResult newRes = helper.getNewImageDetectionResultFromOld(imageDetectionResult);
            Log.v("new_detection", newRes.toString());
            // put markers on map
            // -- get timestamps
            helper.createTimestampsFromImageDetectionResult(mainPolylinePoints,imageDetectionResult);
            // convert list of timestamps into list of LatLng Objects
//            ArrayList<LatLng> framePoints = helper.getCoordinatesFromTimeLocationMap();
            // we will draw the marker on map ready fxn
            // hiding the loading view
            loadingView.setVisibility(View.GONE);
            // hide the single frame chart
            frameChart.setVisibility(View.GONE);

        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // updating progress
                    extractButtonClickAction();
                    //uploadButtonAction();
                    loading.setVisibility(View.GONE);
                    // hide the single frame chart
                    frameChart.setVisibility(View.GONE);
//                answerText.setVisibility(View.GONE);
                }
            }, 1000);
        }

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
     * old function not working right now
     */
    public void uploadButtonAction(){
        try{
            Toast.makeText(FrameTest.this, "Analyzing Frames", Toast.LENGTH_SHORT).show();
            int max = 15000;
            int min = 7000;
            final int random = new Random().nextInt((max - min) + 1) + min;
            Thread.sleep(random);
            Toast.makeText(FrameTest.this, "Done Processing Frames!", Toast.LENGTH_SHORT).show();

            FramesResult res = helper.getFramesData();
            String ans_str = " Car: "+res.getCar() + "\n"
                    +" Vegetation: "+res.getVegetation()+"\n"
                    +" Person: "+res.getPerson()+ "\n"
                    +" Snapshot: "+res.getSnpashot();
            //answerText.setText(ans_str);

            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(1, Float.parseFloat(res.getCar())*100));
            entries.add(new BarEntry(2, Float.parseFloat(res.getVegetation())*100));
            entries.add(new BarEntry(3, Float.parseFloat(res.getPerson())*100));
            entries.add(new BarEntry(4, Float.parseFloat(res.getSnpashot())*100));


            BarDataSet set = new BarDataSet(entries, "Values");
            set.setColors(ColorTemplate.COLORFUL_COLORS);
            BarData data = new BarData(set);
            data.setBarWidth(0.4f);

            // customizing the x-axis labels
            labelsStringArray = new String[]{
                    "","Car","Vegetation", "People", "Snapshot"
            };

            chart.setData(data);
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsStringArray));
            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
            xAxis.setGranularity(1);
            xAxis.setAxisMinimum(1);
            chart.notifyDataSetChanged();
            chart.invalidate();

            // do the chart population

        }catch (Exception e){
            Toast.makeText(FrameTest.this, "", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * extract the frames and detect objects
     */
    public void extractButtonClickAction(){
        ArrayList<String> ansStrs = new ArrayList<>();
        // extract all images
        helper.extractAllFrames(new OnFrameExtracted() {
            @Override
            public void onFrameExtractionCompleted() {
                Toast.makeText(FrameTest.this, "All Frames Extracted !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void getExtractedFrameCount(int count) {
                Log.v("count", count+ "th frame extracted !");
                incrementProgress();
            }

            @Override
            public void getTotalFramesCount(int count) {
                Log.v("count", "Total no of frames to be extracted: "+count);
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
                    Log.v("result", "image Detection result no initialized , intialized it !");
                }
                // hiding the loading view
                loadingView.setVisibility(View.GONE);
                // adding the labels to the image detection result object
                imageDetectionResult.appendImageLabels(frameName, labels);
                // increment the progress
                incrementProgress();
                Log.v("od", "ImageDetection Obj: "+new Gson().toJson(imageDetectionResult));
            }

            @Override
            public void gotLabelsFailure(String error) {
                Toast.makeText(FrameTest.this, "Error Processing Frames!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void gotLabelsCompleted(String videoName) {
                // save the whole object in Shared Pref
                Toast.makeText(FrameTest.this, "Got All the images processed !", Toast.LENGTH_SHORT).show();
                Log.v("completed", "Image Detection Obj after completion: "+new Gson().toJson(imageDetectionResult));
                prefHelp.saveObjectDetectionData(videoName, imageDetectionResult);
                // now try to get the value from Shared Pref
                imageDetectionResult = prefHelp.getObjectDetectionData(videoName);
                Log.v("sp", "\nShared Pref: "+new Gson().toJson(imageDetectionResult));
                // creating time stamp map
                helper.createTimestampsFromImageDetectionResult(mainPolylinePoints,imageDetectionResult);
                // getting coordinates
                ArrayList<LatLng> framePoints = helper.getCoordinatesFromTimeLocationMap();
                // drawing markers on map
                drawMarkersOnMap(framePoints);
            }

            @Override
            public void getProcessedFramesCount(int count) {
                Log.v("count", count+"th frame processed");
                incrementProgress();
            }
        });

    }

    /**
     * increments the progress value by 1
     */
    public void incrementProgress(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int old = progress.getProgress();
                progress.setProgress(old+5);
                Log.v("count", "Progress: "+old+1);
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
        Log.v("count", "Setting Progress Bar Max Value: "+value);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        // drawing polyline
        drawPolyline(mainPolylinePoints);
        // move the camera to show the polyline
        setMapCamera(mainPolylinePoints.get(0));
        // if we have the result for image detection draw the markers on the map
        if(imageDetectionResult!=null){
            ArrayList<LatLng> framePoints = helper.getCoordinatesFromTimeLocationMap();
            drawMarkersOnMap(framePoints);
        }
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
        Toast.makeText(this, "Drawn the polyline !", Toast.LENGTH_SHORT).show();
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
                    .snippet("Jeans: 10")
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
        drawChartData();
        //testDrawMap();
        map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // show the over all button
                overAllButton.setVisibility(View.VISIBLE);
                // get the frame no on click
                Log.v("marker", "Position: "+marker.getPosition().toString());
                String frameName = helper.getFrameNameFromLocation(marker.getPosition());
                ArrayList<ImageLabel> labels =  helper.getImageLabelsFromFrameName(frameName, imageDetectionResult);
                String str = "";
                if (labels != null) {
                    for(ImageLabel label: labels){
                        str += "\n"+label.getName()+" : "+label.getScore();
                    }
                    marker.setSnippet(str);
                }
                // hide the overall chart
                chart.setVisibility(View.GONE);
                showSingleFrameMap(marker.getPosition());
                // now show the image related to this marker
                String absPath = helper.getAbsolutePathOfImageFromFrameName(frameName);
                frameImage.setImageBitmap(BitmapFactory.decodeFile(absPath));
                frameImage.setVisibility(View.VISIBLE);
                ViewCompat.setTranslationZ(frameImage, 5);
                return false;
            }
        });
    }

    /**
     * this fxn draw the chart data
     */
    public void drawChartData(){
        HashMap<String, UniqueLabelData> chartData = helper.getChartData(imageDetectionResult);
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Float> valueList = new ArrayList<>();

        // get labels from chartData
        for(HashMap.Entry<String, UniqueLabelData> entry: chartData.entrySet()){
            labels.add(entry.getKey());
        }

        // get values from hashmap
        for(HashMap.Entry<String, UniqueLabelData> entry: chartData.entrySet()){
            valueList.add(entry.getValue().getAverage());
        }
        // adding into entries
        for(Float value: valueList){
            entries.add(new BarEntry(valueList.indexOf(value), value));
        }
        //
        BarDataSet set = new BarDataSet(entries,"Labels" );
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        XAxis xaxis = chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawGridLines(false);
        xaxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return labels.get(index);
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
     * @param position location of the marker clicked on
     */
    public void showSingleFrameMap(LatLng position){
        // hide the overall frame
        frameChart.setVisibility(View.VISIBLE);
        // get frame data to show on map from this lat lng point
        String frameName = helper.getFrameNameFromLocation(position);
        Log.v("single", "Frame Name: "+frameName);
        ArrayList<ImageLabel> labelList = imageDetectionResult.getFrameDataMap().get(frameName);
        // show it on map with iteration over labels
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for(ImageLabel label: labelList){
            Log.v("single_label", label.getName()+" : "+label.getScore());
            labels.add(label.getName());
            entries.add(new BarEntry(labelList.indexOf(label), label.getScore()));
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
                    return labels.get(index);
                }
            }
        });
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        frameChart.setData(data);
        frameChart.setFitBars(true);
        frameChart.invalidate();

    }

//    /**
//     * this fxn is for testing image detection result allowed labels
//     * @param result result got from the image detection
//     */
//    public void testAllowedLabels(ImageDetectionResult result){
//        Log.v("detect_result", "\n"+result.toString()+"\n");
//        HashMap<String, ArrayList<ImageLabel>> map = result.getFrameDataMap();
//        for(Map.Entry<String, ArrayList<ImageLabel>> entry: map.entrySet()){
//            ArrayList<ImageLabel> labels = entry.getValue();
//            for(ImageLabel label: labels){
//                ImageLabel newLabel = helper.getDesiredLabelObjectFromSimpleImageLabel(label);
//                if(newLabel != null){
//                    Log.v("new_label", "Label: "+newLabel.getName());
//                }else{
//                    Log.v("new_label", "Label: null");
//                }
//            }
//        }
//        Log.v("detect_result", "\n"+result.toString()+"\n");
//    }


}

