package com.wos.play.rootdir.model_application.ui.ComponentLibrary.a_centerManager;

import android.content.Context;
import android.util.LruCache;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.Iview;
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
        referenceViewMap.put(CONTENT_TYPE.image,packageName+"image.CMorePictures");
        referenceViewMap.put(CONTENT_TYPE.button,packageName+"button.CButton");
        referenceViewMap.put(CONTENT_TYPE.video,packageName+"video.CMedio");
        referenceViewMap.put(CONTENT_TYPE.text,packageName+"textshow.TextViewPager");
        referenceViewMap.put(CONTENT_TYPE.html,packageName+"web.IWebView");
        referenceViewMap.put(CONTENT_TYPE.gallary,packageName+"grallery.CGrallery");
        referenceViewMap.put(CONTENT_TYPE.media,packageName+"stream_medio.CStreamMedioForVitamio");
        referenceViewMap.put(CONTENT_TYPE.clock,packageName+"clock.IClock");
        referenceViewMap.put(CONTENT_TYPE.weather,packageName+"weather.Iweather");
        referenceViewMap.put(CONTENT_TYPE.marquee,packageName+"scrolltextview.CMarquee");
        referenceViewMap.put(CONTENT_TYPE.news,packageName+"news.CNews");
    }


    //存储一部分视图
    private static LruCache<Integer,Iview> mLruCache =  new LruCache<Integer,Iview>((int) (Runtime.getRuntime().maxMemory() / 8));//最大内存的1/3;

    private static void putIviewToCache(Integer key,Iview value){
        mLruCache.put(key,value);
    }

    private static Iview getIplayerToCache(Integer key){
        return  mLruCache.get(key);
    }

   public static Iview create(ComponentsBean component, AbsoluteLayout layout, Context context){
        Iview iplay = null;
       try {
           //查看缓存是否存在
           int key = component.getId()+layout.hashCode();

           iplay = getIplayerToCache(key);

           if (iplay == null) {
               //创建
               //先获取 type
               String type = component.getComponentTypeCode();
               if (!referenceViewMap.containsKey(type)){
                   throw new Exception("component type not fount - "+type);
               }
               //获取全类名
               String className = referenceViewMap.get(type);
               Class cls = Class.forName(className);//得到类
               Constructor constructor = cls.getConstructor(Context.class, //得到构造
                       AbsoluteLayout.class,ComponentsBean.class);
               iplay = (Iview) constructor.newInstance(context,layout,component); //得到具体实例
               //添加到 缓存中
               putIviewToCache(key,iplay);
           }

       } catch (Exception e) {
//           e.printStackTrace();
           Logs.e("createComponent"," 创建组件错误 :"+e.getMessage());
       }
       return iplay;
    }
}
