package com.wos.play.rootdir.model_application.baselayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 2016/12/9.
 *      动态创建
 */
public class AppMessageBroad extends BroadcastReceiver{
    private static final String TAG = "AppMessageBroad";
    public static final String ACTION = "com.wos.ui.message";

    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    private BaseActivity activity;

    public AppMessageBroad(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (activity!=null){
            activity.receiveService(intent.getExtras().getString(PARAM1), intent.getExtras().getString(PARAM2));
        }
    }
}
