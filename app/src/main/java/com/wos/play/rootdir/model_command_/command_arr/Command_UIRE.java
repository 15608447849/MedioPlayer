package com.wos.play.rootdir.model_command_.command_arr;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_application.baselayer.AppMessageBroad;
import com.wos.play.rootdir.model_application.viewlayer.ToolsActivity;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by Administrator on 2017/5/5.
 */

public class Command_UIRE implements iCommand {

    private Context context;

    public Command_UIRE(Context context) {
        this.context = context;
    }

    @Override
    public void Execute(String param) {
        if (param==null || param.equals("")){
            reStartApp();
        }
    }

    //重启播放器
    private void reStartApp() {
        Logs.i("重启播放器","========= 重启播放器 ===========");
        try {
            Intent i = new Intent();
            i.setAction(AppMessageBroad.ACTION);
            i.putExtra(AppMessageBroad.PARAM1, CMD_INFO.UIRE);
            context.sendBroadcast(i);
        } catch (Exception e) {
            Logs.e("重启播放器","========= 广播异常 ===========");
            e.printStackTrace();
        }

        try {
            Intent intent = new Intent(context, ToolsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Logs.e("重启播放器","========= 重启异常 ===========");
            e.printStackTrace();
        }

    }
}
