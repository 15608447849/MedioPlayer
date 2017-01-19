package com.wos.play.rootdir.model_monitor.kernes;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wos.play.rootdir.model_application.viewlayer.ToolsActivity;
import com.wos.play.rootdir.model_monitor.tools.Stools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ShellUtils;

import static cn.trinea.android.common.util.ShellUtils.execCommand;

/**
 * Created by user on 2016/12/8.
 * 开机打开
 * 检测 前台app 是不是 指定的几个activity,不是->打开app
 * 检测 app是否存在root权限 - 存在 移动app -> system 目录下
 */
public class WatchServer extends Service {
    private static final String TAG = "守护监听服务";

    //bind
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler.post(SetAppInit);
        Logs.i(TAG, " onCreate -- 监听");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG, " onDestroy -- 监听");
    }

    /**
     * START_NOT_STICKY
     * 如果系统在onStartCommand()方法返回之后杀死这个服务，那么直到接受到新的Intent对象，这个服务才会被重新创建。这是最安全的选项，用来避免在不需要的时候运行你的服务。
     * <p>
     * START_STICKY
     * 如果系统在onStartCommand()返回后杀死了这个服务，系统就会重新创建这个服务并且调用onStartCommand()方法，但是它不会重新传递最后的Intent对象，系统会用一个null的Intent对象来调用onStartCommand()方法，在这个情况下，除非有一些被发送的Intent对象在等待启动服务。这适用于不执行命令的媒体播放器（或类似的服务），它只是无限期的运行着并等待工作的到来。
     * <p>
     * START_REDELIVER_INTENT
     * 如果系统在onStartCommand()方法返回后，系统就会重新创建了这个服务，并且用发送给这个服务的最后的Intent对象调用了onStartCommand()方法。任意等待中的Intent对象会依次被发送。这适用于那些应该立即恢复正在执行的工作的服务，如下载文件。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.i(TAG, " onStartCommand -- 监听");
        handler.postDelayed(LoopActivitys, loopTime);
        return START_NOT_STICKY;
    }



    private static List<String> activityList = null;

    static {
        activityList = new ArrayList<>();
        activityList.add("com.wos.play.rootdir.model_application.viewlayer.MainActivity");
        activityList.add("com.wos.play.rootdir.model_application.viewlayer.ToolsActivity");
        activityList.add("com.wos.play.rootdir.model_application.viewlayer.EpaperActivity");
    }

    private final int loopTime = 10 * 1000;
    private Handler handler = new Handler();
    private final Runnable LoopActivitys = new Runnable() {
        @Override
        public void run() {
            demoerExcuter();
            handler.postDelayed(LoopActivitys, loopTime);
        }
    };
    private final Runnable SetAppInit = new Runnable() {
        @Override
        public void run() {
//            tryOpenRemovePoint();
            excuteAppMoveSystem();
        }
    };


    //放入轮询线程中
    private void demoerExcuter() {
        //查看activity栈第一个
        boolean flag = Stools.isRunningForeground(getApplicationContext(), activityList);
        if (!flag) {
            //尝试打开
            Logs.e(TAG, "==================APP不在栈顶端-尝试打开==================");
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ToolsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
    }


    //放进system cmd
    public String genereteCommand(String _packagepath, String alias) {
        return "mount -o remount,rw /system" + "\n" +
//                "mount -o remount,rw /data" + "\n" +
                "mkdir /system/libtem"+"\n"+
                "cp /data/data/com.wos.play.rootdir/lib/* /system/libtem"+"\n"+
                "chmod 777 /system/libtem/*"+"\n"+
                "cp /system/libtem/* /system/lib"+"\n"+
                "rm -rf /system/libtem"+"\n"+
                "cp " + _packagepath + " /system/app/" + alias + "\n"+
                "chmod 777 /system/app/"+alias+"\n"+
//                "cp " + _packagepath + " /system/app/" + alias.substring(0,alias.lastIndexOf(".")+1)+"odex" + "\n" +
//                "mount -o remount,ro /system" + "\n";
//                "mount -o remount,ro /data"+"\n";
                "rm -rf "+_packagepath+"\n"+
                "rm -rf /data/dalvik-cache/data*"+"\n";
    }

    private static boolean isCheckAppSystem = false;
    //检测root权限 放入system
    private void excuteAppMoveSystem() {
        Logs.e(TAG,"excuteAppMoveSystem >>> ");
        if (ShellUtils.checkRootPermission() && !isCheckAppSystem) {
            ApplicationInfo appinfo = getApplicationContext().getApplicationInfo();
            String packagepath = appinfo.sourceDir;
            //如果不在system目录下  (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 //系统程序,放在/system/app
            boolean flag = ((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
            if (!flag) {    //packagepath.contains("/data/app/")
//                String alias = packagepath.substring(packagepath.lastIndexOf("/") + 1);

                String alias = "zzz.apk";
                Logs.e(TAG, "执行copy源apk路径:" + packagepath + " - 移动路径 :/sysytem/app/" + alias);
                String cmd = genereteCommand(packagepath, alias);
                Logs.e(TAG, "adb shell >>> \n[ " + cmd + " ]");
                ShellUtils.CommandResult cr=ShellUtils.execCommand(cmd,true,true);
                Logs.e(TAG,"提升权限结果:"+cr.result);
                if (cr.result == 0){
                    ShellUtils.execCommand("reboot",true);
                }
            }
            isCheckAppSystem = true;
        }
    }


    private static boolean isOpenPoint = false;
    //开启远程端口 - 方便调试
    //预留远程端口号
    private final String COMMAND_OPEN_POINT =
            "setprop service.adb.tcp.port 9999\n" +
                    "stop adbd\n" +
                    "start adbd\n";

    private void tryOpenRemovePoint() {
        Logs.e(TAG,"tryOpenRemovePoint >>> ");
        if (ShellUtils.checkRootPermission() && !isOpenPoint) {
            ShellUtils.CommandResult cr = execCommand(COMMAND_OPEN_POINT, true, true);
            Logs.d(TAG, "远程端口开启结果:" + cr.result);
            isOpenPoint = true;
        }
    }


}
