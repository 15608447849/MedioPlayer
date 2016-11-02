package lzp.yw.com.medioplayer.model_communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 2016/11/2.
 * 用于 接受 app -> 通讯的 消息
 * 动态创建
 */
public class CommuniReceiverMsgBroadCasd extends BroadcastReceiver{
    public static final String ACTION = "com.communication.receivebroad";
    //通讯服务
    private CommunicationServer commServer;

    public CommuniReceiverMsgBroadCasd(CommunicationServer commServer){
        this.commServer = commServer;
    }


    public static final String PARAM1 = "mothername";
    public static final String PARAM2 ="url";
    @Override
    public void onReceive(Context context, Intent intent) {


    }



}
