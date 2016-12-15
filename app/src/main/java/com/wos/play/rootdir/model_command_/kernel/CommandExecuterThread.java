package com.wos.play.rootdir.model_command_.kernel;

/**
 * Created by user on 2016/12/15.
 */

public class CommandExecuterThread extends Thread {
    private static final String TAG = "_CommandPostServer";

    private volatile  boolean flag = false;

    public void mStart(){
        flag = true;
        this.start();
    }
    public void mStop(){
        flag = false;
    }

    private iCommand icd;
    private int sleepTime = 30;

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public CommandExecuterThread(iCommand icd) {
        this.icd = icd;
    }

    @Override
    public void run() {
        while (flag){
            if (icd!=null){

                try {
                    icd.Execute(null);
                    Thread.sleep(sleepTime * 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
