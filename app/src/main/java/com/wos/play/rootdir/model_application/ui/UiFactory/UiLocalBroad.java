package com.wos.play.rootdir.model_application.ui.UiFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IAdvancedComponent;

/**
 * Created by user on 2016/11/19.
 * 局部刷新控件可用
 */

public class UiLocalBroad extends BroadcastReceiver{
    private String action = null;
    private IAdvancedComponent loaclComponent;


    public UiLocalBroad(String action, IAdvancedComponent loaclComponent) {
        this.action = action;
        this.loaclComponent = loaclComponent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (loaclComponent!=null){
            loaclComponent.broadCall();
        }
    }
}
