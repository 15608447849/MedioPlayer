package com.wos.play.rootdir.model_monitor.broads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_monitor.kernes.WatchServer;
import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by user on 2016/12/8.
 */

public class InitBootBroad extends BroadcastReceiver{
    private static final String TAG = "监听服务";
    @Override
    public void onReceive(Context context, Intent intent) {
        Logs.e(TAG,"开机 - "+intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            context.startService(new Intent(context, WatchServer.class));
            Logs.e(TAG,"-----------------------------开机启动任务完成--------------------------------");
        }
    }
}
