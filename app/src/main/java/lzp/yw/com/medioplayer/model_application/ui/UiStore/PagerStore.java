package lzp.yw.com.medioplayer.model_application.ui.UiStore;

import java.util.LinkedHashMap;

import lzp.yw.com.medioplayer.model_application.ui.UiElements.page.IviewPage;

/**
 * Created by user on 2016/11/10.
 */

public class PagerStore {



    //实例
    private static PagerStore instant;
    //构造
    private PagerStore(){

    }
    //获取 实例
    public static PagerStore getInstant(){
        if (instant==null){
            instant = new PagerStore();
        }
        return instant;
    }



    //页面存储
    private LinkedHashMap<Integer,IviewPage> pagesMap = null;

    //初始化 页面存储
   public void initPagesStore(){
       if (pagesMap==null){
           pagesMap = new LinkedHashMap<>();
       }else{
           pagesMap.clear();
       }

   }

    //添加 一个 页面
    public  void  addPage(int key,IviewPage page){
//        Log.i("","id - "+key + "page - "+page);
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


}