package lzp.yw.com.medioplayer.model_application.ui.UiStore;

import android.util.LruCache;
import android.view.View;

/**
 * Created by user on 2016/11/19.
 */

public class ViewStore {

    private static ViewStore instant;
    private ViewStore(){
        init();
    }
    public static ViewStore getInstants(){
        if (instant==null){
            instant = new ViewStore();
        }
        return instant;
    }
    //页面缓存
    private LruCache<String,View> CacheMap = null;
//获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
    private int maxMemory = 0;
    private int mCacheSize = 0;
    //初始化
    private void init() {
        maxMemory = (int) Runtime.getRuntime().maxMemory();
        mCacheSize = maxMemory / 4;
        if(CacheMap == null){
            CacheMap = new LruCache<String, View>(mCacheSize);
        }
    }

    //获取 一个 缓存 页面
    public View getViewCache(String tag){
        if (CacheMap==null){
            return null;
        }
        return CacheMap.get(tag);
    }


    //添加 一个 缓存 页面
    private  void  addViewCache(String tag,View imageview){
        try{
            CacheMap.put(tag,imageview);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
