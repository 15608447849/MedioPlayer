package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_application.baselayer.AppMessageBroad;
import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2017/1/6.
 *  定时截屏/双击截图
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
                execute();
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

//  public static String screenCommand = "su\nscreencap -p #\nexit";

    public static String screenCommand = "screencap -p #";//截屏命令

    private final String savePath = "/mnt/sdcard/tems";//设置 本地文件路径


    @Override
    public void Execute(String param) {
        if (param == null && "".equals(param)) {
            return;
        }
        if (!FileUtils.isFolderExist(savePath)) {
            FileUtils.makeDirs(savePath);//创建临时目录
        }

        if (param.contains("-") && param.contains(":")) {
            setScheduleTask(param);
        } else if ("false".equals(param)) {//param=false时不做处理
            return;
        } else {
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
        if (timer!=null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask!=null) {
            timerTask.cancel();
            timerTask = null;
        }
        //设置定时器
        timerTask = getTask();
        timer = ICommand_TimeParse.getInstans().parse(timeParam,timerTask);
    }

    /**
     * 定时任务入口
     */
    private void execute() {
        String filename = TimeOperator.getToday(true,true,true,true,true,true,"","") +".png";//设置文件名
        //catchScreen(savePath + filename);//定时截屏
        screenImage(savePath + filename);
    }


    /**
     * 发送截屏指令
     * @param savePath
     */
    private void screenImage(String savePath) {
//        try {
//            Logs.d(TAG,"开始截屏");
//            String cmd = screenCommand.replace("#",savePath);
//            ShellUtils.execCommand(cmd,true,false);
//            Logs.d(TAG,"截屏成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            Logs.d(TAG,"发送截屏指令");
            Intent i = new Intent();
            i.setAction(AppMessageBroad.ACTION);
            i.putExtra(AppMessageBroad.PARAM1, CMD_INFO.SCRN);
            i.putExtra(AppMessageBroad.PARAM2, savePath);
            context.sendBroadcast(i);
        } catch (Exception e) {
            Logs.e("截屏指令","========= 截屏指令 ===========");
            e.printStackTrace();
        }

    }

    /**
     * 回调 - 实时截屏 处理
     */
    @Override
    public void action() {
        String imagePath = "real_"+ TimeOperator.getToday(true,true,true,true,true,true,"","") +".png";
        Logs.e(TAG, imagePath);
        screenImage(savePath + imagePath);//截屏
    }

    @Override
    public void destroys() {
        if(FileUtils.isFolderExist(savePath)) {
            try {
                org.apache.commons.io.FileUtils.deleteDirectory(new File(savePath));//删除目录
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
