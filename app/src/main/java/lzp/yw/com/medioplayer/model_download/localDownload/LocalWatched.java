package lzp.yw.com.medioplayer.model_download.localDownload;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.model_download.singedownload.Loader;
import lzp.yw.com.medioplayer.model_download.singedownload.LoaderResultCall;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/19.
 */

public class LocalWatched implements Observer {
    private static final String TAG ="downloadQueueMagene";
    //同步锁
    private ReentrantLock lock = new ReentrantLock();
    private Context c;
    public LocalWatched(Context c){
        this.c = c;
    }
    private ArrayList<Object> objarr;
    @Override
    public void update(Observable observable, Object data) {
        try {
            lock.lock();
            //data  -> Arraylist<Object> 0广播action 1被取出来的 一个 下载队列 2文件保存路径 3.终端编号-> 执行
            objarr = (ArrayList<Object>)data;

            if (objarr.get(1)==null){
                throw new Exception("--  download list is null  --");
            }

            downloadAction(
                    (String) objarr.get(0),
                    (ArrayList<CharSequence>)objarr.get(1),
                    (String)objarr.get(2),
                    (String) objarr.get(3)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    private String notifyBroadAction;
    private Loader loader = null;
    private int sumCount = -1;
    private int successCount = -1;
    //下载
    private void downloadAction(String action,ArrayList<CharSequence> list,String savepath,String terminalNo) {
        notifyBroadAction = action;
        sumCount = -1;
        successCount = 0;
        Log.i(TAG,"收到一个 下载任务, 队列大小:"+list.size());
        sumCount = list.size();
        for (CharSequence url : list){
            loader = new Loader(c,savepath,terminalNo);
            loader.settingCaller(call);//设置回调
            loader.LoadingUriResource((String)url,null);// 开始任务
        }
    }


    //下载回调
    private LoaderResultCall call = new LoaderResultCall() {
        Intent i = new Intent();
        @Override
        public void downloadResult(String filePath) {
            //下载完成回调
            Logs.i(TAG,"当前成功数量 :["+ successCount++ +"] ,sumCount:["+sumCount+"] \n result: "+ filePath);
            if (successCount == sumCount){
                Log.i(TAG,"________________________下载任务完成 发送通知-保存数据_______________________");
                //发送完成通知
                i.setAction(notifyBroadAction);
                c.sendBroadcast(i);
            }
        }
    };
}
