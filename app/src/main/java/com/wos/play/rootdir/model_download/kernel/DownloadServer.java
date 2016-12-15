package com.wos.play.rootdir.model_download.kernel;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.wos.play.rootdir.model_download.override_download_mode.LoaderHelper;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_download.override_download_mode.TaskQueue;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;

/**
 * lzp
 * 1. 维护下载 队列集合
 * 2. 同步锁
 * 3. 通过一个广播 接受下载任务队列
 */
public class DownloadServer extends Service {
    private static final String TAG = "_download";

    public DownloadServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.i(TAG,"下载 onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.i(TAG,"下载 onCreate");
        registBroad();
        TaskQueue.getInstants().init(getApplicationContext(), LoaderHelper.DOWNLOAD_MODE_CONCURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG,"下载 onDestroy");
        unregistBroad();
        TaskQueue.getInstants().unInit();
    }
    /*--------------------------------------------------------------------------------------------------------------------------------*/


    // 接收全局 下载任务内容
    public void receiveContent(ArrayList<Task> taskList){
        Logs.i(TAG,"下载任务数量 - receiveContent() - >>> "+ taskList.size());
        for (int i = 0;i<taskList.size();i++){
            TaskQueue.getInstants().addTask(taskList.get(i));
        }
    }





    /**
     *通过 广播 接受 其他 进程 发来的消息的,
     */
    private DownloadBroad appReceive = null;

    /**
     * 停止广播 destory call
     */
    private void unregistBroad() {
        if (appReceive!=null){
            getApplicationContext().unregisterReceiver(appReceive);
            appReceive = null;
            Logs.i(TAG,"注销 下载 广播");
        }
    }
    /**
     * 注册广播  create call
     */
    private void registBroad() {
        unregistBroad();
        appReceive = new DownloadBroad(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction(DownloadBroad.ACTION);
        getApplicationContext().registerReceiver(appReceive, filter); //只需要注册一次
        Logs.i(TAG,"已注册 下载 广播");
    }
}
