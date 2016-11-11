package lzp.yw.com.medioplayer.model_application.ui.UiInterfaces;

import android.widget.AbsoluteLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

/**
 * Created by user on 2016/11/10.
 */

public class IviewLayout extends AbsoluteLayout implements Iview{
    protected BaseActivity activity;
    protected int id;
    private boolean isInit = false;//是否初始化
    private boolean isAttr = false;
    public IviewLayout(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }


    @Override
    public void initData(Object object) {

    }

    @Override
    public void setInitSuccess(boolean flag) {
        isInit = flag;
    }

    @Override
    public boolean isInitData() {
        return isInit;
    }

    @Override
    public void setAttrbuteSuccess(boolean flag) {
        this.isAttr = flag;
    }

    @Override
    public boolean isSetAttrbute() {
        return isAttr;
    }

    @Override
    public void setAttrbute() {
        if (isSetAttrbute()) {
            return;
        }
    }

    @Override
    public void layouted() {
        activity.setContentView(this);
    }

    @Override
    public void unLayouted() {
        activity.setContentView(null);
    }


    @Override
    public void startWork() {
        try{
            if (!isInitData()){
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
