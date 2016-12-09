package com.wos.play.rootdir.model_application.ui.UiFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wos.play.rootdir.model_application.ui.UiElements.page.IviewPage;
import com.wos.play.rootdir.model_application.ui.UiStore.PagerStore;
import com.wos.play.rootdir.model_universal.tool.Logs;

import static android.R.attr.id;

/**
 * Created by user on 2016/11/10.
 */
public class UiManager {
    private static final String TAG ="UiMagener";
    private static UiManager instants = null;
    private boolean isInit = false;
    private UiManager() {
        init();
    }
    public static UiManager getInstans(){
        if (instants == null){
            instants = new UiManager();
        }
        return  instants;
    }

    /**
     */
    public void initData() {
        try {
            isInit = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行主线任务
     */
    public void exeMainTask(int homeId) {
        if (isInit){
                currentHomeId = homeId;
                if(PagerStore.getInstant().getPage(currentHomeId)!=null){
                addPage(currentHomeId);
                PagerStore.getInstant().getPage(currentHomeId).startWork();
            }
        }
    }
    /**
     * 执行一个任务
     * 1. 是否存于 栈中
     * 存在 ->
     *       是不是主页面?  是, 删除上面的所有 页面
     *       不是 创建
     *  不存在->
     *         是不是 宽高xy > 主页面的 宽高 xy? 是 -.删除主页面 -> 创建此页面
     *         不是  直接创建显示
     *
     *
     */
    public void exeTask(int id) {
        if (isInit){
//            func1(id);
            func2(id);
        }
    }

    private void func2(int id) {



        if (loadedPageArray.contains(id)){
            //栈中
            if (id == currentHomeId){
                //主页
                keepHomePage();
            }else{
                //不是 创建 执行
                if(PagerStore.getInstant().getPage(id)!=null){
                    addPage(id);
                    PagerStore.getInstant().getPage(id).startWork();
                }

            }

        }

        //不在栈中
        else{
        //判断页面大小
            if (justSizeOnHome(id)){
                    //一样大  删除主页面
                stopTask();
            }
            //直接创建
            if(PagerStore.getInstant().getPage(id)!=null){
                addPage(id);
                PagerStore.getInstant().getPage(id).startWork();
            }






        }













    }
    //真 一样大
    private boolean justSizeOnHome(int id) {
        IviewPage page = PagerStore.getInstant().getPage(id);
        IviewPage home = PagerStore.getInstant().getPage(currentHomeId);
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

    private void func1(int id) {
        if (id == currentHomeId){
            Logs.i(TAG,"当前目标ID 是主页,移除 栈上所有页面");
            //停止 除去 homeid 之外的所有page
            keepHomePage();
        }else{
            //查看是否存在于栈中
            if (loadedPageArray.contains(id)){
                Logs.i(TAG,"当前目标ID 存在于栈中 ,删除这个页面上的所有page");
                // 1 已存在的 -> 删除这个页面上面的 所有页面
                deleteTagerTop(id);
            }else{
                Logs.i(TAG,"当前目标ID 不存在栈中,添加...");
//                    2 不存在 添加到栈顶
                if(PagerStore.getInstant().getPage(id)!=null){
                    addPage(id);
                    PagerStore.getInstant().getPage(id).startWork();
                }

            }
        }
    }

    /**
     * 停止任务
     */
    public void stopTask() {
        if (isInit){
            //清楚 栈内所有 page
            deleteAllPage();
        }
    }


    /**
     * Ui属性
     */
    private void init(){
        //初始化 栈
        loadedPageArray = new ArrayList<>();
    }

    //当前持有的 主页面 id
    private int currentHomeId = -1;

    public int getCurrentHomeId() {
        return currentHomeId;
    }

    public void setCurrentHomeId(int currentHomeId) {
        this.currentHomeId = currentHomeId;
    }

    //当前已加载的页面ID -
    private ArrayList<Integer> loadedPageArray = null;

    //添加一个页面
    public void addPage(int id){
       /* if (loadedPageArray.contains(id)){
            loadedPageArray.remove(loadedPageArray.indexOf(id));
        }*/
        loadedPageArray.add(id);
    }
    //停止 页面
    private void stopPage(int id){
        if(PagerStore.getInstant().getPage(id)!=null){
            PagerStore.getInstant().getPage(id).stopWork();
        }
        /*else if (ViewStore.getInstant().getPageCache(id)!=null){
            ViewStore.getInstant().getPageCache(id).stopWork();
        }*/
    }

    //删除一个页面
    public void deletePage(int id){
        if (loadedPageArray.contains(id)){
            loadedPageArray.remove(loadedPageArray.indexOf(id));
            stopPage(id);
        }
    }


    //删除所有页面
    public void deleteAllPage(){
        for (Integer id:loadedPageArray){
            stopPage(id);
        }
        loadedPageArray.clear();
    }

    //删除 除去主页之外的所有页面
    public void keepHomePage(){
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



    //删除 - 指定页面 之上的所有页面
    public void deleteTagerTop(int keepid){
        int index = loadedPageArray.indexOf(keepid);//这个页面 的下标
        if (index++ == loadedPageArray.size()){
            return;
        }
        List<Integer> deleteList = new ArrayList<>();
        for (int i=index;i<loadedPageArray.size();i++){
            stopPage(id);
            deleteList.add(id);
        }
        for (Integer deleteId :deleteList){
            loadedPageArray.remove(loadedPageArray.indexOf(deleteId));
        }
    }




}
