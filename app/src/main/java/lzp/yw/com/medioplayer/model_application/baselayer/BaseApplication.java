package lzp.yw.com.medioplayer.model_application.baselayer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import lzp.yw.com.medioplayer.model_command_mission.CommandPostServer;
import lzp.yw.com.medioplayer.model_communication.CommunicationServer;
import lzp.yw.com.medioplayer.model_download.DownloadServer;
import lzp.yw.com.medioplayer.model_universal.Logs;

/**
 * Created by user on 2016/10/26.
 */
public class BaseApplication extends Application{
    public static Context appContext = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.i("###################### app start ######################");
        appContext = this.getApplicationContext();
       // initStartServer();
    }


    /**
     * 初始化 打开所有服务
     */
    public void initStartServer(){
        //打开 通讯服务
        startAppServer(CommunicationServer.class);
        //打开 命令分发服务
        startAppServer(CommandPostServer.class);
        //打开 下载服务
        startAppServer(DownloadServer.class);
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
