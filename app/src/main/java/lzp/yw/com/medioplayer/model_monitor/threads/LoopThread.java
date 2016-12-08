package lzp.yw.com.medioplayer.model_monitor.threads;

/**
 * Created by user on 2016/12/8.
 */

public class LoopThread extends Thread {

    private static final String TAG = "监听服务";

    private volatile boolean isFlag = false;
    private int sleepTime = 10;
    private ThreadsInterImp callers;

    public void setCallers(ThreadsInterImp callers) {
        this.callers = callers;
    }

    //设置休眠时间
    public void setSleepTime(int sleepTime){
        this.sleepTime = sleepTime;
    }
    public void startRun(){
        isFlag = true;
    }
    public void closeRun(){
        isFlag =false;
    }

    @Override
    public void run() {
        while(isFlag){
//            Logs.i(TAG," - - loop thread  is runing - - > "+ Thread.currentThread().getName());
            try {
                if (callers!= null){
                    callers.sendBroadUi();
                    callers.sendBroadWatchServer();
                }
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (callers!= null){
                   callers.overs();
                    closeRun();
                }
            }
        }
    }
}
