package lzp.yw.com.medioplayer.model_download;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.model_download.downloadTools.DownloadQueueTask;
import lzp.yw.com.medioplayer.model_universal.Logs;

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
        Logs.i(TAG,"****************************************onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.i(TAG,"****************************************onCreate");
        registBroad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG,"****************************************onDestroy");
        unregistBroad();
    }
    /*--------------------------------------------------------------------------------------------------------------------------------*/


    // 通过广播 接受内容
    public void receiveContent(ArrayList<CharSequence> TaskList,String savepath ,String terminalNo ){
        DownloadQueueTask.getManage(getApplication()).addItemToStore(TaskList,savepath,terminalNo);
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
