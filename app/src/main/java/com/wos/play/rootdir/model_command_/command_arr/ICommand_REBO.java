package com.wos.play.rootdir.model_command_.command_arr;

import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_universal.tool.Logs;

import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by user on2017/1/10.
 * 重启
 */

public class ICommand_REBO implements iCommand {

    @Override
    public void Execute(String param) {
        if (param==null || param.equals("")){
            reBoot();
        }
    }

    //重启
    private void reBoot() {
        Logs.e("重启","========= 重启终端 ===========");
        ShellUtils.execCommand("reboot -p",true,false);
    }
}
