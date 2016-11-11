package lzp.yw.com.medioplayer.model_application.ui.UiInterfaces;

import android.widget.FrameLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

/**
 * Created by user on 2016/11/10.
 */

public class IviewPage extends FrameLayout implements Iview{
    protected BaseActivity activity;
    protected IviewLayout layout;
    protected int id;
    protected boolean isHome = false;
    public int getmId() {
        return id;
    }

    public boolean isHome() {
        return isHome;
    }

    public void setHome(boolean home) {
        isHome = home;
    }

    private boolean isInit = false;//是否初始化
    private boolean isAttr = false;
    private  boolean isLayout = false;










    public IviewPage(BaseActivity activity,IviewLayout layout) {
        super(activity);
        this.activity = activity;
        this.layout = layout;
    }

    @Override
    public void initData(Object object) {
            //子类实现
    }

    @Override
    public void setInitSuccess(boolean flag) {
        this.isInit = flag;
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

    }

    @Override
    public void layouted() {
        if (!isLayout){
            try {
                layout.addView(this);
                mLoadSource();
                isLayout = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void unLayouted() {
        if (isLayout){
            try {
                layout.removeView(this);
                isLayout =false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startWork() {


    }




    @Override
    public void stopWork() {

    }


    //关于 加载fragments

    protected void mLoadSource(){

    }

    protected void mUnLoadSource(){

    }


}
