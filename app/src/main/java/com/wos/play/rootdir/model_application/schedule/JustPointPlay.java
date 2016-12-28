package com.wos.play.rootdir.model_application.schedule;

import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

/**
 * Created by user on 2016/11/9.
 * 点播~
 */

public class JustPointPlay {
    public static int Just(ScheduleBean entity){
        int result = -1;
        try {
//            long startTime = Long.valueOf(entity.getStartTime());
//            long endTime = Long.valueOf(entity.getEndTime());
            long startTime = Long.valueOf(TimeOperator.dateToStamp(entity.getStartTime()));
            long endTime = Long.valueOf(TimeOperator.dateToStamp(entity.getEndTime()));
            result = TimeOperator.justStart_EndTime(startTime,endTime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 2016-12-26 09:00:00 -> 今天的日期 09:00:00
    //2016-12-26 20:00:00 -> 今天的日期 20:00:00
}
