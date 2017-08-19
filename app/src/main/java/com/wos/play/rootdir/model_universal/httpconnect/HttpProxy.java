package com.wos.play.rootdir.model_universal.httpconnect;

import android.util.Log;

import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by user on 2016/10/28.
 */
public class HttpProxy {
    protected static String TAG = "_HttpProxy";

    /**
     * 超时时间
     */
    protected static final int DEFAULT_TIMEOUT = 1;
    /**
     * okhttp 构建
     */
    protected OkHttpClient.Builder httpClientBuilder;
    /**
     * log拦截器
     */
    protected HttpLoggingInterceptor interceptor;
    private Retrofit retrofit;
    private HttpServerInterface movieService;
    private HttpProxy(){
        //拦截器
        interceptor = new HttpLoggingInterceptor();
        //手动创建一个OkHttpClient并设置超时时间
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);//秒
        initRetrofit();
        if (mCompositeSubscription==null){
            mCompositeSubscription = new CompositeSubscription();
        }
       }

    /**
     * 初始化 retrofit 服务器参数接口
     */
    private void initRetrofit(){
        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("http://127.0.0.1:8000")
                .build();
        Logs.d(TAG," -- ScalarsConverterFactory --  not base_uri");
        movieService = retrofit.create(HttpServerInterface.class);
    }

    private static HttpProxy instant = null;
    //获取单例
    public static HttpProxy getInstant(){
        if (instant == null){
            instant = new HttpProxy();
        }
        return instant;
    }

    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    private CompositeSubscription mCompositeSubscription;

    public void kills(){
        if (mCompositeSubscription!=null){
            mCompositeSubscription.unsubscribe();
        }
    }

    /**
     * 创建观察者
     *
     * @param onNext
     * @param <T>
     * @return
     */
    private <T> Subscriber newSubscriber(final Action1<? super T> onNext, final Action1<Throwable> onError) {
        Subscriber<T> subscriber = new Subscriber<T>() {
            @Override
            public void onCompleted() {
               // Log.e(TAG, "onCompleted  ");
            }

            @Override
            public void onError(Throwable e) {
                if (onError==null){
                    Log.e(TAG, "subscribe err : "+ e.getMessage());
                }else{

                    if (!mCompositeSubscription.isUnsubscribed()) {
                        onError.call(e);
                    }
                }

            }
            @Override
            public void onNext(T t) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.call(t);
                }
            }
        };
        mCompositeSubscription.add(subscriber);
        return subscriber;
    }

    /**
     * 获取终端id
     */
    public void getTerminalId(String url,final Action1<? super String> onNext,final Action1<Throwable> onError){ //订阅者
        /*我们对API调用了 observeOn(MainThread) 之后，线程会跑在主线程上，包括 onComplete 也是， unsubscribe 也在主线程，然后如果这时候调用 call.cancel 会导致 NetworkOnMainThreadException ，所以一定要在 retrofit 的API调用 ExampleAPI.subscribeOn(io).observeOn(MainThread) 之后加一句 unsubscribeOn(io) 。

完整的就是 ExampleAPI.subscribeOn(io).observeOn(MainThread).unsubscribeOn(io) 。*/
        try{
            movieService.sendCMD(url)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())//.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newSubscriber(onNext,onError));
        }catch (Exception e){
            Logs.e(TAG,e.getMessage());
        }
    }


    /**
     * 上线 ONFI
     * cmd=ONLI:{terminalid}
     * 心跳 HRBT
     * 文件下载情况上播   FTPS:100000001;1004562123.jpg;2
     * 文件下载进度上报(PRGS:10000001,1004562123.jpg,0.56,200kb/s)
     */
    public void sendCmd(String url,final Action1<String> onNext){
        try{
            movieService.sendCMD(encodeUrlParam("cmd",url))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())//.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newSubscriber(onNext,null));
        }catch (Exception e){
            Logs.e(TAG,e.getMessage());
        }
    }
    /**
     * encode param
     */
    private static String encodeUrlParam(String key,String url){
        // http://192.168.6.14:9000/terminal/heartBeat?cmd=HRBT%3A10000555
        if (url.contains(key)){
            try {
                return url.substring(0,url.indexOf(key)+key.length()+1)  +
                        URLEncoder.encode(url.substring(url.indexOf(key)+key.length()+1),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

}
