package com.wos.play.rootdir.model_application.baselayer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_command_.kernel.CommandPostServer;
import com.wos.play.rootdir.model_communication.CommunicationServer;
import com.wos.play.rootdir.model_download.kernel.DownloadServer;
import com.wos.play.rootdir.model_monitor.kernes.WatchServer;

/**
 * Created by user on 2016/10/26.
 */
public class BaseApplication extends Application{
    public static Context appContext = null;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this.getApplicationContext();
    }
    /**
     * 初始化打开所有服务
     */
    public void initStartServer(String serverName){
        if ("all".equals(serverName)){
            //打开 命令分发服务
            startAppServer(CommandPostServer.class);
            //打开 下载服务
            startAppServer(DownloadServer.class);
            //打开 通讯服务
            startAppServer(CommunicationServer.class);
            //打开监听
           //startAppServer(WatchServer.class);
        }
        if ("communication".equals(serverName)){
            //通讯服务
            startAppServer(CommunicationServer.class);
        }
        if ("download".equals(serverName)){
            //打开 下载服务
            startAppServer(DownloadServer.class);
        }
        if ("command".equals(serverName)){
            //打开 命令分发服务
            startAppServer(CommandPostServer.class);
        }
    }
    /**
     * 初始化 打开所有服务
     */
    public void closeServer(String serverName){
        if ("all".equals(serverName)){
            // 通讯服务
            closeAppServer(CommunicationServer.class);
            // 命令分发服务
            closeAppServer(CommandPostServer.class);
            // 下载服务
            closeAppServer(DownloadServer.class);
            //关闭监听
            closeAppServer(WatchServer.class);
        }
        if ("communication".equals(serverName)){
            //通讯服务
            closeAppServer(CommunicationServer.class);
        }
        if ("download".equals(serverName)){
            //打开 下载服务
            closeAppServer(DownloadServer.class);
        }
        if ("command".equals(serverName)){
            //打开 命令分发服务
            closeAppServer(CommandPostServer.class);
        }
    }

    /**
     * 打开一个服务
     */
    private void startAppServer(Class<?> serverClass) {
        startService(new Intent(appContext,serverClass));
    }

    /**
     * 关闭一个服务
     */
    public void closeAppServer(Class<?> serverClass){
        stopService(new Intent(appContext,serverClass));
    }





}
