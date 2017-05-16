package com.wos.play.rootdir.model_command_.kernel;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.wos.play.rootdir.model_command_.command_arr.Command_UIRE;
import com.wos.play.rootdir.model_command_.command_arr.Command_UPDC;
import com.wos.play.rootdir.model_command_.command_arr.Command_OPEN;
import com.wos.play.rootdir.model_command_.command_arr.Command_SCRN;
import com.wos.play.rootdir.model_command_.command_arr.Command_SHDO;
import com.wos.play.rootdir.model_command_.command_arr.Command_SYTI;
import com.wos.play.rootdir.model_command_.command_arr.Command_UPSC;
import com.wos.play.rootdir.model_command_.command_arr.Command_VOLU;
import com.wos.play.rootdir.model_command_.command_arr.ICommand_DLIF;
import com.wos.play.rootdir.model_command_.command_arr.ICommand_REBO;
import com.wos.play.rootdir.model_command_.command_arr.ICommand_SORE_JsonDataStore;
import com.wos.play.rootdir.model_download.override_download_mode.Task;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import cn.trinea.android.common.util.ShellUtils;

public class CommandPostServer extends Service implements iCommand{
    private static final String TAG = "_CommandPostServer";
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.e(TAG," 指令 - onCreate() ");
        createThrad();
        registBroad();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.e(TAG,"指令 - onDestroy() ");
        overThrad();
        unregistBroad();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(TAG,"指令 -  onStartCommand() ");
        return super.onStartCommand(intent, flags, startId);
    }

    private HashMap<String, iCommand> commandList = null;
    private void initData() {
        // 音量控制
        commandList.put(CMD_INFO.VOLU, new Command_VOLU());
        //收到排期
        commandList.put(CMD_INFO.UPSC, new Command_UPSC(getApplicationContext()));
        //下载调度
        commandList.put(CMD_INFO.DLIF, ICommand_DLIF.get(getApplicationContext()));
        //下载完资源 保存json数据
        commandList.put(CMD_INFO.SORE, ICommand_SORE_JsonDataStore.getInstent(getApplicationContext()));
        boolean isRoot = false;
        try {
            //有root权限
            isRoot = ShellUtils.checkRootPermission();
        } catch (Exception e) {
            Logs.e(TAG,"执行root权限判断错误:"+e.getMessage());
        }
        if (isRoot){
            //syncTime时间同步
            commandList.put(CMD_INFO.SYTI,new Command_SYTI(getApplicationContext()));
            //关闭终端
            commandList.put(CMD_INFO.SHDO, new Command_SHDO());
            //开启终端
            commandList.put(CMD_INFO.OPEN, new Command_OPEN());
            //重启终端
            commandList.put(CMD_INFO.REBO, new ICommand_REBO(getApplicationContext()));
            //截屏 - 定时和实时
            commandList.put(CMD_INFO.SCRN,new Command_SCRN(getApplicationContext()));
            //更新apk
            commandList.put(CMD_INFO.UPDC,new Command_UPDC(getApplicationContext()));
            //重启播放器
            commandList.put(CMD_INFO.UIRE,new Command_UIRE(getApplicationContext()));
        }else{
            Logs.e(TAG,"无\'root\'权限,无法执行 [时间同步][截屏][关闭终端]等操作.");
        }
    }

    public CommandPostServer() {

    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private CommandPostBroad appReceive;
    /**
     * 停止广播 destory call
     */
    private void unregistBroad() {
        if (appReceive!=null){
            getApplicationContext().unregisterReceiver(appReceive);
            appReceive = null;
            Logs.i(TAG,"注销 指令 广播");
        }
    }

    /**
     * 注册广播  create call
     */
    private void registBroad() {
        unregistBroad();
        appReceive = new CommandPostBroad(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction(CommandPostBroad.ACTION);
        getApplicationContext().registerReceiver(appReceive, filter); //只需要注册一次

        Logs.i(TAG,"已注册 指令 广播");
    }

   private CommandExecuterThread executer ;

    private void createThrad(){
       overThrad();
        executer = new CommandExecuterThread(this);
        executer.mStart();
    }

    private void overThrad() {
        if (executer!=null){
            executer.mStop();
            executer = null;
        }
    }



    // 命令
    private class CmdObj{
        public String cmd = null;
        public String param = null;

        public CmdObj(String cmd, String param) {
            this.cmd = cmd;
            this.param = param;
        }
    }


    private LinkedList<CmdObj> cmdList ;

    private synchronized void addCmds(String cmd,String param){
        if (cmdList == null){
            cmdList = new LinkedList<>();
        }
        cmdList.add(new CmdObj(cmd,param));
    }
    private synchronized void getCmds(){
        if (cmdList!=null){
            Iterator<CmdObj> iterator = cmdList.iterator();
            if (iterator.hasNext()){
                CmdObj obj = iterator.next();
                Logs.i("obj.param" + obj.cmd + "========");
                executes(obj.cmd,obj.param);
                iterator.remove();
                obj = null;
            }


        }
    }





    /**
     * 收到一个命令 ->放入队列中 -> 轮询队列有命令 取出来 执行
     */
    public void reserveCmd(String cmd, String param){
//        Logs.i(TAG,"命令 ["+cmd+ " ]  -  参数: [ "+ param +" ]");

        if (commandList==null){
            commandList = new HashMap<>();
            initData();
        }
        if (cmd!=null){
            addCmds(cmd,param);
        }

    }

    private void executes(String cmd, String param){
        if (commandList.containsKey(cmd)) {
            Logs.i(TAG,"执行指令[ "+cmd +" ]"+"参数: [ "+ param+" ]"+"\n所在线程 -["+Thread.currentThread().getName()+"] - 线程数:"+Thread.getAllStackTraces().size());
            commandList.get(cmd).Execute(param);
        }
    }


    @Override
    public void Execute(String param) {
        getCmds();
    }

    //ui 更新下载任务
    public void reserveTaskList(ArrayList<Task> parcelableArrayList) {
        if (parcelableArrayList!=null && parcelableArrayList.size()>0 ){
            Logs.i(TAG," UI - 更新下载任务 - 数量:" + parcelableArrayList.size());
            ICommand_DLIF.get(getApplicationContext()).saveTaskList(parcelableArrayList);
            ICommand_DLIF.get(null).downloadStartNotifiy();
        }
    }

}
