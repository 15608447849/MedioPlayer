package lzp.yw.com.medioplayer.model_universal.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by user on 2016/11/7.
 */

public class SdcardBroad extends BroadcastReceiver{
    private static final String TAG = "SDcard广播";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_MEDIA_EJECT)){
            Log.e(TAG,"- - Intent.ACTION_MEDIA_EJECT 关机");
        }else if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            Log.e(TAG,"- - Intent.ACTION_MEDIA_MOUNTED 开机");
            if (context==null){
                Log.e(TAG,"context is null");
                return;
            }
            SdCardTools.checkSdCard(context);
            //开启监听服务
//            context.startService(new Intent(context, WatchServer.class));
        }
    }
}
