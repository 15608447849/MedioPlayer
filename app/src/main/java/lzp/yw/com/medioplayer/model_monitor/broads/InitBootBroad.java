package lzp.yw.com.medioplayer.model_monitor.broads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lzp.yw.com.medioplayer.model_monitor.kernes.WatchServer;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

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
