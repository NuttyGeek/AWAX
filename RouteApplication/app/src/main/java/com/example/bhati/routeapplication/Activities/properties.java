package com.example.bhati.routeapplication.Activities;

import android.graphics.Color;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class properties
{
    public static JSONArray jsonArrayLocs  = new JSONArray();
    public static HashMap<Integer,String> locdata =  new HashMap<Integer, String>();
    //public static HashMap<Integer,String> colors =  new HashMap<Integer, String>();
    public static HashMap<String,String> colorstr =  new HashMap<String, String>();
    public static ArrayList<String> colorsdata = new ArrayList<String>();
    public static Double loclat=0.0;
    public static Double loclog=0.0;
    public static HashMap<String,String> audiodata =  new HashMap<String, String>();
    public static String Server_IP="192.168.43.205";
    public static ArrayList<LatLng> firstCoordinatesOfPolylines = new ArrayList<>();
    public static ArrayList<List<LatLng>> polylines = new ArrayList<>();
    // interval for frame extraction
    public static int REGULAR_FRAME_INTERVAL_MILLIS = 5000;
    public static ArrayList<String> audioPaths = new ArrayList<>();

    public static String email = "admin@admin.com";
    public static String password = "adminadmin";
    public static int fakeIndex = 1;
    public static String mainAudioFileNameInSavingActivity = "";

}
