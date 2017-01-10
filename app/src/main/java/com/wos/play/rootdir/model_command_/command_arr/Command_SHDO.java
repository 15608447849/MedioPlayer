package com.wos.play.rootdir.model_command_.command_arr;


import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by user on 2016/9/6.
 */
public class Command_SHDO implements iCommand {


    private Timer timer;
    private TimerTask timerTask;
    private TimerTask getTask(){
        return new TimerTask() {
            @Override
            public void run() {
                closeTermail();
            }
        };
    }


    @Override
    public void Execute(String param) {
        
        if (param==null || param.equals("")){
            shutDown();
            return;
        }
        
        
        //OPEN:0-13:00:00;1-13:00:00;2-13:00:00;3-13:00:00;4-13:00:00;5-13:00:00;6-13:00:00  - > 自动开机
        //0-15:17:00;1-15:17:00;2-15:17:00;3-15:17:00;4-15:17:00;5-15:17:00;6-15:17:00
        if (param.equals("false")){
            return;
        }
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        timerTask = getTask();
        timer = ICommand_TimeParse.getInstans().parse(param,timerTask);
     }

    //关机
    private void shutDown() {
        Logs.e("关机","========= 关闭终端 ===========");
        ShellUtils.execCommand("reboot -p",true,false);
    }

    private void closeTermail() {
        //# echo  mem>/sys/power/state    使系统进行睡眠
        //# echo  on>/sys/power/state     使系统从睡眠中唤醒过来
        //休眠多久后关机
//        String commands =  "sleep "+time+" && reboot -p";
//        String commands =  "sleep "+time+" && echo mem>/sys/power/state";

        Logs.e("关机","========= 自动关闭 ===========");
        String commands ="echo mem>/sys/power/state";
        ShellUtils.execCommand(commands,true,false);
    }
}
