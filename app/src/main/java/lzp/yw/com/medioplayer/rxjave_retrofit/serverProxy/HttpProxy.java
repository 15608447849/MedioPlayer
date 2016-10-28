package lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy;

import android.content.Context;

import java.util.List;

import lzp.yw.com.medioplayer.baselayer.DataListEntiy;
import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.baselayer.appTools;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.WosResult;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.ScheduleBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.content_gallary.GallaryBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverInterfaces.WosServerInterface;
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
 *
 *   转换器类型
 *   Gson: com.squareup.retrofit2:converter-gson
 Jackson: com.squareup.retrofit2:converter-jackson
 Moshi: com.squareup.retrofit2:converter-moshi
 Protobuf: com.squareup.retrofit2:converter-protobuf
 Wire: com.squareup.retrofit2:converter-wire
 Simple XML: com.squareup.retrofit2:converter-simplexml
 Scalars (primitives, boxed, and String): com.squareup.retrofit2:converter-scalars
 */
public class HttpProxy extends AbsHttpProxy{




    /**
     * 网络访问框架 1
     */
    private Retrofit retrofit;
    /**
     * 服务器 接口 1
     */
    private WosServerInterface movieService;

    /**
     * app context
     */
    private Context context ;
    //构造方法私有
    private HttpProxy() {
        super();
        Logs.d("------------- creat httpProxy -------" );
    }

    private String baseUri = null;
    public String getBaseUri(){
        return baseUri;
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
     *
     * @param ip  服务器ip
     * @param port 服务器端口
     *@param object 上下文
     */
    @Override
    public void initProxy(String ip,String port,Object object){

        try {
            this.context = (Context) object;
            baseUri = setBaseURI(ip,port);
            Logs.d("BASE_URL :"+baseUri);
            //返回Gson类型需引入：GsonConverterFactory.create()
            retrofit = new Retrofit.Builder()
                    .client(httpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    . baseUrl(baseUri)
                    .build();
            Logs.d(TAG,"GsonConverterFactory");
            movieService = retrofit.create(WosServerInterface.class);

            Logs.d(TAG,"movieService == "+movieService );//+"\n movieService_2 == "+movieService_2);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void getTerminal(Subscriber<WosResult> subscriber,String corpId){
        try{
             DataListEntiy data = new DataListEntiy();
             data.put("version",String.valueOf(appTools.getLocalVersionCode(context)));
             data.put("corpId",corpId);
             data.put("code",corpId);
             data.put("ip", appTools.getLocalIpAddress());
             data.put("mac",appTools.getLocalMacAddressFromBusybox());
            int[] screenSize = appTools.getScreenSize(context);
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



    /**
     * 获取排期
     *    UPSC:http://172.16.0.17:9000/terminal/32/schedule
     */
    public void getSchedule(Subscriber<List<ScheduleBean>> subscriber, String id){
        try{
            if (movieService==null){
                return;
            }
            movieService.UPSC(id)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    //.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }catch (Exception e){
//            Logs.e(TAG,"getSchedule() err:" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 获取 图集
     */
    public void getGrallarys(Subscriber<GallaryBean> subscriber, String path){
        try{
            Logs.d(TAG," getGrallarys() path >>> " + path);
            if (movieService==null){
                return;
            }
            //Base64.encodeToString(path.getBytes("UTF-8"),Base64.NO_PADDING)

            movieService.getGallaryEntity(path)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    //.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }catch (Exception e){
//            Logs.e(TAG,"getSchedule() err:" + e.getMessage());
            e.printStackTrace();
        }
    }





}
