package com.wos.play.rootdir.model_monitor.kernes;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wos.play.rootdir.model_application.viewlayer.ToolsActivity;
import com.wos.play.rootdir.model_monitor.tools.Stools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by user on 2016/12/8.
 * 开机打开
 * 检测 前台app 是不是 指定的几个activity,不是->打开app
 * 检测 app是否存在root权限 - 存在 移动app -> system 目录下
 */
public class WatchServer extends Service {
    private static final String TAG = "Clibs守护监听服务";
//    private Handler handler = new Handler();
    private static boolean isCheck = true;
    //bind
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.i(TAG, "onCreate -- 监听");
        if (isCheck){
            excuteAppMoveSystem();
            ckeckConfigPath();
            isCheck = false;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG, "onDestroy -- 监听");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.i(TAG, "onStartCommand -- 监听"+android.os.Process.myPid());
        startWatch();
        return START_NOT_STICKY;
    }
    public static List<String> activityList = null;
    static {
        activityList = new ArrayList<>();
        activityList.add("com.wos.play.rootdir.model_application.viewlayer.MainActivity");
        activityList.add("com.wos.play.rootdir.model_application.viewlayer.ToolsActivity");
        activityList.add("com.wos.play.rootdir.model_application.viewlayer.EpaperActivity");
    }
    private void startWatch() {
        Logs.i(TAG," <<<<<<<<<<<<<< - - 开始监听 - - >>>>>>>>>>>>");
        demoerExcuter();
    }
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
    public String genereteCommand(String _packagename,String _packagepath, String alias) {
        StringBuffer sb = new StringBuffer();
            sb.append("mount -o remount,rw /system" + "\n");
            sb.append("mkdir /system/woslib\n");
            sb.append( "cp /data/data/"+_packagename+"/lib/* /system/woslib"+"\n");//移动so文件
            sb.append( "cp /system/woslib/* /system/lib"+"\n");//移动so文件
            sb.append("chmod 777 /system/lib/*"+"\n");//+ //赋予权限
            sb.append("cp " + _packagepath + " /system/app/" + alias + "\n");
            sb.append("chmod 777 /system/app/"+alias+"\n");
            sb.append( "rm -rf "+_packagepath+"\n");
            sb.append("rm -rf /data/dalvik-cache/data* \n");
        return  sb.toString();
    }


    //检测root权限 放入system
    private void excuteAppMoveSystem() {
        if (ShellUtils.checkRootPermission()) {
            Logs.e(TAG,"判断是否放入系统目录.");
            ApplicationInfo appinfo = getApplicationContext().getApplicationInfo();
            String packagepath = appinfo.sourceDir;//包路径
            String packageName = appinfo.packageName; // 包名
            String alias = "wosterminal.apk";
            if ( (appinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 || FileUtils.isFileExist("/system/app/"+alias) ) {
                Logs.e(TAG,"####### 已存在/system/app/"+alias+" #######");
                return;
            }
                Logs.e(TAG, "源apk路径:" + packagepath + " - 移动路径 :/sysytem/app/" + alias);
                String cmd = genereteCommand(packageName,packagepath, alias);
                Logs.e(TAG, "adb command:\n[" + cmd + "]");
                ShellUtils.CommandResult cr = ShellUtils.execCommand(cmd,true,true);
                Logs.e(TAG,"提升权限结果:"+cr.result);
                if (cr.result == 0){
                    ShellUtils.execCommand("reboot",true);
                }
        }else{
            Logs.e(TAG,"没有 root 权限. ");
        }
    }






    //检查设置配置文件根目录
    //检查是  system 还是 data 下面.
    //执行文件目录判断类
    private void ckeckConfigPath(){
       String dirPath = SdCardTools.getDircConfigPath(getApplicationContext());
       String storeDir = SdCardTools.getAppSourceDir(getApplicationContext());
       boolean isExist =  FileUtils.isFileExist(dirPath);
        if (isExist){
            //如果文件存在
            //获取文件的资源存储目录路径
            //判断和检测的路径是不是一样,不一样再处理
            String rstoreDir = FileUtils.readFile(dirPath,"utf-8").toString();
            if (!rstoreDir.equals(storeDir)){
                Logs.e(TAG,"在["+ dirPath +"]中保存资源文件目录'路径' - ["+rstoreDir +"] - 与检测值不一致.检测值:["+storeDir+"]");
            }
        }else{
            //如果文件不存在
            //保存文件资源目录路径字符串
            boolean f = FileUtils.writeFile(dirPath,storeDir);
            Logs.i(TAG,"在["+ dirPath +"]中保存资源文件目录'路径' - ["+storeDir +"] - "+ (f?"成功":"失败"));
        }
    }




}
