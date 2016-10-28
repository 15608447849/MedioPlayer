package lzp.yw.com.medioplayer.wosappbroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

import lzp.yw.com.medioplayer.baselayer.CMD_INFO;
import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.wos_command.iCommand;
import lzp.yw.com.medioplayer.wos_command.imp.Command_SHDO;
import lzp.yw.com.medioplayer.wos_command.imp.Command_SYTI;
import lzp.yw.com.medioplayer.wos_command.imp.Command_UPSC;
import lzp.yw.com.medioplayer.wos_command.imp.Command_VOLU;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/10/27.
 */
public class CommunicationBroadcast extends BroadcastReceiver{

    private static final java.lang.String TAG ="_CommunicationBroadcast";
    public static final String Action = "com.cmd.broad";
    public static final String Cmd = "toCmd";
    public static final String Param = "toParam";

    private static final Scheduler.Worker helper1 =  Schedulers.newThread().createWorker();
    private static final Scheduler.Worker helper2 =  Schedulers.newThread().createWorker();

    String msgCmd = null;
    String msgParam =  null;
    @Override
    public void onReceive(Context context, Intent intent) {
        String msgCmd = intent.getExtras().getString(Cmd);
        String msgParam =  intent.getExtras().getString(Param);
        if (msgCmd==null) return;
        Logs.i(TAG,"收到一个命令 "+msgCmd+ " ; 参数:"+ msgParam);
        postCmd(msgCmd,msgParam);
    }



    private static HashMap<String, iCommand> commandList = new HashMap<String, iCommand>();
    static {
        //syncTime
        commandList.put(CMD_INFO.SYTI,new Command_SYTI());
        // 音量控制
        commandList.put(CMD_INFO.VOLU, new Command_VOLU());
        //关闭终端
        commandList.put(CMD_INFO.SHDO, new Command_SHDO());
        //收到排期
        commandList.put(CMD_INFO.UPSC, new Command_UPSC());
    }
    private void postCmd(final String cmd, final String param){

        if (commandList.containsKey(cmd)) {
            Logs.i("准备 执行指令:"+cmd +" \n 所在线程:"+Thread.currentThread().getName()+"- 当前线程数:"+Thread.getAllStackTraces().size());
            if (cmd.equals(CMD_INFO.REBO) || cmd.equals(CMD_INFO.UIRE) || cmd.equals(CMD_INFO.UPDC)
                    || cmd.equals(CMD_INFO.SHDO) || cmd.equals(CMD_INFO.SHDP) || cmd.equals(CMD_INFO.UPLG)
                    || cmd.equals(CMD_INFO.SCRN) || cmd.equals(CMD_INFO.CAPT) || cmd.equals(CMD_INFO.TSLT)
                    ){

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
