package lzp.yw.com.medioplayer.model_monitor.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Created by user on 2016/12/8.
 */

public class Stools {
    /**
     * 用来判断服务是否运行.
     * @param mContext 上下文
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        String var ;
        for (int i=0; i<serviceList.size(); i++) {
//            var =
//            Logs.i("","比较的app server name - "+var);
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static boolean isRunningForeground(Context c,List<String> activityList) {
        try {
            ActivityManager am = (ActivityManager) c
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getClassName();
            for (String activityClassName : activityList){
                if (activityClassName.equals(currentPackageName)){
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }





}
