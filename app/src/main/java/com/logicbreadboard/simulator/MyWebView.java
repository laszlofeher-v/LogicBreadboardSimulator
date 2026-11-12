package com.logicbreadboard.simulator;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.webkit.WebView;

/**
 * Created on 12/11/15.
 */
class MyWebView extends WebView {
    MyWebView(Context context) {
        super(context);
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        Log.i("MyWebView", "onConfigurationChanged");

    }
}

