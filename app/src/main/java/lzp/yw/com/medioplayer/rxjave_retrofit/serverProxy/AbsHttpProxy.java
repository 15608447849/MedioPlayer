package lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy;

import java.util.concurrent.TimeUnit;

import lzp.yw.com.medioplayer.rxjave_retrofit.httptools.HttpLoggingInterceptor;
import okhttp3.OkHttpClient;

/**
 * Created by user on 2016/10/28.
 */
public abstract class AbsHttpProxy {
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

    public String setBaseURI(String serverIp ,String port){
        return  "http://"+serverIp+":"+port+"/";
    }

    protected AbsHttpProxy(){
        interceptor = new HttpLoggingInterceptor();
        //手动创建一个OkHttpClient并设置超时时间
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);//秒
    }

  public abstract void initProxy(String ip,String port,Object object);






}
