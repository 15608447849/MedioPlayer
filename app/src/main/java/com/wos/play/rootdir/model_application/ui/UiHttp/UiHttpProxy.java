package com.wos.play.rootdir.model_application.ui.UiHttp;

import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.BaiduApiObject;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.wos.play.rootdir.model_application.ui.Uitools.UiTools.storeContentToDirFile;

/**
 * Created by user on 2016/11/18.
 *  单例化
 */
public class UiHttpProxy {
    private static final String TAG = "_UiHttpProxy";

    private Context context;

    private static UiHttpProxy proxy;
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
        this.context = context;
    }
    //消亡
    public void unInit(){
        this.context = null;
    }

    private Intent intent = new Intent();
    private ReentrantLock lock = new ReentrantLock();
    //发送指定广播
    private void sendUiComponet(String uiAction) {
        if (context!=null){
            intent.setAction(uiAction);
            context.sendBroadcast(intent);
        }
    }


    //图集 咨询等
    public void getContent(final String url, final String broadAction) {
        try {
            lock.lock();
            Logs.i(TAG, "局部访问 - " + url);
            Observable
                    .just(url)
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String newUrl) {
                            return AppsTools.uriTransionString(AppsTools.urlEncodeParam(newUrl), null, null);//访问URL;
                        }
                    })
                    .map(new Func1<String, String>() {

                        @Override
                        public String call(String result) {
                            return AppsTools.justResultIsBase64decode(result);//base64 解密
                        }
                    })
                    .map(new Func1<String, GallaryBean>() {
                        @Override
                     public GallaryBean call(String datas) {
                            UiTools.storeContentToDirFile(url, datas);//数据存储
                        return  AppsTools.parseJsonWithGson(datas, GallaryBean.class);
                        }
                    })
                    .flatMap(new Func1<GallaryBean, Observable<DataObjsBean>>() {
                        @Override
                        public Observable<DataObjsBean> call(GallaryBean gallaryBean) {
                            return Observable.from(gallaryBean.getDataObjs());
                        }
                    })
                    .map(new Func1<DataObjsBean, UrlList>() {
                        @Override
                        public UrlList call(DataObjsBean dataObjsBean) {
                            UrlList listObj = new UrlList();

                            listObj.addTaskOnList(dataObjsBean.getUrl());

                            if (dataObjsBean.getUrls() != null && !dataObjsBean.getUrls().equals("")) {
                                //切割字符串
                                String[] urlarr = dataObjsBean.getUrls().split(",");

                                for (int i = 0; i < urlarr.length; i++) {
                                    listObj.addTaskOnList(urlarr[i]);
                                }
                            }

                            return listObj;
                        }
                    })
                    .toList()// 合并
                    .map(new Func1<List<UrlList>, UrlList>() {
                        @Override
                        public UrlList call(List<UrlList> urlLists) {
                            UrlList list = new UrlList(context);
                            for (UrlList urls : urlLists) {
                                for (Task task : urls.getList()) {
                                    list.addTaskOnList(task);
                                }
                            }
                            return list;
                        }
                    })
                    .subscribeOn(Schedulers.io()) //执行在异步
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//回到主线程

                    .subscribe(new Observer<UrlList>() {
                        @Override
                        public void onCompleted() {
                            //发送广播 - 通知组件
                            sendUiComponet(broadAction);
                        }

                        @Override
                        public void onError(Throwable e) {
                                e.printStackTrace();
                        }

                        @Override
                        public void onNext(UrlList list) {
                                    list.sendTaskToRemote();
                                    list.destory();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }




    //天气
    public void getWeateher(final String url, final String broadAction) {
        //upsg commad ->  copy  thanks you
        try {
            lock.lock();
            Logs.i(TAG, "局部访问 - " + url);
            Observable.just(url)
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String url) {
                            return AppsTools.uriTransionString(url, AppsTools.baiduApiMap(), null);
                        }
                    })
                    .map(new Func1<String, String>() {

                        @Override
                        public String call(String result) {
                                return AppsTools.justResultIsUNICODEdecode(result);
                        }
                    })
                    .map(new Func1<String, BaiduApiObject>() {
                        @Override
                        public BaiduApiObject call(String datas) {

                                 //存数据
                                    storeContentToDirFile(url, datas);
                                    return AppsTools.parseJsonWithGson(datas, BaiduApiObject.class);
                    }})
                    .subscribeOn(Schedulers.io()) //执行在异步
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//回到主线程
                    .subscribe(new Action1<BaiduApiObject>() {
                        @Override
                        public void call(BaiduApiObject obj) {
                            if (obj != null && obj.getErrNum() == 0 && obj.getErrMsg().equals("success")) {
                                //发送广播 刷新ui
                                sendUiComponet(broadAction);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
