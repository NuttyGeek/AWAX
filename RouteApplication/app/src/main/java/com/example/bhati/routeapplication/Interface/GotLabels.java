package com.example.bhati.routeapplication.Interface;

import com.example.bhati.routeapplication.Pojo.ImageLabel;

import java.util.ArrayList;

public interface GotLabels {

    /**
     * this fxn is called when we get labels for an image
     * @param videoName video name
     * @param frameName name of the frame
     * @param labels ArrayList<ImageLabel>
     */
    public void gotLabelsSuccess(String videoName, String frameName, ArrayList<ImageLabel> labels);

    /**
     * this fxn is called when the firebase failed to process the image
     * @param error
     */
    public void gotLabelsFailure(String error);

    /**
     * this fxn is called when the whole frame processing task is completed
     */
    public void gotLabelsCompleted(String videoName);

    /**
     * this fxn send the no of frames processed to uodate the UI
     * @param count
     */
    public void getProcessedFramesCount(int count);

}
