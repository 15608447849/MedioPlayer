package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
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

import com.wos.play.rootdir.model_universal.tool.Logs;


/**
 * Created by user on 2016/10/27.
 */
public class Command_SYTI implements iCommand {
    private static final String TAG = "SYTI";
    private Context context;

    public Command_SYTI(Context context) {
        this.context = context;
    }

    @Override
    public void Execute(String param) {
        Logs.i(TAG,"终端时间同步 parama:["+param +"]\n当前线程:"+Thread.currentThread().getName());

        if (!RegexMatches(param)){
            Logs.e(TAG,"Sync server time err, param not Matches ,param = " + param);
            return;
        }

        if(justTime(getSystemTime(false),param)){
            return;
        }
        String settingTime = param.replaceAll("-", "").replace(":","").replaceAll(" ", ".");
        Logs.i(TAG,"准备设置时间参数 >date>> "+settingTime);
        liunx_SU_syncTimeCmd(settingTime,"Asia/Shanghai");
        Logs.i(TAG,"当前设置时区 Asia/Shanghai >>> 时间 - "+getSystemTime(true));
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
    public static String getSystemTime(boolean islogs){
        if (islogs){
            Logs.i(TAG,"当前时区 - "+ TimeZone.getDefault().getDisplayName());
        }

        long time= System.currentTimeMillis();
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mYear = mCalendar.get(Calendar.YEAR);//年
        int mMonth = mCalendar.get(Calendar.MONTH);//月
        int mDate = mCalendar.get(Calendar.DATE);//日
        if (islogs){
            Logs.i(TAG,"mCalendar >>> "+mYear+"-"+mMonth+"-"+mDate);
        }

        int mHour=mCalendar.get(Calendar.HOUR_OF_DAY);//取得小时：
        int mMinuts=mCalendar.get(Calendar.MINUTE);//取得分钟：
        int mSecond=mCalendar.get(Calendar.SECOND);//取得秒
        if (islogs){
            Logs.i(TAG,"mCalendar >>> "+mHour+":"+mMinuts+":"+mSecond);
        }


        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        if (islogs){
            Logs.i(TAG,"Time() >>> \n"+year+"-"+month+"-"+date);
        }

        int hour = t.hour;    // 0-23
        int minuts = t.minute;
        int seconds = t.second;
        if (islogs){
            Logs.i(TAG,"Time() >>> \n"+hour+":"+minuts+":"+seconds);
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = df.format(new Date());
        return times;
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




















//        AlarmManager mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        mAlarmManager.setTimeZone("Asia/Shanghai");//"GMT+08:00,中国标准时间"
//        try {
//            setDateTime(2016,11,30,20,0,0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(getSystemTime());
/*
    static Process createSuProcess(String cmd) throws IOException {

        DataOutputStream os = null;
        Process process = createSuProcess();

        try {
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit $?\n");
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }

        return process;
    }

    static void requestPermission() throws InterruptedException, IOException {
        createSuProcess("chmod 666 /dev/alarm").waitFor();
    }

    static Process createSuProcess() throws IOException  {
        File rootUser = new File("/system/xbin/ru");
        if(rootUser.exists()) {
            return Runtime.getRuntime().exec(rootUser.getAbsolutePath());
        } else {
            return Runtime.getRuntime().exec("su");
        }
    }

    public static void setDateTime(int year, int month, int day, int hour, int minute,int second) throws IOException, InterruptedException {

        requestPermission();

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);


        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }

        long now = Calendar.getInstance().getTimeInMillis();
        Logs.i(TAG, "set tm="+when + ", now tm="+now);

        if(now - when > 1000)
            throw new IOException("failed to set Date.");

    }


*/











