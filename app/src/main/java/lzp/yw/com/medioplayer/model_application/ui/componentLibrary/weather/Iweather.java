package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

import lzp.yw.com.medioplayer.model_application.ui.UiFactory.UiLocalBroad;
import lzp.yw.com.medioplayer.model_application.ui.UiHttp.UiHttpProxy;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IAdvancedComponent;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_weather.BaiduApiObject;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_weather.WeathersBean;
import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.tool.MD5Util;
import lzp.yw.com.medioplayer.model_universal.tool.PinYinUtils;

/**
 * Created by user on 2016/11/22.
 */

public class Iweather extends FrameLayout implements IAdvancedComponent {
    private static final java.lang.String TAG = "IClock";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private boolean isInitData;
    private boolean isLayout;
    private String url_style ;//样式xml
    private String url_content;//天气内容xml
    private String mBroadAction; //组件 id+hashcode+mad5
    private  int uptimes ;//
    //创建广播
    private UiLocalBroad broad = null;
    private boolean isRegestBroad = false; //是否注册广播
    private LEDView led;    //创建 时间 天气 显示器
    private boolean isShowTimer = false;//是否显示时间
    public Iweather(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
            this.componentId = cb.getId();
            this.mBroadAction = MD5Util.getStringMD5(this.hashCode()+"."+componentId);

            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);

            initSubComponet();
            if (cb.getContents()!=null && cb.getContents().size()==1) {
                uptimes = cb.getContents().get(0).getUpdateFreq();
                url_style = cb.getContents().get(0).getContentSource();  // http://172.16.0.17:9000/content/getContentSource/weather?contentsourcetype=weather¤tCity=长沙"
                url_content = AppsTools.generWeateherContentUrl(cb.getContents().get(0).getCity());
                createContent(null);
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //创建内容
    @Override
    public void createContent(Object object) {
        //获取天气样式对象
        getWeatherStyle();
        //获取天气内容天气
        getWeatherContent();
    }


    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
    }

    @Override
    public void layouted() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
            if (led !=null){
                led.startText();
                if (isShowTimer){
                    led.startTime();
                }
            }
        }
    }

    @Override
    public void unLayouted() {
        if (isLayout){
            if (led !=null){
                led.stop();
            }
            layout.removeView(this);
            isLayout = false;
        }
    }

    @Override
    public void startWork() {
        try {
            if (!isInitData){
                return;
            }
            setAttrbute();
            layouted();
            createBroad();
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            unLoadContent();
            cancelBroad();
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initSubComponet() {
     //
        if (led ==null){
            led = new LEDView(context,this);
        }
    }

    @Override
    public void loadContent() {
        unLoadContent();
        startBaiDuApi();//访问天气
        startTimer(uptimes * 1000);
    }

    @Override
    public void unLoadContent() {
        stopTimer();//停止计时间 停止当前内容
    }











    @Override
    public void broadCall() {
        //广播 回调
        Logs.i(TAG," 广播 - "+mBroadAction+" - 收到,执行!");
        //得到百度api内容对象
        if (AppsTools.checkUiThread()){
            getWeatherContent();
        }
    }

    @Override
    public void createBroad() {
        if (!isRegestBroad){
            broad = new UiLocalBroad(mBroadAction,this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(mBroadAction);
            context.registerReceiver(broad, filter); //只需要注册一次
            this.isRegestBroad = true;
        }
    }
    @Override
    public void cancelBroad() {
        if (isRegestBroad) {
            //取消注册
            if (broad != null) {
                try {
                    context.unregisterReceiver(broad);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                broad = null;
            }
        }
    }
    private TimerTask timerTask= null;
    private Timer timer = null;
    private void stopTimer(){
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }
    private void startTimer(long millisecond){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                loadContent();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,millisecond);
    }
    //刷新ui 刷新内容
    private void startBaiDuApi() {
        //  - 去通讯服务 - >
        UiHttpProxy.getWeateher(url_content,mBroadAction);
    }
    //获取天气样式
    public void getWeatherStyle() {
        if (url_style!=null && !url_style.equals("")){
            //访问本地文件 - 获取数据- 得到对象 - 获取数据
            getStyleUrlData();
        }
    }
    //获取天气 的 样式 - > 暂时无使用
    private void getStyleUrlData() {
        isShowTimer = true;
        String jsonContent = UiTools.urlTanslationJsonText(url_style);
        if (jsonContent!=null){
            try {
            WeathersBean weather = AppsTools.parseJsonWithGson(jsonContent,WeathersBean.class);
                if (weather!=null){
                    Logs.i(TAG,"weather.getStyle().getLayout().getDisplay() - "+weather.getStyle().getLayout().getDisplay());
                    if (weather.getStyle().getLayout().getDisplay().contains("time")){
                            //显示 时间
                      isShowTimer = true;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //获取天气内容
    public void getWeatherContent() {
        if (url_content!=null && !url_content.equals("")){
            //  访问本地文件 - 获取数据- 得到对象 - 获取数据
            getBaiduUrlData();
        }
    }
    //获取百度天气数据
    public void getBaiduUrlData() {
        String jsonContent = UiTools.urlTanslationJsonText(url_content);

        if (jsonContent!=null){
            try {
                BaiduApiObject baiduApi =  AppsTools.parseJsonWithGson(jsonContent,BaiduApiObject.class);
                if (baiduApi!=null){
//                    存在数据
//                    解析
                    parseBaiduData(baiduApi);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    //解析百度api 数据
    private void parseBaiduData(BaiduApiObject baiduApi) {
        //解析数据

        String [] arr = new String[4];
        arr[0] = baiduApi.getRetData().getCity();//城市
        arr[1] = baiduApi.getRetData().getToday().getType();//类型
        arr[2] = baiduApi.getRetData().getToday().getLowtemp()+" ~ "+baiduApi.getRetData().getToday().getHightemp();//温度
        arr[3] = baiduApi.getRetData().getToday().getFengli();//风力

        //根据类型 -> 获取bitmap
        Bitmap bitmap = tanslateTypeToBitmap(arr[1]);

        if (led!=null){
            led.setValue(arr,bitmap);
        }
    }
    //根据类型 得到 bitmap
    private Bitmap tanslateTypeToBitmap(String type) {
        String path = null;
        // 1. 获取 类型的拼音
        path = PinYinUtils.getPingYin(type);
        // 2. 判断早晚
        path = UiTools.getDateSx()+path;
        // 去对应文件夹 获取 bitmap
        path = UiTools.getWeatherIconPath()+path+".png";
        System.out.println("bitmap path -:"+path);
        if (UiTools.fileIsExt(path)){
            return ImageUtils.getBitmap(path);
        }
        return null;
    }



}