package com.wos.play.rootdir.model_application.ui.ComponentLibrary.stream_medio;

import android.content.Context;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/21.
 */

public class CStreamMedioForVitamio extends FrameLayout implements IComponent{
    private static final java.lang.String TAG = "CMorePictures";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private String streamUrl;
//    private Mvitamios video;
    private MVlcs video;
    private boolean isInitData;
    private boolean isLayout;
    public CStreamMedioForVitamio(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }

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
            if (cb.getContents()!=null ) {//&& cb.getContents().size()==1
                createContent(cb.getContents().get(0));
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
    }

    @Override
    public void layouted() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
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
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createContent(Object object) {
        try {
            ContentsBean content = (ContentsBean)object;
            //video = new Mvitamios(context,content.getContentSource());//content.getContentSource()
            video = new MVlcs(context,content.getContentSource());//content.getContentSource()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadContent() {
        video.allowPlay(this);
    }

    @Override
    public void unLoadContent() {
        video.unAllowPlay();
    }
}
