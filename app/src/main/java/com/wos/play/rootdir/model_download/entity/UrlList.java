package com.wos.play.rootdir.model_download.entity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_download.kernel.DownloadBroad;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2016/11/19.
 */

public class UrlList {
    private static final String TAG = "_UrlList";
    private ArrayList<Task> taskLists = null;

    private Context context;
    private Intent intent ;
    private Bundle bundle;


    public UrlList() {
        taskLists = new ArrayList<>();
    }

    public UrlList(Context context){
        this();
        this.context = context;
        intent = new Intent();
        intent.setAction(DownloadBroad.ACTION);
        bundle = new Bundle();
    }

    /**
     * 初始化下载列表
     */
    public void initLoadingList() {
        Logs.d(TAG,"当前数量  :" + taskLists.size());
        taskLists.clear();
        Logs.d(TAG, "- - - 任务清空- - - -");
    }

    /**
     * 添加任务
     *
     * @param url
     */
    public synchronized void addTaskOnList(String url) {
//        Logs.i(TAG, " 添加任务 url - " + url);
        if (taskLists == null) {
            return;
        }
        if (url == null || url.equals("") || url.equals("null")) {
            return;
        }

       addTaskOnList(TaskFactory.gnrTask(url));
    }

    /**
     * 添加任务
     *
     * @param task
     */
    public synchronized void addTaskOnList(Task task) {
        if (taskLists == null ) {
            return;
        }
        if (task.getType() == Task.Type.FTP || task.getType() == Task.Type.HTTP || task.getType() == Task.Type.FILE){
           if(FileUtils.isFileExist(task.getSavePath()+task.getFileName())){
                //Logs.d(TAG, "添加任务 task - " + task.printInfo() +" - local exits.");
                return;
           }
        }
        if (!taskLists.contains(task)){
            taskLists.add(task);
            //Logs.d(TAG, "添加任务 task - " + task.printInfo() +" - success");
        }else {
            //Logs.d(TAG, "添加任务 task - " + task.printInfo() +" - list exits.");
        }

        if (AppsTools.isMp4Suffix(task.getFileName())) {
            addTaskOnList(TaskFactory.gnrTask(AppsTools.tanslationMp4ToPng(task.getUrl())));
        }
    }
    //收取任务列表
    public synchronized void addTaskOnList(ArrayList<Task> taskList) {
        for (Task task:taskList){
            addTaskOnList(task);
        }
    }




    public int getListSize() {
        return taskLists.size();
    }

    //发送任务到远程服务
    public void sendTaskToRemote(){
        if (context != null && taskLists !=null && getListSize()>0) {
            Logs.i(TAG, "任务url 队列 - 发送下载任务 到 下载中心 ");
            printTasks();
            //发送任务->下载服务
            bundle.clear();
            bundle.putParcelableArrayList(DownloadBroad.PARAM1, taskLists);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
            initLoadingList();
        }
    }

    private void printTasks() {
        if (taskLists !=null){
            for (Task task : taskLists){
                Logs.d(TAG,"["+task.getFileName()+"]");
            }
        }
    }

    public void destory() {
        if (context!=null){
            this.context = null;
        }
    }

    public ArrayList<Task> getList() {
        return taskLists;
    }

    public ArrayList<String> getTaskListFileNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Task task : taskLists){
            list.add(task.getFileName());
        }
        return list;
    }

    public synchronized  void resetFtp(String ip, String port, String user, String pass) {
        for (Task task : taskLists){
            task.setFtpAddress(ip);
            task.setFtpPort(port);
            task.setFtpUser(user);
            task.setFtpPass(pass);
        }
    }
}
