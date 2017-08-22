package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import cn.trinea.android.common.util.ShellUtils;


/**
 * Created by user on2017/1/10.
 * 重启
 */

public class ICommand_REBO implements iCommand {
    private Context context;

    public ICommand_REBO(Context context) {
        this.context = context;
    }

    @Override
    public void Execute(String param) {
        if (param==null || param.equals("")){
            reBoot();
        }
    }

    //重启
    private void reBoot() {
        Logs.i("重启","========= 重启终端 ===========");
        try{ // 发送下线指令  "http://" + ip + ":" + port + "/terminal/heartBeat?cmd=" + param;
            StringBuilder url = new StringBuilder();
            url.append("http://").append(SystemInfos.get().getServerip()).append(":")
            .append(SystemInfos.get().getServerport()).append("/terminal/heartBeat?cmd=")
            .append("OFLI:").append(SystemInfos.get().getTerminalNo());
            AppsTools.uriTransString(new String(url), null, null);
        }catch (Exception e){
            Logs.e("重启","========= 发送下线指令异常 ===========");
        }finally {
            ShellUtils.execCommand("reboot",true,false);
        }

    }
}
