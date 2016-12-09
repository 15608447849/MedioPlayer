package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.DataListEntiyStore;
import com.wos.play.rootdir.model_application.schedule.ScheduleReader;
import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {
    private AbsoluteLayout main_layout;//底层 主布局
    private DataListEntiyStore des;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_layout = (AbsoluteLayout) findViewById(R.id.main_layout);
        //初始化数据存储对象
        des = new DataListEntiyStore(this);
        des.ReadShareData();
        unInitUI();
        Logs.e("MainActivity","-------------onCreate----------------------");
    }

    @Override
    protected void onStop() {
        Logs.e("MainActivity","-------------onStop----------------------");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logs.e("MainActivity","-------------onDestroy----------------------");
        ScheduleReader.getReader().unInit();

        main_layout = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.e("MainActivity","-------------onResume----------------------");
        initUI();

        //初始化排期读取
        ScheduleReader.getReader().initSch(des.GetStringDefualt("jsonStore",""));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //获取activity的底层layout
    @Override
    public ViewGroup getActivityLayout() {
        return main_layout;
    }



}
