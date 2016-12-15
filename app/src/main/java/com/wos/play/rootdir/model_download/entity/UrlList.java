package com.wos.play.rootdir.model_download.entity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_download.kernel.DownloadBroad;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/19.
 */

public class UrlList {
    private static final String TAG = "_UrlList";
    private ArrayList<Task> tasks = null;

    private Context context;
    private Intent intent ;
    private Bundle bundle;


    public UrlList() {
        tasks = new ArrayList<>();
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
        Logs.d(TAG,"当前数量  :" + tasks.size());
        tasks.clear();
        Logs.d(TAG, "- - - 任务清空- - - -");
    }

    /**
     * 添加任务
     *
     * @param url
     */
    public synchronized void addTaskOnList(String url) {
//        Logs.i(TAG, " 添加任务 url - " + url);
        if (tasks == null) {
            return;
        }
        if (url == null || url.equals("") || url.equals("null")) {
            Logs.e(TAG, " 添加失败 [" + url+"]");
            return;
        }

        tasks.add(TaskFactory.gnrTask(url));
        Logs.d(TAG, " 添加成功 - [" + url+"]");

    }

    /**
     * 添加任务
     *
     * @param task
     */
    public synchronized void addTaskOnList(Task task) {
        Logs.d(TAG, "添加任务 task - " + task.getFileName());
        if (tasks == null) {
            return;
        }
        tasks.add(task);
        if (AppsTools.isMp4Suffix(task.getFileName())) {
            task.setFileName(AppsTools.tanslationMp4ToPng(task.getFileName()));
            addTaskOnList(task);
        }
    }
    //收取任务列表
    public synchronized void addTaskOnList(ArrayList<Task> taskList) {
        for (Task task:taskList){
            addTaskOnList(task);
        }
    }




    public int getListSize() {
        return tasks.size();
    }

    //发送任务到远程服务
    public void sendTaskToRemote(){
        if (context != null && tasks !=null && getListSize()>0) {
            Logs.i(TAG, "准备下载 - 任务队列大小 : " + getListSize());
            //发送任务->下载服务
            bundle.clear();
            bundle.putParcelableArrayList(DownloadBroad.PARAM1, tasks);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
            Logs.i(TAG,"##########  sendDownLoadTaskList() success ##########");
            initLoadingList();
        }
    }

    public void destory() {
        if (context!=null){
            this.context = null;
        }
    }

    public ArrayList<Task> getList() {
        return tasks;
    }

    public ArrayList<String> getListNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Task task : tasks){
            list.add(task.getFileName());
        }
        return list;
    }

    public synchronized  void resetFtp(String ip, String port, String user, String pass) {
        for (Task task : tasks){
            task.setFtpAddress(ip);
            task.setFtpPort(port);
            task.setFtpUser(user);
            task.setFtpPass(pass);
        }
    }
}
