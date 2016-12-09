package com.wos.play.rootdir.model_monitor.broads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import com.wos.play.rootdir.model_application.viewlayer.ToolsActivity;
import com.wos.play.rootdir.model_monitor.tools.Stools;
import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by user on 2016/12/8.
 */

public class WacActivitybroad extends BroadcastReceiver {
    private static final String TAG = "监听服务";
    public static final String ACTION = "com.ui.broad.yws";
    private List<String> activityList = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (activityList==null){
            init();
        }

        //查看activity栈第一个
        boolean flag = Stools.isRunningForeground(context,activityList);
//        Logs.i(TAG,"ui watch broad - "+flag);

        if (context == null) {
            Logs.i(TAG," ui watch broad context is null");
            return;
        }

        if (intent == null) {
            intent = new Intent();
        }
        if (flag) {
            //告诉监听服务 广播
            intent.setAction(WatchServerBroad.ACTION);
            context.sendBroadcast(intent);
        } else {
            //尝试打开
            Logs.e(TAG, "APP 不在 栈 顶端 - 尝试打开");
           intent.setClass(context,ToolsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


    }

    private void init() {
        activityList = new ArrayList<>();
        activityList.add("lzp.yw.com.medioplayer.model_application.viewlayer.MainActivity");
        activityList.add("lzp.yw.com.medioplayer.model_application.viewlayer.ToolsActivity");
    }

}
