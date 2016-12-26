package com.wos.play.rootdir.model_application.schedule;

import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

import static com.wos.play.rootdir.model_application.schedule.Efficacy.TYPE.error;

/**
 * Created by user on 2016/12/26.
 */

public class Efficacy {

    public interface TYPE{
        int error = -1;
        /**
         * 在当前时间之前
         */
        int before_the_current_time = 1;
        /**
         * 在当前时间之后
         */
        int after_the_current_time = 3;
        /**
         * 在当前时间之间
         */
        int in_the_current_time = 2;
    }


    /**
     * 判断 时间
     *
     * @param entity //如果开始时间 和结束时间 在当前时间 之前 - 继续 1
     *               //如果开始时间 < 当前时间 < 结束时间  保留 ->返回      2
     *               //如果 当前时间 < 开始时间 < 结束时间  保留 -> 添加到未到时间的排期 3
     *               //以上规则全部不符合 -1
     */
    public static int  justTime(ScheduleBean entity) {
        int result = error;
        int type = entity.getType();

        if (type == 2) {
            //点播
            result = JustPointPlay.Just(entity);
        }
        if (type == 3) {
            //重复
            if (entity.getRules() != null) {
                result = JustRepeteType.just(entity);//
            }
        }
        if (type == 4){
            //插播
        }
        return result;
    }
}
