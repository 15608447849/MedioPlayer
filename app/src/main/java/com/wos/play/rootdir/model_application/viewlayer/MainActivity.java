package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_universal.tool.Logs;

import static com.wos.play.rootdir.R.id.main_layout;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logs.e("MainActivity","活动层-------------onCreate----------------------");
        setContentView(R.layout.activity_main);
        setIsOnBack(false);
        setStopOnDestory(false);
    }
    @Override
    protected void onStart() {
        initUI();
        super.onStart();
        Logs.e("MainActivity","活动层-------------onStart----------------------");
    }
    @Override
    protected void onResume() {
        super.onResume();
        //发送上线指令 ONLI
        sendMsgCommServer("sendTerminaOnline", null);
        Logs.e("MainActivity","活动层-------------onResume----------------------");
    }
    @Override
    protected void onPause() {
        super.onPause();
        //发送下线指令 OFLI
        sendMsgCommServer("sendTerminalOffLine", null);
        Logs.e("MainActivity","活动层-------------onPause----------------------");
    }
    @Override
    protected void onStop() {
        Logs.e("MainActivity","活动层-------------onStop----------------------");
        unInitUI();
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
