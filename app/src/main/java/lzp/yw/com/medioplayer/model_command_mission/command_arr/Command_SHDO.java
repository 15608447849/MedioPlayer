package lzp.yw.com.medioplayer.model_command_mission.command_arr;


import cn.trinea.android.common.util.ShellUtils;
import lzp.yw.com.medioplayer.model_universal.Logs;

/**
 * Created by user on 2016/9/6.
 */
public class Command_SHDO implements iCommand {

    @Override
    public void Execute(String param) {

        Logs.d("SHDO:" + param);

        if (param.equals("false")){
            return;
        }
        //关机
        String [] commands = {
                "adb shell\n",
                "sleep 10 && reboot -p\n"
        };
        ShellUtils.CommandResult cr = ShellUtils.execCommand(commands,true,true);
        String strs = "-------------------------------------------------即将关机: " + cr.result;
        Logs.e(strs);
     }
}
