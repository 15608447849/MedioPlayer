package com.wos.play.rootdir.model_monitor.broads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by user on 2016/11/7.
 */

public class SdcardBroad extends BroadcastReceiver{
    private static final String TAG = "sd卡广播";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG,"["+intent.getAction()+"]");
        if(action.equals(Intent.ACTION_MEDIA_EJECT)){
        }
        if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
        }
    }
}
