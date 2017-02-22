package com.example.anzhuo.normalpractice;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by anzhuo on 2016/8/15.
 */
public class MyService extends Service {
    class MyBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void play(String path,int position){
        if(MediaPlayerManager.mediaPlayer.isPlaying()){
            MediaPlayerManager.mediaPlayer.seekTo(position);
        }else {
            MediaPlayerManager.play(path);
            MediaPlayerManager.mediaPlayer.seekTo(position);
        }
    }

    public void previous(String path){
        MediaPlayerManager.play(path);
    }
    public void next(String path){
        MediaPlayerManager.play(path);
    }

}
