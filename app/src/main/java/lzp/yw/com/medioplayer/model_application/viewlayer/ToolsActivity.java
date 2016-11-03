package lzp.yw.com.medioplayer.model_application.viewlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.baselayer.DataListEntiyStore;
import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.appTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.TerminalNo;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by user on 2016/10/26.
 * lzp
 * 配置页面
 */
public class ToolsActivity extends BaseActivity {

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
    @Bind(R.id.btnGetID)
    public Button btnGetID;
    @Bind(R.id.btnSaveData)
    public Button btnSaveData;

    @Bind(R.id.layotu_restartbeattime)
    public LinearLayout restartLayout;

    private DataListEntiyStore dataList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wostools);
        initAllServer();//开启全部服务
        ButterKnife.bind(this);
        gotoApp();
        //初始化数据
        initData();
        //初始化控件信息
        initViewValue();

        mBindCommuniServer();//绑定通讯服务

    }


    /**
     * 初始化数据对象
     */
    private void initData() {
        if (dataList==null){
            dataList = new DataListEntiyStore(getApplicationContext());
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
            btnSaveData.setEnabled(false);
            serverip.setText(dataList.GetStringDefualt("serverip", "172.16.0.14"));
            serverport.setText(dataList.GetStringDefualt("serverport", "9000"));
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

    /**
     *  是否正在获取数据中
     */
    private boolean isGetDataing = false;

    /**
     * 点击获取id
     * @param view
     */
    public void getId(View view){

        if (!isGetDataing){  //不在获取数据中
            //加载控件值
            GetViewValue();
            //访问网络(通过 通信服务)
            getTerminal();
        }

    }
    /**
     * 制作数据 url
     * 通过通讯服务
     * 获取终端
     * @return
     */
    public void getTerminal() {
        //设置不可点击
        btnGetID.setEnabled(false);
        terminalNo.setText("");
        sendMsgCommServer("GetTerminalId", makeUri());
    }

    private Map<String,String> param ;
    private int[] screenSize;
    /**
     * uri 获取终端id
     * @return
     */
    private String makeUri() {
        if (param==null) {
            param = new HashMap<String, String>();
        }else{
            param.clear();
        }
        param.put("version",String.valueOf(appTools.getLocalVersionCode(getApplicationContext())));
        param.put("corpId",dataList.GetStringDefualt("companyid","999"));
        param.put("code",dataList.GetStringDefualt("companyid","999"));
        param.put("ip", appTools.getLocalIpAddress());
        param.put("mac",appTools.getLocalMacAddressFromBusybox());
        if (screenSize==null){
            screenSize = appTools.getScreenSize(getApplicationContext());
        }
        param.put("screenResolutionWidth",String.valueOf(screenSize[0]));
      param.put("screenResolutionHeight",String.valueOf(screenSize[1]));

       return appTools.mapTanslationUri(dataList.GetStringDefualt("serverip","127.0.0.1"),dataList.GetStringDefualt("serverport","8000"),param);
    }

    /**
     * 保存
     */
    public boolean save(){
        GetViewValue();
        dataList.SaveShareData();
        if (!"".equals(terminalNo.getText().toString())){
            DataListEntiyStore.settingServerInfo(getApplicationContext(),true);
            showToast("---保存完成---");
            return true;
        }else{
            DataListEntiyStore.settingServerInfo(getApplicationContext(),false);
            showToast("-- 保存失败 --");
            return false;
        }
    }

    /**
     * 点击保存数据
     * @param view
     */
    public void saveData(View view){
        if (!isGetDataing){ //不在获取数据中
            if(save()){
                //发送上线指令
                sendMsgCommServer("sendTerminaOnline", null);
                //进入应用
                gotoApp();
            };
        }
    }

    /**
     * 进入应用界面
     */
    private void gotoApp() {
        if (DataListEntiyStore.isSettingServerInfo(getApplicationContext())){
            // 已设置过服务器信息
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        }
    }
    @Override
    public void receiveService(String result) {
        super.receiveService(result);
        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (!result.equals("failure")){
                    TerminalNo teln = appTools.parseJsonWithGson(result,TerminalNo.class);
                    if (teln != null){
                        terminalNo.setText(teln.getTerminalNo());
                        showToast(" -- 获取终端完成 --");
                        dataList.put("terminalNo", teln.getTerminalNo());
                        btnSaveData.setEnabled(true);
                    }
                }else{
                    btnGetID.setEnabled(true); //完成数据获取 数据不正确
                    showToast("ip 或 端口 无效 ,请重试");
                }
            }
        });
    }
}
