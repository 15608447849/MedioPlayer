package lzp.yw.com.medioplayer.viewlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.baselayer.BaseApplication;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy.HttpProxy;
import lzp.yw.com.medioplayer.wosappserver.CommunicationServer;
import lzp.yw.com.medioplayer.wosappserver.LoaderServer;
import lzp.yw.com.medioplayer.wostools.ToolsActivity;
import lzp.yw.com.medioplayer.wostools.ToolsDataListEntity;
import lzp.yw.com.medioplayer.wostools.ToolsUtils;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity{
    private ToolsDataListEntity dataList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (dataList==null){
            dataList = new ToolsDataListEntity();
        }
        dataList.ReadShareData();
         HttpProxy.getInstance().initProxy(
                 dataList.GetStringDefualt("serverip","127.0.0.1"),
                 dataList.GetStringDefualt("serverport","8000"),
                 BaseApplication.appContext
        );
    }

    public void backTop(View v){
        ToolsUtils.settingServerInfo(false);
        this.startActivity(new Intent(this, ToolsActivity.class));
    }

    public void serverStart(View v){
        this.startService(new Intent(this, LoaderServer.class));
        this.startService(new Intent(this, CommunicationServer.class));
    }

    public void serverStop(View v){
        this.stopService(new Intent(this, CommunicationServer.class));
        this.stopService(new Intent(this, LoaderServer.class));
    }

}
