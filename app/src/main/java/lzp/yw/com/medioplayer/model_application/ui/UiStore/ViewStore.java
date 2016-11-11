package lzp.yw.com.medioplayer.model_application.ui.UiStore;

import android.util.LruCache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IviewPage;

/**
 * Created by user on 2016/11/10.
 */

public class ViewStore {


    //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
    private int maxMemory = 0;
    private int mCacheSize = 0;
    //实例
    private static ViewStore instant;
    //构造
    private ViewStore(){
        init();
    }
    //获取 实例
    public static ViewStore getInstant(){
        if (instant==null){
            instant = new ViewStore();
        }
        return instant;
    }

    //初始化
    private void init() {
        maxMemory = (int) Runtime.getRuntime().maxMemory();
       mCacheSize = maxMemory / 4;
    }


    //页面存储
    private LinkedHashMap<Integer,IviewPage> pagesMap = null;

    //页面缓存
    private LruCache<Integer,IviewPage> pagesCacheMap = null;
    //初始化 页面存储
   public void initPagesStore(){
       if (pagesMap==null){
           pagesMap = new LinkedHashMap<>();
       }else{
           pagesMap.clear();
       }
       if(pagesCacheMap == null){
           pagesCacheMap = new LruCache<Integer, IviewPage>(mCacheSize);
       }
   }

    //添加 一个 页面
    public  void  addPage(int key,IviewPage page){
        try{
            pagesMap.put(key,page);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取 一个 页面
    public IviewPage getPage(int key){
        if (pagesMap==null){
            return null;
        }
        if (pagesMap.containsKey(key)){
            return pagesMap.get(key);
        }
        return null;
    }
    // 页面 转储 缓存中
    public void pageTanslationCache(){
        if (pagesMap!=null && pagesCacheMap!=null){
            Iterator iter = pagesMap.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                Integer key = (Integer)entry.getKey();
                IviewPage val = (IviewPage)entry.getValue();
                addPageCache(key,val);
            }
        }
    }
    //添加 一个 缓存 页面
    private  void  addPageCache(int key,IviewPage page){
        try{
            pagesCacheMap.put(key,page);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //获取 一个 缓存 页面
    public IviewPage getPageCache(int key){
        if (pagesCacheMap==null){
            return null;
        }
       return pagesMap.get(key);
    }

    ///////////////////////////////////////////////////////////////缓存组件
    //组件
    private LinkedHashMap<Integer,Iview> ivMap = null;
    //组件缓存
    private LruCache<Integer,Iview> ivCacheMap = null;
    //初始化
    public void initIvStore(){
        if (ivMap==null){
            ivMap = new LinkedHashMap<>();
        }else{
            ivMap.clear();
        }
        if(ivCacheMap == null){
            ivCacheMap = new LruCache<Integer, Iview>(mCacheSize);
        }
    }



    //添加 一个 iv
    public  void  addIv(int key,Iview iv){
        try{

            ivMap.put(key,iv);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取 一个 iv
    public Iview getIv(int key){
        if (ivMap==null){
            return null;
        }
        if (ivMap.containsKey(key)){
            return ivMap.get(key);
        }
        return null;
    }

    // 页面 转储 缓存中
    public void ivTanslationCache(){
        if (ivMap!=null && ivCacheMap!=null){
            Iterator iter = ivMap.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                Integer key = (Integer)entry.getKey();
                IviewPage val = (IviewPage)entry.getValue();
                addIvCache(key,val);
            }
        }
    }
    //添加 一个 缓存 页面
    private  void  addIvCache(int key,Iview iv){
        try{
            ivCacheMap.put(key,iv);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取 一个 缓存 页面
    public Iview getIvCache(int key){
        if (ivCacheMap==null){
            return null;
        }
        return ivCacheMap.get(key);
    }
}
