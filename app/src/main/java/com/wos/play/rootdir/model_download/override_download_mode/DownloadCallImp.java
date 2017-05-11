package com.wos.play.rootdir.model_download.override_download_mode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_communication.CommuniReceiverMsgBroadCasd;
import com.wos.play.rootdir.model_universal.tool.AppsTools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 2016/11/25.
 */

public class DownloadCallImp {

    private static final java.lang.String TAG = "下载回传实现类";
    private Context context;
    private Intent intent = null;
    private Bundle bundle = null;
    private List<String> msgSendingList;


    //添加一个消息
    private synchronized void addMsgToSend(String msg) {
        try {
            if (msgSendingList == null) {
                msgSendingList = Collections.synchronizedList(new LinkedList<String>()); //消息待发送队列
            }
            msgSendingList.add(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取一个消息
    private synchronized String getMsg() {
        String message = null;
        try {
            if (msgSendingList != null && msgSendingList.size() > 0) {
                Iterator<String> itr = msgSendingList.iterator();
                if (itr.hasNext()) {
                    message = itr.next();
                    itr.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    //是否开始 线程
    private boolean _threadStart = false;
    //消息轮询线程
    private Thread loopMsgThread = null;

    private class LoopThread extends Thread {
        @Override
        public void run() {
            while (_threadStart) {
                try {
                    sendMsgToServer(getMsg());
                    Thread.sleep(AppsTools.randomNum(3, 6) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //发送广播 -> 到服务器
    private synchronized void sendMsgToServer(String param) {
        if (param == null) {
            return;
        }
        if (context != null) {
//            Logs.i(TAG, "send msg to server : " + param);
            if (intent == null) {
                intent = new Intent();
            }
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.clear();
            intent.setAction(CommuniReceiverMsgBroadCasd.ACTION);
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM1, "fileDownloadSpeedOrState");
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM2, param);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }
    }

    public void setContext(Context context) {
        this.context = context;
        if (loopMsgThread == null) {
            loopMsgThread = new LoopThread();
            _threadStart = true;
            loopMsgThread.start();
        }


    }

    public void unSetContext() {
        context = null;
        if (loopMsgThread != null) {
            _threadStart = false;
            loopMsgThread = null;
        }
    }

    /**
     * 生成进度
     */
    public void nitifyMsg(String terminalNo, String filename, int type) {
        addMsgToSend("FTPS:" + terminalNo + ";" + filename + ";" + type);
    }

    /**
     * 生成状态
     */
    public void notifyProgress(String terminalNo, String filename, String process, String speed) {
        String message="PRGS:" + terminalNo + "," + filename + "," + process + "," + speed;
        try {
            addMsgToSend(URLEncoder.encode(message, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 指定类型 不匹配
    public boolean downloadResult(Task task, int DownloadState, String fileName, String filterType) {
        if (fileName.endsWith(filterType)) {
            return true;
        }
        downloadResult(task, DownloadState);
        return false;
    }

    /**
     * @param task
     * @param DownloadState
     */
    public void downloadResult(Task task, int DownloadState) {
        if (DownloadState == 0) { //成功
            nitifyMsg(task.getTerminalNo(), task.getFileName(), 3);
        }
        if (DownloadState == 1) {//失败
            nitifyMsg(task.getTerminalNo(), task.getFileName(), 4);
        }
        if (DownloadState == -1){
            //文件上传
        }
        if (task.getCall() != null) {
            task.getCall().downloadResult(task);
        }
        //删除任务
        TaskQueue.getInstants().finishTask(task);
    }




}
