package lzp.yw.com.medioplayer.model_application.ui.UiFactory;

import java.util.ArrayList;
import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.UiStore.ViewStore;

import static android.R.attr.id;

/**
 * Created by user on 2016/11/10.
 */
public class UiManager {
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
                if(ViewStore.getInstant().getPage(currentHomeId)!=null){
                addPage(currentHomeId);
                ViewStore.getInstant().getPage(currentHomeId).startWork();
            }
        }
    }
    /**
     * 执行一个任务
     */
    public void exeask(int id) {
        if (isInit){
            if (id == currentHomeId){
                //停止 除去 homeid 之外的所有page
                keepHomePage();
            }else{
                //查看是否存在于栈中
                if (loadedPageArray.contains(id)){
                   // 1 已存在的 -> 删除这个页面上面的 所有页面
                    deleteTagerTop(id);
                }else{
//                    2 不存在 添加到栈顶
                    if(ViewStore.getInstant().getPage(id)!=null){
                        addPage(id);
                        ViewStore.getInstant().getPage(id).startWork();
                    }

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
        if (loadedPageArray.contains(id)){
            loadedPageArray.remove(loadedPageArray.indexOf(id));
        }
        loadedPageArray.add(id);
    }
    //停止 页面
    private void stopPage(int id){
        if(ViewStore.getInstant().getPage(id)!=null){
            ViewStore.getInstant().getPage(id).stopWork();
        }else if (ViewStore.getInstant().getPageCache(id)!=null){
            ViewStore.getInstant().getPageCache(id).stopWork();
        }
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
