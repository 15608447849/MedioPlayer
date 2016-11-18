package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import android.widget.ImageView;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IContentView;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/11.
 * 单张图片 显示
 *
 */

public class CImageView extends ImageView implements IContentView{
    private static final java.lang.String TAG = "CImageView";
    private Context mCcontext;
    private FrameLayout layout ;
    private String imagePath;
    private Bitmap bitmap;
    private int length;


    private boolean isInitData;
    private boolean isLayout ;
    public CImageView(Context context, FrameLayout layout, ContentsBean content) {
        super(context);
        mCcontext =context;
        this.layout = layout;
        initData(content);
    }
    public int getLength() {
        return length;
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
    public void setAttrbute() {
            this.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
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
//        Logs.i(TAG,"image - startWork()");
        try {
                if (!isInitData){
                    return;
                }
            setAttrbute();
            layouted();
            addBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
//        Logs.i(TAG,"image - stopWork()");
        try {
            removeBitmap();
            unLayouted();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取 bitmap
    public Bitmap getBitmap() {
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
//    @Override
//    protected void onDetachedFromWindow() {
//        try {
//            super.onDetachedFromWindow();
//            setImageDrawable(null);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    /**
     * 设置bitmap
     */
    private void addBitmap(){
        //获取bitmap
        if (bitmap == null){
            bitmap = getBitmap();

        }
        if (bitmap!=null){
            this.setScaleType(ImageView.ScaleType.FIT_XY);
            this.setImageBitmap(bitmap);
        }
    }
    /**
     * 移除bitmap
     */
    private void removeBitmap(){
        if(bitmap!=null){
            bitmap.recycle();
            bitmap=null;
        }
    }
}
