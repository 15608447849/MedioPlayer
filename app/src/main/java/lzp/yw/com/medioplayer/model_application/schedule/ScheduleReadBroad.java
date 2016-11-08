package lzp.yw.com.medioplayer.model_application.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

import lzp.yw.com.medioplayer.model_command_mission.command_arr.Command_UPSC;
import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.MD5Util;
import lzp.yw.com.medioplayer.model_universal.SdCardTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

/**
 * Created by user on 2016/11/8.
 */

public class ScheduleReadBroad extends BroadcastReceiver{
    private static final String TAG = "_ScheduleRead";
    public static final String ACTION  = "com.broad.schedule.read";
    public static final String PARAM = "jsonDirPath";

    private String path = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        path = intent.getExtras().getString(PARAM);
        if (path != null){
            //获取 main 得到 uri -> md5 ->文件名 -> 获取文本内容 -> 变成对象->scheduleReader
            File dir = new File(path) ;
            if(!dir.exists()){
                Logs.e(TAG," 目录不存在 - "+ path);
                return;
            }

            String [] filenames = dir.list();

            if (filenames==null || filenames.length==0){
                Logs.e(TAG," 目录下无内容 - "+ path);
                return;
            }
           String var = getOnlyFileContent(filenames,"main");
            if (var!=null){
                List<ScheduleBean> scheduleList = Command_UPSC.parseJsonToList(var);
                if (scheduleList!=null){
                    ScheduleReader.getReader().startWork(scheduleList);
                }
            }
        }
    }




    private String getOnlyFileContent(String [] filenames ,String targetname){
        String var = null;
        for (String filename : filenames){
            if (filename.equals(targetname)){
                var = SdCardTools.readerJsonToMemory(path,filename);
                var = var;
                break;
            }
        }
        if (var!=null){
            if (targetname.equals("main")){
                var = getOnlyFileContent(filenames, MD5Util.getStringMD5(var));
            }
        }
        return var;
    }
}
