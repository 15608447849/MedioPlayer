package lzp.yw.com.medioplayer.model_application.ui.UiStore;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by user on 2016/12/2.
 */

public class ImageStore {
    private static ImageStore instant;
    private ImageStore(){
        init();
    }
    public static ImageStore getInstants(){
        if (instant==null){
            instant = new ImageStore();
        }
        return instant;
    }
    private LruCache<String,Bitmap> CacheMap = null;
    private int maxMemory = 0;
    private int mCacheSize = 0;
    //初始化
    private void init() {
        maxMemory = (int) Runtime.getRuntime().maxMemory();
        mCacheSize = maxMemory / 4;
        if(CacheMap == null){
            CacheMap = new LruCache<String, Bitmap>(mCacheSize);
        }
    }

    //获取 一个 缓存 view
    public Bitmap getBitmapCache(String tag){
        if (CacheMap==null){
            return null;
        }
        return CacheMap.get(tag);
    }


    //添加 一个 缓存 view
    public  void  addBitmapCache(String tag,Bitmap imageview){
        try{
            CacheMap.put(tag,imageview);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
