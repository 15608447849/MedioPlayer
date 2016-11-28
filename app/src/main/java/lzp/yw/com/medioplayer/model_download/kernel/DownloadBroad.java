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
    public static final String ACTION = "com.download.receivebroad";
    public static final String PARAM0 = "nitifyAction";
    public static final String PARAM1 = "taskList";
    public static final String PARAM2 ="storeDir";
    public static final String PARAM3 = "telminalId";

    private DownloadServer server ;
    public DownloadBroad(DownloadServer server){
        this.server = server;
    }

    private String notifyAction;
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
        terminalNo = intent.getExtras().getString(PARAM2,"0000");//终端id
        savepath = intent.getExtras().getString(PARAM3,"");//终端资源文件路径

        notifyAction = intent.getExtras().getString(PARAM0);
        server.receiveContent(notifyAction,TaskList,savepath,terminalNo);
        TaskList = null;
    }

}
