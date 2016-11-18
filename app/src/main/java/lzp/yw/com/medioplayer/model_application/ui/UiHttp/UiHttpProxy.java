package lzp.yw.com.medioplayer.model_application.ui.UiHttp;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.AppsTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/11/18.
 */

public class UiHttpProxy {


    public static void getContent(final String url, final UiHttpResult reresult){
        Observable.just(url)
                .map(new Func1<String, String>() {
            @Override
            public String call(String newUrl) {
                System.err.println("1 - "+url );
                return AppsTools.uriTranslationString(newUrl);//访问URL;
            }
                })
                .map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String result) {//如果url不为空 ->解析内容->下载-> 存入文件夹 ->
                result = AppsTools.justResultIsBase64decode(result);
                if (result!=null){
                    System.err.println("2 - "+result );

                    parseConentDownload(result);

                    return UiTools.storeContentToDirFile(url,result);
                }
                return false;
            }
        })

         .subscribeOn(Schedulers.io()) //执行在异步
         .unsubscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())//回到主线程
          .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isSuccess) {
                        if (isSuccess){
                            System.err.println("4 - "+isSuccess );
                            reresult.HttpResultCall();
                        }
                    }
                });




    }

    private static void parseConentDownload(String result) {
        GallaryBean gallaryBean = AppsTools.parseJsonWithGson(result,GallaryBean.class);

        if (gallaryBean!=null){
            for (DataObjsBean data : gallaryBean.getDataObjs()){
                parseData(data);
            }
        }
    }

    private static void parseData(DataObjsBean dataobj) {
        UiDownload.downloadTask(dataobj.getUrl());
        if (dataobj.getUrls()!=null && !dataobj.getUrls().equals("")){
            //切割字符串
            parseContentsUrisContent(dataobj.getUrls());
        }
    }


    /**
     *  多个url
     * @param urls
     */
    private static void parseContentsUrisContent(String urls) {
        try {
            String [] urlarr = urls.split(",");
            for(int i=0;i<urlarr.length;i++){
                UiDownload.downloadTask(urlarr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
