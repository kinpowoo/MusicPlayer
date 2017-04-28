package com.example.anzhuo.normalpractice;

import android.app.Activity;
import android.graphics.Color;
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
import android.widget.Toast;

import com.example.anzhuo.normalpractice.javabeans.SingleSongInfo;
import com.example.anzhuo.normalpractice.javabeans.SongAddress;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/17 0017.
 */

public class SingleSongActivity extends Activity implements View.OnClickListener {
    private SimpleDraweeView albumCover;
    private TextView songName, singer, companyName, albumName, publishTime,downloadStatus;
    private Button download;
    private int songId;
    private String songPath;
    private String songURI;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    downloadStatus.setText("获取信息失败！");
                    break;
                case 0:
                    downloadStatus.setText("歌曲地址已获得，现在可以开始下载！");
                    download.setBackgroundColor(Color.BLUE);
                    SongAddress song = (SongAddress) msg.obj;songURI = song.getData().get(0).getUrl();
                    download.setEnabled(true);
                    break;
               case 1:
                    downloadStatus.setText("歌曲已下载到本地！");
                    break;
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
        download = (Button) findViewById(R.id.btn_songInfo_download);
        downloadStatus = (TextView) findViewById(R.id.tv_download_status);

        download.setOnClickListener(this);
        download.setEnabled(false);
        download.setBackgroundColor(Color.GRAY);
        downloadStatus.setText("正在获取歌曲信息...");

        songId = getIntent().getIntExtra("songId", 0);
        new MyTask().execute(songId);
        new Thread(){
            @Override
            public void run() {
                HttpUtils.postNetease(songId,handler);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_songInfo_download:
                Log.d("download","start to download..");
                downloadStatus.setText("歌曲下载中...");
                new Thread() {
                    @Override
                    public void run() {
                        String songN = songName.getText().toString();
                        songN = songN.substring(songN.lastIndexOf(":")+1);
                        songPath = songN + ".mp3";
                        HttpUtils.downMp3(songURI, songPath, handler);
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                publishTime.setText("Publish Time:" +sdf.format(new Date(song.getAlbum().getPublishTime())));
                downloadStatus.setText("正在获取歌曲地址...，请稍等片刻再点击下载。");
            }
        }
    }

}
