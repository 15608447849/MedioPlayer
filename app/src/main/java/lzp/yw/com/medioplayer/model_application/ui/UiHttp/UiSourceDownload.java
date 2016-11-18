package lzp.yw.com.medioplayer.model_application.ui.UiHttp;

import android.content.Context;

import lzp.yw.com.medioplayer.model_download.singedownload.Loader;

/**
 * Created by user on 2016/11/18.
 */

public class UiSourceDownload {
    private static Loader loader = null;
   public static void init(Context context,String savepath,String terminalNo){
       loader = new Loader(context,savepath,terminalNo);
    }

    public static void downloadTask(String url){
        if (url!=null && !url.equals("")){
            loader.LoadingUriResource(url,null);// 开始任务
        }

    }






}
