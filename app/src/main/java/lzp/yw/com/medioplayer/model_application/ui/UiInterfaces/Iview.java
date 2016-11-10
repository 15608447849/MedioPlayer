package lzp.yw.com.medioplayer.model_application.ui.UiInterfaces;

/**
 * Created by user on 2016/11/10.
 */

public interface Iview {

    void initData(Object object);
    void setInitSuccess(boolean flag);
    boolean isInitData();

    void settingSuccess(boolean flag);
    boolean isSetting();

    void setting();

    void startWork();

    void  stopWork();

    String generateKey(int key);


}
