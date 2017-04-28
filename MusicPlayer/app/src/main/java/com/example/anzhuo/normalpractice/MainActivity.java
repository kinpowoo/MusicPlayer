package com.example.anzhuo.normalpractice;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by anzhuo on 2016/8/5.
 */
public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    ViewPager viewPager;
    SeekBar seekBar,viewPin;
    List<View> list;
    ImageView viewChip;
    ImageButton back;
    TextView lyrics, title;
    MyViewAdapter myViewAdapter;
    Intent intent;
    Bundle bundle;
    String songPath;
    int currentSongPosition;
    String songName;
    ArrayList<String> songPathList;
    ArrayList<String> songNameList;
    MyService myService;
    ToggleButton toggleButton;
    ToggleButton loopChoice;
    int songPlayPosition =1;
    int totalLength;
    SharedPreferences sp;
    AudioManager audioManager;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                title.setText(songName);
            }
            if (msg.what == 2) {
                handler.postDelayed(timeTracker, 1000);
            }
        }
    };
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((MyService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    Runnable timeTracker = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(Utils.getDecimal((double) MediaPlayerManager.mediaPlayer.getCurrentPosition()
                    / (double) totalLength));
            handler.sendEmptyMessageDelayed(2, 1000);
            Log.i("system max volum",audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)+"");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
        viewPager = (ViewPager) findViewById(R.id.vp_chip_animation);
        View viewAnimation = View.inflate(this, R.layout.view_animation, null);
        View viewLyrics = View.inflate(this, R.layout.view_lyrics, null);
        viewPin = (SeekBar) viewAnimation.findViewById(R.id.iv_chip_pin);
        viewChip = (ImageView) viewAnimation.findViewById(R.id.iv_chip_chip);
        lyrics = (TextView) viewLyrics.findViewById(R.id.tv_chip_lyrics);
        title = (TextView) findViewById(R.id.tv_chip_songName);
        back = (ImageButton) findViewById(R.id.ib_chip_back);
        seekBar = (SeekBar) findViewById(R.id.sb_chip_move);
        toggleButton = (ToggleButton) findViewById(R.id.tb_chip_play);
        loopChoice = (ToggleButton) findViewById(R.id.tb_loop_choice);

        loopChoice.setOnClickListener(this);
        toggleButton.setOnCheckedChangeListener(this);
        back.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        viewPin.setOnSeekBarChangeListener(this);
        seekBar.setEnabled(true);
        seekBar.setMax(100);
        viewPin.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        viewPin.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        viewChip.setOnClickListener(this);
        lyrics.setText("");
        list = new ArrayList<View>();
        list.add(viewAnimation);
        list.add(viewLyrics);
        myViewAdapter = new MyViewAdapter(list);
        viewPager.setAdapter(myViewAdapter);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            currentSongPosition = bundle.getInt("position");
            songName = bundle.getString("songName");
            songPathList = bundle.getStringArrayList("songPathList");
            songNameList = bundle.getStringArrayList("songNameList");
            songPath = songPathList.get(currentSongPosition);
            MediaPlayerManager.play(songPath);
            startAnimation();
            totalLength = MediaPlayerManager.mediaPlayer.getDuration();
            songPlayPosition = MediaPlayerManager.mediaPlayer.getCurrentPosition();
            handler.sendEmptyMessage(2);
//            Log.i("tag",songPath);
        }

        title.setText(songName);
        sp=getSharedPreferences("loopMode",MODE_PRIVATE);
        if(sp!=null) {
            Boolean isLoop = sp.getBoolean("isLoop",false);
            if(isLoop){
                loopChoice.setChecked(true);
            }else {
                loopChoice.setChecked(false);
            }
        }
        MediaPlayerManager.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(loopChoice.isChecked()){
                    nextSong();
                }else {
                    MediaPlayerManager.play(songPath);
                }
            }
        });

    }


    @Override
    public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.sb_chip_move:
                songPlayPosition = (int) ((MediaPlayerManager.mediaPlayer.getDuration() * progress) / 100);
                break;
            case R.id.iv_chip_pin:
                if(viewPin.getVisibility()==View.VISIBLE) {
                    while (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)<progress) {
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
                                AudioManager.FLAG_PLAY_SOUND);
                    }
                    while (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)>progress){
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,
                                AudioManager.FLAG_PLAY_SOUND);
                    }
                }
                    break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.sb_chip_move:
                myService.play(songPath, songPlayPosition);
                break;
            case R.id.iv_chip_pin:
                viewPin.setVisibility(View.GONE);
                break;
        }
    }

    public void startAnimation() {
        RotateAnimation rotateAnimation4 = new RotateAnimation(0, 36000, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation4.setDuration(80 * 10000);
        viewChip.startAnimation(rotateAnimation4);
    }

    public void nextSong(){
        currentSongPosition += 1;
        if (currentSongPosition == songPathList.size() - 1) {
            currentSongPosition = 0;
        }
        songPath = songPathList.get(currentSongPosition);
        songName = songNameList.get(currentSongPosition);
        handler.sendEmptyMessage(0);
        myService.next(songPath);
        totalLength = MediaPlayerManager.mediaPlayer.getDuration();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_chip_previous:
                if(songNameList.size()<=1){
                    Toast.makeText(this,"已无更多歌曲!",Toast.LENGTH_SHORT).show();
                }else {
                    currentSongPosition -= 1;
                    if (currentSongPosition == 0) {
                        currentSongPosition = songPathList.size() - 1;
                    }
                    songPath = songPathList.get(currentSongPosition);
                    songName = songNameList.get(currentSongPosition);
                    handler.sendEmptyMessage(0);
                    myService.previous(songPath);
                    totalLength = MediaPlayerManager.mediaPlayer.getDuration();
                    startAnimation();
                }
                break;
            case R.id.rb_chip_next:
                if(songNameList.size()<=1) {
                    Toast.makeText(this, "已无更多歌曲!", Toast.LENGTH_SHORT).show();
                }else {
                    nextSong();
                    startAnimation();
                }
                break;
            case R.id.ib_chip_back:
                handler.removeCallbacks(timeTracker);
                MediaPlayerManager.free();
                finish();
                break;
            case R.id.iv_chip_chip:
                viewPin.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_chip_play:
            if (isChecked) {
                RotateAnimation rotateAnimation2 = new RotateAnimation(0, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation2.setDuration(1000);
                viewChip.startAnimation(rotateAnimation2);
                MediaPlayerManager.stop();
                songPlayPosition = MediaPlayerManager.mediaPlayer.getCurrentPosition();
            } else {
                startAnimation();
                myService.play(songPath, songPlayPosition);
                totalLength = MediaPlayerManager.mediaPlayer.getDuration();
            }
                break;
            case R.id.tb_loop_choice:
                sp=getSharedPreferences("loopMode",MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                if(isChecked){
                    MediaPlayerManager.looping(true);
                    editor.clear();
                    editor.putBoolean("isLoop",true);
                    editor.commit();
                    Log.i("sharedpreferences",sp.getBoolean("isLoop",false)+"");
                }else {
                    MediaPlayerManager.looping(false);
                    editor.clear();
                    editor.putBoolean("isLoop",false);
                    editor.commit();
                }
                break;
        }
    }

}
