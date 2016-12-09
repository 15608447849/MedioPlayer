package com.wos.play.rootdir.model_monitor.broads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_monitor.kernes.WatchServer;
import com.wos.play.rootdir.model_monitor.tools.Stools;


/**
 * Created by user on 2016/12/8.
 *   判断 监听服务是不是存在
 *   app - 给我监听服务的
 */
public class WatchServerBroad extends BroadcastReceiver {
    private static final String TAG = "监听服务";
    public static final String ACTION = "com.watch.server";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean flag = Stools.isServiceRunning(context,"lzp.yw.com.medioplayer.model_monitor.kernels.kernes.WatchServer");
//        Log.e(TAG," flag - "+ flag);
        if (!flag){
            //尝试开启监听服务
            if (context!=null){
                context.startService(new Intent(context, WatchServer.class));
            }
        }
    }



}
