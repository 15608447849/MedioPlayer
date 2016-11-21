package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.button;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiFactory.UiManager;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/12.
 * 按钮 点击
 */
public class CButton extends ImageButton implements View.OnClickListener,View.OnTouchListener,Iview{
    private static final String TAG = "_CButton";
    private Context context;
    private AbsoluteLayout layout;
    private int componentId;
    private int x,y,width,height;
    private int linkId;
    private String upImagePath;
    private String downImagePath;
    private Bitmap upBitmap;//抬起图片
    private Bitmap downBitmap;//按下图片
    private boolean isInitData;
    private boolean isLayout;
    private AbsoluteLayout.LayoutParams layoutParams;
    public CButton(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }


    //初始化
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
            this.componentId = cb.getId();
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            this.linkId = cb.getLinkId();
            this.isInitData = true;
            if (cb.getContents()!=null && cb.getContents().size()==1){
                upImagePath = UiTools.getUrlTanslationFilename(cb.getContents().get(0).getSourceUp());
                downImagePath = UiTools.getUrlTanslationFilename(cb.getContents().get(0).getSourceDown());
            }
            this.setOnClickListener(this);
            this.setOnTouchListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //设置属性
    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private void loadBitmap() {
        if (upImagePath!=null && UiTools.fileIsExt(upImagePath)){
            Logs.i(TAG,"指定图片 默认效果 path - "+upImagePath);
            upBitmap = ImageUtils.getBitmap(upImagePath);
        }else{
            Logs.i(TAG,"图片资源本地不存在 - "+upImagePath);
        }

        if (downImagePath!=null && UiTools.fileIsExt(downImagePath)){
            Logs.i(TAG,"指定图片 点击效果 path - "+downImagePath);
            downBitmap = ImageUtils.getBitmap(downImagePath);
        }else{
            Logs.i(TAG,"图片资源本地不存在 - "+downImagePath);
        }

        if (upBitmap!=null){
            this.setImageBitmap(upBitmap);
        }else{
            Logs.i(TAG,"-- 使用默认图片 --");
           // this.setImageResource(R.drawable.onclick_up);//没有指定图片 使用默认图片
            this.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    //清理bitmap
    private void unloadBitmap() {
        if (upBitmap!=null){
            upBitmap.recycle();
            upBitmap=null;
        }
        if (downBitmap!=null){
            downBitmap.recycle();
            downBitmap=null;
        }
    }

    //布局
    @Override
    public void layouted() {
        if (isLayout){
            return;
        }
        layout.addView(this);
        isLayout = true;
    }
    //未布局
    @Override
    public void unLayouted() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    //开始工作
    @Override
    public void startWork() {
//        Logs.i(TAG,"button - startWork()");
        try {
            if (!isInitData){
                return;
            }
            setAttrbute();
            loadBitmap();
            layouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //停止
    @Override
    public void stopWork() {
//        Logs.i(TAG,"button - startWork()");
        try {
            unLayouted(); //移除布局
            unloadBitmap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        try {
            ((BaseActivity)context).showToast("按钮 -"+componentId +" linkId - "+linkId);
            UiManager.getInstans().exeTask(linkId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                Logs.i(TAG,"按下");
                if (downBitmap!=null){
                    //更改为按下时的背景图片
                    ((ImageButton)v).setImageBitmap(downBitmap);
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                Logs.i(TAG,"抬起");
                if (upBitmap!=null){
                    //改为抬起时的图片
                    ((ImageButton)v).setImageBitmap(upBitmap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
