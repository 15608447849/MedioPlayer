package lzp.yw.com.medioplayer.model_download.override_download_mode;

import android.content.Context;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/25.
 */
public class TaskQueue extends Observable { //被观察者
    private static final String TAG = "下载任务队列";
    private static TaskQueue instants;
    private List<Task> queue;//队列
    private TaskQueue(){
        queue = new LinkedList<>();
//        init(new LoaderHelper());
    }

    //获取队列实例
    public static TaskQueue  getInstants(){
        if (instants==null){
            instants = new TaskQueue();
        }
        return instants;
    }

    //下载助手
    private LoaderHelper helper;
    public void init(Context context,int loaderModel){
        Logs.i(TAG,"初始化 下载队列");
        if (helper==null){
            helper = new LoaderHelper(context,loaderModel);
            //绑定关系
            this.addObserver(helper);
        }
    }
    public void unInit(){
        Logs.i(TAG,"取消 - 初始化 下载队列");
        if (helper!=null){
            this.deleteObserver(helper);
            //绑定关系
            helper.unInitWord();
            helper = null;
        }
        if (queue!=null){
            queue.clear();
        }
    }




    // 添加一项任务
    public synchronized void addTask(Task task) {
        if (task != null) {
//            Logs.i(TAG,"addTask()" +task.getUrl());
            queue.add(task);
            excute();//通知->观察者
        }
    }
    // 完成任务后将它从任务队列中删除
    public synchronized void finishTask(Task task) {
        if (task != null) {
//            log.i(TAG,"finishTask()"+task.getUrl());
            task.setState(Task.State.FINISHED);
            queue.remove(task);
        }
    }
    // 取得一项待执行任务
    public synchronized Task getTask() {

                Iterator<Task> it = queue.iterator();
                Task task;
                while (it.hasNext()) {
                    task = it.next();
                    //寻找一个新建的任务
                    if (Task.State.NEW == task.getState()) {
                        //把任务状态置为运行中
                        task.setState(Task.State.RUNNING);
//                        log.i(TAG,"getTask()"+task.getUrl() +"queue size = "+queue.size());
                        return task;
                    }
                }
                return null;
            }


    private void excute(){
//        log.i(TAG,"excute()");
        setChanged();
        notifyObservers(getTask());  //取出任务
    }
}
