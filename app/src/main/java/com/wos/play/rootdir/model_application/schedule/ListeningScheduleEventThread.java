package com.wos.play.rootdir.model_application.schedule;

/**
 * Created by user on 2016/12/9.
 */

public class ListeningScheduleEventThread extends Thread{

    /**
     * 轮询时间  默认1分钟
     */
    private int loopTime = 60;



    public void setLoopTime(int loopTime) {
        this.loopTime = loopTime;
    }

    //是否开始
    private volatile boolean isStart = false;
    public void mStart() {
        isStart = true;
        this.start();
    }
    public void mStop() {
        isStart = false;
    }

    /**
     * 排期事件
     */
    private OnScheduleEvent event;

    public void setEvent(OnScheduleEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        while (isStart){
            try {

                if (event!=null){
                    event.loopEvent();
                }
                sleep(loopTime * 1000);
            } catch (Exception e) {
              if (event!=null){
                  event.error(e);
              }
            }
        }
    }



    public interface OnScheduleEvent{
        void loopEvent();
        void error(Exception e);
    }

}
