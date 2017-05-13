package com.wos.play.rootdir.model_application.ui.UiFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IAdvancedComponent;

import io.vov.vitamio.utils.Log;

/**
 * Created by user on 2016/11/19.
 * 局部刷新控件可用
 */

public class UiLocalBroad extends BroadcastReceiver{
    private String action = null;
    private IAdvancedComponent localComponent;
    private static final String TAG = UiLocalBroad.class.getSimpleName();

    public UiLocalBroad(String action, IAdvancedComponent localComponent) {
        this.action = action;
        this.localComponent = localComponent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,action);
        if (localComponent != null){
            localComponent.broadCall();
        }
    }
}
