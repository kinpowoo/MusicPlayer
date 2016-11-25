package com.example.anzhuo.normalpractice;

import android.app.Application;

/**
 * Created by jianbo on 2016/11/23.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public void init(){

    MediaPlayerManager.init(this);
    }
}
