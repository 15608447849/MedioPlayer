package lzp.yw.com.medioplayer.wosappserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.baselayer.Logs;

/**
 * Created by user on 2016/10/28.
 * 下载进程
 */
public class LoaderServer  extends Service {
    private static String TAG ="_Loader_Server";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *  请注册在使用
     */
    public class LoaderServerReceiveNotification extends BroadcastReceiver {

        public static final String ACTION = "com.send.message.broad";
        public static final String key = "toService";
        String msg = null;
        @Override
        public void onReceive(Context context, Intent intent) {
             msg = intent.getExtras().getString(key);

            if (msg==null) return;
//            sendMsgToService(msg);
            Logs.i(TAG,"收到一个广播..."+msg);
            startTask(msg);
        }
    }

    //广播
    private LoaderServerReceiveNotification broad = null;

    /**
     * 注销广播
     */
    private void unregistSSendBroad() {
        if (broad!=null){
            getApplicationContext().unregisterReceiver(broad);
            broad = null;
           Logs.e(TAG,"注销  接受本地 -下载任务消息- 广播");
        }
    }
    /**
     * 创建时 _ 注册广播
     */
    private void registSendBroad() {
        unregistSSendBroad();
        broad = new LoaderServerReceiveNotification();
        IntentFilter filter=new IntentFilter();
        filter.addAction(LoaderServerReceiveNotification.ACTION);
        getApplicationContext().registerReceiver(broad, filter); //只需要注册一次
        Logs.e("已注册^  接受本地-下载消息- 广播");
    }

    /**
     * 创建
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        registSendBroad();
    }
    /**
     * 销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregistSSendBroad();
    }

    private ReentrantLock lock = new ReentrantLock();
    private Thread t;
    private boolean isLoading = false;
    private void startTask(final String url){

/*
        if (t == null){
            t = new Thread(){
                @Override
                public void run() {
                    while(true){
                        try {
                            if (!isLoading){ //不在下载中
                                lock.lock();
                                isLoading=true;
                                Logs.e(TAG,"-------------------------------------------开始: "+ url);
                                Thread.sleep(5*1000);
                                Logs.e(TAG,"-------------------------------------------结束: "+ url);
                                isLoading= false;
                            }
                        }catch (Exception e){
                            Logs.e(TAG," "+e.getMessage());
                        }finally{
                            lock.unlock();
                        }
                    }

                }
            };
            t.start();
        }
*/



    }


}
