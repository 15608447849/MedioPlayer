package com.wos.play.rootdir.model_monitor.soexcute;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by 79306 on 2017/3/8.
 */

public class WatchServerHelp extends Service {
    private static final String TAG = "Clibs";
    public static final String DEAMS_KEY = "keys";
    public static final int OPEN_DEAMS = 666;
    public static final int CLOSE_DEAMS = 777;
    public static final int CLOSE_DEAMS_ALL = 888;
    public static final int RESET_DEAMS = 999;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            int type = intent.getIntExtra(DEAMS_KEY,-1);
            if (type == OPEN_DEAMS){
                open();
            }
            if (type == CLOSE_DEAMS){
                close();
            }
            if (type == CLOSE_DEAMS_ALL){
                closeAll();
            }
            if (type == RESET_DEAMS){
                openAll();
            }
        }

        return START_NOT_STICKY;//super.onStartCommand(intent, flags, startId);
    }



    private void open() {
        //获取包名
        String packageName = this.getPackageName();
        String temPath = "/mnt/sdcard";//createRootPath(this);
        String watchServerPath = "am startservice --user 0 "+packageName+"/com.wos.play.rootdir.model_monitor.kernes.WatchServer";
        String activityComd = "am start --user 0 -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n "+packageName+"/com.wos.play.rootdir.model_application.viewlayer.ToolsActivity";
        RunJniHelper.getInstance().startMservice(watchServerPath,activityComd,temPath+"/mpcpid",temPath+"/mpclog",30);
    }
    private void close() {
        RunJniHelper.getInstance().stopMservice(createRootPath(this));
    }
    private void openAll() {
        RunJniHelper.getInstance().liveAll(createRootPath(this));
    }
    private void closeAll() {
        RunJniHelper.getInstance().killAll(createRootPath(this));
    }


    /**
     * sd卡是否可用
     *
     * @return
     */
    private static boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String createRootPath(Context context ) {
        String cacheRootPath = "/mnt/sdcard/wosplayer";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static void openDeams(Context content) {
        Log.e(TAG,"准备打开守护进程服务!");
        Intent intent = new Intent(content, WatchServerHelp.class);
        intent.putExtra(WatchServerHelp.DEAMS_KEY, WatchServerHelp.OPEN_DEAMS);
        content.startService(intent);
    }
    public static void closeDeams(Context content) {
        Log.e(TAG,"准备关闭守护进程服务!");
        Intent intent = new Intent(content, WatchServerHelp.class);
        intent.putExtra(WatchServerHelp.DEAMS_KEY, WatchServerHelp.CLOSE_DEAMS);
        content.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = new Notification();
        startForeground(1, notification);
    }
}
