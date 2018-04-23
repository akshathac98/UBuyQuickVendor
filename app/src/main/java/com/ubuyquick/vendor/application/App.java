package com.ubuyquick.vendor.application;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

/**
 * Created by ajays on 4/15/2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.getInstance().init(new UniversalImageLoader(this).getConfig());
    }
}
