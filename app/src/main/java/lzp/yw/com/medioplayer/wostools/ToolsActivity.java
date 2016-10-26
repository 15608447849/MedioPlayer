package lzp.yw.com.medioplayer.wostools;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.baselayer.BaseApplication;
import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.WosResult;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy.HttpProxy;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by user on 2016/10/26.
 * lzp
 * 配置页面
 */
public class ToolsActivity extends BaseActivity{

    private static final String TAG = "ToolsActivity";
    @Bind(R.id.serverip)
    public EditText serverip;
    @Bind(R.id.serverport)
    public EditText serverport;
    @Bind(R.id.companyid)
    public EditText companyid;
    @Bind(R.id.terminalNo)
    public EditText terminalNo;
    @Bind(R.id.BasePath)
    public EditText BasePath;
    @Bind(R.id.HeartBeatInterval)
    public EditText heartbeattime;

    @Bind(R.id.layotu_restartbeattime)
    public LinearLayout restartLayout;

    private ToolsDataListEntity dataList;

    private Subscriber subscriber ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wostools);
        ButterKnife.bind(this);
        //初始化数据
        initData();
        //初始化控件信息
        initViewValue();


    }


    /**
     * 初始化数据对象
     */
    private void initData() {
        if (dataList==null){
            dataList = new ToolsDataListEntity();
        }
        dataList.ReadShareData();
    }
    /**
     *  加载数据
     */
    public void initViewValue()
    {
        try
        {
            serverip.setText(dataList.GetStringDefualt("serverip", "127.0.0.1"));
            serverport.setText(dataList.GetStringDefualt("serverport", "8000"));
            companyid.setText(dataList.GetStringDefualt("companyid", "999"));
            terminalNo.setText(dataList.GetStringDefualt("terminalNo", ""));
            heartbeattime.setText(dataList.GetStringDefualt("HeartBeatInterval", "30"));
            BasePath.setText(dataList.GetStringDefualt("basepath", "mnt/sdcard"));
            //焦点默认在这个控件上
            serverip.setFocusable(true);

        }catch(Exception e)
        {
            Logs.e(TAG, e.getMessage());
        }
    }

    /**
     * 获取控件传入的数据并封装
     */
    public void GetViewValue()
    {
        dataList.put("terminalNo",terminalNo.getText().toString());
        dataList.put("serverip",  serverip.getText().toString());
        dataList.put("serverport",  serverport.getText().toString());
        dataList.put("companyid",  companyid.getText().toString());
        dataList.put("HeartBeatInterval",  heartbeattime.getText().toString());
        String basepath=BasePath.getText().toString();
        if(!basepath.endsWith("/"))
        {
            basepath=basepath+"/";
        }
        dataList.put("basepath",  basepath);
    }

    private void initSubscriber() {
        if (subscriber!=null){
            subscriber.unsubscribe();
            subscriber = null;
        }

        subscriber = newSubscriber(new Action1<WosResult>() {
            @Override
            public void call(WosResult result) {

                if (result!=null){
                    terminalNo.setText(result.getTerminalNo());
                    showToast(" -- 获取终端完成 --");
                    dataList.put("terminalNo", result.getTerminalNo());
                }
            }
        });
        mCompositeSubscription.add(subscriber);
    }
    /**
     * 点击获取id
     * @param view
     */
    public void getId(View view){

        showLoadingDialog();
        //加载控件值
        GetViewValue();
        //访问网络
        getTerminal();
    }
    /**
     * 获取终端
     * @return
     */
    public void getTerminal() {
        HttpProxy.getInstance()
                .init(
                        BaseApplication.appContext,
                        dataList.GetStringDefualt("serverip","127.0.0.1"),
                        dataList.GetStringDefualt("serverport","8000")
                                );
        initSubscriber();
       HttpProxy.getInstance().getTerminal(subscriber);
    }


    /**
     * 保存
     */
    public void save(){
        GetViewValue();
        dataList.SaveShareData();
        if (!"".equals(terminalNo.getText().toString())){
            ToolsUtils.settingServerInfo(true);
            showToast("保存完成");
            //进入应用
        }
    }








}
