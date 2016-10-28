package lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy;

import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverInterfaces.WosServerInterfaceRemote;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/10/28.
 */
public class HttpProxyRemote extends AbsHttpProxy{

    /**
     * 网络访问框架 2
     */
    private Retrofit retrofit;
    /**
     * 服务器 接口 2
     */
    private WosServerInterfaceRemote movieService;

    //构造方法私有
    private HttpProxyRemote() {
        super();
        Logs.d("------------- creat HttpProxyRemote -------" );
    }



    private static HttpProxyRemote instants = null;
    //在访问HttpMethods时创建单例
    //获取单例
    public static HttpProxyRemote getInstance(){
        if( instants == null){
            instants = new HttpProxyRemote();
        }
        return instants;
    }

    @Override
    public void initProxy(String ip, String port, Object object) {
        try {
            //直接返回String类型需引入：ScalarsConverterFactory.create()
            retrofit = new Retrofit.Builder()
                    .client(httpClientBuilder.build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    . baseUrl(setBaseURI(ip,port))
                    .build();
            Logs.d(TAG,"ScalarsConverterFactory ");
            movieService = retrofit.create(WosServerInterfaceRemote.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        /**
     * 上线 ONFI
     * cmd=ONLI:{terminalid}
     */
    public void sendONFI(Subscriber<String> subscriber, String terminalId){
        try{
            movieService.ONFI("ONLI:"+terminalId)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                   //.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }catch (Exception e){
            Logs.e(TAG,e.getMessage());
        }
    }




}
