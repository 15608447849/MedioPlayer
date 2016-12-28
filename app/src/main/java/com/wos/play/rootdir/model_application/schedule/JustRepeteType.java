package com.wos.play.rootdir.model_application.schedule;

import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.Rules;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

/**
 * Created by user on 2016/11/9.
 */

public class JustRepeteType {
    public static int just(ScheduleBean entity) {

        Rules.RepeatRulesBean rep = entity.getRules().getRepeatRules();
        //开始时间 -> 这个时间
        entity.setStartTime(TimeOperator.dateToStamp(rep.getStartTime()));

        int result = -1;

        //判断 类型 (1 每天, 2每周 3 每月 4 每年)
        int type = rep.getRepeatType().getCode();

        if (type == 1) {
            //每天  - 判断 全天ture 还是 时间段false
            result = ifDay(entity, rep);
        }

        if (type == 2) {
            //每周    1 - 获取 今天是这周的第几天  -> 获取 是不是在排期规定的天数中 -> 如果是,判断规定的天数 是一天还是 时间段

            int todayInWeek = TimeOperator.getDateInWeekDay(null);

            if (rep.getWeekday().get(todayInWeek).isChecked()) {
                //是这天 还是 时间段
                result = ifDay(entity, rep);
            } else {
                //不在这天
                result = -1;
            }
        }

        if (type == 3) {//如果是每月

            //获取今天时这个月的第几天
            //是不是和规定的一样
            if (iScheduleMonthInDay(rep.getStartday(), TimeOperator.getDateInMonthDay(null))) {
                result = ifDay(entity, rep);
            } else {
                result = -1;
            }
        }

        if (type == 4) {
            // no ~
        }

        return result;
    }



    private static boolean iScheduleMonthInDay(String startday, int mouthDayNunber) {

        try {
            startday = (startday.split("\\s"))[0];
            startday = startday.substring(startday.lastIndexOf("-") + 1);
            int sDay = Integer.parseInt(startday);
            if (sDay == mouthDayNunber) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    private static int ifDay(ScheduleBean entity, Rules.RepeatRulesBean rep) {
        int result;
        if (isStops_(rep)) {
            return  -1;
        }


        if (rep.isRepeatWholeDay()) {
            //全天
            //结束时间 - 今天的 23:59
            entity.setEndTime(TimeOperator.dateToStamp(TimeOperator.getToday() + "\\s" + "23:59:59"));
            result = 2;
        } else {
            //                判断时间段
            long startTime = Long.valueOf(TimeOperator.dateToStamp(rep.getStartTime()));
            long endTime = Long.valueOf(TimeOperator.dateToStamp(rep.getEndTime()));
            result = TimeOperator.justStart_EndTime(startTime, endTime);
            if (result == 2) {
                //设置结束时间
                entity.setEndTime(TimeOperator.dateToStamp(rep.getEndTime()));
            }
        }
        return result;
    }

    private static boolean isStops_(Rules.RepeatRulesBean rep) {
        //是不是从不停止?
        if (rep.isStop() && rep.getEndday() != null) {
            //有停止时间
            //查看停止时间  是不是在当前时间之后
            String sday = rep.getEndday();
            //判断年
            int mYear = TimeOperator.getDateInYear(null);
            //判断 月份
            int mMonth = TimeOperator.getDateYearInMonth(null);

            int sYear = Integer.parseInt(sday.substring(0, sday.indexOf("-")));
            int sMonth = Integer.parseInt(sday.substring(sday.indexOf("-") + 1, sday.lastIndexOf("-")));

            if (sYear < mYear || sMonth < mMonth) {
                //停止 不符合条件
                return true;
            }
        }
        return false;
    }
}
