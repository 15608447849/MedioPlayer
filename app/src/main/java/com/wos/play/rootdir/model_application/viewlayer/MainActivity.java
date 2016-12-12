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
        setContentView(R.layout.activity_main);
        initUI();
        Logs.e("MainActivity","-------------onCreate----------------------");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Logs.e("MainActivity","-------------onResume----------------------");
    }

    @Override
    protected void onStop() {
        Logs.e("MainActivity","-------------onStop----------------------");
        unInitUI();
        super.onStop();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.e("MainActivity","-------------onDestroy----------------------");
    }



    //获取activity的底层layout
    @Override
    public ViewGroup getActivityLayout() {
        return (AbsoluteLayout) findViewById(main_layout);
    }



}
