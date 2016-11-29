package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import android.widget.ImageView;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IContentView;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.MedioInterface;
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
    private FrameLayout.LayoutParams layoutParams;
    private boolean isInitData;
    private boolean isLayout;
    private MedioInterface bridge;//与 上级 通讯 的桥梁

    //设置 上级组件
    public void setMedioInterface(MedioInterface bridge) {
        this.bridge = bridge;
    }

    public CImageView(Context context, FrameLayout layout, ContentsBean content) {
        super(context);
        mCcontext =context;
        this.layout = layout;
        initData(content);
    }
    //获取长度
    public int getLength() {
        return length;
    }
    //初始化
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
        if (layoutParams==null){
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        }
        this.setLayoutParams(layoutParams);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
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
            getBitmap();
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
    private void getBitmap(){

        //获取bitmap
        if (bitmap == null){
            if (UiTools.fileIsExt(imagePath)){
                bitmap = ImageUtils.getBitmap(imagePath);
            }else{
                bitmap = ImageUtils.getBitmap(UiTools.getDefImagePath());
                if (bridge!=null){
                    bridge.playOver(this);
                }
            }
        }
        if (bitmap!=null){
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
