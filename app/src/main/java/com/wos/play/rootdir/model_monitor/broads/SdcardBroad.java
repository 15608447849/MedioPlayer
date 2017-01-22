package com.wos.play.rootdir.model_monitor.broads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wos.play.rootdir.model_monitor.soexcute.RunJniHelper;
import com.wos.play.rootdir.model_monitor.tools.Stools;


/**
 * Created by user on 2016/11/7.
 */

public class SdcardBroad extends BroadcastReceiver{
    private static final String TAG = "wosreceive";
    public static final String ACTION = "COM.WOS.PLAYER.BROADS";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"WOSPLAYER_RECEIVE_ACTION:   ["+intent.getAction()+"]");

//        if (action.equals(ACTION)){
//
//        }
//        if(action.equals(Intent.ACTION_MEDIA_EJECT)){
//
//        }
        if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            String path = Stools.createRootPath(context);
            RunJniHelper.getInstance().startMservice(context.getPackageName()+"/com.wos.play.rootdir.model_monitor.kernes.WatchServer", path);
        }


    }
}
