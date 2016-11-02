package lzp.yw.com.medioplayer.model_command_mission.command_arr;

import android.text.format.Time;

import java.io.DataOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lzp.yw.com.medioplayer.model_universal.Logs;


/**
 * Created by user on 2016/10/27.
 */
public class Command_SYTI implements iCommand {
    private static final String TAG = "SYTI";
    @Override
    public void Execute(String param) {
        Logs.i(TAG,"终端时间同步 parama:"+param +";当前线程:"+Thread.currentThread().getName());

        if (!RegexMatches(param)){
            Logs.e(TAG,"Sync server time err, param not Matches ,param = " + param);
            return;
        }
        if(justTime(param,null,true)){

            return;
        }

        String settingTime = param.replaceAll("-", "").replace(":","").replaceAll(" ", ".");
        Logs.i(TAG,settingTime);
        String newTime = null;
        Logs.e(TAG,"srtting zone GMT+08:00 >>> ");
        newTime = liunx_SU_syncTimeCmd(settingTime,"GMT+08:00");
        if(!justTime(param,newTime,false)){
            Logs.e(TAG,"srtting zone GMT-08:00 >>> ");
            liunx_SU_syncTimeCmd(settingTime,"GMT-08:00");
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
    private boolean justTime(String param,String nt,boolean flag) {
        DateFormat dataFormatUtils = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); // 格式化 时间 工具
        try {
            long systemTime = dataFormatUtils.parse(param).getTime();

            long currentTime = 0;
            if (flag){
                //当前时间
                currentTime = new Date().getTime();
                if (Math.abs(currentTime-systemTime) < (5 * 1000)){
                    Logs.e(TAG, "不需要 同步时间");
                    return true;
                }

            }else{
                currentTime = dataFormatUtils.parse(nt).getTime();

                if (Math.abs(currentTime-systemTime) < (10 * 1000)){
                    Logs.e(TAG, "同步时间 正确");
                    return true;
                }

            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取系统时间
     */
    private String getSystemTime(){
        long time= System.currentTimeMillis();
        final Calendar mCalendar= Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        int mHour=mCalendar.get(Calendar.HOUR);//取得小时：
        int mMinuts=mCalendar.get(Calendar.MINUTE);//取得分钟：
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        int month = t.month;
        int date = t.monthDay;
        int hour = t.hour;    // 0-23
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = df.format(new Date());
        return times;
    }

    private String  liunx_SU_syncTimeCmd(String param,String timeZone){
        Logs.i(TAG,"yyyyMMdd.HHmmss ==>"+param);
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            String datetime=param;//"20131023.112800" _测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone "+ timeZone+"\n");
            os.writeBytes("/system/bin/date -s "+datetime+"\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            Logs.i(TAG,"时间同步成功 : "+getSystemTime());

            return getSystemTime();


        }catch (Exception e) {
            Logs.e(TAG,"time sync \"su\" cmd err");
            return "";
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
