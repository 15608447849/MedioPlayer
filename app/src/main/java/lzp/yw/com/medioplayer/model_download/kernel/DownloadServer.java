package lzp.yw.com.medioplayer.model_download.kernel;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.model_download.override_download_mode.LoaderHelper;
import lzp.yw.com.medioplayer.model_download.override_download_mode.Task;
import lzp.yw.com.medioplayer.model_download.override_download_mode.TaskQueue;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

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
        Logs.i(TAG,"**************************************** onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.i(TAG,"**************************************** onCreate");
        registBroad();
        TaskQueue.getInstants().init(getApplicationContext(), LoaderHelper.DOWNLOAD_MODE_CONCURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG,"**************************************** onDestroy");
        unregistBroad();
        TaskQueue.getInstants().unInit();
    }
    /*--------------------------------------------------------------------------------------------------------------------------------*/


    // 接收全局 下载任务内容
    public void receiveContent(ArrayList<CharSequence> TaskList,String savepath ,String terminalNo ){
        Logs.i(TAG,"初始化任务队列 - >>>> " + TaskList);
        for (int i = 0;i<TaskList.size();i++){
            TaskQueue.getInstants().addTask(new Task(savepath,terminalNo,(String)TaskList.get(i),null));
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
