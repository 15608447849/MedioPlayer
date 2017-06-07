package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.epaper.CEpaperView;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopMonitorFiles;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopSuccessInterfaces;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_download.entity.TaskFactory;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.io.File;
import java.util.ArrayList;
import cn.trinea.android.common.util.FileUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by Administrator on 2017/6/6.
 */

public class Command_REEP implements iCommand, LoopSuccessInterfaces {
    private static final String TAG ="电子报更新";

    private Context context;
    public Command_REEP(Context context) {
        this.context = context;
    }

    @Override
    public void Execute(String param) {
        //epaper/18&2017-06-05,2017-06-04,2017-06-02,2017-04-17&7
        if (param != null && !"".equals(param)) {
            updateEPapers(param);
        }
    }

    private void updateEPapers(String param){
        String path = param.substring(0, param.indexOf("&")) + "/";
        String localPath = SystemInfos.get().getEpaperSourcePath() + path;
        String dates = param.substring(param.indexOf("&") + 1, param.lastIndexOf("&"));
        if (FileUtils.isFolderExist(localPath)) {
            UrlList taskStore = new UrlList();
            taskStore.initLoadingList();
            if (dates.contains(",")) {
                String[] date = dates.split(",");
                for (String d : date) {
                    taskStore.addTaskOnList(TaskFactory.gnrTask(path,localPath, d +".zip"));
                    notifyDownLoad(taskStore.getList());
                    sendLoopThread(localPath + d + ".zip");
                }
            } else {
                taskStore.addTaskOnList(TaskFactory.gnrTask(path,localPath, dates +".zip"));
                notifyDownLoad(taskStore.getList());
                sendLoopThread(localPath + dates + ".zip");
            }
        }
    }

    //无效数据源-送入轮询机制
    private void sendLoopThread(String filePath) {
        //开轮询线程
        if (filePath!=null && !UiTools.fileIsExt(filePath)){
            LoopMonitorFiles.getInstance().addMonitorFile(this,filePath);
        }
    }

    private void sendMessage(String filePath) {
        try {
            Logs.d(TAG,"发送REEP指令");
            Intent intent = new Intent(context, CEpaperView.class);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            Logs.e("REEP指令","========= REEP指令 ===========");
            e.printStackTrace();
        }
    }

    //循环线程资源存在回传
    @Override
    public void sourceExist(final String data, boolean isFile) {
        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (data != null) {
                    Logs.e(TAG, data + " 下载完成");
                    //sendMessage(data);
                }
            }
        });
    }

    //通知服务器 可以下载了
    private void notifyDownLoad(ArrayList<Task> list) {
        ICommand_DLIF.get(context).saveTaskList(list);
        ICommand_DLIF.get(context).downloadStartNotifiy();
    }
}
