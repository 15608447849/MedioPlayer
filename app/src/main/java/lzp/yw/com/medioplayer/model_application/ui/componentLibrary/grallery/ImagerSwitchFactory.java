package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.grallery;

import android.content.Context;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;

/**
 * Created by user on 2016/11/18.
 */

public class ImagerSwitchFactory implements ViewSwitcher.ViewFactory{
    private ImageView imager1;
    private ImageView imager2;
    private int current = 0;
    public ImagerSwitchFactory(Context context){
        imager1 = ImageUtils.createImageView(context);
        initAttr(imager1,0);
        imager2 = ImageUtils.createImageView(context);
        initAttr(imager2,1);
    }

    //初始化属性
    private void initAttr(ImageView image,int tag) {
        if (image!=null){
            image.setTag(tag);
            image.setBackgroundColor(0xFF000000);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setLayoutParams(new ImageSwitcher.LayoutParams(
                    Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));
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
