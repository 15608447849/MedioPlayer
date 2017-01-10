package com.wos.play.rootdir.model_command_.command_arr;

import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2017/1/6.
 * 时间解析 - 开启定时器 - 存储定时器
 * 定时关机
 * 定时截屏
 *
 */

public class ICommand_TimeParse {

    private static ICommand_TimeParse instans = null;


    private ICommand_TimeParse() {
    }

    public static ICommand_TimeParse getInstans(){
        if (instans==null){
            instans = new ICommand_TimeParse();
        }
        return instans;
    }

    interface TKEY{
         public String WEEK = "#WEEK_";
        public String WEEK_0 = WEEK+"0";
        public String WEEK_1 = WEEK+"1";
        public String WEEK_2 = WEEK+"2";
        public String WEEK_3 = WEEK+"3";
        public String WEEK_4 = WEEK+"4";
        public String WEEK_5 = WEEK+"5";
        public String WEEK_6 = WEEK+"6";

        public String DATE_START = "#kaishishijian";
        public String DATE_END = "#jieshishijian";
    }

    public long parse(String param){
        //SCRN: 0-17:00:00; 1-17:00:00; 2-17:00:00; 3-17:00:00; 4-17:00:00; 5-17:00:00; 6-17:00:00 & 2017-01-01,2017-01-29
        //SHDO: 0-10:00:00; 1-11:00:00; 2-12:00:00; 3-13:00:00; 4-14:00:00; 5-15:00:00; 6-16:00:00

        String timeparam[] = param.split(";");

        //查看最后一个 是不是 包含 &
        HashMap<String,String> map = checkString(timeparam);
        if (map!=null){
            return getTimes(map);
        }
        return -1;
    }

    public Timer parse(String param, TimerTask task) {

         long times = parse(param);
            if (task!=null && times!=-1){
                return getTimes(task,times);
            }

        return null;
    }


    //检测日期
    private HashMap<String,String> checkString(String[] timeparam) {
        if (timeparam!=null){
            HashMap<String,String> map = new HashMap<>();

          String date =  timeparam[timeparam.length-1];
            if (date.contains("&")){
                String arr[] = date.split("&");
                map.put(TKEY.WEEK_6,splics_time(arr[0]));
                arr = arr[1].split(",");
                map.put(TKEY.DATE_START,arr[0]);
                map.put(TKEY.DATE_END,arr[1]);
            }else{
                map.put(TKEY.WEEK_6,date);
            }
            for (int i = 0;i<timeparam.length-1;i++){
                map.put(TKEY.WEEK+i,splics_time(timeparam[i]));
            }
            return map;
        }
      return null;
    }

    private String splics_time(String param){
        if (param.contains("-")){
            return (param.split("-"))[1];
        }
        return "00:00:00";
    }





    //获取时间 - //查看是不是有开始时间和结束时间
    private long getTimes(HashMap<String, String> map) {
        long iTime = -1;
        //如果有开始时间和结束时间
        if (map.get(TKEY.DATE_START)!=null){
            //- 查看是不是这个时间范围内
            String today = TimeOperator.getToday();
            int result = TimeOperator.compareTime_clearTime(map.get(TKEY.DATE_START),today);
            if (result<0){
                result = TimeOperator.compareTime_clearTime(today,map.get(TKEY.DATE_END));
                if (result<0){
                    //在当前时间内
                    iTime = checkTodays(map);
                }else{
                    //不在当前时间内
                }
            }else{
                //还没到指定的日期内
            }
        }else{
            //没有开始时间 和 结束时间
            iTime = checkTodays(map);
        }
       return iTime;
    }

    //判断今天
    private long checkTodays(HashMap<String,String> map) {
        long time = -1;
        //获取今天的日期
        int todays = TimeOperator.getDateInWeekDay(new Date())-1;

        String str = TimeOperator.getToday()+" "+map.get(TKEY.WEEK+todays);
        String todayStr = TimeOperator.getTodayString();
        //获取当前的时间 - 指定的时间 大小
            int result = TimeOperator.compareTime(todayStr,str);
        if (result<0){
            // 计算插值
        }
        else{
            //计算到明天的差值
            str = TimeOperator.getTodayGotoDays(1)+" "+map.get(TKEY.WEEK+( (++todays )>=7?(0):todays ));
        }
        time = TimeOperator.getMillis(todayStr,str);
        return time;
    }


    /**
     *创建定时器
     */
    private Timer getTimes(TimerTask task, long times) {
        Logs.d("定时器","时间 - "+times);
        Timer timer = new Timer();
        timer.schedule(task,times);
      return timer;
    }


}
