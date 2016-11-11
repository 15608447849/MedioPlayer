package lzp.yw.com.medioplayer.model_application.ui.componentLibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/11.
 * 单张图片 显示
 *
 */

public class CImageView extends ImageView implements Iview{
    private static final java.lang.String TAG = "CImageView";
    private Context mCcontext;
    private FrameLayout layout ;
    private int x=0;
    private int y=0;
    private int h=0;
    private int w=0;
    private String imagePath;
    private Bitmap bitmap;
    private int length;

    public int getLength() {
        return length;
    }

    private boolean isAttr;
    private boolean isInitData;
    private boolean isLayout ;
    public CImageView(Context context, FrameLayout layout, ContentsBean content) {
        super(context);
        mCcontext =context;
        this.layout = layout;
        initData(content);
    }


    @Override
    public void initData(Object object) {
        try {
            ContentsBean content = ((ContentsBean)object);
            this.imagePath = UiTools.getUrlTanslationFilename(content.getContentSource());
            this.length = content.getTimeLength();
            isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setInitSuccess(boolean flag) {

    }

    @Override
    public boolean isInitData() {
        return false;
    }

    @Override
    public void setAttrbuteSuccess(boolean flag) {

    }

    @Override
    public boolean isSetAttrbute() {
        return false;
    }

    @Override
    public void setAttrbute() {
        if (!isAttr){
            this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
            this.setBackgroundColor(Color.BLACK);
            isAttr = true;
        }
        //获取bitmap
        if (bitmap == null){
             bitmap = getBitmap();
        }
        if (bitmap!=null){
            Log.i("","图片 组件()");
            this.setScaleType(ImageView.ScaleType.FIT_XY);
            this.setImageBitmap(bitmap);
        }
    }

    @Override
    public void layouted() {
        if (isLayout){
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    @Override
    public void unLayouted() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }

    @Override
    public void startWork() {
        try {
                if (!isInitData){
                    return;
                }
            setAttrbute();
            layouted();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
//            this.setImageBitmap(null);
            unLayouted();
//            bitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取 bitmap
    public Bitmap getBitmap() {
        //
        if (UiTools.fileIsExt(imagePath)){
            bitmap = ImageUtils.getBitmap(imagePath);
        }
        return bitmap;
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
