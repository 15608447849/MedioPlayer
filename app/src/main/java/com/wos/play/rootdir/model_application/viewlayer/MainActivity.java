package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_monitor.kernes.WatchServer;
import com.wos.play.rootdir.model_monitor.tools.Stools;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.Logs;

import static com.wos.play.rootdir.R.id.main_layout;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {
    private boolean isSend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.e("MainActivity","活动层-------------onCreate----------------------");
        setContentView(R.layout.activity_main);
        setIsOnBack(false);
        setStopOnDestory(false);
        registerBroad(1);

        //发送上线指令 ONLI
        sendMsgCommServer("sendTerminalOnline", null);
    }

    @Override
    protected void receiveService(String result) {
        Logs.i("MainActivity","接收广播：result->"+result);
        if(CMD_INFO.UIRE.equals(result)){
            unInitUI();
            //发送下线指令 OFLI
            sendMsgCommServer("sendTerminalOffLine", null);
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        Logs.e("MainActivity","活动层-------------onStart----------------------");
    }
    @Override
    protected void onResume() {
        super.onResume();
        initUI();
        Logs.e("MainActivity","活动层-------------onResume----------------------");

    }
    @Override
    protected void onPause() {
        super.onPause();
        Logs.e("MainActivity","活动层-------------onPause----------------------");

    }
    @Override
    protected void onStop() {
        Logs.e("MainActivity","活动层-------------onStop----------------------");
        boolean flag = Stools.isRunningForeground(getApplicationContext(), WatchServer.activityList);

        if (!flag){
            unInitUI();
            //发送下线指令 OFLI
            sendMsgCommServer("sendTerminalOffLine", null);
        }

        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.e("MainActivity","活动层-------------onDestroy----------------------");
    }

    //获取activity的底层layout
    @Override
    public ViewGroup getActivityLayout() {
        return (AbsoluteLayout) findViewById(main_layout);
    }
}
