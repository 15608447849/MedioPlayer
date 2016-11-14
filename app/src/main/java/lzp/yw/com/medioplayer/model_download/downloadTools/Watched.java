package lzp.yw.com.medioplayer.model_download.downloadTools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.model_command_.CommandPostBroad;
import lzp.yw.com.medioplayer.model_download.singedownload.Loader;
import lzp.yw.com.medioplayer.model_download.singedownload.LoaderResultCall;
import lzp.yw.com.medioplayer.model_universal.CMD_INFO;

/**
 * Created by user on 2016/11/3.
 * 观察者
 */

public class Watched implements Observer{
    //同步锁
    private ReentrantLock lock = new ReentrantLock();
    private Context c;
    public Watched(Context c){
        this.c = c;
    }
    private Object[] objarr;

    @Override
    public void update(Observable observable, Object data) {

        try {
            lock.lock();
            //data  -> Object[] 0被取出来的 一个 下载队列 -> 执行  1 文件保存路径 3.终端编号
            objarr = (Object[])data;

            if (objarr[0]==null){
              throw new Exception("--  download list is null  --");
            }

            downloadAction((ArrayList<CharSequence>)objarr[0],(String)objarr[1],(String)objarr[2]);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    private Loader loader = null;
    private int sumCount = -1;
    private int successCount = -1;
    private LoaderResultCall call = new LoaderResultCall() {
        @Override
        public void downloadResult(String filePath) {
            //下载完成回调
            Log.i("","当前成功数量 :["+ successCount++ +"] ,sumCount:["+sumCount+"] \n result: "+ filePath);

            if (successCount == sumCount){
                Log.i("","________________________任务完成 发送通知_______________________");
                //发送完成通知
                Intent i = new Intent();
                i.setAction(CommandPostBroad.ACTION);
                Bundle b = new Bundle();
                b.putString(CommandPostBroad.PARAM1, CMD_INFO.SORE);
                i.putExtras(b);
                c.sendBroadcast(i);
            }
        }
    };
    //下载
    private void downloadAction(ArrayList<CharSequence> list,String savepath,String terminalNo) {
        sumCount = -1;
        successCount = 0;
        Log.i("","收到一个 下载任务, 队列大小:"+list.size());
        sumCount = list.size();
        for (CharSequence url : list){
            loader = new Loader(c,savepath,terminalNo);
            loader.settingCaller(call);//设置回调
            loader.LoadingUriResource((String)url,null);// 开始任务
        }
    }
}