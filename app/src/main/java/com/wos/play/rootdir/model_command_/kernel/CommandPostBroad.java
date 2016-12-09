package com.wos.play.rootdir.model_command_.kernel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 2016/11/2.
 */

public class CommandPostBroad extends BroadcastReceiver{
    public static final String ACTION = "com.commandpost.receivebroad";
    public static final String PARAM1 = "cmd";
    public static final String PARAM2 ="param";


    private CommandPostServer server ;
    public CommandPostBroad(CommandPostServer server){
        this.server = server;
    }
    String msgCmd = null;
    String msgParam =  null;
    @Override
    public void onReceive(Context context, Intent intent) {
        msgCmd = intent.getExtras().getString(PARAM1);
        msgParam =  intent.getExtras().getString(PARAM2);
        server.reserveCmd(msgCmd,msgParam);
    }
}
