package com.example.anzhuo.normalpractice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by anzhuo on 2016/8/17.
 */
public class MyImageView extends ImageView {
    int width=getWidth();
    int height=getHeight();

    public MyImageView(Context context){
        super(context);
    }
    public MyImageView(Context context, AttributeSet attrs){
        super(context,attrs);
    }
    public MyImageView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(50);
        Paint paint=new Paint();
        canvas.save();
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.index_pin);
        int viewWidth=bitmap.getWidth();
        int viewHeight=bitmap.getHeight();
        int startX=viewWidth/2;
        int startY=height/4+viewHeight;
        canvas.drawBitmap(bitmap,startX,startY,paint);
        canvas.restore();
    }
}
