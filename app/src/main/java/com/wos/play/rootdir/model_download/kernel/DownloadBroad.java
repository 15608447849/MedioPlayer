package com.wos.play.rootdir.model_download.kernel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/3.
 */

public class DownloadBroad extends BroadcastReceiver {
    public static final String TAG = "_download";
    public static final String ACTION = "com.download.receivebroad";
    /**
     * 任务队列
     */
    public static final String PARAM1 = "taskList";


    private DownloadServer server ;
    public DownloadBroad(DownloadServer server){
        this.server = server;
    }

    private ArrayList<Task> TaskList = null;
    @Override
    public void onReceive(Context context, Intent intent) {

        TaskList =  intent.getExtras().getParcelableArrayList(PARAM1);
        if (TaskList == null || TaskList.size() == 0){
            Logs.e(TAG,"DownloadBroad 收到空任务队列 !!!");
            return;
        }
        server.receiveContent(TaskList);
        TaskList = null;
    }

}
