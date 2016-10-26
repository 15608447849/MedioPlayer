package lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import lzp.yw.com.medioplayer.baselayer.DataListEntiy;
import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.baselayer.appTools;
import lzp.yw.com.medioplayer.rxjave_retrofit.httptools.HttpLoggingInterceptor;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.WosResult;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverInterfaces.WosServerInterface;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/10/26.
 * lzp
 *   调用  init()后
 *   在调用其他方法
 */
public class HttpProxy {

    private static String TAG = "_HttpProxy";
    public String serverIp = "172.0.0.1";
    public String port = "8980";
    public String getBaseURI(){
        return  "http://"+serverIp+":"+port+"/";
    }

    /**
     * 超时时间
     */
    private static final int DEFAULT_TIMEOUT = 1;

    /**
     * 网络访问框架
     */
    private Retrofit retrofit;
    /**
     * okhttp 构建
     */
    private OkHttpClient.Builder httpClientBuilder;

    /**
     * 服务器 接口
     */
    private WosServerInterface movieService;

    /**
     * log拦截器
     */
    HttpLoggingInterceptor interceptor;
    /**
     * app context
     */
    private Context context ;
    //构造方法私有
    private HttpProxy() {
        Logs.d("------------- creat httpProxy -------" );
        interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logs.i(TAG,message);
            }
        });

        //手动创建一个OkHttpClient并设置超时时间
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);//秒



    }

    private static HttpProxy instants = null;
    //在访问HttpMethods时创建单例


    //获取单例
    public static HttpProxy getInstance(){
        if( instants == null){
            instants = new HttpProxy();
        }
        return instants;
    }

    /**
     *
     * @param context 上下文
     * @param ip  服务器ip
     * @param port 服务器端口
     */
    public void init(Context context,String ip,String port){
        this.context = context;
        this.serverIp = ip;
        this.port = port;
        Logs.d("BASE_URL :"+getBaseURI());

        retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                . baseUrl(getBaseURI())
                .build();

        movieService = retrofit.create(WosServerInterface.class);
 }

    /**
     * subscriber 订阅者
     * @param subscriber
     * http://ip:port/terminal/apply
     * {
     *"version":"18.7.3",
      "code":"999",
      "mac":"D0-92-91-AD-3F-F9",
      "screenResolutionWidth":"1024",
      "screenResolutionHeight":"1024"
      id
      corpid
    }
     */
    public void getTerminal(Subscriber<WosResult> subscriber){
        try{

             int[] screenSize = appTools.getScreenSize(context);
             DataListEntiy data = new DataListEntiy();
             data.put("version",String.valueOf(appTools.getLocalVersionCode(context)));
             data.put("corpId","999");
             data.put("code","999");
             data.put("ip", appTools.getLocalIpAddress());
             data.put("mac",appTools.getLocalMacAddressFromBusybox());
             data.put("screenResolutionWidth",String.valueOf(screenSize[0]));
             data.put("screenResolutionHeight",String.valueOf(screenSize[1]));

           movieService.getTerminal(data.getMap())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }catch (Exception e){
            Logs.e(TAG,e.getMessage());
        }

    }









}
