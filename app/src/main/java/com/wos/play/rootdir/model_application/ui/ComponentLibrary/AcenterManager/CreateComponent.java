package com.wos.play.rootdir.model_application.ui.ComponentLibrary.AcenterManager;
import android.content.Context;
import android.util.LruCache;
import android.widget.AbsoluteLayout;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IView;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.tool.CONTENT_TYPE;
import com.wos.play.rootdir.model_universal.tool.Logs;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/11/11.
 */

public class CreateComponent {
    private static final String packageName = "com.wos.play.rootdir.model_application.ui.ComponentLibrary.";
    private static Map<String,String> referenceViewMap = new HashMap<String,String>();
    static{
        referenceViewMap.put(CONTENT_TYPE.epaper,packageName+"epaper.CEpaperView");
        referenceViewMap.put(CONTENT_TYPE.image,packageName+"image.CMorePictures");
        referenceViewMap.put(CONTENT_TYPE.qrCode,packageName+"image.CMorePictures");
        referenceViewMap.put(CONTENT_TYPE.button,packageName+"button.CButton");
        referenceViewMap.put(CONTENT_TYPE.video,packageName+"video.CMedia");
        referenceViewMap.put(CONTENT_TYPE.text,packageName+"text.TextViewPager");
        referenceViewMap.put(CONTENT_TYPE.html,packageName+"web.IWebView");
        referenceViewMap.put(CONTENT_TYPE.gallary,packageName+"gallery.CGallery");
        referenceViewMap.put(CONTENT_TYPE.media,packageName+"stream_media.CStreamMediaForVitamio");
        referenceViewMap.put(CONTENT_TYPE.clock,packageName+"clock.IClock");
        referenceViewMap.put(CONTENT_TYPE.weather,packageName+"weather.IWeather");
        referenceViewMap.put(CONTENT_TYPE.marquee,packageName+"marquee.CScrollTextView");
        referenceViewMap.put(CONTENT_TYPE.news,packageName+"news.CNews");

    }


    //存储一部分视图
    private static LruCache<Integer,IView> mLruCache =  new LruCache<Integer,IView>((int) (Runtime.getRuntime().maxMemory() / 8));//最大内存的1/3;

    private static void putIViewToCache(Integer key,IView value){
        mLruCache.put(key,value);
    }

    private static IView getIPlayerToCache(Integer key){
        return  mLruCache.get(key);
    }

   public static IView create(ComponentsBean component, AbsoluteLayout layout, Context context){
       IView iPlay = null;
       try {
           //查看缓存是否存在
           int key = component.getId()+layout.hashCode();

           iPlay = getIPlayerToCache(key);

           if (iPlay == null) {
               //创建
               //先获取 type
               String type = component.getComponentTypeCode();
               if (!referenceViewMap.containsKey(type)){
                   throw new Exception("Component Type Not Fount : "+type);
               }
               //获取全类名
               String className = referenceViewMap.get(type);
               Class<?> cls = Class.forName(className);//得到类
               Constructor<?> constructor = cls.getConstructor(Context.class, //得到构造
                       AbsoluteLayout.class,ComponentsBean.class);
               iPlay = (IView) constructor.newInstance(context,layout,component); //得到具体实例
               //添加到 缓存中
               putIViewToCache(key,iPlay);
           }

       } catch (Exception e) {
            Logs.ii("CreateComponent","iPlay:%s message:%s",iPlay, e.getMessage());
       }
       return iPlay;
    }
}
