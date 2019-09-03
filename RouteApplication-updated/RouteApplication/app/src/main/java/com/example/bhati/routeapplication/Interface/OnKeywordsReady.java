package com.example.bhati.routeapplication.Interface;

import java.util.ArrayList;

/**
 * this interface is to be used in keywords helper to indicate that the keywords are ready
 *
 */
public interface OnKeywordsReady {


    /**
     * called when the keywords are ready
     * @param keywords list of keywords extracted
     */
    void onSuccess(ArrayList<String> keywords);

    /**
     * called when there i an error getting keywords
     */
    void onFailure();

}
