package lzp.yw.com.medioplayer.model_download.entity;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/19.
 */

public class UrlList {
    private static final String TAG = "UrlList";
    private ArrayList<CharSequence> loadingList = null;
    public UrlList(){
        loadingList = new ArrayList<CharSequence>();
    }
    /**
     * 初始化下载列表
     */
    public void initLoadingList(){
        if (loadingList==null){
            loadingList = new ArrayList<CharSequence>();
        }else{
            loadingList.clear();
            Logs.d(TAG,"任务清空,当前数量:" + loadingList.size());
        }
    }
    /**
     * 添加任务
     * @param url
     */
    public void addTaskOnList(String url){
        if (loadingList==null){
            return;
        }
        if (url==null || url.equals("") || url.equals("null")){
            Logs.e(TAG," add task is failt url = " + url);
            return;
        }
        if(!loadingList.contains(url)){
            loadingList.add(url);
            Logs.w(TAG," add task is succsee !");
        }else{
            Logs.e(TAG," add task failt ,because is exist !!!");
        }
        if (AppsTools.isMp4Suffix(url)){
            this.addTaskOnList(AppsTools.tanslationMp4ToPng(url));
        }
    }
    /**
     * 添加任务
     * @param url
     */
    public void addTaskOnList(CharSequence url){
        addTaskOnList((String)url);
    }

    public ArrayList<CharSequence> getList(){
        return loadingList;
    }
    public int getListSize(){
        return loadingList.size();
    }
}
