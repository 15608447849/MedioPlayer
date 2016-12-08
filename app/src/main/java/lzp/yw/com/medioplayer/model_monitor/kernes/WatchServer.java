package lzp.yw.com.medioplayer.model_monitor.kernes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import lzp.yw.com.medioplayer.model_monitor.broads.WacActivitybroad;
import lzp.yw.com.medioplayer.model_monitor.broads.WatchServerBroad;
import lzp.yw.com.medioplayer.model_monitor.threads.LoopThread;
import lzp.yw.com.medioplayer.model_monitor.threads.ThreadsInterImp;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/12/8.
 *  开机打开
 *  检测 前台app 是不是 这个包
 *
 */
public class WatchServer extends Service implements ThreadsInterImp {
    private static final String TAG = "监听服务";
    private LoopThread looper;
    //bind
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        overs();
        Logs.i(TAG," onCreate --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        overs();
        Logs.i(TAG," onDestroy --");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLoopThread();
        Logs.i(TAG," onStartCommand --");
        return START_STICKY;
    }
    //打开 轮询 线程
    private void startLoopThread() {
        if (looper==null){
            looper = new LoopThread();
            looper.setCallers(this);
            looper.setSleepTime(30);
            looper.startRun();
            looper.start();
        }
    }
    private void closeLoopThread(){
        if (looper!=null){
            looper.closeRun();
            looper = null;
        }
    }

    @Override
    public void overs() {
        //监听线程要死了...
        closeLoopThread();
        startLoopThread();
    }

    @Override
    public void sendBroadUi() {
        Intent intent = new Intent();
        intent.setAction(WacActivitybroad.ACTION);
        this.sendBroadcast(intent);
    }

    @Override
    public void sendBroadWatchServer() {
        Intent intent = new Intent();
        intent.setAction(WatchServerBroad.ACTION);
        this.sendBroadcast(intent);
    }
}
