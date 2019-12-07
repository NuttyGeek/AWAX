package com.example.bhati.routeapplication.Activities;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.net.IpSecManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bhati.routeapplication.Database.DBHelper;
import com.example.bhati.routeapplication.Interface.OnKeywordsReady;
import com.example.bhati.routeapplication.Interface.OnMarkerReadyListener;
import com.example.bhati.routeapplication.Interface.OnWordClicked;
import com.example.bhati.routeapplication.Model.BottomSheetDialog;
import com.example.bhati.routeapplication.Model.ColorText;
import com.example.bhati.routeapplication.Model.Recorder;
import com.example.bhati.routeapplication.MyAppl;
import com.example.bhati.routeapplication.R;
import com.example.bhati.routeapplication.directionhelpers.TaskLoadedCallback;
import com.example.bhati.routeapplication.helpers.AudioChunkDialog;
import com.example.bhati.routeapplication.helpers.AudioSyncHelper;
import com.example.bhati.routeapplication.helpers.FramesHelper;
import com.example.bhati.routeapplication.helpers.KeywordsDialog;
import com.example.bhati.routeapplication.helpers.KeywordsHelper;
import com.example.bhati.routeapplication.helpers.MapAndVideoSeekHelper;
import com.example.bhati.routeapplication.helpers.WordCloudHelper;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.flexbox.FlexboxLayout;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


//import com.example.audiolib.AndroidAudioConverter;
//import com.example.audiolib.callback.IConvertCallback;
//import com.example.audiolib.model.AudioFormat;

public class SavingActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback /*, LocationEngineListener*/ {


    private com.mapbox.mapboxsdk.annotations.PolylineOptions plo;
    private Polyline currentPolyline;
    String videoUri;
    private MapView mapView;
    private MapboxMap map;
    public Boolean prevplay;
    private Location originLocation;
    private Button btnUpload;
    private Button btnSpeechToText;
    // no need now
    //ToggleButton wordCloudButton;
    private VideoView videoView;
    private ToggleButton btnPlay;
    private Marker currentLocationMarker;
    // list of point on main polyline
    private ArrayList<LatLng> list;
    private ArrayList<LatLng> list_overlay_polyline;
    private ArrayList<Recorder> list1;
    private LatLng point1;
    private LatLng seek_point;
    private LatLng point2;
    private static final String TAG = "SavingActivtiy";
    private File videoFile;
    private boolean isVideoIsPlaying;
    private Marker marker_start_point, marker_end_point, intial_marker;
    private long pauseTime;
    private ValueAnimator markerAnimator;
    private boolean isVideoCompleted;
    private String str;
    private String str1;
    public String filePath;
    private String str_aurdio_file;
    private String[] arrayStr;
    private String[] arrayStr1;
    ArrayList<String> colorStrList;
    FrameLayout videoFrame;
    WordCloudHelper wordCloudHelper;
    //  to get the distinct no of polylines created on map

    int count = 0;
    int ct = 0;
    int ctotal = 0;
    double speed = 0;
    long time_to_speed = 0;
    Handler handler;
    double distance = 0;
    long d = 0;
    long rem_time = 0;
    private ImageView imgLogout;
    SeekBar seekbar_video;
    int last_seekbarvalue;
    Map<Integer, Integer> mapOfPosts;
    int RECORDER_BPP = 16;
    private static int RECORDER_SAMPLERATE = 8000;
    ProgressDialog progress;
    FFmpeg fFmpeg;
    ProgressBar progress_bar_speechto_text;
    Polyline mpolines;
    Polyline mpolines1;
    double smallestDistance = 50;
    Location closestLocation;
    Icon icon_strt;
    Icon icon_playing;
    Icon icon_pause;
    boolean is_pollyline_tounched = false;
    int previous_second;
    DBHelper dd;
    LinearLayout menuLayout;
    ListView colorList;
    ArrayList<ColorText> mainColorTextList;
    ColorAdapter colorAdapter;
    //ImageView audioImage;
    //  Text to Speech Linear Layout
    LinearLayout speechToTextLayout;
    MapAndVideoSeekHelper mapAndVideoSeekHelper;
    private ArrayList<String> listChumktime;
    private ArrayList<String> listChumktext;
    List<List<LatLng>> lists_pollline = new ArrayList<List<LatLng>>();
    Button framesButton;
    FramesHelper framesHelper;
    WebView webView;
    ImageButton backButton;
    private Button videoTabButton, segmentTabButton, semanticTabButton;
    int[] tabIds = {R.id.videoTab, R.id.segmentTab, R.id.semanticsTab};
    LinearLayout videoLayout, webViewLayout;
    LinearLayout  buttonsLayout;
    RelativeLayout mapLayout;

    ArrayList<Polyline> highlightedPolyines;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//region getting fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//endregion
        setContentView(R.layout.activity_saving);

        // clear the old audio files in properties audioFiles
        properties.audioPaths.clear();
        // clear old polylines from singleton class
        properties.polylines.clear();

        // initializing button tabs
        webViewLayout = findViewById(R.id.webViewLayout);
        buttonsLayout = findViewById(R.id.buttons_layout);
        mapLayout = findViewById(R.id.mapLayout);
        videoLayout = findViewById(R.id.videoPlayerLayout);
        videoTabButton = findViewById(R.id.videoTab);
        segmentTabButton = findViewById(R.id.segmentTab);
        semanticTabButton = findViewById(R.id.semanticsTab);
        // init with video tab clicked
        showVideo(R.id.videoTab);
        highlightedPolyines = new ArrayList<>();

        // creating frames helper
        videoFrame = findViewById(R.id.videoFrame);
        framesHelper = new FramesHelper(this);
        framesButton = findViewById(R.id.framesButton);
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        list_overlay_polyline = new ArrayList<>();
        mapOfPosts = new HashMap<Integer, Integer>();
        initialize();
//      region init UI
        // speechToTextLayout = findViewById(R.id.text_speech_layout);
        btnUpload = findViewById(R.id.btnUpload);
        progress_bar_speechto_text = findViewById(R.id.progress_bar_speechto_text);
        btnSpeechToText = findViewById(R.id.btnSpeechToText);
        seekbar_video = findViewById(R.id.seekbar_video);
        imgLogout = findViewById(R.id.logout);
        //audioImage = findViewById(R.id.menu_button);
        menuLayout = findViewById(R.id.menu);
        colorList = findViewById(R.id.color_list);
        webView = findViewById(R.id.webView);
        //wordCloudButton = findViewById(R.id.toggle_button);
        // webview content
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
//        backButton = (ImageButton) findViewById(R.id.back);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SavingActivity.super.onBackPressed();
//            }
//        });

        // be default show videoview & hide wordcloud
        webView.setVisibility(View.GONE);
//        wordCloudButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                // show wordCloud & hide videoView
//                if(b){
//
//                    // show semantics
//                    showSemantics(R.id.semanticsTab);
//                    videoFrame.setVisibility(View.GONE);
//
//                }else{
//                    // show videoView & hide word cloud
//                    webView.setVisibility(View.GONE);
//                    videoFrame.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        endregion

        Bundle bundle = getIntent().getBundleExtra("bundle_values");
        String afile = bundle.getString("AUDIOFILE");
        Log.v("ng_afile", afile);
        filePath = afile;
        properties.mainAudioFileNameInSavingActivity = filePath.replace(".wav", "");
        //region frames button click listener
        framesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // moving to new activity
                Intent i = new Intent(SavingActivity.this, FrameTest.class);
                i.putExtra("videoUri", videoUri);
                i.putExtra("list", list);
                startActivity(i);
                // adding animation on activity
            }
        });
        // hiding the 2 buttons
        framesButton.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);
        //endregion

//       region visibilities
        btnUpload.setVisibility(View.GONE);
        menuLayout.setVisibility(View.GONE);
//       endregion
//        region init color list view
        mainColorTextList = new ArrayList<>();
        //mainColorTextList.add(new ColorText("#ff0000", "Text 1"));
//      initializing the adapter
        colorAdapter = new ColorAdapter(this, mainColorTextList);
//      setting the adapter
        colorList.setAdapter(colorAdapter);
//      setting click listener on color items
        colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("ng_color_click", "clicked on : "+i);
                Log.v("ng_audio_text", "text: "+mainColorTextList.get(i).getText()+ " color: "+mainColorTextList.get(i).getColor());
                //using custom dialog
                AudioChunkDialog audioChunkDialog = new AudioChunkDialog(SavingActivity.this);
                audioChunkDialog.showDialog(mainColorTextList.get(i).getText(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Call the keywords endpoint
                        // Toast.makeText(SavingActivity.this, "Not getting keywords from sentence", Toast.LENGTH_SHORT).show();
                        KeywordsHelper keywordsHelper = new KeywordsHelper(getApplicationContext());
                        keywordsHelper.getKeywordsAsync(mainColorTextList.get(i).getText(),
                                new OnKeywordsReady() {
                            @Override
                            public void onSuccess(ArrayList<String> keywords) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Log.v("ng_keywords", keywords.toString());
                                        // showToast("keywords: "+keywords.toString());
                                        KeywordsDialog keywordsDialog = new KeywordsDialog(SavingActivity.this);
                                        String content  = keywordsDialog.convertListIntoString(keywords);
                                        Log.v("ng_content", content);
                                        // do not show the dialog
                                        //keywordsDialog.showDialog(content);
                                        String videoName = getVideoNameFromVideoUri(videoUri);
                                        // create arrylist from keywords string
                                        //Log.v("ng_saving", "saving keywords: "+videoName+" chunk"+i+" content: "+content);
                                        saveKeywordsInSharedPref(videoName, "chunk"+i, content);
                                        audioChunkDialog.dismiss();
                                    }
                                });
                            }
                            @Override
                            public void onFailure() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SavingActivity.this, "Error getting Keywords !", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                });

                //showDialogWithText(mainColorTextList.get(i).getText());
                //region calling simulate map click function
                //        create a static map location for testing, this location will be shown on map on every color click
                Log.v("ng", "[Color Clicked]: "+i);
                //getting the first point of the respective polyline from properties class
                LatLng point = properties.firstCoordinatesOfPolylines.get(i);
                mapAndVideoSeekHelper = new MapAndVideoSeekHelper();
                Log.v("ngt_colorlist","point: "+point+" smallestDistance: "+smallestDistance+" list: "+list.toString());
                mapAndVideoSeekHelper.simulateMapClick(getApplicationContext(), point, smallestDistance, list, closestLocation, new OnMarkerReadyListener() {
                            @Override
                            public void onSuccess(double smallestDistance, LatLng latlng, int position) {
                                Log.v("ng", "[Simulate Map Click]: adding marker");
                                addMarkerNew(smallestDistance, latlng, position);
                            }
                            @Override
                            public void onFailure() {
                                Toast.makeText(getApplicationContext(), "Please click on path", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
//                endregion
            }
        });
//        endregion
//        btnUpload.setOnClickListener(v -> {
//        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(SavingActivity.this, imgLogout);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.signout, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(SavingActivity.this, "Sign out successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SavingActivity.this, Home.class));
                        finish();
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
        bundle = getIntent().getBundleExtra("bundle_values");
        videoUri = bundle.getString("uri");
        Log.v("ng_vuri", videoUri);
        str = bundle.getString("listLatLng");
        str1 = bundle.getString("listOthers");
        Log.d("RECEIVED_STRING", "IS:" + str1);
        JSONObject JArray;
        try {
            JArray = new JSONObject(str1);
            JSONArray jsonArray = JArray.getJSONArray("Data");
            HashMap<Integer, String> data = new HashMap<Integer, String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                if (explrObject.has("LATITUDE") && explrObject.has("LONGITUDE")) {
                    data.put(i, explrObject.getString("LATITUDE").toString() + ":" + explrObject.getString("LONGITUDE").toString());
                } else {
                    data.put(i, "0.0:0.0");
                }
            }
            properties.locdata = data;
            properties.jsonArrayLocs = jsonArray;
            //Toast.makeText(this, "str to jsom:" + jsonArray.length(), Toast.LENGTH_LONG).show();
            Log.d("RECEIVED_STRING_Array", "IS:" + jsonArray.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "data is:" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        str_aurdio_file = bundle.getString("AUDIOFILE");
        //Log.d(TAG, "onCreate: original = "+str);
        //str = "[LatLng [latitude=18.53062255, longitude=73.91222603, altitude=0.0], LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0],LatLng [latitude=18.53063313, longitude=73.91225538, altitude=0.0], LatLng [latitude=18.53065339, longitude=73.91226651, altitude=0.0]]";
        str = str.replace("[", "");
        str = str.replace("]", "");
        str = str.replace("[latlng", "");
        str = str.replace(", altitude=0.0", "");
        str = str.replace("LatLng", "");
        str = str.replace("latitude=", "");
        str = str.replace("longitude=", "");
        arrayStr = str.split(",");
        double[] lat = new double[arrayStr.length + 1];
        double[] lng = new double[arrayStr.length + 1];
        for (int i = 0; i < arrayStr.length; i++) {
            if (i % 2 == 0) {
                lat[i] = Double.parseDouble(arrayStr[i]);
            } else {
                lng[i] = Double.parseDouble(arrayStr[i]);
            }
            Log.d(TAG, "onCreate:lat  =  " + i + " = " + lat[i] + " j = " + i + " lng =  " + lng[i]);
        }
        for (int i = 0; i < arrayStr.length; i++) {
            if (lat[i] != 0 && lng[i + 1] != 0) {
                Log.d(TAG, "onCreate: lat = " + i + " " + lat[i] + " lng " + lng[i + 1]);
                list.add(new LatLng(lat[i], lng[i + 1]));
            }
        }
        populateRecorder(str1);
        btnPlay = findViewById(R.id.btnPlay);
        videoView = findViewById(R.id.videoView);
        seekbar_video.setVisibility(View.GONE);
        videoView.setVideoURI(Uri.parse(videoUri));
        //Audio_Converter();
//      region play button click listener
        btnPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mapSecodsWithCordiates(list.size(), videoView.getDuration());
            seekbar_video.setVisibility(View.VISIBLE);
            Log.d("TOTAL", "DATAPOINTS" + list.size());

            if (isChecked) {
                mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 100);
                videoView.start();
                isVideoIsPlaying = true;
                isVideoCompleted = false;
                if (list != null)
                    marker_anim(getVideoTime());
            } else {
                videoView.pause();
                pauseTime = videoView.getCurrentPosition();
                Log.d(TAG, "onCreate: " + videoView.getCurrentPosition());
                isVideoIsPlaying = false;
                isVideoCompleted = false;
                // count = count - 1;
                // time_to_speed = 2 * time_to_speed;

                if (list != null)
                    marker_anim(getVideoTime());
                // marker_anim(pauseTime);
            }
        });
//        endregion
        videoView.setOnCompletionListener(mp -> {
            isVideoCompleted = true;
            btnPlay.setChecked(false);
            count = 0;
            marker_anim(getVideoTime());
            marker_start_point.setPosition(point1);
        });
        //videoFile = new File(videoUri);
        Mapbox.getInstance(this, "pk.eyJ1IjoiZGVlcHNoaWtoYTc3NyIsImEiOiJjamk2cno3dmEwNDBxM3JwcDFlb2ZtNTMzIn0.jVGIfJplTqKXFg6SROl_9g");
        // setContentView(R.layout.activity_home);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        if (list != null && list.size()!=0) {
            int n = list.size() - 1;
            point1 = list.get(0);
            point2 = list.get(n);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oops! cannot view the video ");
            builder.setMessage("While recording this we were not able to get your location updates ");
            builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            Toast.makeText(this, "Your Location was not turned on while recording cannot show anything", Toast.LENGTH_LONG).show();
        }
//region seekbar dragging functionality
        seekbar_video.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int mProgressAtStartTracking;
            private final int SENSITIVITY = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //marker_anim(getVideoTime());
                // handle progress change
                /*try {
                    String idx = seekBar.getProgress()+"";
                    int loc =0;
                    if(Integer.parseInt(idx)>999)
                    {
                        loc = Integer.parseInt(idx)/1000;
                    }else
                    {
                        loc = Integer.parseInt(idx)/100;
                    }
                    LatLng lt= new LatLng();
                    lt.setLatitude(Double.parseDouble(properties.locdata.get(loc).toString().split(":")[0]));
                    lt.setLongitude(Double.parseDouble(properties.locdata.get(loc).toString().split(":")[1]));
                    addMarkerLoc(lt);
                    Log.d("SEEKBAR_MARKERS", ""+loc);



                }catch (Exception ex)
                {
                    Toast.makeText(SavingActivity.this, ""+ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }*/

//                last_seekbarvalue=seekBar.getProgress();
//                if(mapOfPosts.containsKey(seekBar.getProgress()))
//                {
//        Log.d("ALREADY","found"+seekBar.getProgress()+"COUNT"+mapOfPosts.get(seekBar.getProgress()));
//                }
//                else
//                {
//                    mapOfPosts.put(seekBar.getProgress(), count);
//                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgressAtStartTracking = seekBar.getProgress();
                Log.d("SEEKBAR", "START_TRACKING");
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("SEEKBAR", "STOP_TRACKING");
                if (mapOfPosts.size() > 0) {
                    videoView.seekTo(seekBar.getProgress());
                    videoView.pause();
                    isVideoIsPlaying = false;
                    isVideoCompleted = false;
                    btnPlay.setChecked(false);
                    Log.d("PROGRESS", "AT :" + seekBar.getProgress());
                    if (list != null) {
                        marker_anim(getVideoTime());
                    }
//                  NOTE:  updating the marker in map
                    UpdateMarker(seekBar.getProgress());
                    Toast.makeText(SavingActivity.this, "" + mapOfPosts.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "first play video", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        endregion
        TestCordiate();
// region text2speech btn click
        btnSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        hiding the speech to text button layout
                //wordCloudButton.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.GONE);
                //btnSpeechToText.setVisibility(View.GONE);
                startSpeechToTextProcess();
            }
        });
//        endregion
        Readlatlng();
//        intentionally removed
//region creating map markers
        IconFactory iconFactory = IconFactory.getInstance(SavingActivity.this);
        icon_strt = iconFactory.fromResource(R.drawable.marker_red);
        icon_playing = iconFactory.fromResource(R.drawable.marker_moveable);
        icon_pause = iconFactory.fromResource(R.drawable.marker_red);
    }
//    endregion

    public String GetText(String files) {
        try {
            // Create data variable for sent values to server
            //String data = URLEncoder.encode("files", "UTF-8")+ "=" + URLEncoder.encode(files, "UTF-8");
            String text = "";
            BufferedReader reader=null;
            // Send data
            try
            {
                Log.v("ng_gt", "getting text: "+Config.SPEECH_TO_TEXT_URL+files.substring(0,files.length()-4));
                // Defined URL  where to send data
                URL url = new URL(Config.SPEECH_TO_TEXT_URL+files.substring(0,files.length()-4));
                // Send POST data request
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                //wr.write( data );
                //wr.flush();
                // Get the server response
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                }
                text = sb.toString();
            }
            catch(Exception ex)
            {
                Log.d("STSEXP:","e",ex);
            }
            finally {
                try {
                    reader.close();
                }
                catch(Exception ex) {}
            }
            // Show response on activity
            Log.v("ng_text", "text: "+text);
            // ":first time:@chunk_1575640065466_2.wav:chunk_1575640065466_1.wav:"
            return text;
        }catch(Exception ex) {
            return  "exp:"+ex.getMessage();
        }
    }

    //region saving to file
    public static boolean saveToFile(String data,String fileName){
        try {
            String path=Environment.getExternalStorageDirectory() + "/RouteApp";
            new File(path).mkdir();
            File file = new File(path+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            return true;
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return  false;
    }
//endregion

    //    region showing alert dialog
    public  void ShowAlertDialogList()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SavingActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        alertDialog.setCancelable(false);
        // create view for add item in dialog
        View convertView = (View) inflater.inflate(R.layout.dialogalertlist, null);
        // on dialog cancel button listner
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        // add custom view in dialog
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.dialoglist);
        final AlertDialog alert = alertDialog.create();
        alert.setTitle("Audio Text"); // Title
        alert.setCancelable(false);
        MyAdapter myadapter = new MyAdapter(SavingActivity.this, R.layout.activity_listrow, listChumktext);
        lv.setAdapter(myadapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                showAlert(listChumktext.get(position),"Chunk text");
                //Toast.makeText(SavingActivity.this, "" + listChumktext.get(position), Toast.LENGTH_SHORT).show();
                alert.cancel();
            }
        });
        alert.show();
    }


    private class ViewHolder {
        TextView colorrow;
        TextView chumkrow;
    }


    class MyAdapter extends ArrayAdapter<String>{
        LayoutInflater inflater;
        Context myContext;
        List<String> newList;
        public MyAdapter(Context context, int resource, List<String> list) {
            super(context, resource, list);
            // TODO Auto-generated constructor stub
            myContext = context;
            newList = list;
            inflater = LayoutInflater.from(context);
        }
        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.activity_listrow, null);
                holder.chumkrow = (TextView) view.findViewById(R.id.rowchumk);
                holder.colorrow= (TextView) view.findViewById(R.id.rowcolor);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            Log.d("ColorChoiceAdap:",properties.colorsdata.get(position)+"");
            holder.colorrow.setBackgroundColor(Color.parseColor(properties.colorsdata.get(position)));
            String[] colorNames = getResources().getStringArray(R.array.colorNames);
            Log.d("GetValue1", ""+properties.colorsdata.get(position));
            holder.chumkrow.setText("chunk no: "+(position+1)+"");
            Log.d("GetValue2",""+properties.colorstr.get(properties.colorsdata.get(position)));
            return view;
        }
    }
    //    region showing dialog for actual text string
    private void showAlert(String message,String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setCancelable(false);
        String html = "<html><head></head><body><p>"+message+"<p></body></html>";

        WebView wv = new WebView(this);
        wv.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Audio Chunk Text")
                .setCancelable(false)
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/
    }
    //   endregion
//    region long async operation
    private class LongOperation extends AsyncTask<String, Void, String> {

        ProgressDialog pdialog;
        @Override
        protected String doInBackground(String... params) {
            return GetText(filePath);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("ng_pe", result);
            // TODO: start here
            AudioSyncHelper audioSyncHelper = new AudioSyncHelper(SavingActivity.this, getVideoNameFromVideoUri(videoUri));
            // saving result in shared pref
            audioSyncHelper.saveResultInSharedPref(result);
            pdialog.dismiss();
            Toast.makeText(SavingActivity.this, ""+result, Toast.LENGTH_SHORT).show();
            Log.d("UPLOADERROR:",result);
            saveToFile(result,filePath);
            dd = new DBHelper(SavingActivity.this);
            if(result.contains("@")) {
                dd.insertSpeechData(filePath,result);
                try {
                    String[] separatedata = result.split("@");
                    listChumktext = new ArrayList<String>();
                    listChumktime = new ArrayList<String>();
                    separatedata[0]=separatedata[0].toString().substring(0,separatedata[0].toString().length()-1);
                    separatedata[1]=separatedata[1].toString().substring(1,separatedata[1].toString().length()-1);
                    String[] textdata=separatedata[0].split(":");
                    Log.v("ng_text", textdata.toString());
                    String[] chumkdata=separatedata[1].split(":");
                    if(textdata.length>0 && chumkdata.length>0)
                    {
                        for(int i =0;i<textdata.length;i++)
                        {
                            String temp = chumkdata[i];
                            String[] tempdata=temp.split("_");
                            String chumk = tempdata[1]+"_chunk_"+tempdata[4];
                            //String chumk=tempdata[1]+"_"+tempdata[2]+"-to-"+tempdata[3]+"_chunk no:"+tempdata[4];
                            Log.v("ng_chumkli",  i+" : "+chumk);
                            listChumktext.add(textdata[i]);
                            listChumktime.add(chumk);
                        }
                    }
//                    don't need it
                    //ShowAlertDialogList();
                    showColorList(null, null);
                    //ShowAlertDialogWithListview();
                }catch (Exception ex) {
                    Log.v("STSEXP:","after text",ex);
                }
            }else
            {
                Toast.makeText(SavingActivity.this, ""+result, Toast.LENGTH_LONG).show();
            }

            //showAlert(result+",tc:"+listChumktime.size()+",cc:"+listChumktext.size());
        }

        @Override
        protected void onPreExecute() {
            pdialog = new ProgressDialog(SavingActivity.this);
            pdialog.setTitle("Speech To Text");
            pdialog.setCancelable(false);
            pdialog.setMessage("Please wait..");
            pdialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
//    endregion

    // region uploading chunks
    private void chumkUpload(final String imagePath,final int sizes) {
        Log.v("ng_chum", "inside chunk upload function");
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Config.FILE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");
                            Log.v("ng_msg", message);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            ct++;
                            if(ct == sizes)
                            {
                                new LongOperation().execute();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            Log.d("UPLOADERROR:","e",e);
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("verr", error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        smr.addFile("image", imagePath);
        RequestQueue queue = Volley.newRequestQueue(SavingActivity.this);
        queue.add(smr);
        // this is not working
        //MyAppl.getInstance().addToRequestQueue(smr);
    }
//    endregion

    private long getVideoTime() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, Uri.parse(videoUri));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        Log.d(TAG, "getVideoTime: " + timeInMillisec);
        retriever.release();
        return timeInMillisec;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(SavingActivity.this, Home.class));
        finish();
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;

        if (list != null) {
            properties.colorsdata = new ArrayList<String>();
            properties.colorstr =  new HashMap<String, String>();
            String filter=filePath.substring(0,filePath.length()-4);
            ArrayList<String> chumkfiles = FileUtils.getFileNames(Environment.getExternalStorageDirectory() + "/RouteApp", "chunk_" + filter, 1);
            ctotal = chumkfiles.size();
            for (int i = 0; i < chumkfiles.size(); i++) {
                createColors();
            }
            enableLocation();
        }
        // map.setOnMyLocationChangeListener(this);
        intialMarkerClick();
    }

    private void enableLocation() {
        initializeLocationEngine();
        //initializeLocationLayer();
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationEngine() {

        addMarker(point1);
        //start marker
        intialMarker(point1);
        addMarkerEndPoint(point2);
        Location point1_location = new Location("Start");
        point1_location.setLatitude(point1.getLatitude());
        point1_location.setLongitude(point1.getLongitude());
        setCameraPosition(point1_location);
        draw_ployline(list);
        //createColors();
        AddNewPollyLine();
    }

    private void setCameraPosition(Location location) {

        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .build(); // Creates a CameraPosition from the builder

        map.animateCamera(CameraUpdateFactory.newCameraPosition(position));

    }


    private void addMarkerLoc(LatLng latLng) {
        IconFactory iconFactory = IconFactory.getInstance(SavingActivity.this);
        //  Drawable iconDrawable = ContextCompat.getDrawable(SavingActivity.this, R.drawable.marker_red);
        Icon icon = null;
        icon = iconFactory.fromResource(R.drawable.marker_moveable);

        marker_start_point = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon)
                .snippet(latLng + "")
                .title("moving"));
    }

    private void addMarker(LatLng latLng) {
        Log.v("ng_add" , "adding marker on: "+latLng.toString());
        IconFactory iconFactory = IconFactory.getInstance(SavingActivity.this);
        //  Drawable iconDrawable = ContextCompat.getDrawable(SavingActivity.this, R.drawable.marker_red);
        Icon icon = null;
        if (is_pollyline_tounched) {
            icon = iconFactory.fromResource(R.drawable.marker_moveable);
        } else {
            icon = iconFactory.fromResource(R.drawable.marker_blue);
        }
        marker_start_point = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon)
                .snippet(latLng + "")
                .title("Start point"));

    }

    private void addMarkerEndPoint(LatLng latLng) {
        IconFactory iconFactory = IconFactory.getInstance(SavingActivity.this);
        //  Drawable iconDrawable = ContextCompat.getDrawable(SavingActivity.this, R.drawable.marker_red);
        Icon icon = iconFactory.fromResource(R.drawable.marker_red);

        marker_end_point = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon)
                .snippet(latLng + "")
                .title("End point"));
    }

    //    region draw main polyline
    private void draw_ployline(List<LatLng> latLngList) {
        mpolines = map.addPolyline(new PolylineOptions()
                .width(20f)
                .color(Color.GREEN)
                .alpha(1f)
                .addAll(latLngList));
    }

//    endregion

    @SuppressLint("NewApi")
    private void marker_anim(long time) {
        Log.d("GET_SPEED", "time: " + time);

        if (isVideoCompleted) {
            marker_start_point.setPosition(point1);
            marker_start_point.setIcon(icon_strt);
        }
        if (isVideoIsPlaying) {
            //double distance = marker_start_point.getPosition().distanceTo(list.get(count));
            marker_start_point.setIcon(icon_playing);
            Log.d("GET_SPEED", "run: " + time / (list.size() - 1));
            time_to_speed = time / (list.size());
            //time_to_speed = rem_time + time_to_speed;
            handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if ((list.size()) > count) {
                        Log.d(TAG, "run: is running");
                        markerAnimator = ObjectAnimator.ofObject(marker_start_point, "position",
                                new LatLngEvaluator(), marker_start_point.getPosition(), list.get(count));
                        markerAnimator.setDuration(time_to_speed);
                        markerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        markerAnimator.start();

                        d = d + time_to_speed;
                        Log.e(TAG, "run: d = :" + d + " count: " + count);
                        count++;
                        handler.postDelayed(this, time_to_speed);
                        map.animateCamera(CameraUpdateFactory.newLatLng(marker_start_point.getPosition()));
                    } else {
                        Log.d(TAG, "run: stopped");
                    }
                }
            };
            handler.post(runnable);
        } else {
            //Toast.makeText(this, "Video is Paused = " + count, Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, "List size = "+list.size(), Toast.LENGTH_SHORT).show();
            String datalats = "";
            for (int i = 0; i < list.size(); i++) {
                datalats += "," + list.get(i);
            }

            Toast.makeText(this, "" + mapOfPosts.get(count), Toast.LENGTH_SHORT).show();
            if ((list.size() > 1)) {
                ///count = count - 1;
                markerAnimator.pause();
                handler.removeCallbacksAndMessages(null);

                marker_start_point.setIcon(icon_pause);
                //addMarkerLoc(list.get(seekbar_video.getProgress()));

                // rem_time = rem_time + (time_to_speed + rem_time);
            }
        }


    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.getLatitude() + "," + origin.getLongitude();
        // Destination of route
        String str_dest = "destination=" + dest.getLatitude() + "," + dest.getLongitude();
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyDt_Ha3B1LYABIhv4Qg-C3O_d__BAqnOFg";
        Log.d("URL_DATA", url);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
        Log.d("PolyAdded", currentPolyline.toString());
    }

    private static class LatLngEvaluator implements TypeEvaluator<LatLng> {
        // Method is used to interpolate the marker animation.

        private LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    }

    public Handler mSeekbarUpdateHandler = new Handler();
    public Runnable mUpdateSeekbar = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            int totatmili = 0, rem = 0;
            seekbar_video.setMax(videoView.getDuration());
            seekbar_video.setProgress(videoView.getCurrentPosition());
            //  sharedclass.lastplayedduration=videoView.getCurrentPosition();
            totatmili = videoView.getDuration();
            rem = totatmili - videoView.getCurrentPosition();
            //  }
            long minutes = TimeUnit.MILLISECONDS.toMinutes(rem);
            int minutmili = (((int) minutes) * 60000);
            int durat = rem - minutmili;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(durat);
            if (seconds < 10) {
//                txtremainingduration.setText((String.valueOf( minutes+":0"+seconds)));
            } else {
//                txtremainingduration.setText((String.valueOf(minutes + ":" + seconds)));
            }
            mSeekbarUpdateHandler.postDelayed(this, 50);


        }

    };

    public void UpdateMarkerLoc(int n_progressbar) {
        Log.d("CURRENT", "POINT : " + count);
        Log.d("TOTAL_VIDEO", "DURATION : " + videoView.getDuration());
        Log.d("SEEKTO", "DURATION : " + n_progressbar);
        int remaining_time = videoView.getDuration() - n_progressbar;
        Log.d("REMAINING_TIME", "IS : " + remaining_time);
        Log.d("HashMap", "Size : " + mapOfPosts.size());
        int speed = 1000;
//     marker_anim(remaining_time);
        try {
            if (mapOfPosts.size() > 0) {
                Log.d("SEEKER_SECOND", "IS :" + n_progressbar / 1000);
            }
        } catch (Exception ex) {
            Log.d("Exception_marker", "is:" + ex.getMessage());
        }

    }

    public void UpdateMarker(int n_progressbar) {
        Log.d("CURRENT", "POINT : " + count);
        Log.d("TOTAL_VIDEO", "DURATION : " + videoView.getDuration());
        Log.d("SEEKTO", "DURATION : " + n_progressbar);
        int remaining_time = videoView.getDuration() - n_progressbar;
        Log.d("REMAINING_TIME", "IS : " + remaining_time);
        Log.d("HashMap", "Size : " + mapOfPosts.size());
        int speed = 1000;
//     marker_anim(remaining_time);
        try {
            if (mapOfPosts.size() > 0) {

                Log.d("SEEKER_SECOND", "IS :" + n_progressbar / 1000);
                int found_value = mapOfPosts.get(n_progressbar / 1000);
                Log.d("Found", "Value" + found_value);
                markerAnimator = ObjectAnimator.ofObject(marker_start_point, "position",
                        new LatLngEvaluator(), list.get(count), list.get(found_value));
                markerAnimator.setDuration(time_to_speed);
                markerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                markerAnimator.start();
                count = found_value;
            } else {

            }
        } catch (Exception ex) {
            Log.d("Exception_marker", "is:" + ex.getMessage());
        }
    }

    public void mapSecodsWithCordiates(int coordinates, int duration) {
        Log.d("TIMES", "IS :" + duration);
        Log.d("COORDIATES", "IS :" + coordinates);
        double factor = (double) coordinates / (duration / 1000);
        Log.d("FACTOR", "IS :" + factor);
        for (int i = 0; i < duration / 1000; i++) {
            Log.d("KEY_PAIR", "i:" + i + "IS : " + Math.round(i * factor));
            mapOfPosts.put(i, Integer.parseInt(Math.round(i * factor) + ""));
        }
    }

    public void TestCordiate() {
        for (int i = 0; i < list.size(); i++) {
            Log.d("MAIN_CORDIATES", "ARE : " + list.get(i) + "index" + i);
        }
    }

    public void normalizeFile() {

        //While data come from microphone.

        Log.d("RECIVED_AUDIO", "IS :" + str_aurdio_file);
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/RouteApp");
        File file = new File(directory, str_aurdio_file);
        File folder = new File(Environment.getExternalStorageDirectory() + "/RouteApp");
        if (!folder.exists()) {
            folder.mkdir();
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final long currentTimeMillis1 = System.currentTimeMillis();
        File folder1 = new File(Environment.getExternalStorageDirectory() + "/RouteApp");
        if (!folder1.exists()) {
            folder1.mkdir();
        }
        final String audio_file_path = currentTimeMillis + ".mp3";
        final String normalize_file_path = currentTimeMillis1 + ".mp3";
        File outfile = new File(folder, audio_file_path);
        File normailzefile = new File(folder, normalize_file_path);
        String nomralize_command = "-y -i " + file + " -af apad,atrim=0:3,loudnorm=I=-16:TP=-1.5:LRA=11:measured_I=-23.54:measured_TP=-7.96:measured_LRA=0.00:measured_thresh=-34.17:offset=7.09:linear=true:print_format=summary -ar 16k " + normailzefile;
        String[] cmd = nomralize_command.split(" ");
        progress_bar_speechto_text.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            public void run() {

                //getText(file.getAbsolutePath());
            }
        }).start();

        new CountDownTimer(10000, 1000) {
            public void onFinish() {
                // When timer is finished
                // Execute your code here
                progress_bar_speechto_text.setVisibility(View.GONE);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();
//        speechToText(file,outfile);
//        try {
//            fFmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onStart() {
//                    super.onStart();
//                    //  Log.d("FFMpeg", "onStart: ");
//                    progress.setMessage("Nomalizing please wait...");
//                    progress.show();
//
//                }
//
//                @Override
//                public void onProgress(String message) {
////                    progress.setMessage(message);
//                    //Log.d("FFMpeg", message);
//                    Log.d("Nomalizing_OnProgress","IS :"+message);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    // Log.d("FFMpeg",message);
//                    Log.d("Nomalizing__onFailure","IS :"+message);
//                    progress.dismiss();
//                }
//
//                @Override
//                public void onSuccess(String message) {
//                    Log.d("Nomalizing_Success","IS :"+message);
//                    speechToText(normailzefile,outfile);
//
//                }
//
//                @Override
//                public void onFinish() {
//
//                    }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // Log.e("FFMpeg", "convertToAudio: " , e);
//            e.printStackTrace();
//        }

    }

    public void initialize() {
        fFmpeg = FFmpeg.getInstance(this);
        try {
            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Log.d("FFMPEG", "onFailure: ");
                }

                @Override
                public void onSuccess() {
                    Log.d("FFMPEG", "onSuccess: ");
                }

                @Override
                public void onStart() {
                    Log.d("FFMPEG", "onStart: ");
                }

                @Override
                public void onFinish() {
                    Log.d("FFMPEG", "onFinish: ");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }
    //    region unused code
    public void createSpeechToText(String body) {
        final long currentTimeMillis = System.currentTimeMillis();
        File folder1 = new File(Environment.getExternalStorageDirectory() + "/RouteApp");
        if (!folder1.exists()) {
            folder1.mkdir();
        }
        File gpxfile = new File(folder1, currentTimeMillis + ".txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gpxfile);

            writer.append(body);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            Log.d("Writing", "Exception" + ex.getMessage());
        }
    }
//    endregion

    /*
        public void getText(String file_path) {

            try {
                InputStream stream = getResources().openRawResource(R.raw.credentials);
                SpeechSettings settings =
                        SpeechSettings.newBuilder().setCredentialsProvider(
                                new CredentialsProvider() {
                                    @Override
                                    public Credentials getCredentials() throws IOException {
                                        return GoogleCredentials.fromStream(stream);
                                    }
                                }
                        ).build();
                SpeechClient speech = com.google.cloud.speech.v1p1beta1.SpeechClient.create(settings);
                // The path to the audio file to transcribe
                String fileName = file_path;
                Log.d("FILE_Name", "IS :" + fileName);
                // Reads the audio file into memory
                Path path = Paths.get(fileName);
                byte[] data = Files.readAllBytes(path);
                ByteString audioBytes = ByteString.copyFrom(data);

                // Builds the sync recognize request
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(16000)
                        .setLanguageCode("en-US")
                        .build();
                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(audioBytes)
                        .build();

                // Performs speech recognition on the audio file
                RecognizeResponse response = speech.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                for (SpeechRecognitionResult result : results) {
                    // There can be several alternative transcripts for a given chunk of speech. Just use the
                    // first (most likely) one here.
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    Log.d("Transcription:", alternative.getTranscript());
                    createSpeechToText(String.valueOf(alternative));
                }
                speech.close();
            } catch (Exception ex) {

                Log.d("TRANSLATING", "EXCEPTION :" + ex.getLocalizedMessage());

            }
            //For more, refer this link
        }
    */

    public void intialMarkerClick() {

 /*       map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                Log.d("Clicked", "latitude" + point.getLatitude());
                Log.d("Clicked", "longitude" + point.getLongitude());
                Location closetlocation = new Location("closet");
                LatLng latLng = null;
                int position = 0;
                Location startPoint = new
                float[] results = new float[1];
                for (int i = 0; i < list.size(); i++) {
 Location("locationStart");
                    startPoint.setLatitude(point.getLatitude());
                    startPoint.setLongitude(point.getLongitude());

                    Location endPoint = new Location("locationLast");
                    endPoint.setLatitude(list.get(i).getLatitude());
                    endPoint.setLongitude(list.get(i).getLongitude());
                    double distance = startPoint.distanceTo(endPoint);
                    Log.d("DISTANCE", "IS" + distance);
                    if(smallestDistance == 100 || distance < smallestDistance){
                        closetlocation.setLatitude(list.get(i).getLatitude());
                        closetlocation.setLongitude(list.get(i).getLongitude());
                        closestLocation = closetlocation;
                        smallestDistance = distance;
                        position=i;
                        latLng=new LatLng(list.get(i).getLatitude(),list.get(i).getLongitude());
                    }
                }
//
                if(latLng!=null) {
                    addMarkerNew(smallestDistance, latLng, position);
                }
                else
                {
                Toast.makeText(getApplicationContext(),"Please click on path",Toast.LENGTH_SHORT).show();
                }

            }
        });
*/
//      region map click listener
        map.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                Log.d("Clicked", "latitude" + point.getLatitude());
                Log.d("Clicked", "longitude" + point.getLongitude());
                Location closetlocation = new Location("closet");
                LatLng latLng = null;
                float[] results = new float[1];
                smallestDistance = 50;
                int position = 0;
                for (int i = 0; i < list.size(); i++) {
                    Location startPoint = new Location("locationA");
                    startPoint.setLatitude(point.getLatitude());
                    startPoint.setLongitude(point.getLongitude());
                    Location endPoint = new Location("locationA");
                    endPoint.setLatitude(list.get(i).getLatitude());
                    endPoint.setLongitude(list.get(i).getLongitude());
                    double distance = startPoint.distanceTo(endPoint);
                    Log.d("DISTANCE", "IS" + distance);
                    if (smallestDistance == 50 || distance < smallestDistance) {
                        closetlocation.setLatitude(list.get(i).getLatitude());
                        closetlocation.setLongitude(list.get(i).getLongitude());
                        closestLocation = closetlocation;
                        smallestDistance = distance;
                        position = i;
                        latLng = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
                    }
                }
                if (latLng != null) {
                    addMarkerNew(smallestDistance, latLng, position);
                } else {
                    Toast.makeText(getApplicationContext(), "Please click on path", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        endregion

//    mapView.getMap().setOnMapClickListener(new MapboxMap.OnMapClickListener() {
//
//        @Override
//        public void onMapClick(LatLng latlng) {
//            // TODO Auto-generated method stub
//
//            if (marker_start_point != null) {
//                marker_start_point.remove();
//            }
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(latlng)
//                    .icon(BitmapDescriptorFactory
//                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//            System.out.println(latlng);
//
//        }
//    });
    }

    public void Readlatlng() {
        for (int i = 0; i < list.size(); i++) {
            Log.d("LATITUDE", "IS :" + list.get(i).getLatitude());
            Log.d("Longitude", "IS :" + list.get(i).getLongitude());
        }
    }

//    public void showAccuracyDialogue() {
//        new AlertDialog.Builder(this, R.style.MyDialogTheme)
//                .setTitle("Alert")
//                .setMessage("For Better Accuracy please Zoom map then move marker")
//                .setPositiveButton("OK", (dialog, which) -> {
//                    dialog.dismiss();
//                })
//                .setCancelable(false)
//                .show();
//    }

    public void populateRecorder(String recorder_Str) {
        try {
            JSONObject jsnobject = new JSONObject(recorder_Str);
            JSONArray jsonArray = jsnobject.getJSONArray("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                if (c.has("LATITUDE")) {
                    Log.d("Latitude", "IS" + c.getString("LATITUDE"));
                    Log.d("Longitude", "IS" + c.getString("LONGITUDE"));
                    Log.d("Time", "IS" + c.getString("TIME"));
                    Recorder recorder = new Recorder(c.getString("LATITUDE"),
                            c.getString("LONGITUDE"),
                            c.getString("TIME"), c.getString("AUDIO"));
                    list1.add(recorder);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * adds marker on the map
     * @param distance
     * @param point
     * @param i
     */
    public void addMarkerNew(double distance, LatLng point, int i) {
        Log.v("ng", "[addMarkerNew]: adding new marker on map");
        if (distance < 50) {
            Log.v("ng_checking", point.toString()+ " "+distance+ " "+i);
            // If distance is less than 50 meters, this is your polyline
            marker_start_point.remove();
            is_pollyline_tounched = true;
            addMarker(point);
            for (int j = 0; j < list1.size(); j++) {
                Log.d("Compare_lat", "is:" + list1.get(j).getLat());
                Log.d("Compare_lat1", "is:" + list.get(i).getLatitude());
                if (list1.get(j).getLat().trim().equals(String.valueOf(list.get(i).getLatitude()).trim())) {
                    String time = list1.get(j).getTime().replaceAll(" ", "");

                    Log.d("Time", "IS" + time);
                    String[] separated = time.split(":");
                    int hours = Integer.parseInt(separated[0]) * 3600000;
                    int minutes = Integer.parseInt(separated[1]) * 60000;
                    int seconds = Integer.parseInt(separated[2]) * 1000;
                    int total_time = hours + minutes + seconds;
                    Log.d("MIlliseconds", "are" + total_time);
                    videoView.seekTo(total_time);
                    Log.d("MARKER_PROGRESS", "IS :" + seekbar_video.getProgress());
                    count = i;
                    Log.d("PREVIOS_SECONDS", "IS:" + previous_second);
                    Log.d("CURRENT_SECONDS", "IS:" + seconds);

                    if (previous_second + 1 == seconds) {
                        list_overlay_polyline.add(new LatLng(Double.parseDouble(list1.get(j).getLat()), Double.parseDouble(list1.get(j).getLat())));

                    } else {
                        if (list_overlay_polyline.size() > 0) {
                            //addOverLayPlouline(list_overlay_polyline);
                        }
                    }
                    previous_second++;
                    //
                    return;
                }
            }
        }
    }

    public void intialMarker(LatLng latLng) {
        IconFactory iconFactory = IconFactory.getInstance(SavingActivity.this);
        //  Drawable iconDrawable = ContextCompat.getDrawable(SavingActivity.this, R.drawable.marker_red);
        Icon icon = null;
        icon = iconFactory.fromResource(R.drawable.marker_blue);
        intial_marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(icon)
                .snippet(latLng + "")
                .title("Start point"));
    }

    public void addOverLayPloulineold(List<LatLng> latLngList) {
        PolylineOptions lineOptions = new PolylineOptions();
        map.addPolyline(lineOptions
                .width(10f)
                .color(Color.RED)
                .alpha(1f)
                .addAll(latLngList));
        Log.d("MapPoly", "added");
        //list_overlay_polyline.clear();
    }

    /**
     * get random color strings
     * @return color string
     */
    protected String getColorString() {
        String SALTCHARS = "ABCDEF1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() != 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        Log.d("ng", "[Create Color String]: #"+saltStr);
        return "#"+saltStr;
    }

    //    region creating colors to be used in map
    public void createColors()
    {
        boolean flag = true;
        String cdata="#000000";
        while(flag!=false)
        {
            cdata=getColorString();
            if(!properties.colorstr.containsKey(cdata)) {
                flag = false;
            }
        }
        properties.colorstr.put(cdata,cdata);
        properties.colorsdata.add(cdata);
        Log.d("ng", "[Create Colors] adding color: "+cdata);
        Log.v("ng", "Color String Hashmap: "+properties.colorstr.toString());
        Log.v("ng", "ColorsData ArrayList: "+properties.colorsdata.toString());
    }
    //    endregion
//    region adding polylines on map

    /**
     * this fxn gets no of polylines to be added on map
     * @param latLngList latlng list to be drawn on map
     * @param ct no of color
     */
    public void addOverLayPlouline(List<LatLng> latLngList, int ct, boolean first) {
        // get the first coordinate from latlng list, add it to a list on properties class
        if(!first){
            // if it is not the first time that means it is being drawn because of word cloud click
            //doing it coz, ct starts from 1
            PolylineOptions lineOptions = new PolylineOptions();
            Polyline polylineAdded = map.addPolyline(lineOptions
                    .width(20f)
                    //.color(Color.parseColor(properties.colorsdata.get(ct)))
                    .color(Color.BLACK)
                    .addAll(latLngList));
            //TODO: why was it here
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    polylineAdded.remove();
//                }
//            }, 5000);
            Log.v("ng_added", polylineAdded.toString());
        }else{
            properties.firstCoordinatesOfPolylines.add(latLngList.get(0));
            // clearing the old polylines
            // properties.polylines.clear();
            properties.polylines.add(latLngList);
            PolylineOptions lineOptions = new PolylineOptions();
            Polyline polylineAdded = map.addPolyline(lineOptions
                    .width(10f)
                    .color(Color.parseColor(properties.colorsdata.get(ct)))
                    .addAll(latLngList));
            Log.v("ng_added", polylineAdded.toString());
            // trying to remove polyline
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    polylineAdded.remove();
//                }
//            }, 5000);
        }
    }
//   endregion

    //    region adding audio ploylines on map
    public void AddNewPollyLine() {
        List<LatLng> listaudio = new ArrayList<>();
        String prevaudio = "0";
        int v=0;
        for (int j = 0; j < list1.size(); j++) {
            String time = list1.get(j).getTime().replaceAll(" ", "");
            String audio = list1.get(j).getAudio().replaceAll(" ", "");
            Log.d("Time", "IS" + time);
            String[] separated = time.split(":");
            int hours = Integer.parseInt(separated[0]);
            int minutes = Integer.parseInt(separated[1]);
            int seconds = Integer.parseInt(separated[2]);
            int total_time = hours + minutes + seconds;
            Log.d("PREVIOS_SECONDS", "IS:" + previous_second);
            Log.d("AUDIO_CHECK", prevaudio + ":" + audio);
            if (prevaudio.equals("1") && audio.equals("1")) {
                listaudio.add(new LatLng(Double.parseDouble(list1.get(j).getLat()), Double.parseDouble(list1.get(j).getLng())));
            } else if (prevaudio.equals("0") && audio.equals("1")) {
                listaudio = new ArrayList<>();
                listaudio.add(new LatLng(Double.parseDouble(list1.get(j).getLat()), Double.parseDouble(list1.get(j).getLng())));
                prevaudio = "1";
            } else if (prevaudio.equals("1") && audio.equals("0")) {
                prevaudio = "0";
                lists_pollline.add(listaudio);
            } else if (prevaudio.equals("0") && audio.equals("0")) {
            }
            previous_second = seconds;
        }
        Log.d("Pollline_list", "is" + lists_pollline.size());
        int j= 0;
        for (int k = 0; k < lists_pollline.size(); k++) {
            Log.d("ListLatitude", "size" + k);
            Log.d("ListLatitude", "size" + lists_pollline.get(k));
            addOverLayPlouline(lists_pollline.get(k),j, true);
            if(j==ctotal-1) {
                j=0;
            }else {
                j=j+1;
            }
        }
    }
//    endregion

    //        region color list functionality
    /**
     * get the list of colors
     *
     */
    public void showColorList(ArrayList<String> colors, ArrayList<String> texts){
//    make  a list of color text objects
//        get list of texts
        colorStrList = properties.colorsdata;
        ArrayList<String> textList = listChumktext;
        Log.v("ng_scl", colorStrList.toString()+ " - "+textList.toString());
        for (int i=0; i<textList.size(); i++){
            String color = colorStrList.get(i);
            String text = textList.get(i);
            ColorText colorTextObj = new ColorText(color, text);
            mainColorTextList.add(colorTextObj);
            Log.v("ng_cl", mainColorTextList.toString());
        }
        colorAdapter.notifyDataSetChanged();
//      showing the menu layout
        menuLayout.setVisibility(View.VISIBLE);
    }

    //        make adapter for color list
    //    region custom adapter for color list
    class ColorAdapter extends BaseAdapter  {
        Context context;
        ArrayList<ColorText> colorTextList;
        public ColorAdapter(Context context, ArrayList<ColorText> colorTextList){
            this.context = context;
            this.colorTextList = colorTextList;
        }

        @Override
        public int getCount() {
            return colorTextList.size();
        }

        @Override
        public Object getItem(int i) {
            return colorTextList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view =  getLayoutInflater().inflate(R.layout.color_list_item, null);
            View colorView = view.findViewById(R.id.color);
            colorView.setBackgroundColor(Color.parseColor(colorTextList.get(i).getColor()));
            return view;
        }

    }
//    endregion


    /**
     * show toast for quick view of data
     * @param msg message to eb shown in toats
     */
    public void showToast(String msg){
        Toast.makeText(SavingActivity.this,msg, Toast.LENGTH_LONG ).show();
    }

    /**
     * this fxn saves the keywords in shared pref
     * @param videoName name of video
     * @param audioChunkName audio chunk name
     * @param keywords list of keywords
     */
    public void saveKeywordsInSharedPref(String videoName, String audioChunkName, String keywords){

        // if there is something in the videoName property then  append if not simply add
        String[] keywordsArr = keywords.split(",");
        SharedPreferences sharedPref = getSharedPreferences("keywords", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String readStr = sharedPref.getString(videoName, null);
        JSONObject object;
        Log.v("ng_before", "There is no value in Shared Pref when saving "+audioChunkName);
        if(readStr!=null){
            // there is already something in shared pref
            Log.v("ng_before", "Threre is some value present in shared pref already: "+readStr);
            try{
                object = new JSONObject(readStr);
            }catch(Exception e){
                e.printStackTrace();
                // if there was some error then don't worry
                object = new JSONObject();
            }
        }else{
            object = new JSONObject();
        }
        try {
            object.put(audioChunkName,getJSONArrayFromStringArray(keywordsArr));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("ng_saved", "videoName: "+videoName+ "obj: "+object.toString());
        editor.putString(videoName, object.toString());
        boolean saved = editor.commit();
        if(saved) {
            Log.v("ng_saved", "keywords saved in Shared Pref");
        }else{
            Log.v("ng_saved", "keywords are not saved on Shared Pref");
        }
    }

    /**
     * this fxn converts the string array into json array
     * @param arr string array
     * @return JSONArray
     */
    public JSONArray getJSONArrayFromStringArray(String[] arr){
        JSONArray jsonArray = new JSONArray();
        for(String str: arr){
            jsonArray.put(str);
        }
        return jsonArray;
    }

    /**
     * this fxn returns the video name from video Uri
     * @param uri uri of video
     * @return Video Name
     */
    public String getVideoNameFromVideoUri(String uri){
        String videoName = uri.split("/RouteApp/")[1];
        String newStr = videoName.replace(".mp4", "");
        Log.v("ng_uri", newStr);
        return newStr;
    }

    /**
     * executed when a tab is clicked
     * @param v view on which user clicked
     */
    public void tabClicked(View v){
        int id = v.getId();
        switch(id){
            case R.id.segmentTab: showSegment(id);
            break;
            case R.id.semanticsTab: showSemantics(id);
            break;
            case R.id.videoTab: showVideo(id);
            default: showVideo(id);
        }
    }


    /**
     * this fxn shows the segment part of UI
     * @param id id of segment
     */
    public void showSegment(int id){
        // select segment button and unselect other tabs
        unFocusAll();
        setFocus(id);
        // who map and bar graph , right now just move to another activity
        Intent i = new Intent(SavingActivity.this, FrameTest.class);
        i.putExtra("videoUri", videoUri);
        i.putExtra("list", list);
        startActivity(i);

    }

    /**
     * this fxn shows the semantic part pf activity
     * @param id
     */
    public void showSemantics(int id){
        // start how text process
        // only if keywords are not stored in shared pref
        // startSpeechToTextProcess();
        // select semantics button and unselect other tabs
        unFocusAll();
        setFocus(id);
        // show map and wordcloud and hide everything else
        mapLayout.setVisibility(View.VISIBLE);
        webViewLayout.setVisibility(View.VISIBLE);
        webView.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);
        // enable wordCloud UI
        // now read the shared and do the stuff
        wordCloudHelper = new WordCloudHelper(SavingActivity.this);
        HashMap<String, JSONArray> hashmap = wordCloudHelper.getKeywordsFromSharedPref(getVideoNameFromVideoUri(videoUri));
        Log.v("ng_map", hashmap.toString());
        try{
            // got the keyWordListString
//            String redundantKeywordListString = wordCloudHelper.getStringOfKeywords(hashmap);
//            String redundantValuesListString = wordCloudHelper.getStringOfImportanceValues(redundantKeywordListString);
//            Log.v("ng_top", redundantKeywordListString+ " - "+redundantValuesListString);
//                        String keywordListString = wordCloudHelper.getTop10Keywords(redundantKeywordListString).replace("\'", "").replace("`", "");
//                        String valuesListString = wordCloudHelper.getTop10Values(redundantValuesListString).replace("\'", "").replace("`", "");;
//                        Log.v("ng_top", keywordListString+ " - "+valuesListString);

//            String combinedString = wordCloudHelper.getFilteredKeywordValuesString(redundantKeywordListString, redundantValuesListString)
//                    .replace("`", "").replace("'", "").replace(".", "").replace("I", "");
            String combinedString = wordCloudHelper.getCombinedStringFromMap(hashmap)
                    .replace("`", "").replace("'", "").replace(".", "").replace("I", "");
            Log.v("ng_combined", combinedString);
            String [] stringParts = combinedString.split("-:-");
            String keywordListString = stringParts[0];
            String valuesListString = stringParts[1];
            // pass it to webview
            webView.addJavascriptInterface(new JavaScriptAction(SavingActivity.this, new OnWordClicked() {
                @Override
                public void onClick(String keyword) {
                    try{
                        // first of all get sentences
                        AudioSyncHelper audioSync = new AudioSyncHelper(SavingActivity.this, getVideoNameFromVideoUri(videoUri));
                        ArrayList<String> sentences = audioSync.getSentencesFromKeyword(keyword);
                        // now get the new audio list
                        ArrayList<String> audioPaths = audioSync.getAudioPathsFromSentences(sentences);
                        ArrayList<Integer> indexes = wordCloudHelper.getAudioChunkIndexFromKeyword(keyword, hashmap);
                        Log.v("ng_ind", indexes.toString());
                        // get sentences from indexes
//                        ArrayList<String> sentences = wordCloudHelper.getSentencesFromIndexes(indexes, mainColorTextList);
//                        Log.v("ng_sen", sentences.toString());
                        // till here everything is right , we are getting right sentences
                        // get correct audio paths
//                        ArrayList<String> audioPaths = wordCloudHelper.getAudioPathsFromIndexes(indexes, properties.audioPaths);
//                        Log.v("ng_aud", audioPaths.toString());
                        BottomSheetDialog dialog = new BottomSheetDialog(SavingActivity.this, keyword, sentences,
                                audioPaths);
                        dialog.show(getSupportFragmentManager(), "bottom_sheet");
                        Log.v("ng_ap", properties.audioPaths.toString());

                        Log.v("ng_plist", highlightedPolyines.toString());
                        // if there is some value already in highlighted list then
                        // remove the polylines and clear the list first then do the process

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(highlightedPolyines.size()!=0){
                                    for(Polyline p: highlightedPolyines){
                                        p.remove();
                                    }
                                    highlightedPolyines.clear();
                                    Log.v("ng_plist", "removed values: "+highlightedPolyines.toString());
                                }
                                for(int index: indexes){
                                    Log.v("ng_ix", index+"");
                                    ArrayList<List<LatLng>> polylines = properties.polylines;
                                    List<LatLng> relatedPolyline = polylines.get(index);
                                    // if the highlight polyline is being drawn for the second time
                                    // get the polyline related with the keyword selected
                                    Polyline polyline = map.addPolyline(new PolylineOptions()
                                            .width(Color.BLACK)
                                            .width(20f)
                                            .addAll(relatedPolyline));
                                    highlightedPolyines.add(polyline);
                                    Log.v("ng_plist", highlightedPolyines.toString());
                                }
                            }
                        });

                        // when clicked on word cloud word
                        // Toast.makeText(SavingActivity.this, keyword, Toast.LENGTH_SHORT).show();
                        // move the map marker
                        // HashMap<String, JSONArray> map = wordCloudHelper.getKeywordsFromSharedPref(getVideoNameFromVideoUri(videoUri));
                        // LatLng point = properties.firstCoordinatesOfPolylines.get(index);



                        // Log.v("ngt_wordcloudlist","point: "+point.toString()+" smallestDistance: "+smallestDistance+" list: "+list.toString()+ " closestLocation: "+closestLocation.toString());
//                        mapAndVideoSeekHelper = new MapAndVideoSeekHelper();
//                        Log.v("ng_click", "simulating map click");
//                        mapAndVideoSeekHelper.simulateMapClick(getApplicationContext(), point, smallestDistance, list, closestLocation, new OnMarkerReadyListener() {
//                            @Override
//                            public void onSuccess(double smallestDistance, LatLng latlng, int position) {
//                                Log.v("ng", "[Simulate Map Click]: adding marker");
//                                Log.v("ng_mapmap", "smalestDistance: "+smallestDistance+ " latlng: "+latlng+ " position: "+position);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        addMarkerNew(smallestDistance, latlng, position);
//                                    }
//                                });
//                            }
//                            @Override
//                            public void onFailure() {
//                                Log.v("ng_mapmap", "map click simulation not successful");
//                                Toast.makeText(getApplicationContext(), "Please click on path", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }catch(Exception e){
                        Log.v("ngt_error", "error: "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }), "JSAction");
            webView.loadUrl("file:///android_asset/wordcloud.html");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
                { WebView.setWebContentsDebuggingEnabled(true); }
            }
            webView.setWebViewClient(new WebViewClient(){
                public void onPageFinished(WebView view, String url){
                    Log.v("ng_keywords", keywordListString+ " : "+valuesListString);
                    webView.loadUrl("javascript:handleData('"+keywordListString+"', '"+valuesListString+"')");
                }
            });
        }catch (Exception e){
            Log.v("ngt_error", "error: "+e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * this fxn shows the video part of UI
     * @param id
     */
    public void showVideo(int id){
        // select video tab and unselect other tabs
        unFocusAll();
        setFocus(id);
        // hide webview and show map and video view
        webViewLayout.setVisibility(View.GONE);
        mapLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.VISIBLE);

    }

    /**
     * this fxn unfocuses all tabs
     */
    public void unFocusAll(){
        segmentTabButton.setBackgroundColor(getResources().getColor(R.color.white));
        segmentTabButton.setTextColor(getResources().getColor(R.color.black));
        semanticTabButton.setBackgroundColor(getResources().getColor(R.color.white));
        semanticTabButton.setTextColor(getResources().getColor(R.color.black));
        videoTabButton.setBackgroundColor(getResources().getColor(R.color.white));
        videoTabButton.setTextColor(getResources().getColor(R.color.black));
    }

    /**
     * this fxn sets the focus to one given tab
     * @param id
     */
    public void setFocus(int id){
        Button btn = findViewById(id);
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btn.setTextColor(getResources().getColor(R.color.white));
    }


    /**
     * this fxn does makes uploads audio files to server and gets the sentences back from the server
     * and poplulate the color list
     */
    public void startSpeechToTextProcess(){
        dd = new DBHelper(SavingActivity.this);
        String speech =dd.getSpeechData(filePath).replace("\"", "").replaceAll(":$", "");
        Log.v("ng", "speech"+speech);
//                if result is not already present, we have to upload the file
        if(speech.equals(""))
        {
            Toast.makeText(SavingActivity.this,"string is empty", Toast.LENGTH_LONG).show();
            Toast.makeText(SavingActivity.this, "file is"+filePath, Toast.LENGTH_SHORT).show();
            ct = 0;
            String filter=filePath.substring(0,filePath.length()-4);
            ArrayList<String> chumkfiles = FileUtils.getFileNames(Environment.getExternalStorageDirectory() + "/RouteApp","chunk_"+filter,1);
//            properties.audioPaths.clear();
//            for(int i=0; i<chumkfiles.size(); i++){
//                // add according to i
//                Log.v("ng_audio_path", "path "+i+" : "+chumkfiles.get(i));
//                properties.audioPaths.add(chumkfiles.get(i));
//            }
//                              for every audio chunk file do this
            for(int i = 0;i<chumkfiles.size();i++)
            {
                try {
//                                      get the individual file path
                    String filePath =  Environment.getExternalStorageDirectory() + "/RouteApp/"+chumkfiles.get(i);
                    Log.v("ng_audio_paths", filePath);
//                                      upload it to server
                    chumkUpload(filePath,chumkfiles.size());
                }catch (Exception ex)
                {
                    Log.d("CHUMKEXP:","Error:",ex);
                }
            }
//region getting actual text from speech
        }else if(speech.contains("@")) {
            // ***************** testing something ************************
            String filter=filePath.substring(0,filePath.length()-4);
            ArrayList<String> chumkfiles = FileUtils.getFileNames(Environment.getExternalStorageDirectory() + "/RouteApp","chunk_"+filter,1);
            Log.v("ng_fs", "files searched: \n\n"+chumkfiles.toString());
            // if the audio paths are empty add the paths to that arraylist
//            properties.audioPaths.clear();
//            for(int i=0; i<chumkfiles.size(); i++){
//                Log.v("ng_audio_path", "path "+i+" : "+chumkfiles.get(i));
//                properties.audioPaths.add(chumkfiles.get(i));
//            }

            // *****************  end of testing **************************
            Toast.makeText(SavingActivity.this, "speech contains @ ", Toast.LENGTH_LONG).show();
            try
            {
                String[] separatedata = speech.split("@");
                listChumktext = new ArrayList<String>();
                listChumktime = new ArrayList<String>();
                separatedata[0]=separatedata[0].toString().substring(0,separatedata[0].toString().length()-1);
                separatedata[1]=separatedata[1].toString().substring(1,separatedata[1].toString().length()-1);
                String[] textdata=separatedata[0].split(":");
                String[] chumkdata=separatedata[1].split(":");
                if(textdata.length>0 && chumkdata.length>0)
                {
                    for(int i =0;i<textdata.length;i++)
                    {
                        String temp = chumkdata[i];
                        String[] tempdata=temp.split("_");
                        String chumk=tempdata[1]+"_chunk_"+tempdata[2];
                        //String chumk=tempdata[1]+"_"+tempdata[2]+"-to-"+tempdata[3]+"_chunk no:"+tempdata[4];
//                                  getting actual data
                        listChumktext.add(textdata[i]);
                        listChumktime.add(chumk);
                    }
                }
//              testing
                showColorList(null, null);
                // ShowAlertDialogList();
                // ShowAlertDialogWithListview();
//              endregion
            }catch (Exception ex)
            {
                Log.d("STSEXP:","after text",ex);
            }
        }
    }


    /**
     * do everything related to segment here
     */
    public void startSegmentProcess(){

    }

}
