package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_application.baselayer.SystemInitInfo;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_communication.CommuniReceiverMsgBroadCasd;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;

/**
 * Created by user on 2016/12/15.
 * 1.服务端通过心跳发送排期至终端后，终端根据排期发起下载素材申请；
 * 相关命令：DLRS:终端编号
 * 方向：终端-服务端
 * <p>
 * 2.服务端根据当前下载带宽和下载情况，通知终端启用排队或开始下载任务；服务端通知终端开始下载时，告知从哪台中继服务器下载的FTP配置（IP，端口，用户名，密码），或者不告知即是使用主服务器的FTP；
 * 相关命令：DLIF:指令+”-”+参数
 * 方向：服务端-终端
 * DLIF:START-[192.168.1.1,21,username:password] 开始下载，告知FTP信息。
 * DLIF:DELAY-2/20 排队下载，当前进度2/20，共有20台终端需要进行下载任务，前面还有2台。
 * <p>
 * 3.终端下载任务结束时，通知服务端下载结束；
 * 相关命令：DLOV:终端编号
 * <p>
 * <p>
 * //单例模式
 */
public class ICommand_DLIF implements iCommand {
    public static final String TAG = "ICommand_DLIF";


    private boolean isInit = false;

    private Context context;

    private UrlList lister;


    private ICommand_DLIF(Context context) {
        if (!isInit) {
            this.context = context;
            this.lister = new UrlList(context);
            this.isInit = true;
        }
    }

    private static ICommand_DLIF ins;

    public static ICommand_DLIF get(Context context) {
        if (ins == null) {
            ins = new ICommand_DLIF(context);
        }
        return ins;
    }

    // 收取任务列表 - (不同进程 -> 通过广播)
    public void saveTaskList(ArrayList<Task> taskList) {
        if (lister != null) {
            lister.addTaskOnList(taskList);
        }
    }

    //收取单个任务
    public void saveTask(Task task) {
        if (lister != null) {
            lister.addTaskOnList(task);
        }
    }

    private Intent intent;
    private Bundle bundle;

    //发送广播 -> 告诉通讯服务 ->我要下载任务 fileDownloadNotifiy()
    private synchronized void sendNotifyToServer(String param) {
        if (context != null) {
            Logs.i(TAG, "发送下载通知到服务器 - " + param);
            if (intent == null) {
                intent = new Intent();
            }
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.clear();
            intent.setAction(CommuniReceiverMsgBroadCasd.ACTION);
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM1, "fileDownloadNotifiy");
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM2, param);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }
    }

    public void downloadStartNotifiy() {
        sendNotifyToServer("DLRS:");//开始下载
    }

    public void downloadEndNotifiy() {
        sendNotifyToServer("DLOV:");//下载完成
    }


    //发送广播 -> 通知UI
    private synchronized void sendUiState() {
        //未实现
    }

    @Override
    public void Execute(String param) {

        //START-[192.168.1.1,21,username,password]
        //   DELAY-2/20
        if (param.startsWith("START")) { //可以下载 后面是ftp服务器的信息
            param = param.substring(param.indexOf("-") + 1);
            downloadZB(param);
        }
        if (param.startsWith("DELAY")) { //不可下载 显示排队进度

        }

    }

    //下载准备
    private void downloadZB(String param) {
        String[] var = null;
        if (param == null) {
            if (param.startsWith("[")) {
                param = param.substring(param.indexOf("[") + 1);
            }
            if (param.endsWith("]")) {
                param = param.substring(param.lastIndexOf("]"));
            }
            var = param.split(",");
        } else {
            var = new String[]{
                    SystemInitInfo.get().getFtpAddress(),
                    SystemInitInfo.get().getFtpPort(),
                    SystemInitInfo.get().getFtpUser(),
                    SystemInitInfo.get().getFtpPass()
            };
        }
        resetFTP(var[0], var[1], var[2], var[3]);
        //发送下载队列
        sendDownList();
//      downloadEndNotifiy();
    }

    private void resetFTP(String ip, String port, String user, String pass) {
        if (lister != null) {
            lister.resetFtp(ip, port, user, pass);
        }
    }

    private void sendDownList() {
        if (lister != null) {
            lister.sendTaskToRemote();
        }
    }
}
