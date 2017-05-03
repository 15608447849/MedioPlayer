package com.wos.play.rootdir.model_application.baselayer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_command_.kernel.CommandPostServer;
import com.wos.play.rootdir.model_communication.CommunicationServer;
import com.wos.play.rootdir.model_download.kernel.DownloadServer;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_updateapp.UpdateServer;

/**
 * Created by user on 2016/10/26.
 */
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    public static Context appContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Logs.d(TAG, "---onCreate---");
        System.out.println(TAG + "---onCreate---");
        appContext = this.getApplicationContext();
    }

    /**
     * 初始化打开所有服务
     */
    public void initStartServer(String serverName) {
        if ("all".equals(serverName)) {
            //打开监听
            //startAppServer(WatchServer.class);
            //打开 命令分发服务
            startAppServer(CommandPostServer.class);
            //打开 下载服务
            startAppServer(DownloadServer.class);
            //打开 通讯服务
            startAppServer(CommunicationServer.class);
            //打开更新app的服务
            startAppServer(UpdateServer.class);
        }
        if ("communication".equals(serverName)) {
            //通讯服务
            startAppServer(CommunicationServer.class);
        }
        if ("download".equals(serverName)) {
            //打开 下载服务
            startAppServer(DownloadServer.class);
        }
        if ("command".equals(serverName)) {
            //打开 命令分发服务
            startAppServer(CommandPostServer.class);
        }
    }

    /**
     * 初始化 打开所有服务
     */
    public void closeServer(String serverName) {
        if ("all".equals(serverName)) {
            // 通讯服务
            closeAppServer(CommunicationServer.class);
            // 命令分发服务
            closeAppServer(CommandPostServer.class);
            // 下载服务
            closeAppServer(DownloadServer.class);
            //关闭监听
            //closeAppServer(WatchServer.class);
        }
        if ("communication".equals(serverName)) {
            //通讯服务
            closeAppServer(CommunicationServer.class);
        }
        if ("download".equals(serverName)) {
            //打开 下载服务
            closeAppServer(DownloadServer.class);
        }
        if ("command".equals(serverName)) {
            //打开 命令分发服务
            closeAppServer(CommandPostServer.class);
        }
    }

    /**
     * 打开一个服务
     */
    private void startAppServer(Class<?> serverClass) {
        startService(new Intent(appContext, serverClass));
    }

    /**
     * 关闭一个服务
     */
    public void closeAppServer(Class<?> serverClass) {
        stopService(new Intent(appContext, serverClass));
    }

    interface ServerName {
        String ALL = "all";
        String COMMAND = "command";
        String DOWNLOAD = "download";
        String COMMUNICATION = "communication";
    }

}
