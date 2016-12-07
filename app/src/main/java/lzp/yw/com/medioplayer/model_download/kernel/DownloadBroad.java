package lzp.yw.com.medioplayer.model_download.kernel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.model_universal.tool.Logs;

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
    /**
     * 保存路径
     */
    public static final String PARAM2 ="storeDir";
    /**
     * 终端id
     */
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

        TaskList =  intent.getExtras().getCharSequenceArrayList(PARAM1);
        terminalNo = intent.getExtras().getString(PARAM2,"0000");//终端id
        savepath = intent.getExtras().getString(PARAM3,"");//终端资源文件路径
        if (TaskList == null || TaskList.size() == 0){
            Logs.e(TAG,"DownloadBroad 收到空任务队列 !!!");
            return;
        }
        Logs.i(TAG,"下载入口广播 收到 任务队列 - 终端id - "+terminalNo+" - 资源保存路径 - "+savepath);
        server.receiveContent(TaskList,savepath,terminalNo);
        TaskList = null;
    }

}
