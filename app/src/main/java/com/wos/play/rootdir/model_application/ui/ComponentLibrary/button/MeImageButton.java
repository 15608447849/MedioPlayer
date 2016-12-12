package com.wos.play.rootdir.model_application.ui.ComponentLibrary.button;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by user on 2016/12/10.
 */

public class MeImageButton extends ImageButton{
    public MeImageButton(Context context) {
        super(context);
    }

    public MeImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
}
