package com.wos.play.rootdir.model_command_.command_arr;

/**
 * Created by user on 2017/1/9.
 */

public class Command_SCRN_RtThread extends Thread {


    /**
     * 接口 - 执行动作对象
     */
    public interface RtThreadAction {
        void action();
        void destorys();
    }

    private static String TAG = "实时截屏线程";
    private volatile boolean isStat = false;

    /**
     * 是否开始
     *
     * @param stat
     */
    public void setStat(boolean stat) {
        isStat = stat;
    }

    private RtThreadAction rta;

    public Command_SCRN_RtThread(RtThreadAction rta) {
        this.rta = rta;
    }
    private int checkTime = 10;//时间 单位:s
    public Command_SCRN_RtThread(RtThreadAction rta, int checkTime) {
        this(rta);
        this.checkTime = checkTime;
    }

    @Override
    public void run() {
        while (isStat){
            try {
                excute();
                Thread.sleep(checkTime*1000);
            } catch (InterruptedException e) {
                isStat = false;
            }
        }
        close();
    }



    private void excute() {
        if (rta!=null){
            rta.action();
        }else{
            isStat = false;
        }
    }
    private void close() {
        if (rta!=null){
            rta.destorys();
        }
    }
}
