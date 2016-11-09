package lzp.yw.com.medioplayer.model_application.schedule;

import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.Rules;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

/**
 * Created by user on 2016/11/9.
 */

public class JustRepeteType {
    public static int just(ScheduleBean entity){
       Rules.RepeatRulesBean rep = entity.getRules().getRepeatRules();
        int result = -1;
        //判断 类型 (1 每天, 2 每周, 3 每月 4 每年)
        int type = rep.getRepeatType().getCode();

        if (type == 1){
            //每天  - 判断 全天 还是 时间段
            if (rep.isRepeatWholeDay()){
//                判断时间段
                long startTime = Long.valueOf(TimeOperator.dateToStamp(rep.getStartTime()));
                long endTime = Long.valueOf(TimeOperator.dateToStamp(rep.getEndTime()));
                result = TimeOperator.justStart_EndTime(startTime,endTime);
            }else{
                //fasle 全天
                //开始时间 - 这个时间
                entity.setStartTime(TimeOperator.dateToStamp(rep.getStartTime()));
                //结束时间 - 今天的 23:59
                entity.setEndTime(TimeOperator.dateToStamp(TimeOperator.getToday()+" "+"23:59:59"));
                result = 2;
            }
        }

        if (type == 2){

        }

        if (type == 3){

        }

        if (type == 4){

        }


     return result;
    }
}
