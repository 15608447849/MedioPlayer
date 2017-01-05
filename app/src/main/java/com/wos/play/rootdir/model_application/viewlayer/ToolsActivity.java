package com.wos.play.rootdir.model_application.viewlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.SystemInitInfo;
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

import static butterknife.ButterKnife.bind;
import static com.wos.play.rootdir.R.id.HeartBeatInterval;

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
    @Bind(R.id.EpaperPath)
    public EditText EpaperPath;
    @Bind(R.id.SchudulePath)
    public EditText SchudulePath;
    @Bind(HeartBeatInterval)
    public EditText heartbeattime;
    @Bind(R.id.StorageLimits)
    public EditText StorageLimits;
    @Bind(R.id.RestartBeatInterval)
    public EditText RestartBeatInterval;

    @Bind(R.id.ftpaddress)
    public EditText ftpaddress;
    @Bind(R.id.ftpport)
    public EditText ftpport;
    @Bind(R.id.ftpuser)
    public EditText ftpuser;
    @Bind(R.id.ftppass)
    public EditText ftppass;


    @Bind(R.id.btnGetID)
    public Button btnGetID;
    @Bind(R.id.btnSaveData)
    public Button btnSaveData;
    private boolean isBind = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroad(1);
        if(gotoApp()){
            setContentView(R.layout.activity_wostools);
            bind(this);
            //初始化控件信息
            initViewValue();
            isBind = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind){
            ButterKnife.unbind(this);
        }
    }

    /**
     *  加载数据
     */
    public void initViewValue()
    {
        try
        {
            btnSaveData.setEnabled(false);
            serverip.setText(SystemInitInfo.get().getServerip());
            serverport.setText(SystemInitInfo.get().getServerport());
            companyid.setText(SystemInitInfo.get().getCompanyid());
            terminalNo.setText(SystemInitInfo.get().getTerminalNo());
            heartbeattime.setText(SystemInitInfo.get().getHeartBeatInterval());//心跳
            StorageLimits.setText(SystemInitInfo.get().getStorageLimits());//sdcard 阔值
            RestartBeatInterval.setText(SystemInitInfo.get().getSleepTime());//重启时间
            BasePath.setText(catPathfile(SystemInitInfo.get().getBasepath()));//资源文件夹
            EpaperPath.setText(catPathfile(SystemInitInfo.get().getEpaperSourcePath()));//电子报资源文件夹
            SchudulePath.setText(catPathfile(SystemInitInfo.get().getJsonStore()));//排期json文件夹
            ftpaddress.setText(SystemInitInfo.get().getFtpAddress());
            ftpport.setText(SystemInitInfo.get().getFtpPort());
            ftpuser.setText(SystemInitInfo.get().getFtpUser());
            ftppass.setText(SystemInitInfo.get().getFtpPass());
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
        SystemInitInfo.get().setServerport(serverport.getText().toString());
        SystemInitInfo.get().setStorageLimits(StorageLimits.getText().toString());
        SystemInitInfo.get().setSleepTime(RestartBeatInterval.getText().toString());
        SystemInitInfo.get().setHeartBeatInterval(heartbeattime.getText().toString());
        SystemInitInfo.get().setCompanyid(companyid.getText().toString());
        SystemInitInfo.get().setTerminalNo(terminalNo.getText().toString());
        SystemInitInfo.get().setServerip(serverip.getText().toString());
        SystemInitInfo.get().setFtpAddress(ftpaddress.getText().toString());//ftp 地址
        SystemInitInfo.get().setFtpPort(ftpport.getText().toString());// 端口
        SystemInitInfo.get().setFtpUser(ftpuser.getText().toString());// 用户名
        SystemInitInfo.get().setFtpPass(ftppass.getText().toString());// 密码
        //资源保存路径
        String dirpath = SdCardTools.getAppSourceDir(this) + completePath(BasePath.getText().toString());
        if (SdCardTools.MkDir(dirpath)) {
            SystemInitInfo.get().setBasepath(dirpath);
        }
        //电子报保存路径
        dirpath =SdCardTools.getAppSourceDir(this) + completePath(EpaperPath.getText().toString());
        if (SdCardTools.MkDir(dirpath)){
            SystemInitInfo.get().setEpaperSourcePath(dirpath);
        }
        //json保存路径
        dirpath = SdCardTools.getAppSourceDir(this) + completePath(SchudulePath.getText().toString());
        if (SdCardTools.MkDir(dirpath)){
            SystemInitInfo.get().setJsonStore(dirpath);
        }
        //app icon 路径
        dirpath = SdCardTools.getAppSourceDir(this) + "/appicon/";
        if (SdCardTools.MkDir(dirpath)){
            SystemInitInfo.get().setAppicon(dirpath);
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
        param.put("corpId",SystemInitInfo.get().getCompanyid());
        param.put("code",SystemInitInfo.get().getCompanyid());
        param.put("ip", AppsTools.getLocalIpAddress());
        param.put("mac", AppsTools.getMacAddress(getApplicationContext()));
        if (screenSize==null){
            screenSize = AppsTools.getScreenSize(getApplicationContext());
        }
        param.put("screenResolutionWidth",String.valueOf(screenSize[0]));
        param.put("screenResolutionHeight",String.valueOf(screenSize[1]));
       return AppsTools.mapTanslationUri(SystemInitInfo.get().getServerip(),String.valueOf(SystemInitInfo.get().getServerport()),param);
    }

    /**
     * 保存
     */
    public boolean save(){
        if (!"".equals(terminalNo.getText().toString())){
            GetViewValue();
            SystemInitInfo.get().saveInfo();
            SystemInitInfo.get().setConfig(true);
            showToast("---保存完成---");
            return true;
        }else{
            SystemInitInfo.get().setConfig(false);
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
        if (SystemInitInfo.get().isConfig()){
            // 已设置过服务器信息
            //发送上线指令
            sendMsgCommServer("sendTerminaOnline", null);
            unregistBroad();
            this.startActivity(new Intent(this, MainActivity.class));
            this.stopActivityOnArr(this);
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
