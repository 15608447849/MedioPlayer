package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by user on 2016/11/29.
 * lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather.LedImageView
 */

public class LedImageView extends ImageView{
    public LedImageView(Context context) {
        super(context);
    }

    public LedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
