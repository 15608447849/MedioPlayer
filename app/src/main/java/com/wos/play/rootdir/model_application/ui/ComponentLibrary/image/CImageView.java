package com.wos.play.rootdir.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IContentView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.MediaInterface;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/11.
 * 单张图片 显示
 *
 */

public class CImageView extends MeImageView implements IContentView{
    private static final java.lang.String TAG = "CImageView";
    private Context mCcontext;
    private FrameLayout layout ;
    private String imagePath;

    private int length;
    private FrameLayout.LayoutParams layoutParams;
    private boolean isInitData;
    private boolean isLayout;
    private MediaInterface bridge;//与 上级 通讯 的桥梁

    //设置 上级组件
    public void setMediaInterface(MediaInterface bridge) {
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
    public void setAttribute() {
        if (layoutParams==null){
            layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        }
        this.setLayoutParams(layoutParams);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
        if (UiTools.fileIsExt(imagePath)){
           this.setImageBitmap(ImageUtils.getBitmap(imagePath));
        }else {
            this.setImageBitmap(ImageUtils.getBitmap(UiTools.getDefImagePath()));
            if (bridge != null) {
                bridge.playOver(this);
            }
        }
    }

    @Override
    public void onLayouts() {
        if (isLayout){
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    @Override
    public void unLayouts() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }

    @Override
    public void startWork() {
        try {
            if (!isInitData) return;
            setAttribute();
            onLayouts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            unLayouts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
