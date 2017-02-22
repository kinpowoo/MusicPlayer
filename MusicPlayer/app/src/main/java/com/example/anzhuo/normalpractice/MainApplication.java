package com.example.anzhuo.normalpractice;

import android.app.Application;
import android.os.Environment;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by jianbo on 2016/11/23.
 */
public class MainApplication extends Application {
   static final String cachePath= Environment.getDownloadCacheDirectory().getPath();
   static final String downloadPath=Environment.getExternalStorageDirectory().getPath();
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init() {
        Fresco.initialize(this);
        MediaPlayerManager.init(this);
    }
}
