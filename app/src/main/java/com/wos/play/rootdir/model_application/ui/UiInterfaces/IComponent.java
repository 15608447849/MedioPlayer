package com.wos.play.rootdir.model_application.ui.UiInterfaces;

/**
 * Created by Administrator on 2016/11/12.
 */

public interface IComponent extends IView{
    //创建内容
    public void createContent(Object object);
    //加载 组件内容
    public void loadContent();
    //取消 组件内容
    public void unLoadContent();
}
