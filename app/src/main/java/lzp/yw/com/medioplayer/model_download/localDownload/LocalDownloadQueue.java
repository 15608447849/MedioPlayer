package lzp.yw.com.medioplayer.model_download.localDownload;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/19.
 * 局部下载任务 - 任务队列
 **/
public class LocalDownloadQueue {
    private static final String TAG ="downloadQueueMagene";
    private static LocalDownloadQueue taskManage = null;

    LocalBeingWatched beingWatched = null;
    LocalWatched watcher =null;
    private LocalDownloadQueue(Context c){
        beingWatched = new LocalBeingWatched();//受查者
        watcher = new LocalWatched(c);//观察者
        beingWatched.addObserver(watcher);
    }

    public static LocalDownloadQueue getManage(Context c){
        if (taskManage == null){
            taskManage = new LocalDownloadQueue(c);
        }
        return taskManage;
    }


    // map - key广播名  value 队列任务  (存储map)
    private static HashMap<String,ArrayList<CharSequence>> localTaskMap = null;

    /**
     * 添加任务
     * @param taskList
     */
    public void addItemToStore(String notifyBoradAction,ArrayList<CharSequence> taskList,String savepath,String telminalid){
        if (localTaskMap==null){
            localTaskMap = new HashMap<>();
        }
        localTaskMap.put(notifyBoradAction,taskList);
        beingWatched.excute(this,savepath,telminalid);// 每次添加任务 就触发一次 下载执行
    }
    /**
     * 取出任务队列
     * @return
     */
    public  ArrayList<Object> getTaskAndDelete(){

        ArrayList<Object> objarr = null;
        if(localTaskMap.size()==0){
            Logs.e(TAG,"没有 存储的 任务队列 ");
            return objarr;
        }
        //每次取出一个
        if (localTaskMap.size()>0){//至少存在一个
            Iterator<Map.Entry<String,ArrayList<CharSequence>>> itr = localTaskMap.entrySet().iterator();

            if (itr.hasNext()){//cun zai xia yi ge
                Logs.e(TAG,"存在 任务队列");
                Map.Entry<String, ArrayList<CharSequence>> entry=itr.next();//get
                objarr =new ArrayList<Object>();
                objarr.add(entry.getKey());
                objarr.add(entry.getValue());
                itr.remove();//delete
            }
        }
        return objarr;
    }
}
