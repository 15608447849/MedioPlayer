package com.wos.play.rootdir.model_communication;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.wos.play.rootdir.model_application.baselayer.AppMessageBroad;
import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_command_.command_arr.Command_SYTI;
import com.wos.play.rootdir.model_command_.kernel.CommandPostBroad;
import com.wos.play.rootdir.model_download.entity.TaskFactory;
import com.wos.play.rootdir.model_download.kernel.DownloadBroad;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_report.ReportHelper;
import com.wos.play.rootdir.model_universal.httpconnect.HttpProxy;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

/**
 * 在 AndroidManifest.xml 里 Service 元素的常见选项
 * android:name　　-------------　　服务类名
 * android:label　 -------------- 　服务的名字，如果此项不设置，那么默认显示的服务名则为类名
 * android:icon　　--------------　 服务的图标
 * android:permission　　-------　　申明此服务的权限，这意味着只有提供了该权限的应用才能控制或连接此服务
 * android:process　　----------　　表示该服务是否运行在另外一个进程，如果设置了此项，那么将会在包名后面加上这段字符串表示另一进程的名字
 * android:enabled　　----------　　如果此项设置为 true，那么 Service 将会默认被系统启动，不设置默认此项为 false
 * android:exported　　---------　　表示该服务是否能够被其他应用程序所控制或连接，不设置默认此项为 false
 */
public class CommunicationServer extends Service {
    private static String TAG = "_CommunicationServer";
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.e(TAG, "--onCreate() pid: " + android.os.Process.myPid());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logs.e(TAG, "--onBind() " );
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Logs.e(TAG, "--onUnbind()");
        return super.onUnbind(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(TAG, "--onStartCommand() flags =" + flags);
        if(intent != null && intent.hasExtra("cmd")){
            String cmd = intent.getStringExtra("cmd");
            String param = intent.getStringExtra("param");
            Logs.e(TAG, "--onStartCommand() cmd =" + cmd+ " param = "+param);
            switch (cmd){
                case "online": sendTerminalOnline(); break;
                case "offline": sendTerminalOffLine(); break;
                case "getTerminalId":  getTerminalId(param);break;
                case "sendGenerateCmd": sendGenerateCmd(param);break;
                case "fileDownloadNotify": fileDownloadNotify(param);break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.e(TAG, "--onDestroy()");
        stopHeartbeat();
    }

    /**
     * 初始化
     * 如果已经配置了服务器信息
     * 发送上线指令
     */
    private void initParam() {//String otherPackage
        boolean isConfig = SystemInfos.get().isConfig();
        Logs.e(TAG, "--服务器信息 配置完成 -> " + isConfig);
        if (isConfig) { // 已设置过服务器信息
            initData();
            sendONLI(makeOnlineUri());//上线 -> 延时上线
        }
    }

    private String ip = null;
    private String port = null;
    private String terminalId = null;
    private int heartBeatTime = 0;
    private void initData() {
            ip = SystemInfos.get().getServerip();
            port = SystemInfos.get().getServerport();
            terminalId = SystemInfos.get().getTerminalNo();
            heartBeatTime = Integer.parseInt(SystemInfos.get().getHeartBeatInterval());
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
            case 2: //处理字符串   >> 发送广播
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
     * 获取终端Id
     */
    private void getTerminalId(String url) {
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

    private Thread delayThread = null;
    /**
     * 终端可以上线
     */
    private void sendTerminalOnline() {
        if (delayThread!=null){
            delayThread = null;
        }
        delayThread = new Thread(new Runnable() {
            @Override
            public void run() { Logs.i(TAG,"即将访问服务器 . . .");
                try {
                    Thread.sleep(AppsTools.randomNum(2,5)*1000);  //随机休眠5 - 10秒
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initParam();
                try {
                    Thread.sleep(AppsTools.randomNum(2,5)*1000);  //随机休眠5 - 10秒
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uploadReportDb();
                delayThread = null;
            }
        });
        delayThread.start();
    }

    private void uploadReportDb() {
        ArrayList<Task> tasks = new ArrayList<>();
        File dbFile;
        for (int i= 1; i < 10; i++){
            dbFile = getDatabasePath(ReportHelper.getDbName(i));
            Log.d(TAG, "检查本地报表数据文件是否存在："+dbFile.getAbsolutePath());
            if(dbFile.exists()){
                tasks.add(TaskFactory.gnrTaskUploadFTP(dbFile.getAbsolutePath(),"/statistics/"));
            }
        }
        if (getApplicationContext()!=null && tasks.size()>0 ){
            Intent intent = new Intent();
            intent.setAction(DownloadBroad.ACTION);
            intent.putParcelableArrayListExtra(DownloadBroad.PARAM1, tasks);
            getApplicationContext().sendBroadcast(intent);//发送广播
        }
    }

    /**
     * 下线
     */
    private void sendTerminalOffLine(){
        // sendCmd(makeOffLineUri()); 下线增加关闭心跳
        HttpProxy.getInstant().sendCmd(makeOffLineUri(), new Action1<String>() {
            @Override
            public void call(String s) {
                successAccessServer(s);
                stopHeartbeat();
            }
        });

    }


    //上线 url
    private String makeOnlineUri() {
        //http://192.168.6.14:9000/terminal/heartBeat?cmd=HRBT%3A10000555
        return generateUri("ONLI:" + terminalId);
    }
    /**
     * 下线
     */
    private String makeOffLineUri(){
        return generateUri("OFLI:" + terminalId);
    }


    /**
     * 发送通用指令
     * @param cmd
     */
    private void sendGenerateCmd(String cmd) {
        sendCmd(generateUri(cmd));
    }

    //文件下载生成url ｛ http://192.168.6.14:9000/terminal/heartBeat?cmd=HRBT%3A10000555｝
    private String generateUri(String param) {
        return "http://" + ip + ":" + port + "/terminal/heartBeat?cmd=" + param;
    }
    //文件下载调度通知
    private void fileDownloadNotify(String param) {
        sendCmd(generateFileDownLoadUrl(param));
    }


    private String generateFileDownLoadUrl(String param) {
        return generateUri(param + terminalId);
    }

    /**
     * 终端上线特别处理
     */
    private void sendONLI(final String url) {
        HttpProxy.getInstant().getTerminalId(url, new Action1<String>() {
            @Override
            public void call(String s) {
                successAccessServer(s);
                startHeartbeat();//开始心跳
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
     * 5. 发送 文件下载通知
     */
    private void sendCmd(String url) {//URLEncoder.encode
        HttpProxy.getInstant().sendCmd(url, new Action1<String>() {
            @Override
            public void call(String s) {
                successAccessServer(s);
            }
        });
    }
    //成功接入服务器
    private void successAccessServer(String result) {
        Logs.i(TAG, " 访问服务器 结果: \n" + result);
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
        if (t.trim().equals("cmd:error") || t.trim().equals("cmd:success")
                || t.trim().equals("cmd:timeout") ) {
            return;
        }
        StringTokenizer stz;
        String cmd ,param , message ;
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

    private String getHearBeatUri() {
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
                sendHearBeating();
            }
        };
        //创建定时器任务 发送心跳
        timer.schedule(timertask, heartBeatTime * 1000, heartBeatTime * 1000);
    }
    /**
     * 发送心跳
     */
    private void sendHearBeating() {
        Logs.i(TAG, "当前时间:" + Command_SYTI.getSystemTime(false) + " HRBT:" + terminalId);
        sendCmd(getHearBeatUri());
    }
}
