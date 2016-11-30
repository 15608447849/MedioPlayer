package lzp.yw.com.medioplayer.model_application.ui.UiHttp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.model_download.kernel.DownloadBroad;

/**
 * Created by user on 2016/11/18.
 */

public class UiDownload {

    private static Context context;
    private static String terminalNo;
    private static String savepath;

   public static void init(Context context,String savepath,String terminalNo){
       UiDownload.context = context;
       UiDownload.savepath =savepath;
       UiDownload.terminalNo = terminalNo;
    }
    public static void unInit(){

        UiDownload.context = null;
        UiDownload.savepath =null;
        UiDownload.terminalNo = null;
    }

    public static void downloadTask(String actionName,ArrayList<CharSequence> list){
        sendTaskList(actionName,list);
    }

    private static Intent intent = new Intent();
    private static Bundle bundle = new Bundle();
    /**
     * 发送任务到下载服务广播
     */
    private static void sendTaskList(String action, ArrayList<CharSequence> loadingList) {
        if (context!=null){
            //发送下载任务 -> 下载服务
            bundle.clear();
            intent.setAction(DownloadBroad.ACTION);
            bundle.putCharSequenceArrayList(DownloadBroad.PARAM1,loadingList);
            bundle.putString(DownloadBroad.PARAM2, terminalNo);
            bundle.putString(DownloadBroad.PARAM3, savepath);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
            sendTansUiComponet(action);
        }
    }

    /**
     * 发送 ui组件
     */
    public static void sendTansUiComponet(String action){

        if (context!=null){
            bundle.clear();
            intent.setAction(action);
            context.sendBroadcast(intent);
        }
    }



}
