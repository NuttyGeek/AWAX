package com.example.bhati.routeapplication.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bhati.routeapplication.Pojo.FramesResult;
import com.example.bhati.routeapplication.R;
import com.example.bhati.routeapplication.helpers.FramesHelper;
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
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.github.mikephil.charting.charts.HorizontalBarChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FrameTest extends AppCompatActivity {


    BarChart chart;
    String videoUri;
    FramesHelper helper;
    ImageView image;
    Button extractButton;
    ProgressBar loading;
    TextView answerText;
    ArrayList<String> propertyValues;
    ArrayList<String> labels;
    int currentLabelIndex;
    HashMap<String, String> valueHashmap;
    String[] labelsStringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_test);

        // init UI
        image = findViewById(R.id.image);
        // init helpers
        helper = new FramesHelper(this);
        // getting values from intent
        Intent i = getIntent();
        videoUri = i.getStringExtra("videoUri");
        Log.v("nuttygeek", videoUri);
        helper.setVideoPath(videoUri);

        valueHashmap = new HashMap<>();
        propertyValues = new ArrayList<>();
        loading = findViewById(R.id.loading);
        answerText = findViewById(R.id.answer);
        chart = findViewById(R.id.chart);
        labels = new ArrayList<>();
        labels.add("Car");
        labels.add("Vegetation");
        labels.add("Person");
        labels.add("Snapshot");

        answerText.setText("Please wait.. Analyzing the Video !");

        chart.setDrawBarShadow(false);
        chart.setMaxVisibleValueCount(100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                extractButtonClickAction();
                uploadButtonAction();
                loading.setVisibility(View.GONE);
                answerText.setVisibility(View.GONE);
            }
        }, 2000);
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

    public void extractButtonClickAction(){
        helper.extractAllFrames();
    }

}

