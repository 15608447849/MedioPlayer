package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.util.Log;

import com.wos.play.rootdir.model_command_.kernel.iCommand;

/**
 * Created by Administrator on 2017/4/28.
 */

public class Command_UPDC implements iCommand {

    private Context context;

    public Command_UPDC(Context context) {
        this.context = context;
    }

    @Override
    public void Execute(String param) {
        // UPDC:FTP://.....APK
        Log.e("更新app传递", "Execute: "+param);
//        if (context!=null || !param.equals("")){
//            Intent intent = new Intent(context, UpdateServer.class);
//            intent.putExtra(UpdateServer.UPDATEKEY,param);
//            context.startService(intent);
//        }

    }
}
