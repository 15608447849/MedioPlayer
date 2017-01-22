package com.wos.play.rootdir.model_application.viewlayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_universal.jsonBeanArray.TerminalNo;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ShellUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static cn.trinea.android.common.util.ShellUtils.execCommand;
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

    @Bind(R.id.openPointEdit)
    public EditText openPointEdit;

    private boolean isBind = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkConfigFile()){
            setContentView(R.layout.activity_wostools);
            ButterKnife.bind(this);
            //初始化控件信息
            initViewValue();
            registBroad(1);//注册服务消息广播
            isBind = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind){
            ButterKnife.unbind(this);
            unregistBroad();
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
            serverip.setText(SystemInfos.get().getServerip());
            serverport.setText(SystemInfos.get().getServerport());
            companyid.setText(SystemInfos.get().getCompanyid());
            terminalNo.setText(SystemInfos.get().getTerminalNo());
            heartbeattime.setText(SystemInfos.get().getHeartBeatInterval());//心跳
            StorageLimits.setText(SystemInfos.get().getStorageLimits());//sdcard 阔值
            RestartBeatInterval.setText(SystemInfos.get().getSleepTime());//重启时间
            BasePath.setText(catPathfile(SystemInfos.get().getBasepath()));//资源文件夹
            EpaperPath.setText(catPathfile(SystemInfos.get().getEpaperSourcePath()));//电子报资源文件夹
            SchudulePath.setText(catPathfile(SystemInfos.get().getJsonStore()));//排期json文件夹
            ftpaddress.setText(SystemInfos.get().getFtpAddress());
            ftpport.setText(SystemInfos.get().getFtpPort());
            ftpuser.setText(SystemInfos.get().getFtpUser());
            ftppass.setText(SystemInfos.get().getFtpPass());
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
        SystemInfos.get().setServerport(serverport.getText().toString());
        SystemInfos.get().setStorageLimits(StorageLimits.getText().toString());
        SystemInfos.get().setSleepTime(RestartBeatInterval.getText().toString());
        SystemInfos.get().setHeartBeatInterval(heartbeattime.getText().toString());
        SystemInfos.get().setCompanyid(companyid.getText().toString());
        SystemInfos.get().setTerminalNo(terminalNo.getText().toString());
        SystemInfos.get().setServerip(serverip.getText().toString());
        SystemInfos.get().setFtpAddress(ftpaddress.getText().toString());//ftp 地址
        SystemInfos.get().setFtpPort(ftpport.getText().toString());// 端口
        SystemInfos.get().setFtpUser(ftpuser.getText().toString());// 用户名
        SystemInfos.get().setFtpPass(ftppass.getText().toString());// 密码


        String storeDirc = SdCardTools.getDircConfigPathValue(getApplicationContext());
        if (storeDirc==null){
            new IllegalStateException("没有保存资源文件目录路径,无法获取资源路径主目录.");
        }
        //资源保存路径
        String dirpath = storeDirc + completePath(BasePath.getText().toString());
        if (SdCardTools.MkDir(dirpath)) {
            SystemInfos.get().setBasepath(dirpath);
        }
        //电子报保存路径
        dirpath = storeDirc + completePath(EpaperPath.getText().toString());
        if (SdCardTools.MkDir(dirpath)){
            SystemInfos.get().setEpaperSourcePath(dirpath);
        }
        //json保存路径
        dirpath = storeDirc + completePath(SchudulePath.getText().toString());
        if (SdCardTools.MkDir(dirpath)){
            SystemInfos.get().setJsonStore(dirpath);
        }
        //app icon 路径
        dirpath = storeDirc + "/appicon/";
        if (SdCardTools.MkDir(dirpath)){
            SystemInfos.get().setAppicon(dirpath);
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
        param.put("corpId", SystemInfos.get().getCompanyid());
        param.put("code", SystemInfos.get().getCompanyid());
        param.put("ip", AppsTools.getLocalIpAddress());
        param.put("mac", AppsTools.getMacAddress(getApplicationContext()));
        if (screenSize==null){
            screenSize = AppsTools.getScreenSize(getApplicationContext());
        }
        param.put("screenResolutionWidth",String.valueOf(screenSize[0]));
        param.put("screenResolutionHeight",String.valueOf(screenSize[1]));
       return AppsTools.mapTanslationUri(SystemInfos.get().getServerip(),String.valueOf(SystemInfos.get().getServerport()),param);
    }

    /**
     * 保存
     */
    public boolean save(){
        if (!"".equals(terminalNo.getText().toString())){
            GetViewValue();
            boolean f = SystemInfos.get().saveInfo();
            showToast("保存完成");
            return f;
        }else{
            showToast("保存失败");
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
                checkConfigFile();
            }
        }
    }

    /**
     * 检查配置文件
     * 进入应用界面
     */
    private boolean checkConfigFile() {

        if (SystemInfos.get().isConfig()){
            // 已设置过服务器信息
            //发送上线指令
//          sendMsgCommServer("sendTerminaOnline", null);
            startMain(true);
            this.stopActivityOnArr(this);
            return false;
        }
        return true;
    }
    //跳转到 主页面
    private void startMain(boolean flag) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
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



    //打开本地端口
    public void openPoint(View view){
        int point = 0;
        try {
            point = Integer.parseInt(openPointEdit.getText().toString());
        } catch (NumberFormatException e) {
            showToast("打开端口失败,请输入数字");
            return;
        }
        if (point>1024){
            showToast("打开端口号:"+point);
            final int pt = point;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tryOpenRemotePoint(pt);
                }
            }).start();
        }else{
            showToast("请输入1024-9999任意端口号");
        }
    }

    private void tryOpenRemotePoint(int point) {
        Logs.e(TAG,"tryOpenRemovePoint >>> "+point);
        if (ShellUtils.checkRootPermission()) {
            ShellUtils.CommandResult cr = execCommand(getOpenPointCmd(point), true, true);
            Logs.e(TAG, "远程端口开启结果:" + cr.result);
        }
    }
    //开启远程端口 - 方便调试
    //预留远程端口号
    private String getOpenPointCmd(int point){
        return "setprop service.adb.tcp.port "+point+"\n" +
                "stop adbd\n" +
                "start adbd\n";
    }
}
