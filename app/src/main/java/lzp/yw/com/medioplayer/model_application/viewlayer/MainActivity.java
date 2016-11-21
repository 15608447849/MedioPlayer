package lzp.yw.com.medioplayer.model_application.viewlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.baselayer.DataListEntiyStore;
import lzp.yw.com.medioplayer.model_application.schedule.ScheduleReadBroad;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {
    private AbsoluteLayout main_layout;//底层 主布局
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_layout = (AbsoluteLayout) findViewById(R.id.main_layout);
        initUI();
        //开启指令
        initAllServer("command");
        //开启下载
        initAllServer("download");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unInitUI();
        //关闭所有
        closeAllServer("command");
        closeAllServer("download");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAllServer("communication");
        //读取排期
//        readSchuduler();
    }



    @Override
    protected void onPause() {
        super.onPause();
        closeAllServer("communication");
    }

    @Override
    public ViewGroup getActivityLayout() {
        return main_layout;
    }

    private DataListEntiyStore des;
    private Intent intent =null;
    private Bundle bundle = null;
    //读取排期
    private void readSchuduler() {
        if (intent == null){
            intent = new Intent();
        }
        if (bundle == null){
            bundle = new Bundle();
        }
        if (des==null){
            des = new DataListEntiyStore(this);
            des.ReadShareData();
        }
        bundle.clear();
        intent.setAction(ScheduleReadBroad.ACTION);
        bundle.putString(ScheduleReadBroad.PARAM, des.GetStringDefualt("jsonStore",""));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }
}
