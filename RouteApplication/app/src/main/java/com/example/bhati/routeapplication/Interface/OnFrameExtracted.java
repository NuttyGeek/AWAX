package com.example.bhati.routeapplication.Interface;

public interface OnFrameExtracted {

    /**
     * this fxn is called when the whle frames extraction task is completed
     */
    public void onFrameExtractionCompleted();

    /**
     * this fxn is called to send the no of frames extracted
     * @param count index of latest frame extracted
     */
    public void getExtractedFrameCount(int count);

    /**
     * get total no of frames
     * @param count total no of frames to be extracted
     */
    public void getTotalFramesCount(int count);

}
