package com.wos.play.rootdir.model_application.ui.ComponentLibrary.grallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;

/**
 * Created by user on 2016/11/18.
 */

public class ImagerSwitchFactory implements ViewSwitcher.ViewFactory{
    private MeImageView imager1;
    private MeImageView imager2;
    private int current = 0;
    public ImagerSwitchFactory(Context context){
        imager1 = ImageUtils.createImageViewScale(context);
        initAttr(imager1,0);
        imager2 = ImageUtils.createImageViewScale(context);
        initAttr(imager2,1);
    }

    //初始化属性
    private void initAttr(ImageView image,int tag) {
        if (image!=null){
            image.setTag(tag);
//            image.setBackgroundColor(0xFF000000);
//            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setLayoutParams(new ImageSwitcher.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
    //停止
    public void stop(){
        if (imager1 != null){
            ImageUtils.removeImageViewDrawable(imager1);
        }
        if (imager2!= null){
            ImageUtils.removeImageViewDrawable(imager2);
        }
    }

    @Override
    public View makeView() {
        if (current==0){
            current++;
            return imager1;
        }else{
            current--;
            return imager2;
        }
    }
}
