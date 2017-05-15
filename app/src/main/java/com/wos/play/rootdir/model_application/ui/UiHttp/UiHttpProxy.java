package com.wos.play.rootdir.model_application.ui.UiHttp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_command_.kernel.CommandPostBroad;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.OtweatherBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.wos.play.rootdir.model_universal.tool.AppsTools.uriTransionString;

/**
 * Created by user on 2016/11/18.
 *  单例化
 */
public class UiHttpProxy{
    private static final String TAG = "_UiHttpProxy";

    private Context context;
    private Handler handler ;



    private boolean isInit = false;
    private static UiHttpProxy proxy;
    private ExecutorService executor;

    private UiHttpProxy(){

    }
    public static UiHttpProxy getPeoxy(){
        if (proxy==null){
            proxy = new UiHttpProxy();
        }
        return proxy;
    }
    //初始化
    public void init(Context context){

        if (!isInit) {
            this.context = context;
            handler = new Handler();
            executor = Executors.newCachedThreadPool();
            intent = new Intent();
            isInit = true;
            Logs.i(TAG,"初始化 - UI网络代理类 - 完成");
        }
    }
    //消亡
    public void unInit(){
        if (isInit) {
            Logs.i(TAG,"注销 - UI网络访问类");
            if (executor!=null){
                try {
                    executor.shutdown();
                    if(!executor.awaitTermination(2 * 1000, TimeUnit.MILLISECONDS)){
                        //超时的时候向线程池中所有的线程发出中断(interrupted)。
                        executor.shutdownNow();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    executor=null;
                }
            }
            this.context = null;
            intent = null;
            bundle = null;
            isInit = false;
        }
    }

    private Intent  intent;
    private Bundle bundle;
    private ReentrantLock lock = new ReentrantLock();
    //发送指定广播
    private void sendUiComponet(String uiAction) {
        if (context!=null){
            intent.setAction(uiAction);
            context.sendBroadcast(intent);
        }
    }

    private void sendTaskList(ArrayList<Task> list) {
        if (context!=null && list!=null && list.size()>0){
            if (bundle==null){
                bundle = new Bundle();
            }else{
                bundle.clear();
            }
            intent.setAction(CommandPostBroad.ACTION);
            bundle.putParcelableArrayList(CommandPostBroad.PARAM3,list);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }
    }

    public static final int GALLERY_TYPE = 0;
    public static final int NEWS_TYPE = 1;
    public static final int WEATHRE_TYPE = 2;


    public void update(final String url,final String action, final int type){
        if (executor!=null && !executor.isShutdown()){
            Logs.i(TAG, "UI - 网络访问操作  - [" + url+"]" +" current thread - "+Thread.currentThread().getName() );
            if (url==null)  return;
            executor.execute(new Runnable() {
                public void run() {
                    try {
                       switch (type){
                           case GALLERY_TYPE:
                           case NEWS_TYPE:
                               func1(url,action);
                               break;
                           case WEATHRE_TYPE:
                               func2(url,action);
                               break;
                       }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
      }
    }

    private void func1(String url, String action) {

        try {
            String result = uriTransionString(AppsTools.urlEncodeParam(url), null, null);//访问URL;
            if (result==null) return;
            result = AppsTools.justResultIsBase64decode(result);//base64 解密
            if (result==null) return;
            UiTools.storeContentToDirFile(url, result);//数据存储

            GallaryBean gallaryBean = AppsTools.parseJsonWithGson(result, GallaryBean.class);
            if (gallaryBean==null) return;
            List<DataObjsBean> dataObjList = gallaryBean.getDataObjs();
            if (dataObjList!=null && dataObjList.size()>0) {
                UrlList listObj = new UrlList();
                for (DataObjsBean datas : dataObjList) {

                    //------------图集资讯添加下载缩略图--------
                    if (datas.getImageUrl() != null && !"".equals(datas.getImageUrl()))
                        listObj.addTaskOnList(datas.getImageUrl());

                    listObj.addTaskOnList(datas.getUrl());
                    if (datas.getUrls() != null && !datas.getUrls().equals("")) {
                        //切割字符串
                        String[] urlarr = datas.getUrls().split(",");

                        for (int i = 0; i < urlarr.length; i++) {
                            listObj.addTaskOnList(urlarr[i]);
                        }
                    }
                }
                sendTaskList(listObj.getList());
                sendAction(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAction(final String action) {
        try {
            lock.lock();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //发送广播 - 通知组件
                    sendUiComponet(action);
                }
            });
        } finally {
            lock.unlock();
        }
    }


    private void func2(String url, final String action) {

        try {
            String result = uriTransionString(url, null, null);
            if (result==null) return;
            result = AppsTools.getJsonStringFromGZIP(result);
            OtweatherBean obj = AppsTools.parseJsonWithGson(result, OtweatherBean.class);
            if (obj != null && obj.getStatus() == 1000 && obj.getDesc().equals("OK")) {
                UiTools.storeContentToDirFile(url, result);
                //发送广播 刷新ui
                sendAction(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
