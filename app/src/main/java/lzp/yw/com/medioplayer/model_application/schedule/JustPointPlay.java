package lzp.yw.com.medioplayer.model_application.schedule;

import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

/**
 * Created by user on 2016/11/9.
 */

public class JustPointPlay {
    public static int Just(ScheduleBean entity){
        int result = -1;
        try {
            long startTime = Long.valueOf(entity.getStartTime());
            long endTime = Long.valueOf(entity.getEndTime());
            result = TimeOperator.justStart_EndTime(startTime,endTime);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }
}
