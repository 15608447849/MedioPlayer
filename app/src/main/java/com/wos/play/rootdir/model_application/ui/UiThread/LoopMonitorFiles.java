package com.wos.play.rootdir.model_application.ui.UiThread;

import android.util.Log;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.trinea.android.common.util.FileUtils;
import com.wos.play.rootdir.model_universal.tool.AppsTools;


/**
 * Created by Administrator on 2017/5/15.
 */

public class LoopMonitorFiles {
    private static final String TAG = LoopMonitorFiles.class.getSimpleName();
    private final Map<LoopSuccessInterfaces,List<String>> cache= new ConcurrentHashMap<>();
    private final Object mLock = new Object();
    private LoopSourceThread loopSourceThread;



    private static class MySingletonHandler{
        private static LoopMonitorFiles instance = new LoopMonitorFiles();
    }

    /**
     * 循环检测线程
     */
    private class LoopSourceThread extends Thread{
        private long time = 0L;
        private boolean isStart = false;
        private void startLoop(){
            isStart = true;

        }
        private void stopLoop() {
            isStart = false;
        }

        @Override
        public void run() {
            while (isStart){
                time  = System.currentTimeMillis();
                if(looping()) continue;
                Log.i(TAG,"遍历文件花费时间:"+(System.currentTimeMillis() -time));
                try {
                    Thread.sleep(AppsTools.randomNum(10,30)*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 复制迭代数据
     */
    private class CopiedIterator<T> implements Iterator<T> {
        private Iterator<T> iterator = null;
        CopiedIterator(Iterator<T> itr) {
            LinkedList<T> list = new LinkedList<>( );
            while(itr.hasNext( )) {
                list.add(itr.next( ));
            }
            this.iterator = list.iterator( );
        }
        public boolean hasNext( ) {
            return this.iterator.hasNext( );
        }
        public void remove( ) {
            throw new UnsupportedOperationException("This is a read-only iterator.");
        }
        public T next( ) {
            return this.iterator.next( );
        }
    }
    private LoopMonitorFiles(){}

    public static LoopMonitorFiles getInstance() {
        return MySingletonHandler.instance;
    }

    public void addMonitorFile(LoopSuccessInterfaces face, String filePath){
        List<String> fileList = null;
        synchronized (cache) {
            if (cache.containsKey(face)) {
                fileList = cache.get(face);
            }
        }
        synchronized (mLock){
            if(fileList==null){
                fileList = new CopyOnWriteArrayList<>();
            }
            fileList.add(filePath);
        }
        synchronized (cache){
            cache.put(face,fileList);
        }

        // 当有监控的时候，开启轮询进程
        if (loopSourceThread == null) {
            Log.i(TAG,"开启文件监控线程");
            loopSourceThread = new LoopSourceThread();
            loopSourceThread.startLoop();
            loopSourceThread.start();
        }
    }

    public void clearMonitor(LoopSuccessInterfaces face){
        synchronized (cache){
            cache.remove(face);
        }
        // 当没有监控的时候，关闭轮询进程
        if(cache.size()==0 && loopSourceThread != null){
            Log.i(TAG,"停止文件监控线程");
            loopSourceThread.stopLoop();
            loopSourceThread = null;
        }
    }

    private boolean looping() {
        String filePath;
        boolean isFile,isFolder;
        List<String> fileList;
        LoopSuccessInterfaces binder;
        Set<LoopSuccessInterfaces> keySet;
        CopiedIterator<String> iterator = null ;
        synchronized (cache){  //有了Set集合就可以获取其迭代器，取值
            Log.i(TAG,"looping:"+cache.size());
            keySet = cache.keySet();
        }
        for (LoopSuccessInterfaces aKeySet : keySet) {
            binder = aKeySet;
            synchronized (cache){
                fileList = cache.get(binder);
            }
            if(fileList == null) continue;
            synchronized (mLock){
                if (fileList.size()>0) {
                    iterator = new CopiedIterator<>(fileList.iterator());
                }
            }
            while (iterator !=null && iterator.hasNext()) {
                filePath = iterator.next();
                isFile = FileUtils.isFileExist(filePath);
                isFolder = FileUtils.isFolderExist(filePath);
                Log.i(TAG,"filePath:"+filePath +" isFile:"+isFile +" isFolder:"+isFolder);
                if (isFile || isFolder){
                    synchronized (mLock){
                        fileList.remove(filePath);
                    }
                    binder.sourceExist(filePath, isFile);//已存在,通知绑定的接口
                }
            }
            synchronized (cache){
                if (fileList.size()==0) {
                    cache.remove(binder);
                }
            }
        }
        return false;
    }

}
