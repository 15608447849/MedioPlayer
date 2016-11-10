package lzp.yw.com.medioplayer.model_application.ui.UiInterfaces;

import android.widget.FrameLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

/**
 * Created by user on 2016/11/10.
 */

public class IviewPage extends FrameLayout implements Iview{
    protected BaseActivity activity;
    private int id;
    public IviewPage(BaseActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void initData(Object object) {

    }

    @Override
    public void setInitSuccess(boolean flag) {

    }

    @Override
    public boolean isInitData() {
        return false;
    }

    @Override
    public void settingSuccess(boolean flag) {

    }

    @Override
    public boolean isSetting() {
        return false;
    }

    @Override
    public void setting() {

    }

    @Override
    public void startWork() {

    }

    @Override
    public void stopWork() {

    }

    @Override
    public String generateKey(int key) {
        return null;
    }
}
