package lzp.yw.com.medioplayer.model_command_.command_arr;

import android.text.format.Time;

import java.io.DataOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lzp.yw.com.medioplayer.model_universal.tool.Logs;


/**
 * Created by user on 2016/10/27.
 */
public class Command_SYTI implements iCommand {
    private static final String TAG = "SYTI";
    @Override
    public void Execute(String param) {
        Logs.i(TAG,"终端时间同步 parama:["+param +"]\n当前线程:"+Thread.currentThread().getName());

        if (!RegexMatches(param)){
            Logs.e(TAG,"Sync server time err, param not Matches ,param = " + param);
            return;
        }
        String currentTime = getSystemTime();
        if(justTime(currentTime,param)){
            return;
        }

        String settingTime = param.replaceAll("-", "").replace(":","").replaceAll(" ", ".");
        Logs.i(TAG,"准备设置时间参数 >date>> "+settingTime);

        liunx_SU_syncTimeCmd(settingTime,"GMT+8");
        currentTime = getSystemTime();
        Logs.i(TAG,"当前设置时区 GMT+08:00 >>> 时间 - "+currentTime);
        if(!justTime(currentTime,param)){
            liunx_SU_syncTimeCmd(settingTime,"GMT-08:00");
            currentTime = getSystemTime();
            Logs.i(TAG,"当前设置时区 GMT-08:00 >>> 时间 - "+currentTime);
            liunx_SU_syncTimeCmd(null,"GMT+8");
            getSystemTime();
        }
    }


    /**
     * 匹配格式
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

    /**
     * 判断时间
     */
    private boolean justTime(String androidTime,String serviceTime) {

        try {
            DateFormat dataFormatUtils = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); // 格式化 时间 工具
            long systemTime = dataFormatUtils.parse(serviceTime).getTime();    //服务器传来的时间
            long currentTime = dataFormatUtils.parse(androidTime).getTime();    //当前时间

                if (Math.abs(currentTime-systemTime) < (5 * 1000)){
                    Logs.i(TAG, "时间正确 - android"+currentTime +"   server - "+systemTime);
                    return true;
                }else{
                    Logs.i(TAG, "时间错误 android - "+currentTime +"   server - "+systemTime);
                }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取系统时间
     */
    public static String getSystemTime(){
        Logs.i(TAG,"当前时区 - "+ TimeZone.getDefault().getDisplayName());
        long time= System.currentTimeMillis();
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mYear = mCalendar.get(Calendar.YEAR);//年
        int mMonth = mCalendar.get(Calendar.MONTH);//月
        int mDate = mCalendar.get(Calendar.DATE);//日
        Logs.i(TAG,"mCalendar >>> "+mYear+"-"+mMonth+"-"+mDate);
        int mHour=mCalendar.get(Calendar.HOUR);//取得小时：
        int mMinuts=mCalendar.get(Calendar.MINUTE);//取得分钟：
        int mSecond=mCalendar.get(Calendar.SECOND);//取得秒
        Logs.i(TAG,"mCalendar >>> "+mHour+":"+mMinuts+":"+mSecond);

        Time t=new Time(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        Logs.i(TAG,"Time() >>> \n"+year+":"+month+":"+date);
        int hour = t.hour;    // 0-23
        int minuts = t.minute;
        int seconds = t.second;
        Logs.i(TAG,"Time() >>> \n"+hour+":"+minuts+":"+seconds);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = df.format(new Date());
        return times;
    }
    /**
     * 获取系统时间并传递出来
     */
    public static Calendar getSystemTime(Void s){
        int mHour, mMinuts, mSecond;
        long time= System.currentTimeMillis();
        Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        mHour=mCalendar.get(Calendar.HOUR);//取得小时：
        mMinuts=mCalendar.get(Calendar.MINUTE);//取得分钟：
        mSecond=mCalendar.get(Calendar.SECOND);//取得秒
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = df.format(new Date());
        System.out.println(TAG+"当前时区 - "+ TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        System.out.println(TAG+"获取系统时间 ->"+times);
        return mCalendar;
    }





    private void  liunx_SU_syncTimeCmd(String datetime,String timeZone){
        Logs.i(TAG,"yyyyMMdd.HHmmss ==>"+datetime+"\n zone==>"+timeZone);
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            //String datetime=param;//"20131023.112800" _测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            os = new DataOutputStream(process.getOutputStream());
            if (timeZone!=null){
                os.writeBytes("setprop persist.sys.timezone "+ timeZone+"\n");
            }
            if (datetime!=null){
                os.writeBytes("/system/bin/date -s "+datetime+"\n");
            }
            os.writeBytes("clock -w\n"); //将当前系统时间写入CMOS中去
            os.writeBytes("date\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
//            Logs.i(TAG,"时间同步完成 : "+getSystemTime());
            Logs.i(TAG,"-------------------------------------------\n-------------------------------------------");
        }catch (Exception e) {
            Logs.e(TAG,"time sync \"su\" cmd err" +e.getMessage());
//            return "";
        }finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
