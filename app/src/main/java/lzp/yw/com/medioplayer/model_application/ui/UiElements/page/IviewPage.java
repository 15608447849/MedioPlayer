package lzp.yw.com.medioplayer.model_application.ui.UiElements.page;

import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import java.util.Map;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/10.
 */

public abstract class IviewPage extends FrameLayout implements Iview {
    protected static final String TAG = "IviewPage";
    protected BaseActivity activity;
    protected AbsoluteLayout layout;//父布局
    protected int id;
    protected boolean isHome = false;
    public boolean isHome() {
        return isHome;
    }
    public void setHome(boolean home) {
        isHome = home;
    }
    protected boolean isInit = false;//是否初始化
    protected  boolean isLayout = false;
    public IviewPage(BaseActivity activity) {
        super(activity);

            this.activity = activity;
            this.layout = (AbsoluteLayout) activity.getActivityLayout();

    }

    @Override
    public abstract void initData(Object object) ;    //子类实现
    @Override
    public abstract void setAttrbute();
    @Override
    public void layouted() {
        Logs.i("","layouted() - "+isLayout);
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
        Logs.i(TAG,"unLayouted() - "+isLayout);
        if (isLayout){
            try {
                removeFragment();
                layout.removeView(this);
                isLayout =false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void startWork() {
        Logs.i(TAG,"startWork() - "+isLayout);
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
        Logs.i(TAG,"stopWork() - "+isLayout);
        try {
            unLayouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //关于 加载fragments
    protected abstract void loadFragment();
    //移除
    protected abstract void removeFragment();


    public Map<String,Integer> getPageSize(){
        return null;
    }
}
