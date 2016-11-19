package lzp.yw.com.medioplayer.model_application.ui.UiHttp;

import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;
import lzp.yw.com.medioplayer.model_download.entity.UrlList;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/11/18.
 */

public class UiHttpProxy {


    public static void getContent(final String url,final String broadAction){
        Observable.just(url)
                .map(new Func1<String, String>() {
            @Override
            public String call(String newUrl) {
                System.out.println("局部 访问 - "+url );
                return AppsTools.uriTranslationString(newUrl);//访问URL;
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
                        if (datas!=null){
                            if (UiTools.storeContentToDirFile(url,datas)){
                                return AppsTools.parseJsonWithGson(datas,GallaryBean.class);
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
                        if (dataObjsBean.getUrls()!=null && !dataObjsBean.getUrls().equals("")){
                            //切割字符串
                            String [] urlarr = dataObjsBean.getUrls().split(",");
                            for(int i=0;i<urlarr.length;i++){
                                listObj.addTaskOnList(urlarr[i]);
                            }
                        }
                        return listObj;
                    }
                })
                .toList()
         .subscribeOn(Schedulers.io()) //执行在异步
         .unsubscribeOn(Schedulers.io())
//          .observeOn(AndroidSchedulers.mainThread())//回到主线程
          .subscribe(new Action1<List<UrlList>>() {
              @Override
              public void call(List<UrlList> urlLists) {
                  UrlList list = new UrlList();
                  for (UrlList urls:urlLists){
                      for (CharSequence url:urls.getList()){
                          list.addTaskOnList(url);
                      }
                  }
                  if (list.getList().size()>0){
                      UiDownload.downloadTask(broadAction,list.getList());
                  }
              }
          });




    }
//
//    private static void parseConentDownload(String result) {
//        GallaryBean gallaryBean = AppsTools.parseJsonWithGson(result,GallaryBean.class);
//
//        if (gallaryBean!=null){
//            for (DataObjsBean data : gallaryBean.getDataObjs()){
//                parseData(data);
//            }
//        }
//    }
//
//    private static void parseData(DataObjsBean dataobj) {
//        UiDownload.downloadTask(dataobj.getUrl());
//        if (dataobj.getUrls()!=null && !dataobj.getUrls().equals("")){
//            //切割字符串
//            parseContentsUrisContent(dataobj.getUrls());
//        }
//    }
//
//
//    /**
//     *  多个url
//     * @param urls
//     */
//    private static void parseContentsUrisContent(String urls) {
//        try {
//            String [] urlarr = urls.split(",");
//            for(int i=0;i<urlarr.length;i++){
//                UiDownload.downloadTask(urlarr[i]);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
