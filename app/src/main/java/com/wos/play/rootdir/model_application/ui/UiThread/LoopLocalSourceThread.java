package com.wos.play.rootdir.model_application.ui.UiThread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;
import com.wos.play.rootdir.model_universal.tool.AppsTools;

/**
 * Created by user on 2016/11/30.
 */

public class LoopLocalSourceThread extends Thread{
    private boolean isStart = false;
    public void startLoop(){
        isStart = true;

    }
    public void stopLoop(){
        isStart = false;
    }

    public LoopLocalSourceThread(LoopSuccessInterfaces binder) {
        this.binder = binder;
    }

    //放入一个资源列表
    private List<String> fileList = null;

    public synchronized void addLoopSource(String sourceName){
        if (fileList==null){
            fileList = new ArrayList<>();
        }
        fileList.add(sourceName);
        System.out.println("loop - add item : "+sourceName);
    }



    private LoopSuccessInterfaces binder;

    private void relase() {
        if (binder!=null){
            binder = null;
        }
        if (fileList!=null){
            fileList=null;
        }
    }

    @Override
    public void run() {
        while (isStart){
            looping();
            try {
                Thread.sleep(AppsTools.randomNum(10,30)*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        relase();
    }

    private synchronized void looping() {
        if (fileList!=null && fileList.size()>0){

            Iterator<String> fileIter = fileList.iterator();

            while (fileIter.hasNext()) {
                String filePath = fileIter.next();
                if (FileUtils.isFileExist(filePath)){
                    //已存在
                    //通知绑定的接口
                    binder.SourceExist(filePath);
                    //删除
                    fileIter.remove();
                }
            }
        }
    }
}
