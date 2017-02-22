package com.example.anzhuo.normalpractice;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.anzhuo.normalpractice.javabeans.SingleSongInfo;
import com.example.anzhuo.normalpractice.javabeans.SongAddress;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/17 0017.
 */

public class SingleSongActivity extends Activity implements View.OnClickListener {
    private SimpleDraweeView albumCover;
    private TextView songName, singer, companyName, albumName, publishTime;
    private Button play;
    private int songId;
    private String songPath;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                SongAddress song = (SongAddress) msg.obj;
                String url = song.getData().get(0).getUrl();
                String songName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
                songPath = songName + ".mp3";
                HttpUtils.downMp3(url, songName, handler);
            }
            if (msg.what == 1) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                MediaPlayerManager.play(MainApplication.cachePath + File.separator + songPath);

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_song_activity);
        albumCover = (SimpleDraweeView) findViewById(R.id.sdv_songInfo_album_photo);
        songName = (TextView) findViewById(R.id.tv_songInfo_songName);
        singer = (TextView) findViewById(R.id.tv_songInfo_singer);
        companyName = (TextView) findViewById(R.id.tv_songInfo_companyName);
        albumName = (TextView) findViewById(R.id.tv_songInfo_albumName);
        publishTime = (TextView) findViewById(R.id.tv_songInfo_publishTime);
        play = (Button) findViewById(R.id.btn_songInfo_play);

        play.setOnClickListener(this);

        songId = getIntent().getIntExtra("songId", 0);
        new MyTask().execute(songId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_songInfo_play:
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        SongAddress song = HttpUtils.postNetease(songId);
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = song;
                        handler.sendMessage(msg);
                    }
                }.start();
                break;
        }
    }


    class MyTask extends AsyncTask<Integer, Void, SingleSongInfo> {

        @Override
        protected SingleSongInfo doInBackground(Integer... integers) {
            return CurlPackage.getSongDetail(integers[0]);
        }

        @Override
        protected void onPostExecute(SingleSongInfo songsBean) {
            super.onPostExecute(songsBean);
            if (songsBean != null) {
                SingleSongInfo.Songs song = songsBean.getSongs().get(0);
                albumName.setText("Album:" + song.getAlbum().getName());
                songName.setText("Name:" + song.getName());
                String singers = "Singer:";
                for (SingleSongInfo.Artists artist : song.getArtists()) {
                    singers += artist.getName() + "-";
                }
                singer.setText(singers.substring(0, singers.lastIndexOf("-")));
                companyName.setText("Company:" + song.getAlbum().getCompany());

                albumCover.setImageURI(song.getAlbum().getBlurPicUrl());
                publishTime.setText("Publish Time:" + "\r" + new Date(song.getAlbum().getPublishTime()) + "");
            }
        }
    }

}
