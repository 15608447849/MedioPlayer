package com.wos.play.rootdir.model_application.ui.UiInterfaces;

/**
 * Created by user on 2016/11/10.
 */

public interface IView {
    void initData(Object object);
    void setAttribute();
    void onLayouts();
    void unLayouts();
    void startWork();
    void stopWork();

}
