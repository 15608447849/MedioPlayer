package lzp.yw.com.medioplayer.wosappserver;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.StringTokenizer;

import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy.HttpProxyRemote;
import lzp.yw.com.medioplayer.wosappbroadcast.CommunicationBroadcast;
import lzp.yw.com.medioplayer.wostools.ToolsDataListEntity;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by user on 2016/10/27.
 * lzp
 * 遠程服務
 *
 * 在 AndroidManifest.xml 里 Service 元素的常见选项
 android:name　　-------------　　服务类名
 android:label　　--------------　　服务的名字，如果此项不设置，那么默认显示的服务名则为类名
 android:icon　　--------------　　服务的图标
 android:permission　　-------　　申明此服务的权限，这意味着只有提供了该权限的应用才能控制或连接此服务
 android:process　　----------　　表示该服务是否运行在另外一个进程，如果设置了此项，那么将会在包名后面加上这段字符串表示另一进程的名字
 android:enabled　　----------　　如果此项设置为 true，那么 Service 将会默认被系统启动，不设置默认此项为 false
 android:exported　　---------　　表示该服务是否能够被其他应用程序所控制或连接，不设置默认此项为 false
 *
 */
public class CommunicationServer extends Service{

    private static String TAG = "_CommunicationServer";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logs.e(TAG,"----------------------------------------onBind()");
        return null;
    }
//    public CommunicationServer() {
//        super();
//        Logs.e(TAG,"----------------------------------------CommunicationServer()");
//    }
    @Override
    public void onCreate() {
        Logs.e(TAG,"----------------------------------------onCreate()");
        createSubscription();
        initData();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(TAG,"----------------------------------------onStartCommand()");
        startCommd();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        Logs.e(TAG,"----------------------------------------onDestroy()");
        super.onDestroy();
        destorySubscription();
    }


    /**
     * --------------------------------------me mother------------------------------------------------------
     */

    protected CompositeSubscription mCompositeSubscription;

    //server create call
    private void createSubscription(){
        mCompositeSubscription = new CompositeSubscription();
    }
    private void destorySubscription(){
        mCompositeSubscription.unsubscribe();
    }
    private Subscriber createSubscriber(){
        return new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Logs.d(TAG,"---onCompleted()");
            }
            @Override
            public void onError(Throwable e) {
                Logs.e(TAG, "--- "+  String.valueOf(e.getMessage()));
            }
            @Override
            public void onNext(String t) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    Logs.d(TAG,"--- onNext()");
                    Logs.i(TAG," 访问服务器 结果:\n" + t);

                    processingResults(t);
                }
            }

        };
    }
    private ToolsDataListEntity datalist ;
    private HttpProxyRemote httpProxy;

    /**
     * 初始化数据
     * server create call
     */
    public void initData(){

        if (datalist==null){
            datalist = new ToolsDataListEntity();
        }
        datalist.ReadShareData();
        httpProxy = HttpProxyRemote.getInstance();
        httpProxy.initProxy(
                datalist.GetStringDefualt("serverip","127.0.0.1"),
                datalist.GetStringDefualt("serverport","8000"),
                null
                );
    }
    private Subscriber<String> subscriber ;
    private Subscriber<String> getSubscriber(){
        if (subscriber!=null){
            subscriber.unsubscribe();
            subscriber = null;
        }
        subscriber = createSubscriber();
        mCompositeSubscription.add(subscriber);
        return subscriber;
    }

    /**
     * 开始通讯
     * server start call
     */
    public void startCommd(){
        if(httpProxy == null){
            return;
        }
        String tmrID = datalist.GetStringDefualt("terminalNo","");
        Logs.d(TAG,"--- terminal id == " + tmrID);
        tmrID = "10000092";
        httpProxy.sendONFI(getSubscriber(),tmrID);
    }








    /**
     * 处理结果
     * @param t
     */
    private void processingResults(String t) {

        if (t.trim().equals("cmd:error")){
            return;
        }
        /*
         *
         VOLU:10
         SYTI:2016-10-27 12:16:35
         SHDO:false
         UPSC:http://192.168.6.14:9000/terminal/1/schedule
         */
        StringTokenizer stz = new StringTokenizer(t,"\n\r");
        String message = null;
        String cmd = null;
        String param = null;
        while(stz.hasMoreElements()){
            message = stz.nextToken();
            cmd = message.substring(0, 5);
            param = message.substring(5);
            postTask(cmd,param);
        }
    }
    /**
     * 分发任务
     */
    /**
     * 分发任务
     * @param cmd
     * @param param
     */
    private void postTask(String cmd, String param) {

        Intent i = new Intent();
        i.setAction(CommunicationBroadcast.Action);
        Bundle b = new Bundle();
        b.putString(CommunicationBroadcast.Cmd,cmd);
        b.putString(CommunicationBroadcast.Param,param);
        i.putExtras(b);
        getApplicationContext().sendBroadcast(i);
    }




}
