package com.wos.play.rootdir.model_application.viewlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.DataListEntiyStore;
import com.wos.play.rootdir.model_universal.jsonBeanArray.TerminalNo;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.SchudulePath)
    public EditText SchudulePath;
    @Bind(R.id.HeartBeatInterval)
    public EditText heartbeattime;
    @Bind(R.id.StorageLimits)
    public EditText StorageLimits;
    @Bind(R.id.RestartBeatInterval)
    public EditText RestartBeatInterval;

    @Bind(R.id.btnGetID)
    public Button btnGetID;
    @Bind(R.id.btnSaveData)
    public Button btnSaveData;

    private DataListEntiyStore dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wostools);
        ButterKnife.bind(this);
        registBroad(1);
        if(gotoApp()){
            //初始化数据
            initData();
            //初始化控件信息
            initViewValue();
        }
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
            heartbeattime.setText(dataList.GetStringDefualt("HeartBeatInterval", "30"));//心跳
            StorageLimits.setText(dataList.GetStringDefualt("storageLimits","50"));//sdcard 阔值
            RestartBeatInterval.setText(dataList.GetStringDefualt("RestartBeatInterval","30"));//重启时间
            BasePath.setText(catPathfile(dataList.GetStringDefualt("basepath", "")));//资源文件夹
            SchudulePath.setText(catPathfile(dataList.GetStringDefualt("jsonStore","")));//排期文件夹
            //焦点默认在这个控件上
            serverip.setFocusable(true);
        }catch(Exception e)
        {
            Logs.e(TAG, e.getMessage());
        }
    }
    //获取资源文件名
    private String catPathfile(String path){
        if (path.contains("/")){
            path = path.substring(0,path.lastIndexOf("/"));
            path = path.substring(path.lastIndexOf("/")+1);
        }
        return path.equals("")?"playlist":path;
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
        dataList.put("storageLimits",StorageLimits.getText().toString());//sdcard 清理阔值
        dataList.put("RestartBeatInterval",RestartBeatInterval.getText().toString()); //重启时间
        dataList.put("HeartBeatInterval",  heartbeattime.getText().toString());



        String dirpath = SdCardTools.getAppSourceDir(this) + completePath(BasePath.getText().toString());
        if (SdCardTools.MkDir(dirpath)) {
            dataList.put("basepath", dirpath);//资源存储的 文件名
        }

        dirpath = SdCardTools.getAppSourceDir(this) + completePath(SchudulePath.getText().toString());
        if (SdCardTools.MkDir(dirpath)){
        dataList.put("jsonStore", dirpath );//json存储的 文件名
        }

        //app icon 路径
        dirpath = SdCardTools.getAppSourceDir(this) + "/appicon/";
        if (SdCardTools.MkDir(dirpath)){
            dataList.put("appicon", dirpath );//json存储的 文件名
        }

    }

    private String completePath(String path){
        //例: xxx前缀 /basepath/资源1
        if (!path.startsWith("/")){
            path = "/"+path;
        }
        if(!path.endsWith("/"))
        {
            path=path+"/";
        }
        return path;
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
        sendMsgCommServer("GetTerminalId", makeUri());//发送消息到通讯服务
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
        param.put("version",String.valueOf(AppsTools.getLocalVersionCode(getApplicationContext())));
        param.put("corpId",dataList.GetStringDefualt("companyid","999"));
        param.put("code",dataList.GetStringDefualt("companyid","999"));
        param.put("ip", AppsTools.getLocalIpAddress());
        param.put("mac", AppsTools.getLocalMacAddressFromBusybox());
        if (screenSize==null){
            screenSize = AppsTools.getScreenSize(getApplicationContext());
        }
        param.put("screenResolutionWidth",String.valueOf(screenSize[0]));
      param.put("screenResolutionHeight",String.valueOf(screenSize[1]));

       return AppsTools.mapTanslationUri(dataList.GetStringDefualt("serverip","127.0.0.1"),dataList.GetStringDefualt("serverport","8000"),param);
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
                //进入应用
                gotoApp();
            }
        }
    }

    /**
     * 进入应用界面
     */
    private boolean gotoApp() {
        if (DataListEntiyStore.isSettingServerInfo(getApplicationContext())){
            // 已设置过服务器信息
            //发送上线指令
            sendMsgCommServer("sendTerminaOnline", null);
            unregistBroad();
            this.startActivity(new Intent(this, MainActivity.class));
            return false;
        }
        return true;
    }
    //收到服务器的返回值
    @Override
    public void receiveService(final String result) {

        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                if (!result.equals("failure")){
                    TerminalNo teln = AppsTools.parseJsonWithGson(result,TerminalNo.class);
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
