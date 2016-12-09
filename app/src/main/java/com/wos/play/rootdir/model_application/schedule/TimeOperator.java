package com.wos.play.rootdir.model_application.schedule;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2016/11/9.
 * api java8
 */

public class TimeOperator {
    /**
     * 对比时间格式是否正确
     * @param matcherStr
     * @return
     */
    public static boolean RegexMatches (String matcherStr){
        String date = "((((1[6-9]|[2-9]\\d)\\d{2})-(1[02]|0?[13578])-([12]\\d|3[01]|0?[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-(1[012]|0?[13456789])-([12]\\d|30|0?[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(1\\d|2[0-8]|0?[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))";
        String space = "\\s";
        String time = "([01]?\\d|2[0-3]):[0-5]?\\d:[0-5]?\\d";
        String pattern = date+space+time;
        Pattern pattenrn = Pattern.compile(pattern);
        Matcher matcher = pattenrn.matcher(matcherStr);
        boolean flog = matcher.matches();
        return flog;
    }
    //只包含日期，没有时间
    public static void printToday(){
        LocalDate today = LocalDate.now();
        System.out.println("Today's Local date : " + today);

    }
    //只包含日期，没有时间
    public static String getToday(){
        LocalDate today = LocalDate.now();
       return today.toString();
    }
    public static void printNowTime(){ //默认的格式是hh:mm:ss:nnn，这里的nnn是纳秒
        LocalTime time = LocalTime.now();
        System.out.println("local time now : " + time);
    }
    //打印年月日
    public static void printYearMonthDay(){
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthOfYear();
        int day = today.getDayOfMonth();
        System.out.printf("Year : %d Month : %d day : %d \t %n", year, month, day);
    }
    //时间戳
    public static void printTimeStamp(){ //比如Date.from(Instant)是用来将Instant转换成java.util.Date的，而Date.toInstant()是将Date转换成Instant的
        Instant timestamp = Instant.now();
        System.out.println("What is value of this instant " + timestamp);
    }


    /**
     * 传递时间戳 变成具体日期
     */
    public static void printTargetTimeStamp(long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        System.out.println(" -> "+res);//"当前时间戳:"+s+

    }

    /*
   * 将时间转换为时间戳
   */
    public static String dateToStamp(String s){
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }
    /*
     * 将当前时间转换为时间戳
     */
    public static long dateToStamp(){
        long ts = 0;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            ts = date.getTime();
        return ts;
    }
    /**
     * 比较时间大小
     */
    public static int compareTime(long timeStamp1 ,long timeStamp2,boolean clearY_M_D){

        System.out.println("timeStamp1-");
        printTargetTimeStamp(timeStamp1);
        System.out.println("timeStamp2-");
        printTargetTimeStamp(timeStamp2);

        int result= 0;
        try
        {
        java.util.Calendar c1=java.util.Calendar.getInstance();
        java.util.Calendar c2=java.util.Calendar.getInstance();
        c1.setTimeInMillis(timeStamp1);
        c2.setTimeInMillis(timeStamp2);
            if (clearY_M_D){
                c1.clear(Calendar.MONTH);
                c1.clear(Calendar.YEAR);
                c1.set(Calendar.DATE,1);

                c2.clear(Calendar.MONTH);
                c2.clear(Calendar.YEAR);
                c2.set(Calendar.DATE,1);

            }
//            System.out.println("11-");
//            printTargetTimeStamp(c1.getTimeInMillis());
//            System.out.println("22-");
//            printTargetTimeStamp(c2.getTimeInMillis());
//            System.out.println("->"+c1.toString());
//            System.out.println("->"+c2.toString());

         result=c1.compareTo(c2);
        }catch(Exception e){
            e.printStackTrace();
        }

        /*if(result==0)
            System.out.println("timeStamp1 相等 timeStamp2");
        else if(result<0)
            System.out.println("timeStamp1 小于 timeStamp1");
        else
            System.out.println("timeStamp1 大于 timeStamp2");*/

        return result;
    }

    /**
     *
     * @param result
     * @param type 1 开始时间 比较 当前时间, 2结束时间 比较 当前时间
     */
    public static void printlnResult(int result,int type){
        String var = type == 0?"开始时间":"结束时间";
        if(result==0)
            System.out.println(var+" 相等 当前时间");
        else if(result<0)
            System.out.println(var+" 小于 当前时间");
        else
            System.out.println(var+" 大于 当前时间");
    }

    /**
     * 比较时间大小
     * 例子:2008-01-25 09:12:09
     */
    public static int compareTime(String time1 ,String time2){
        int result= 0;
        try
        {
            java.text.DateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Calendar c1=java.util.Calendar.getInstance();
            java.util.Calendar c2=java.util.Calendar.getInstance();
            c1.setTime(df.parse(time1));
            c2.setTime(df.parse(time2));
            result=c1.compareTo(c2);
        }catch(Exception e){
            e.printStackTrace();
        }

       /* if(result==0)
            System.out.println(result+"->timeStamp1 相等 timeStamp2");
        else if(result<0)
            System.out.println(result+"->timeStamp1 小于 timeStamp1");
        else
            System.out.println(result+"->timeStamp1 大于 timeStamp2");*/
        return result;
    }

    /**
     * 计算毫秒数 差值
     * 忽略年月日
     */
    public static long getMillisecond(long start,long end){


        long milli = 0;
        java.util.Calendar s=java.util.Calendar.getInstance();
        java.util.Calendar d=java.util.Calendar.getInstance();
        s.setTimeInMillis(start);
        d.setTimeInMillis(end);
        milli = ((d.get(Calendar.HOUR_OF_DAY) - s.get(Calendar.HOUR_OF_DAY)) * 60 * 60 *1000);
        milli += ((d.get(Calendar.MINUTE) - s.get(Calendar.MINUTE)) * 60 *1000);
        milli += ((d.get(Calendar.SECOND) - s.get(Calendar.SECOND))*1000);
        System.err.println("------------------- 时间差 : "+milli +" 毫秒 " );
        return milli;
    }

    /**
     * 计算毫秒数 差值
     * 忽略年月日
     * 冲当前时间到结束
     */
    public static long getMillisecond(long end){
       return getMillisecond(dateToStamp(),end);
    }
    /**
     * 计算毫秒数 差值
     * 忽略年月日
     * 冲当前时间到结束
     */
    public static long getMillisecond(String end){

        return getMillisecond(dateToStamp(),Long.valueOf(end));
    }

    public static int justStart_EndTime(long startTime,long endTime) {

        int result = -1;
        int res = TimeOperator.compareTime(startTime, TimeOperator.dateToStamp(), true);
        printlnResult(res,0);
        if (res != 0) {
            if (res > 0) {
                //开始时间大于 当前时间
                result = 3;
            }
            if (res < 0) {
                //判断 结束时间
                res = TimeOperator.compareTime(endTime, TimeOperator.dateToStamp(), true);
                printlnResult(res,1);
                if (res != 0) {
                    if (res > 0) {
                        //结束时间 大于 当前时间
                        result = 2;
                    } else {
                        //结束时间 小于 当前时间
                        result = 1;
                    }
                }
            }
        } else {
            //开始时间 == 当前时间
            //判断 结束时间
            res = TimeOperator.compareTime(endTime, TimeOperator.dateToStamp(), true);
            printlnResult(res,1);
            if (res != 0) {
                if (res > 0) {
                    //结束时间 大于 当前时间
                    result = 2;
                } else {
                    //结束时间 小于 当前时间
                    result = 1;
                }
            } else {
                //结束时间 == 当前时间
                result = -1;
            }
        }
        return result;
    }
}
