package com.wos.play.rootdir.model_application.ui.ComponentLibrary.grallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

/**
 * Created by user on 2016/12/9.
 */

public class MImageSwitcher extends ImageSwitcher {
    public MImageSwitcher(Context context) {
        super(context);
    }

    public MImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        ImageView image = (ImageView)this.getNextView();
        image.setImageBitmap(bitmap);
        showNext();
    }

    public ImageView getCurrentImageView(){
        ImageView image = (ImageView)this.getNextView();
        showNext();
        return image;
    }
}
