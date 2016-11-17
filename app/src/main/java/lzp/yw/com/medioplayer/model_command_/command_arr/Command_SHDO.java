package lzp.yw.com.medioplayer.model_command_.command_arr;


import cn.trinea.android.common.util.ShellUtils;
import lzp.yw.com.medioplayer.model_universal.Logs;

/**
 * Created by user on 2016/9/6.
 */
public class Command_SHDO implements iCommand {

    @Override
    public void Execute(String param) {

        Logs.d("SHDO-[" + param+"]");
        //0-15:17:00;1-15:17:00;2-15:17:00;3-15:17:00;4-15:17:00;5-15:17:00;6-15:17:00
        if (param.equals("false")){
            return;
        }

        //关机
        String [] commands = {
                "adb shell\n",
                "su\n",
                "sleep 10 && reboot -p\n"
        };
        ShellUtils.CommandResult cr = ShellUtils.execCommand(commands,true,true);
        if (cr.result!=0){
            Logs.e("关机失败 - " + cr.result);
        }
     }
}
