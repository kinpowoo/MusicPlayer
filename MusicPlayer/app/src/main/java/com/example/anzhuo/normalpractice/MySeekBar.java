package com.example.anzhuo.normalpractice;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by anzhuo on 2016/8/8.
 */
public class MySeekBar extends View {
    int progress;
    int max;
    float mheight;
    float mwidth;

    public MySeekBar(Context context) {
        super(context);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mtype = context.obtainStyledAttributes(attrs, R.styleable.MySeekBar);
        progress = mtype.getInteger(R.styleable.MySeekBar_progress, 100);
        max = mtype.getInteger(R.styleable.MySeekBar_max, 0);
        mheight=mtype.getDimension(R.styleable.MySeekBar_mheight,10);
        mwidth=mtype.getDimension(R.styleable.MySeekBar_mwidth,400);
    }
   public float getProgress(MediaPlayer mediaPlayer){
       int width=getMeasuredWidth();
       int duration= mediaPlayer.getDuration();
       float speed=((float)width/(float)duration);
       progress+=speed;
       return progress;
   }

}

