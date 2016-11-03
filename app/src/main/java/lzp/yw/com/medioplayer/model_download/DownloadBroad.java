package lzp.yw.com.medioplayer.model_download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.model_universal.Logs;

/**
 * Created by user on 2016/11/3.
 */

public class DownloadBroad extends BroadcastReceiver {
    public static final String ACTION = "com.download.receivebroad";
    public static final String PARAM1 = "taskList";
    public static final String PARAM2 ="storeDir";
    public static final String PARAM3 = "telminalId";

    private DownloadServer server ;
    public DownloadBroad(DownloadServer server){
        this.server = server;
    }

    private String terminalNo ;
    private String savepath ;
    private ArrayList<CharSequence> TaskList = null;
    @Override
    public void onReceive(Context context, Intent intent) {

        TaskList =   intent.getExtras().getCharSequenceArrayList(PARAM1);
        if (TaskList == null || TaskList.size() == 0){
            Logs.e("","DownloadBroad 收到空任务队列 !!!");
            return;
        }
        terminalNo = intent.getExtras().getString(PARAM2,"0000");
        savepath = intent.getExtras().getString(PARAM3,"/sdcare/playerErr/");
        server.receiveContent(TaskList,savepath,terminalNo);
        TaskList = null;
    }

}
