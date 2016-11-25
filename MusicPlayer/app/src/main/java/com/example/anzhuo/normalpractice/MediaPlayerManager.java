package com.example.anzhuo.normalpractice;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by jianbo on 2016/11/23.
 */
public class MediaPlayerManager {

    public static MediaPlayer mediaPlayer;
    public static Context context;

    public static void init(Context ctx){
        mediaPlayer=new MediaPlayer();
        context=ctx;
    }


    public static void play(String path){
        File file=new File(path);
        if(file.exists()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Log.i("music","song have been player");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void free(){
        mediaPlayer.stop();
    }

    public static void stop(){
        mediaPlayer.pause();
    }

    public static void looping(boolean choose){
        mediaPlayer.setLooping(choose);
    }

}
