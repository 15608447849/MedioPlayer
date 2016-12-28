package com.wos.play.rootdir.model_application.schedule;

import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lzp on 2016/11/9.
 *
 */
public class LocalScheduleObject {
    private String start ;
    private String end;
    private ScheduleBean schedule;

    private TimerTask timerTask= null;
    private Timer timer = null;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void startTimer(TimerTask atimerTask, long millisecond){
        stopTimer();
        this.timerTask = atimerTask;
        timer = new Timer();
        timer.schedule(timerTask,millisecond);
        Logs.d("本地时间对象","排期id - "+schedule.getId()+" - "+AppsTools.printTimes(millisecond));
    }
    public void stopTimer(){
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }


    public LocalScheduleObject(String start, String end, ScheduleBean schedule) {
        this.start = start;
        this.end = end;
        this.schedule = schedule;

    }
    public LocalScheduleObject(int type,TimerTask timerTask,long millisecond) {
            this.type = type;
            startTimer(timerTask,millisecond);
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public ScheduleBean getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleBean schedule) {
        this.schedule = schedule;
    }


}
