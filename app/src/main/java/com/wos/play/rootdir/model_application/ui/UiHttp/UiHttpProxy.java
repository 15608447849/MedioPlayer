package com.wos.play.rootdir.model_application.ui.UiHttp;

import java.util.List;

import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.BaiduApiObject;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/11/18.
 */

public class UiHttpProxy {
    private static final String TAG = "UiHttpProxy";

    //图集 咨询等
    public static void getContent(final String url, final String broadAction) {
        try {
            Logs.i(TAG, "局部访问 - " + url);
            Observable.just(url)
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String newUrl) {

                            return AppsTools.uriTransionString(AppsTools.urlEncodeParam(newUrl), null, null);//访问URL;
                        }
                    })
                    .map(new Func1<String, String>() {

                        @Override
                        public String call(String result) {
                            return AppsTools.justResultIsBase64decode(result);
                        }
                    })
                    .map(new Func1<String, GallaryBean>() {
                        @Override
                        public GallaryBean call(String datas) {
                            if (datas != null) {
                                if (UiTools.storeContentToDirFile(url, datas)) {
                                    return AppsTools.parseJsonWithGson(datas, GallaryBean.class);
                                }
                            }
                            return null;
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
                    .toList()
                    .map(new Func1<List<UrlList>, Void>() {
                        @Override
                        public Void call(List<UrlList> urlLists) {
                            UrlList list = new UrlList();
                            for (UrlList urls : urlLists) {
                                for (CharSequence url : urls.getList()) {
                                    list.addTaskOnList(url);
                                }
                            }
                            if (list.getList().size() > 0) {
                                UiDownload.sendTaskList(list.getList());
                            }
                            return null;
                        }
                    })
                    .subscribeOn(Schedulers.io()) //执行在异步
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//回到主线程
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            //发送广播
                            UiDownload.sendTansUiComponet(broadAction);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //天气
    public static void getWeateher(final String url, final String broadAction) {
        //upsg commad ->  copy  thanks you
        try {
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
                            if (result != null) {
                                return AppsTools.justResultIsUNICODEdecode(result);
                            }
                            return null;
                        }
                    })
                    .map(new Func1<String, BaiduApiObject>() {
                        @Override
                        public BaiduApiObject call(String datas) {
                            if (datas != null) {
                                if (UiTools.storeContentToDirFile(url, datas)) { //存数据
                                    return AppsTools.parseJsonWithGson(datas, BaiduApiObject.class);
                                }
                            }
                            return null;
                        }
                    })
                    .subscribeOn(Schedulers.io()) //执行在异步
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//回到主线程
                    .subscribe(new Action1<BaiduApiObject>() {
                        @Override
                        public void call(BaiduApiObject obj) {
                            if (obj != null && obj.getErrNum() == 0 && obj.getErrMsg().equals("success")) {
                                //发送广播 刷新ui
                                UiDownload.sendTansUiComponet(broadAction);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
