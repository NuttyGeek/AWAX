package com.example.bhati.routeapplication.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
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
import com.example.bhati.routeapplication.R;
import com.example.bhati.routeapplication.helpers.FramesHelper;
import com.example.bhati.routeapplication.helpers.SharedPrefHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import java.util.List;
import java.util.Random;

public class FrameTest extends AppCompatActivity implements OnMapReadyCallback {


    BarChart chart;
    String videoUri;
    FramesHelper helper;
    ImageView image;
    Button extractButton;
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
        progress = findViewById(R.id.progress);
        mapView = findViewById(R.id.map);
        loadingView  = findViewById(R.id.loading_view);

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
        Log.v("nuttygeek_poly", "polylines coordinates: "+mainPolylinePoints.toString());
        Log.v("nuttygeek", videoUri);
        helper.setVideoPath(videoUri);
        // right, after setting video path, get the video name
        videoName = helper.getVideoName();
        Log.v("nuttygeek_vid", "Video Name: "+videoName);
        answerText.setText("Please wait.. Analyzing the Video !");

        chart.setDrawBarShadow(false);
        chart.setMaxVisibleValueCount(100);

        // if data is already present in shared pref don't do any processing
        imageDetectionResult = prefHelp.getObjectDetectionData(videoName);
        Log.v("nuttygeek_oncreate", "Image Detection Object: "+new Gson().toJson(imageDetectionResult));
        if(imageDetectionResult != null){
            Log.v("nuttygeek_oncreate", "Shared Pref Already have the data no need to do anything ");
            // put markers on map
            // -- get timestamps
            helper.createTimestampsFromImageDetectionResult(mainPolylinePoints,imageDetectionResult);
            // convert list of timestamps into list of LatLng Objects
//            ArrayList<LatLng> framePoints = helper.getCoordinatesFromTimeLocationMap();
            // we will draw the marker on map ready fxn

            // hiding the loading view
            loadingView.setVisibility(View.GONE);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // updating progress
                    extractButtonClickAction();
                    //uploadButtonAction();
                    loading.setVisibility(View.GONE);
//                answerText.setVisibility(View.GONE);
                }
            }, 2000);
        }


        //endregion

        // calling helper method
        //helper.getFrameFromVideo(videoUri, 10000, image);
        //Toast.makeText(this, "Length: "+helper.getLengthOfVideo(videoUri), Toast.LENGTH_SHORT).show();

    }

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
                Log.v("nuttygeek_od", "ImageDetecttion Obj: "+new Gson().toJson(imageDetectionResult));
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
                prefHelp.saveObjectDetectionData(videoName, imageDetectionResult);
                // now try to get the value from Shared Pref
                imageDetectionResult = prefHelp.getObjectDetectionData(videoName);
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
        // if we have the result for image detecttion draw the markers on the map
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
            map.addMarker(new MarkerOptions()
                    .position(point)
                    .title("Frame: "+points.indexOf(point))
            );
        }
    }




}

