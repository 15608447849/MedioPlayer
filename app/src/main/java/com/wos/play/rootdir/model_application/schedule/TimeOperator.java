package com.wos.play.rootdir.model_application.schedule;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2016/11/9.
 * api java8
 */

public class TimeOperator {
    /**
     * 对比时间格式是否正确 -> true 1997-01-01 00:00:00
     * false - 00:00:00
     * @param matcherStr
     * @return
     */
    public static boolean RegexMatches (String matcherStr,boolean isf){
        String date = "((((1[6-9]|[2-9]\\d)\\d{2})-(1[02]|0?[13578])-([12]\\d|3[01]|0?[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-(1[012]|0?[13456789])-([12]\\d|30|0?[1-9]))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(1\\d|2[0-8]|0?[1-9]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))";
        String space = "\\s";
        String time = "([01]?\\d|2[0-3]):[0-5]?\\d:[0-5]?\\d";
        String pattern;
        if (isf){
            pattern  = date+space+time;
        }else{
            pattern = time;
        }

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
    //只包含日期，没有时间 - 获取当天日期
    public static String getToday(){
        LocalDate today = LocalDate.now();
       return today.toString();
    }
    //年月日 时分秒
    public static String  getTodayString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
    public static String getToday(boolean y,boolean m,boolean d,boolean h,boolean mie,boolean s,String spaceMark,String spaceMark2){
        spaceMark = spaceMark ==null?"":spaceMark;
        spaceMark2 = spaceMark2 ==null?"":spaceMark2;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month=cal.get(Calendar.MONTH);//获取月份 月份要+1
        int day=cal.get(Calendar.DATE);//获取日
        int hour=cal.get(Calendar.HOUR);//小时
        int minute=cal.get(Calendar.MINUTE);//分
        int second=cal.get(Calendar.SECOND);//秒

        StringBuilder sb = new StringBuilder();
       sb.append(y?year+spaceMark:"");
       sb.append(m?(month+1)+spaceMark:"");
       sb.append(d?day+spaceMark2:"");

       sb.append(h?hour+spaceMark:"");
       sb.append(mie?minute+spaceMark:"");
       sb.append(s?second:"");
        return sb.toString();
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

    // 获取Calendar
    public static Calendar getCalendar(){
        return Calendar.getInstance();
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
   * 将时间字符串转换为时间戳
   */
    public static String dateToStamp(String s){

        if (RegexMatches(s,true)){
           s = (s.split("\\s"))[1];
        }
        if (RegexMatches(s,false)){
            s = getToday() +" " + s;
        }

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
     * 比较时间大小  boolean 清除年月日
     */
    public static int compareTime(long timeStamp1 ,long timeStamp2,boolean clearY_M_D){
//        System.out.println("timeStamp1-");
        printTargetTimeStamp(timeStamp1);
//        System.out.println("timeStamp2-");
        printTargetTimeStamp(timeStamp2);

        int result= 0;
        try
        {
        java.util.Calendar c1=getCalendar();
        java.util.Calendar c2=getCalendar();
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
         result=c1.compareTo(c2);
        }catch(Exception e){
            e.printStackTrace();
        }
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
            System.out.println(var+" == 当前时间");
        else if(result<0)
            System.out.println(var+" < 当前时间");
        else
            System.out.println(var+" > 当前时间");
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
            java.util.Calendar c1=getCalendar();
            java.util.Calendar c2=getCalendar();
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
     * 比较时间大小
     * 例子:2008-01-25 he 2010-01-01
     *
     if(result==0)
     System.out.println(result+"->timeStamp1 相等 timeStamp2");
     else if(result<0)
     System.out.println(result+"->timeStamp1 小于 timeStamp2");
     else
     System.out.println(result+"->timeStamp1 大于 timeStamp2");
     */
    public static int compareTime_clearTime(String time1 ,String time2){
        int result= 0;
        try
        {
            java.text.DateFormat df=new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Calendar c1=getCalendar();
            java.util.Calendar c2=getCalendar();
            c1.setTime(df.parse(time1));
            c2.setTime(df.parse(time2));
            result=c1.compareTo(c2);
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 计算毫秒数
     *  2011-02-02 00:00:00 - 2015-02-02 23:50:50
     */
    public static long getMillis(String strMin,String strMax){

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(strMin);
            Date date2 = sdf.parse(strMax);
            return date2.getTime()-date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算毫秒数 差值
     * 忽略年月日
     */
    public static long getMillisecond(long start,long end){
        long milli = 0;
        java.util.Calendar s=getCalendar();
        java.util.Calendar d=getCalendar();
        s.setTimeInMillis(start);
        d.setTimeInMillis(end);
        milli = ((d.get(Calendar.HOUR_OF_DAY) - s.get(Calendar.HOUR_OF_DAY)) * 60 * 60 *1000);
        milli += ((d.get(Calendar.MINUTE) - s.get(Calendar.MINUTE)) * 60 *1000);
        milli += ((d.get(Calendar.SECOND) - s.get(Calendar.SECOND))*1000);
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
     * 当前时间 -> 结束时间
     */
    public static long getMillisecond(String end){
        System.out.println("getMillisecond(String end) - end - 1 -> "+ end);
        if (RegexMatches(end,true)){
            end = (end.split("\\s"))[1];
        }
        if (RegexMatches(end,false)){
            end = dateToStamp(end);
        }
        long endDate = dateToStamp()+1000;
        try {
            System.out.println("getMillisecond(String end) - end - 2 ->"+ end);
            endDate = Long.valueOf(end);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return getMillisecond(dateToStamp(),endDate);//当前时间 - 结束 时间的时间差 - 毫秒数
    }

    /**判断开始时间和结束时间与当前时间的关系*/
    public static int justStart_EndTime(long startTime,long endTime) {

        int result = Efficacy.TYPE.error;

        int res = TimeOperator.compareTime(startTime, TimeOperator.dateToStamp(), true);
        printlnResult(res,0);
        if (res != 0) {
            if (res > 0) {
                //开始时间 > 当前时间
                result = Efficacy.TYPE.after_the_current_time;
            }
            if (res < 0) {// 开始时间<当前时间
                //继续 判断 结束时间
                res = TimeOperator.compareTime(endTime, TimeOperator.dateToStamp(), true);
                printlnResult(res,1);
                if (res != 0) {
                    if (res > 0) {
                        //结束时间 大于 当前时间
                        result = Efficacy.TYPE.in_the_current_time;
                    } else {
                        //结束时间 小于 当前时间
                        result = Efficacy.TYPE.before_the_current_time;
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
                    result = Efficacy.TYPE.in_the_current_time;
                } else {
                    //结束时间 小于 当前时间
                    result = Efficacy.TYPE.before_the_current_time;
                }
            } else {
                //结束时间 == 当前时间
                result = Efficacy.TYPE.error;
            }
        }
        return result;
    }


    //获取今天是这周的第几天
    public static int getDateInWeekDay(Date date){
        Calendar cal = getCalendar();
        if (date!=null){
            cal.setTime(date);
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    //获取今天是这月的第几天
    public static int getDateInMonthDay(Date date){
        Calendar cal = getCalendar();
        if (date!=null){
            cal.setTime(date);
        }
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    //获取今天是第几年
    public static int getDateInYear(Date date){
        Calendar cal = getCalendar();
        if (date!=null){
            cal.setTime(date);
        }
        return cal.get(Calendar.YEAR);
    }

    //获取今年第几个月
    public static int getDateYearInMonth(Date date){
        Calendar cal = getCalendar();
        if (date!=null){
            cal.setTime(date);
        }
        return cal.get(Calendar.MONTH)+1;
    }

    // 获取 以今天为准 向前 N天的 日期 ,例如  2017-01-03
    public static String getTodayGotoDays(int days_chazhi){
        String dateString = null;
        try {
            Date date=new Date();//取时间

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            if (days_chazhi!=0){
                calendar.add(calendar.DATE,days_chazhi);//把日期往后增加一天.整数往后推,负数往前移动
            }
            date=calendar.getTime(); //这个时间就是日期往后推一天的结果
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            dateString = formatter.format(date);
            if (RegexMatches(dateString,true)){
                dateString = (dateString.split("\\s"))[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

}
