package lzp.yw.com.medioplayer.model_application.ui.UiElements.layout;

import android.widget.AbsoluteLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;

/**
 * Created by user on 2016/11/10.
 */

public abstract class IviewLayout extends AbsoluteLayout implements Iview {
    protected BaseActivity activity;
    protected int id;
    protected boolean isInit = false;//是否初始化
    protected boolean isAttr = false;
    protected boolean isLayout = false;
    public IviewLayout(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }
    @Override
    public abstract void initData(Object object);
    @Override
    public abstract void setAttrbute();
    @Override
    public void layouted() {
        if (!isLayout) {
            activity.setContentView(this);
            isLayout = true;
        }
    }
    @Override
    public void unLayouted() {
        if (isLayout) {
            activity.setContentView(null);
            isLayout = false;
        }
    }
    @Override
    public void startWork() {
        try{
            if (!isInit){
            return;
            }
            setAttrbute();
            layouted();

        }catch (Exception e){
        e.printStackTrace();
        }
    }
    @Override
    public void stopWork() {
        try {
            unLayouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
