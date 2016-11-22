package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.weather;

import android.content.Context;
import android.graphics.Color;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponent;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_application.ui.componentLibrary.clock.LEDView;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;

/**
 * Created by user on 2016/11/22.
 */

public class Iweather extends RelativeLayout implements IComponent {
    private static final java.lang.String TAG = "IClock";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private String swfFilename;
    private boolean isInitData;
    private boolean isLayout;
    private String url_style ;//样式xml
    private String url_content;//天气内容xml


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
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);

            if (cb.getContents()!=null && cb.getContents().size()==1) {
                url_style = cb.getContents().get(0).getContentSource();  //http://172.16.0.17:9000/content/getContentSource/weather?contentsourcetype=weather¤tCity=长沙"
                url_content = generContentUrl(cb.getContents().get(0).getCity());
                createContent(null);
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
        this.setBackgroundColor(Color.RED);
    }

    @Override
    public void layouted() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }

    @Override
    public void unLayouted() {
        if (isLayout){
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
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void createContent(Object object) {
       //获取天气样式对象
        if (url_style!=null && url_style.equals("")){
            //访问本地文件 - 获取数据- 得到对象 - 获取数据
            tanslationUrl();
        }
        createSubLayout();
    }


    private void createSubLayout() {
        //创建 时钟
        createTime();
        //创建 天气
    }


    private LEDView time;
    private void createTime() {
        time = new LEDView(context);
        this.addView(time);
        RelativeLayout.LayoutParams lp =  (RelativeLayout.LayoutParams)time.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
    }



    @Override
    public void loadContent() {
        time.start();
    }

    @Override
    public void unLoadContent() {
        time.stop();
    }



    private void tanslationUrl() {
        String jsonContent = UiTools.urlTanslationJsonText(url_style);
        if (jsonContent!=null){
            try {
//            WeathersBean weather = AppsTools.parseJsonWithGson(jsonContent,WeathersBean.class); 暂时没什么鸟用

            }catch (Exception e){
                e.printStackTrace();
            }

        }


    }







    //生成城市url
    private String generContentUrl(String city) {
        return "http://apis.baidu.com/apistore/weatherservice/recentweathers?cityname="+city;
    }
    //获取 天气内容
    private void updateDatas() {

    }


}
