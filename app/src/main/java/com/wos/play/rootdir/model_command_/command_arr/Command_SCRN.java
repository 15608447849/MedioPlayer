package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_communication.CommuniReceiverMsgBroadCasd;
import com.wos.play.rootdir.model_download.entity.TaskFactory;
import com.wos.play.rootdir.model_download.kernel.DownloadBroad;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by user on 2017/1/6.
 *      定时截屏
 *  1 获取时间 -创建定时器
 *  2 获取图片 -发送ftp
 *  3 通知服务器
 */

public class Command_SCRN implements iCommand,Command_SCRN_RtThread.RtThreadAction{
    private static final String TAG ="截屏";
    private Context context;
    private Timer timer = null ;
    private TimerTask timerTask = null;
    //定时截屏任务
    private TimerTask getTask(){
        return new TimerTask() {
            @Override
            public void run() {
                excute();
                System.gc();
            }
        };
    }
    //实时截屏
    private Command_SCRN_RtThread rThread;

    private void startRealTimeScreen(int loopTime){
        stopRealTimeScreen();
        if (rThread==null && loopTime>0){
            rThread = new Command_SCRN_RtThread(this,loopTime);
            rThread.setStat(true);
            rThread.start();
        }
    }
    private  void  stopRealTimeScreen(){
        if (rThread!=null){
            rThread.setStat(false);
            rThread=null;
        }
    }



    public Command_SCRN(Context context) {
        this.context = context;
    }

    /**
     * 截屏命令
     */
//    public static String screenCommand = "su\nscreencap -p #\nexit";
    public static String screenCommand = "screencap -p #";
    //设置 本地文件路径
    private final String savePath = "/mnt/sdcard/tems/";

    @Override
    public void Execute(String param) {
        if (param==null && "".equals(param)){
            return;
        }
        if (!FileUtils.isFolderExist(savePath)){
            FileUtils.makeDirs(savePath);//创建临时目录
        }

        if (param.contains("-") && param.contains(":")){ //定时截屏
            setScheduleTask(param);
        }else{
            //实时截屏 SCRN:0 - 结束 SCRN:6 每6秒 发送一张截屏
            //开启实时截屏线程
            int loopTime = 0;
            try {
                loopTime = Integer.parseInt(param);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            startRealTimeScreen(loopTime);
        }


    }
    //设置定时任务
    private void setScheduleTask(String timeParam) {
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        //设置定时器
        timerTask = getTask();
        timer = ICommand_TimeParse.getInstans().parse(timeParam,timerTask);
    }

    //定时任务入口
    private void excute() {

        //设置文件名
        String filename = TimeOperator.getToday(true,true,true,true,true,true,"-","#")+".png";
        //设置 远程文件目录
        String remotePath = "/Android/"+ SystemInfos.get().getTerminalNo()+"/ScreenCapturerDirc/";
        //设置响应服务器
        String ftpUrl = getFTPUrls(remotePath+filename);

        screenImage(savePath+filename); //截屏
        uploadFTP(savePath+filename,remotePath);//上传
        notifyServer(ftpUrl);//通知
    }


    //ftp_url   -> [ CAPT:10000406（终端号）,1483786140221（日期）,ftp://ftp:FTPmedia@172.16.0.17:21/ShotcutPic-10000406/1483786140221.jpg（ftp地址）]
    private String getFTPUrls(String str) {
        return "CAPT:"
                + SystemInfos.get().getTerminalNo()+","
                +TimeOperator.dateToStamp()+","
                +"ftp://"+ SystemInfos.get().getFtpUser()+":"+ SystemInfos.get().getFtpPass()+"@"+ SystemInfos.get().getFtpAddress()+":"+ SystemInfos.get().getFtpPort()+str;
    }

    //生成图片
    private void screenImage(String savePath){
        try {
            Logs.d(TAG,"开始截屏");
            String cmd = screenCommand.replace("#",savePath);
            ShellUtils.execCommand(cmd,true,false);
            Logs.d(TAG,"截屏成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上传ftp服务器
    private void uploadFTP(String LocalFilePath,String remoteFilePath) {
        sendTaskToLocalServer(TaskFactory.gnrTaskUploadFTP(LocalFilePath,remoteFilePath));
    }
    //上传任务中心
    private synchronized void sendTaskToLocalServer(Task task) {
        if (task!=null){
            //创建任务
            ArrayList<Task> tasks = new ArrayList<>();
            tasks.add(task);
            //发送广播
            if (context!=null){
                Intent intent = new Intent();
                intent.setAction(DownloadBroad.ACTION);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(DownloadBroad.PARAM1, tasks);
                intent.putExtras(bundle);
                context.sendBroadcast(intent);
            }
        }
    }

    //上传截屏地址到服务器 -> pointTimeScreen();
    private void notifyServer(String ftpUrl) {
        if (context!=null){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            intent.setAction(CommuniReceiverMsgBroadCasd.ACTION);
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM1, "pointTimeScreen");
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM2, ftpUrl);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }
    }

    //回调 - 实时截屏 处理
    @Override
    public void action() {
        String imagePath = savePath+"real_"+TimeOperator.getToday(true,true,true,true,true,true,"","")+".png";
        //截屏
        screenImage(imagePath);
        //发送任务到文件上传
        uploadServer(imagePath);
    }

    @Override
    public void destorys() {
        //删除目录
        if(FileUtils.isFolderExist(savePath)) {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(new File(savePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //文件上传接口 -> http://配置的IP地址:端口号/terminal/jpgUpload?terminalId=终端号
    private final String uploadUrl = "http://"+ SystemInfos.get().getServerip()+":"+ SystemInfos.get().getServerport()+"/terminal/jpgUpload?terminalId="+ SystemInfos.get().getTerminalNo();
   // private final String uploadUrl = "http://"+"172.16.2.74"+":"+"9000"+"/terminal/jpgUpload?terminalId="+"10000013";
    private void uploadServer(String localpath) {

       sendTaskToLocalServer(TaskFactory.gnrTaskUploadHTTP(uploadUrl,localpath));
    }

}
