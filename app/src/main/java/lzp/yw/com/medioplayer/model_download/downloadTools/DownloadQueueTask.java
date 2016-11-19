package lzp.yw.com.medioplayer.model_download.downloadTools;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/3.
 * 任务队列
 *
 */
public class DownloadQueueTask {
    private static final String TAG ="downloadQueueMagene";

    private static DownloadQueueTask taskManage = null;
    BeingWatched beingWatched = null;
    Watched watcher =null;
    private DownloadQueueTask(Context c){
        beingWatched = new BeingWatched();//受查者
        watcher = new Watched(c);//观察者
        beingWatched.addObserver(watcher);
    }
    public static DownloadQueueTask getManage(Context c){
        if (taskManage == null){
            taskManage = new DownloadQueueTask(c);
        }
        return taskManage;
    }

    /**
     * 任务存储队列
     */
    private ArrayList<ArrayList<CharSequence>>  storeList = null;
    /**
     * 添加任务
     * @param taskList
     */
    public void addItemToStore(ArrayList<CharSequence> taskList,String savepath,String telminalid){
        if (storeList==null){
            storeList = new ArrayList<ArrayList<CharSequence>>();
        }
        storeList.add(taskList);
        beingWatched.excute(this,savepath,telminalid);
    }
    /**
     * 取出任务队列
     * @return
     */
    public  ArrayList<CharSequence> getTaskAndDelete(){
        Logs.i(TAG,"start gettask -> storeList size: "+storeList.size());
        ArrayList<CharSequence> tasklist = null;
        if(storeList.size()==0){
            Logs.e(TAG,"没有 存储的 任务队列 ");
            return tasklist;
        }

        //每次取出一个
        if (storeList.size()>0){//至少存在一个

            Iterator<ArrayList<CharSequence>> itr = storeList.iterator();
            if (itr.hasNext()){//cun zai xia yi ge
                Logs.e(TAG,"存在 任务队列");
                tasklist = itr.next();//get
                itr.remove();//delete
            }
        }
        Logs.i(TAG,"end gettask -> storeList size: "+storeList.size() +"\n"+tasklist);
        return tasklist;
    }

/**************************************************************************************-*/






}
