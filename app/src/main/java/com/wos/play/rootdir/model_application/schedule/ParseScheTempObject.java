package com.wos.play.rootdir.model_application.schedule;

import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

import java.util.ArrayList;

/**
 * Created by user on 2016/12/26.
 */

public class ParseScheTempObject {
    public int scheType;
    public  ArrayList<ScheduleBean> scheArr;
    public ParseScheTempObject(int scheType, ArrayList<ScheduleBean> scheArr) {
        this.scheType = scheType;
        this.scheArr = scheArr;
    }
}
