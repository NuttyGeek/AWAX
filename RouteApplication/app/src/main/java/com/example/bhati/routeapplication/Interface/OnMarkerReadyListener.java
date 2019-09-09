package com.example.bhati.routeapplication.Interface;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * This interface is specially usable in MapAndVideoSeekHelper
 * whoever implements this method will get to know when the marker is ready to be updated on map view
 *
 * NOTE: This interface is used in MapAndSeekHelper.java inside simulateMapClick fxn
 */
public interface OnMarkerReadyListener {
    /**
     * call this fxn when everything is working correctly and we can use addMarkerNew() fxn which is declared in
     *
     * NOTE: this fxn is used in MapAndSeekerHelper.java - simulateMapClick()
     */
    public void onSuccess(double smallestDistance, LatLng latlng, int position);

    /**
     * call this fxn when we cannot use addMarkerNew() fxn which is declared in SavingActivity.java
     *
     * NOTE: this fxn is used in MapAndSeekerHelper.java - simulateMapClick()
     */
    public void onFailure();
}
