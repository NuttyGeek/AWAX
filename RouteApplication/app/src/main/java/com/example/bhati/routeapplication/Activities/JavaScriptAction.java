package com.example.bhati.routeapplication.Activities;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.bhati.routeapplication.Interface.OnWordClicked;

class JavaScriptAction {

    Context context;
    OnWordClicked callback;

    public JavaScriptAction(Context context, OnWordClicked callback){
        this.context = context;
        this.callback = callback;
    }

    @JavascriptInterface
    public void action(String value) {
        /* passing the callback from JS Interface to another
         interface to be implemented in other activity */
        callback.onClick(value);
    }
}
