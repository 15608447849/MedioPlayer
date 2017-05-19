package com.wos.play.rootdir.model_application.ui.ComponentLibrary.button;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wos.play.rootdir.model_application.ui.UiFactory.UiManager;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IView;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;

/**
 * Created by user on 2016/11/12.
 * 按钮 点击
 */
public class CButton extends MeImageButton implements View.OnClickListener,View.OnTouchListener,IView{
    private static final String TAG = "_CButton";
    private Context context;
    private AbsoluteLayout layout;
    private int componentId;
    private int x,y,width,height;
    private int linkId;
    private String upImagePath;
    private String downImagePath;

    private boolean isInitData;
    private boolean isLayout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private String group;
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
            this.group = cb.getContents().get(0).getGroup();
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
    public void setAttribute() {
        this.setLayoutParams(layoutParams);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
        if (upImagePath!=null && UiTools.fileIsExt(upImagePath)){
                this.setImageBitmap(ImageUtils.getBitmap(upImagePath));
        }else{
            this.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    //加载图片
    private void loadBitmap() {

    }


    //布局
    @Override
    public void onLayouts() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }
    //未布局
    @Override
    public void unLayouts() {
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
            setAttribute();
            loadBitmap();
            onLayouts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //停止
    @Override
    public void stopWork() {
//        Logs.i(TAG,"button - startWork()");
        try {
            unLayouts(); //移除布局
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
            UiManager.getInstance().exeTask(linkId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //触摸事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if (downImagePath!=null && UiTools.fileIsExt(downImagePath)){
                    ((ImageButton)v).setImageBitmap(ImageUtils.getBitmap(downImagePath));
                    if ("1".equals(group)) upImagePath = downImagePath;
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                if (upImagePath!=null && UiTools.fileIsExt(upImagePath)){
                    this.setImageBitmap(ImageUtils.getBitmap(upImagePath));
                }else{
                    this.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
