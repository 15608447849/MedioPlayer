package com.wos.play.rootdir.model_communication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.wos.play.rootdir.model_application.baselayer.AppMessageBroad;
import com.wos.play.rootdir.model_application.baselayer.DataListEntiyStore;
import com.wos.play.rootdir.model_command_.command_arr.Command_SYTI;
import com.wos.play.rootdir.model_command_.kernel.CommandPostBroad;
import com.wos.play.rootdir.model_universal.httpconnect.HttpProxy;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

/**
 * 在 AndroidManifest.xml 里 Service 元素的常见选项
 * android:name　　-------------　　服务类名
 * android:label　　--------------　　服务的名字，如果此项不设置，那么默认显示的服务名则为类名
 * android:icon　　--------------　　服务的图标
 * android:permission　　-------　　申明此服务的权限，这意味着只有提供了该权限的应用才能控制或连接此服务
 * android:process　　----------　　表示该服务是否运行在另外一个进程，如果设置了此项，那么将会在包名后面加上这段字符串表示另一进程的名字
 * android:enabled　　----------　　如果此项设置为 true，那么 Service 将会默认被系统启动，不设置默认此项为 false
 * android:exported　　---------　　表示该服务是否能够被其他应用程序所控制或连接，不设置默认此项为 false
 */
public class CommunicationServer extends Service {
    private static String TAG = "_CommunicationServer";
    public CommunicationServer() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.e(TAG, "----------------------------------------onCreate() pid: " + android.os.Process.myPid());
        registBroad();
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Logs.e(TAG, "----------------------------------------onBind() " );
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Logs.e(TAG, "----------------------------------------onUnbind()");
        return super.onUnbind(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(TAG, "----------------------------------------onStartCommand() flags =" + flags);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.e(TAG, "----------------------------------------onDestroy()");
        stopHeartbeat();
        unregistBroad();
    }

    private DataListEntiyStore dataList = null;
    /**
     * 初始化
     * 如果已经配置了服务器信息
     * 发送上线指令
     */
    private void initparam() {//String otherPackage
        Logs.e(TAG, " 服务器信息 配置完成 -> " + DataListEntiyStore.isSettingServerInfo(getApplicationContext()));
        if (DataListEntiyStore.isSettingServerInfo(getApplicationContext())) {
            // 已设置过服务器信息
            if (dataList == null) {
                dataList = new DataListEntiyStore(this.getApplicationContext());
                dataList.ReadShareData();
                initData();
                sendONLI(makeOnlineUri());//上线 -> 延时上线
//                startLoopHeartbeat();//开始心跳 -> 改变开始位置 在收到上线信息之后 发送心跳
            }
        }
    }




    private String ip = null;
    private String port = null;
    private String terminalId = null;
    private int heartBeatTime = 0;
    private void initData() {
        if (dataList != null) {
            ip = dataList.GetStringDefualt("serverip", "127.0.0.1");
            port = dataList.GetStringDefualt("serverport", "8000");
            terminalId = dataList.GetStringDefualt("terminalNo","0000");//"10000090";//"10000141";//"10001110";//"10000125";;//dataList.GetStringDefualt("terminalNo","0000");
            heartBeatTime = dataList.GetIntDefualt("HeartBeatInterval", 1);
        }
    }
    /**
     * 通过 广播 接受 其他 进程 发来的消息的,
     */
    private CommuniReceiverMsgBroadCasd appReceive = null;
    /**
     * 停止广播 destory call
     */
    private void unregistBroad() {
        if (appReceive != null) {
            try {
                getApplicationContext().unregisterReceiver(appReceive);
            } catch (Exception e) {
                e.printStackTrace();
            }
            appReceive = null;
            Logs.i(TAG, "注销 通讯 广播");
        }
    }
    /**
     * 注册广播  create call
     */
    private void registBroad() {
        unregistBroad();
        appReceive = new CommuniReceiverMsgBroadCasd(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommuniReceiverMsgBroadCasd.ACTION);
        getApplicationContext().registerReceiver(appReceive, filter); //只需要注册一次
        Logs.i(TAG, "已注册 通讯 广播");
    }
    /**
     * 收到一个app给我的消息到服务器
     */
    public void receiveAppMsg(String mothername, String msg) {
//        Logs.e(TAG,"反射调用 : " + mothername+"("+ msg+")");
        invokeMother(mothername, msg);
    }
    /**
     * 收到一个服务器给我的消息到app
     * <p>
     * 1 -> activity
     * 2 -> cmd post
     */
    private void receiveServerMsg(int type, String msg) {
        switch (type) {
            case 1:
                callBack(msg);
                break;
            case 2:
                //处理字符串   >> 发送广播
                processingResults(msg);
                break;
        }
    }
    //发送到activity
    private void callBack(String msg) {
        try {
            Intent i = new Intent();
            i.setAction(AppMessageBroad.ACTION);
            Bundle b = new Bundle();
            b.putString(AppMessageBroad.PARAM1, msg);
            i.putExtras(b);
            getApplicationContext().sendBroadcast(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射调用方法
     */
    private void invokeMother(String motherName, String url) {
        try {
            Method method = null;
            if (url == null) {
//              Logs.d(TAG,"invoke not param");
                method = this.getClass().getDeclaredMethod(motherName);
                method.setAccessible(true);// 调用private方法的关键一句话
                method.invoke(this);
            } else {
//              Logs.d(TAG,"invoke one param");
                method = this.getClass().getDeclaredMethod(motherName, String.class);
                method.setAccessible(true);//调用private方法的关键一句话
                method.invoke(this, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取终端Id
     */
    private void GetTerminalId(String url) {
        HttpProxy.getInstant().getTerminalId(url, new Action1<String>() {
            @Override
            public void call(String s) {
                receiveServerMsg(1, s);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                receiveServerMsg(1, "failure");
            }
        });
    }

    private Thread layeThread = null;
    /**
     * 终端可以上线
     */
    private void sendTerminaOnline() {
        if (layeThread!=null){
            layeThread = null;
        }
        layeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Logs.i(TAG,"即将访问服务器 . . .");
                try {
                    Thread.sleep(AppsTools.randomNum(2,5)*1000);  //随机休眠5 - 10秒
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initparam();
                layeThread = null;
            }
        });
        layeThread.start();
    }

    //上线 url
    private String makeOnlineUri() {
        //http://192.168.6.14:9000/terminal/heartBeat?cmd=HRBT%3A10000555
        return "http://" + ip + ":" + port + "/terminal/heartBeat?cmd=ONLI:" + terminalId;
    }
    /**
     * 文件下载进度,状态
     */
    private void fileDownloadSpeedOrState(String url) {
//        Logs.e(TAG,"call fileDownloadSpeedOrState() success ,param :"+url);
        sendCmds(generateUri(url));
    }

    //文件下载生成url
    private String generateUri(String param) {
//        http://192.168.6.14:9000/terminal/heartBeat?cmd=HRBT%3A10000555
        return "http://" + ip + ":" + port + "/terminal/heartBeat?cmd=" + param;
    }
    /**
     * 终端上线特别处理
     */
    private void sendONLI(final String url) {
        HttpProxy.getInstant().getTerminalId(url, new Action1<String>() {

            @Override
            public void call(String s) {
                successAccessServer(s);
                startLoopHeartbeat();//开始心跳
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                sendONLI(url);
            }
        });
    }
    /**
     * 1 .发送 上线指令
     * 2 .发送 心跳指令
     * 3. 发送 文件下载状态
     * 4. 发送 文件下载进度
     */
    private void sendCmds(String url) {
        //URLEncoder.encode
        HttpProxy.getInstant().sendCmd(url, new Action1<String>() {
            @Override
            public void call(String s) {
                successAccessServer(s);
            }
        });
    }
    //成功接入服务器
    private void successAccessServer(String result) {
        Logs.i(TAG, " 访问服务器 结果:\n" + result);
        if (result != null && !result.equals("")) {
            receiveServerMsg(2, result);
        }
    }
    /**
     * VOLU:10
     * SYTI:2016-10-27 12:16:35
     * SHDO:false
     * UPSC:http://192.168.6.14:9000/terminal/1/schedule
     * @param t 服务器返回值
     */
    private void processingResults(String t) {
        if (t.trim().equals("cmd:error") || t.trim().equals("cmd:success") || t.trim().equals("cmd:sucess")) {
            return;
        }
        StringTokenizer stz = null;
        String message = null;
        String cmd = null;
        String param = null;
        try {
            stz = new StringTokenizer(t, "\n\r");
            while (stz.hasMoreElements()) {
                message = stz.nextToken();
                cmd = message.substring(0, 5);
                param = message.substring(5);
                postTask(cmd, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 分发任务 -> 任务处理进程
     * @param cmd
     * @param param
     */
    private void postTask(String cmd, String param) {
        try {
            Intent i = new Intent();
            i.setAction(CommandPostBroad.ACTION);
            Bundle b = new Bundle();
            b.putString(CommandPostBroad.PARAM1, cmd);
            b.putString(CommandPostBroad.PARAM2, param);
            i.putExtras(b);
            getApplicationContext().sendBroadcast(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 开始轮询
     * 定时器
     */
    private void startLoopHeartbeat() {
        startHeartbeat();
    }
    private String getHearbeatUri() {
        //http://localhost:9000/terminal/heartBeat?cmd=HRBT%3A100000001
        return "http://" + ip + ":" + port + "/terminal/heartBeat?cmd=HRBT:" + terminalId;
    }
    //发送心跳定时器
    private Timer timer = null;
    private TimerTask timertask = null;
    //关闭心跳
    private void stopHeartbeat() {
        if (timer != null) {
            //关闭定时器
            timer.cancel();
            timer = null;
        }
        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
    }
    //开启 心跳
    private void startHeartbeat() {
        stopHeartbeat();
        timer = new Timer();
        timertask = new TimerTask() {
            @Override
            public void run() {
                sendHearbeating();
            }
        };
        //创建定时器任务 发送心跳
        timer.schedule(timertask, heartBeatTime * 1000, heartBeatTime * 1000);
    }
    /**
     * 发送心跳
     */
    private void sendHearbeating() {
        Logs.i(TAG, "当前时间:" + Command_SYTI.getSystemTime(false) + " HRBT:" + terminalId);
        sendCmds(getHearbeatUri());
    }
}
