package com.ubuyquick.vendor.application;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ubuyquick.vendor.utils.UniversalImageLoader;

/**
 * Created by ajays on 4/15/2018.
 */

public class App extends Application {

    private FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.getInstance().init(new UniversalImageLoader(this).getConfig());

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
    }
}
