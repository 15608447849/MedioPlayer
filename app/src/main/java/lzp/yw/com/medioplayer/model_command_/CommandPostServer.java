package lzp.yw.com.medioplayer.model_command_;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.HashMap;

import lzp.yw.com.medioplayer.model_command_.command_arr.Command_SHDO;
import lzp.yw.com.medioplayer.model_command_.command_arr.Command_SYTI;
import lzp.yw.com.medioplayer.model_command_.command_arr.Command_UPSC;
import lzp.yw.com.medioplayer.model_command_.command_arr.Command_VOLU;
import lzp.yw.com.medioplayer.model_command_.command_arr.iCommand;
import lzp.yw.com.medioplayer.model_universal.CMD_INFO;
import lzp.yw.com.medioplayer.model_universal.Logs;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CommandPostServer extends Service {

    private static final String TAG = "_CommandPostServer";
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.e(TAG,"############################## onCreate() ");

        registBroad();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.e(TAG,"############################## onDestroy() ");
        unregistBroad();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logs.e(TAG,"############################## onStartCommand() ");
        return super.onStartCommand(intent, flags, startId);
    }

    private HashMap<String, iCommand> commandList = null;
    private void initData() {

        //syncTime
        commandList.put(CMD_INFO.SYTI,new Command_SYTI());
        // 音量控制
        commandList.put(CMD_INFO.VOLU, new Command_VOLU());
        //关闭终端
        commandList.put(CMD_INFO.SHDO, new Command_SHDO());
        //收到排期
        commandList.put(CMD_INFO.UPSC, new Command_UPSC(getApplicationContext()));
        //下载完资源 保存json数据
        commandList.put(CMD_INFO.SORE,JsonDataStore.getInstent(getApplicationContext()));
    }



    public CommandPostServer() {

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
            Logs.i(TAG,"注销 接受本地app msg -> 到服务器 ,广播");
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

        Logs.i(TAG,"已注册 接受本地app msg -> 到服务器 ,广播");
    }



    private static final Scheduler.Worker helper1 =  Schedulers.newThread().createWorker();
    private static final Scheduler.Worker helper2 =  Schedulers.newThread().createWorker();

    /**
     * 收到一个命令
     */
    public void reserveCmd(final String cmd, final String param){
        Logs.i(TAG,"收到一个命令 ["+cmd+ " ] -  参数: [ "+ param+" ]");

        if (commandList==null){
            commandList = new HashMap<>();
            initData();
        }
        if (commandList.containsKey(cmd)) {
            Logs.i(TAG,"准备 执行指令:"+cmd +" \n 所在线程:"+Thread.currentThread().getName()+" - 当前线程数:"+Thread.getAllStackTraces().size());

            if (!cmd.equals(CMD_INFO.UPSC)){

                helper1.schedule(new Action0() {
                    @Override
                    public void call() {
                        commandList.get(cmd).Execute(param);
                    }
                });
            }else{

                helper2.schedule(new Action0() {
                    @Override
                    public void call() {
                            commandList.get(cmd).Execute(param);
                    }
                });
            }
        }


    }


}
