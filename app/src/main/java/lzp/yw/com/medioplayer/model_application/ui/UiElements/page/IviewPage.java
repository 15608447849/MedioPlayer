package lzp.yw.com.medioplayer.model_application.ui.UiElements.page;

import android.widget.FrameLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.layout.IviewLayout;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;

/**
 * Created by user on 2016/11/10.
 */

public abstract class IviewPage extends FrameLayout implements Iview {
    protected BaseActivity activity;
    protected IviewLayout layout;
    protected int id;
    protected boolean isHome = false;
    public boolean isHome() {
        return isHome;
    }
    public void setHome(boolean home) {
        isHome = home;
    }
    protected boolean isInit = false;//是否初始化
    protected boolean isAttr = false;
    protected  boolean isLayout = false;
    public IviewPage(BaseActivity activity,IviewLayout layout) {
        super(activity);
        this.activity = activity;
        this.layout = layout;
    }

    @Override
    public abstract void initData(Object object) ;    //子类实现
    @Override
    public abstract void setAttrbute();
    @Override
    public void layouted() {
        if (!isLayout){
            try {
                layout.addView(this);
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
        try{
            if (!isInit){
                return;
            }
            setAttrbute();//设置属性
            layouted();//设置布局
            if (isLayout){
                loadFragment();
            }
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
    //关于 加载fragments
    protected abstract void loadFragment();
}
