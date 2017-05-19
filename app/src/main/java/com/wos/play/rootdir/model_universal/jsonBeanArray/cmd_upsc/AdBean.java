package com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc;


/**
 * Created by user on 2016/10/27.
 * 广告页面
 */
public class AdBean extends PagesBean{

    /**
     * id : 365
     * coordX : 0.0
     * coordY : 0.0
     * width : 0.0
     * height : 0.0
     * backgroundColor : #FFFFFF
     * components : []
     * adEnabled : false
     * waitTime : 0
     */
    private int waitTime;
    private boolean adEnabled;

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public boolean isAdEnabled() {
        return adEnabled;
    }

    public void setAdEnabled(boolean adEnabled) {
        this.adEnabled = adEnabled;
    }
}
