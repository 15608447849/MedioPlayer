package com.wos.play.rootdir.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/29.
 * lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather.LedImageView
 */

public class MeImageView extends ImageView{
    private static final String TAG = "MeImageView";
    public MeImageView(Context context) {
        super(context);
    }

    public MeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    private boolean isTouch = false;

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        Logs.e(TAG,"onTouchEvent - action:"+event.getAction()+" x :"+x +" y :"+y);
//      return super.onTouchEvent(event);
        return isTouch?isTouch:super.onTouchEvent(event);
    }


    @Override
    public boolean onDragEvent(DragEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        Logs.e(TAG,"onDragEvent - action:"+event.getAction()+" x :"+x +" y :"+y);
        return super.onDragEvent(event);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        Logs.e(TAG,"onScrollChanged - l:"+l+" t :"+t +" \n oldl :"+oldl+" oldt "+oldt);
        super.onScrollChanged(l, t, oldl, oldt);
    }




}
