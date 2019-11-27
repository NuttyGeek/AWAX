package com.example.bhati.routeapplication.helpers;

import com.mapbox.mapboxsdk.annotations.Polyline;

public class PolylinesHelper {

    PolylinesHelper(){

    }

    /**
     * this fxn removes the given polyline from map
     * @param polyline polyline object
     */
    public void removeHighlightPolyline(Polyline polyline){
        polyline.remove();
    }

}
