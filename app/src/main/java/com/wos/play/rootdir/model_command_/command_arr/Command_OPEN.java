package com.wos.play.rootdir.model_command_.command_arr;

import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by user on 2017/1/10.
 */

public class Command_OPEN implements iCommand {


        private Timer timer;
    private TimerTask timerTask;
    private TimerTask getTask(){
        return new TimerTask() {
            @Override
            public void run() {
                openTermail();
            }
        };
    }
    @Override
    public void Execute(String param) {
        if (param==null||param.equals("")){
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

    private void openTermail() {
        Logs.e("开机","=========== 自动开启 ===========");
        //"sleep "+time+" &&
        //# echo  mem>/sys/power/state    使系统进行睡眠
        //# echo  on>/sys/power/state     使系统从睡眠中唤醒过来
        String commands ="echo on>/sys/power/state";
        ShellUtils.execCommand(commands,true,false);
    }


}
