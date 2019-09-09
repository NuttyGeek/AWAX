package com.example.bhati.routeapplication.helpers;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.bhati.routeapplication.Interface.OnMarkerReadyListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class MapAndVideoSeekHelper {

    /**
     * this fxn simulate a map click with a specific coordinate
     * @param point coordinate of the place where to want to perform map click
     */
    public void simulateMapClick(Context appContext, LatLng point , double smallestDistance, ArrayList<LatLng> list, Location closestLocation, OnMarkerReadyListener callback){
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
            callback.onSuccess(smallestDistance, latLng,position);
            //addMarkerNew(smallestDistance, latLng, position);
        } else {
            callback.onFailure();
//            Toast.makeText(appContext, "Please click on path", Toast.LENGTH_SHORT).show();
        }

    }

}
