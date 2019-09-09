package com.example.bhati.routeapplication.Pojo;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ImageDetectionResult {

    // name of the video
    String videoName;

    /**
     *      key is frame name
     *      {'frame_name':  [ImageLabel obj, .....] }
     */
    private HashMap<String, ArrayList<ImageLabel>> frameDataMap;

    public ImageDetectionResult(String videoName){
        this.videoName = videoName;
        frameDataMap = new HashMap<>();
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public HashMap<String, ArrayList<ImageLabel>> getFrameDataMap() {
        return frameDataMap;
    }

    public void setFrameDataMap(HashMap<String, ArrayList<ImageLabel>> frameDataMap) {
        this.frameDataMap = frameDataMap;
    }

    /**
     * this fxn appends the list of image labels in the HashMap given above
     * @param frameName frame name
     * @param labels arraylist of image labels
     */
    public void appendImageLabels(String frameName, ArrayList<ImageLabel> labels){
        Log.v("nuttygeek_append", "adding labels to the object: "+labels.toString());
        frameDataMap.put(frameName, labels);
    }
}
