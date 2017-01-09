package com.wos.play.rootdir.model_communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/2.
 * 用于 接受 app -> 通讯的 消息
 * 动态创建
 */
public class CommuniReceiverMsgBroadCasd extends BroadcastReceiver{
    private static final String TAG = "CommuniReceiverMsgBroadCasd";
    public static final String ACTION = "com.communication.receivebroad";
    //通讯服务
    private CommunicationServer commServer;

    public CommuniReceiverMsgBroadCasd(CommunicationServer commServer){
        this.commServer = commServer;
    }
    public static final String PARAM1 = "param1";
    public static final String PARAM2 ="param2";
//    private  String urlparam ;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (commServer!=null){
            commServer.receiveAppMsg(intent.getExtras().getString(PARAM1),intent.getExtras().getString(PARAM2));
        }else{
            Logs.e(TAG,"通讯服务不存在");
        }

    }



}
