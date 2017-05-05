package com.wos.play.rootdir.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by user on 2016/11/29.
 * lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather.LedImageView
 */

public class MeImageView extends ImageView{
    protected static final String TAG = "MeImageView";
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
    public void setImagePath(String path){

    }
}
