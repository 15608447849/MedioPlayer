package com.wos.play.rootdir.model_application.ui.UiFactory;

import com.wos.play.rootdir.model_application.ui.UiElements.page.IViewPage;
import com.wos.play.rootdir.model_application.ui.UiStore.PagerStore;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by user on 2016/11/10.
 */
public class UiManager {
    private static final String TAG = UiManager.class.getSimpleName();
    private static UiManager instants = null;
    private boolean isInit = false;
    private int currentAdId = -1, currentHomeId = -1, lastViewId = -1;    //当前持有的 广告ID/主页ID
    private UiManager() {

    }
    public static UiManager getInstance(){
        if (instants == null){
            instants = new UiManager();
        }
        return  instants;
    }

    /**
     */
    public void initData() {
        if (!isInit){
            init();
            isInit = true;
            Logs.i(TAG,"初始化 - UI 页面管理器 - 完成");
        }

    }
    public void unInitData(){
        if (isInit){
            stopTask();
            isInit=false;
            Logs.i(TAG,"注销 - UI 页面管理器");
        }
    }

    /**
     * 执行指定任务
     */
    public void exeTask(int id) {
        if (isInit){
            func(lastViewId = id);
        }
    }

    /**
     * 执行广告任务
     */
    public void exeAdTask(int adId) {
        if (isInit){
            if(adId > 0){
                currentAdId = adId;
                if(justSizeOnHome(adId)){
                    stopTask();
                }
                if(PagerStore.getInstant().getPage(currentAdId)!=null){
                    PagerStore.getInstant().getPage(currentAdId).startWork();
                }
            }else{
                stopPage(currentAdId);
                exeTask(lastViewId);
            }

        }
    }
    /**
     * 执行主线任务
     */
    public void exeMainTask(int homeId) {
        if (isInit){
            lastViewId = currentHomeId = homeId;
            if(PagerStore.getInstant().getPage(currentHomeId)!=null){
                addPage(currentHomeId);
                PagerStore.getInstant().getPage(currentHomeId).startWork();
            }
        }
    }



    /**
     * 停止任务
     */
    public void stopTask() {
        if (isInit){ //清除 栈内所有 page
            deleteAllPage();
        }
    }

    /**
     * 执行一个任务 1. 是否存于 栈中
     * 存在 ->
     *       是不是主页面?  是, 删除上面的所有 页面
     *       不是 创建
     *  不存在->
     *         是不是 宽高xy > 主页面的 宽高 xy? 是 -.删除主页面 -> 创建此页面
     *         不是  直接创建显示
     *
     *
     */
    private void func(int id) {
        if (loadedPageArray.contains(id)){ //栈中
            if (id == currentHomeId){//主页
                keepHomePage();
            }else{//不是 创建 执行
                if(PagerStore.getInstant().getPage(id)!=null){
                    PagerStore.getInstant().getPage(id).startWork();
                }
            }
        }else{//不在栈中,判断页面大小
            if (justSizeOnHome(id)){ //一样大  删除主页面
                stopTask();
            }
            if(PagerStore.getInstant().getPage(id)!=null){ //直接创建
                addPage(id);
                PagerStore.getInstant().getPage(id).startWork();
            }
        }
    }
    //真 一样大
    private boolean justSizeOnHome(int id) {
        IViewPage page = PagerStore.getInstant().getPage(id);
        IViewPage home = PagerStore.getInstant().getPage(currentHomeId);
        if (page!=null){
            Map<String,Integer> map1 = page.getPageSize();
            Map<String,Integer> map2 = home.getPageSize();

            int w1 = map1.get("width");
            int w2 = map2.get("width");
            int h1 = map1.get("height");
            int h2 = map2.get("height");

            if (w1==w2){
                if (h1==h2) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Ui属性
     */
    private void init(){ //初始化 栈
        loadedPageArray = new ArrayList<>();
    }


    //当前已加载的页面ID -
    private ArrayList<Integer> loadedPageArray = null;

    //添加一个页面
    private void addPage(int id){
        if(!loadedPageArray.contains(id)){
            loadedPageArray.add(id);
        }
    }
    //停止 页面
    private void stopPage(int id){
        if(PagerStore.getInstant().getPage(id)!=null){
            PagerStore.getInstant().getPage(id).stopWork();
        }
    }


    //删除所有页面
    private void deleteAllPage(){
        for (Integer id:loadedPageArray){
            stopPage(id);
        }
        loadedPageArray.clear();
        stopPage(currentAdId);  // 同时停止无人值守页面
    }

    //删除 除去主页之外的所有页面
    private void keepHomePage(){
        List<Integer> deleteList = new ArrayList<>();
        for (Integer id:loadedPageArray){
            if (id==currentHomeId){
                continue;
            }
            stopPage(id);
            deleteList.add(id);
        }
        for (Integer deleteId :deleteList){
            loadedPageArray.remove(loadedPageArray.indexOf(deleteId));
        }
    }

}
