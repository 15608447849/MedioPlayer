package lzp.yw.com.medioplayer.model_application.ui.UiHttp;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.AppsTools;
import rx.Observable;
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
                    public String call(String oldUrl) {
                        return AppsTools.justUriIsBase64GetUrl(oldUrl);//分解URL;
                    }
                } )
                .map(new Func1<String, String>() {
            @Override
            public String call(String newUrl) {
                return AppsTools.uriTranslationString(newUrl);//访问URL;
            }
                })
                .map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String result) {//如果url不为空 -> 存入文件夹 ->完成 通知

                if (result!=null){
                    return UiTools.storeContentToDirFile(url,result);
                }
                return false;
            }
        })
         .subscribeOn(Schedulers.io()) //执行在异步
         .unsubscribeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean isSuccess) {
                        if (isSuccess){
                            reresult.HttpResultCall();
                        }
                    }
                });




    }





}
