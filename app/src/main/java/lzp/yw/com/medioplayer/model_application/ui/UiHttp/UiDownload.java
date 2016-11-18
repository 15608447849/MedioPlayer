package lzp.yw.com.medioplayer.model_application.ui.UiHttp;

import android.content.Context;

import lzp.yw.com.medioplayer.model_download.singedownload.Loader;

/**
 * Created by user on 2016/11/18.
 */

public class UiDownload {

    private static String terminalNo;
    private static String savepath;
   public static void init(Context context,String savepath,String terminalNo){
       UiDownload.savepath =savepath;
       UiDownload.terminalNo = terminalNo;
    }
    public static void unInit(){
        UiDownload.savepath =null;
        UiDownload.terminalNo = null;
    }

    public static void downloadTask(String url){
        if (url!=null && !url.equals("")){
            new Loader(null,savepath,terminalNo).LoadingUriResource(url,null);// 开始任务
        }

    }






}
