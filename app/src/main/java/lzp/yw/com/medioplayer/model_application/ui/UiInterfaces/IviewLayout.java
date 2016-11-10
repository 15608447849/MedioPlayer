package lzp.yw.com.medioplayer.model_application.ui.UiInterfaces;

import android.widget.AbsoluteLayout;

import java.util.LinkedHashMap;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

/**
 * Created by user on 2016/11/10.
 */

public class IviewLayout extends AbsoluteLayout implements Iview{
    protected BaseActivity activity;
    protected int id;
    private boolean isInit = false;//是否初始化
    private boolean isLaout = false;
    public IviewLayout(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }
    protected LinkedHashMap<String,IviewPage> pagesMap = null;

    //初始化页面
    private void initPagesMap(){
        if (pagesMap==null){
            pagesMap = new LinkedHashMap<>();
        }
    }
    //添加 一个 页面
    public  void  addPage(int key,IviewPage page){
        if (pagesMap == null){
            initPagesMap();
        }
      pagesMap.put(generateKey(key),page);
    }
    //获取 一个 页面
    public IviewPage getPage(int key){
        if (pagesMap==null){
            return null;
        }
        if (pagesMap.containsKey(generateKey(key))){
            return pagesMap.get(generateKey(key));
        }
        return null;
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
    public void settingSuccess(boolean flag) {
        this.isLaout = flag;
    }

    @Override
    public boolean isSetting() {
        return isLaout;
    }

    @Override
    public void setting() {
        if (isSetting()) {
            return;
        }
    }
    @Override
    public void startWork() {
        try{
            if (!isInitData()){
            return;
            }
            setting();
            activity.setContentView(this);
        }catch (Exception e){
        e.printStackTrace();
        }
    }
    @Override
    public void stopWork() {
        try {
            activity.setContentView(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public String generateKey(int key) {
        return key+"#"+hashCode();
    }
}
