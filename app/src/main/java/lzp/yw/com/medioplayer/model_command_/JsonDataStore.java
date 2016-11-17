package lzp.yw.com.medioplayer.model_command_;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lzp.yw.com.medioplayer.model_application.baselayer.DataListEntiyStore;
import lzp.yw.com.medioplayer.model_application.schedule.ScheduleReadBroad;
import lzp.yw.com.medioplayer.model_command_.command_arr.iCommand;
import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.MD5Util;
import lzp.yw.com.medioplayer.model_universal.SdCardTools;

/**
 * Created by user on 2016/11/8.
 */

public class JsonDataStore implements iCommand {
    private ConcurrentMap<String, String> jsonMap = new ConcurrentHashMap<>();
    private static JsonDataStore instant = null;
    private DataListEntiyStore dls = null;
    private String jsonStoreDir = null;
    private Context c;
    private JsonDataStore(Context c){
        this.c  = c;
        dls = new DataListEntiyStore(c);
        dls.ReadShareData();
        jsonStoreDir = dls.GetStringDefualt("jsonStore","");
    }

    public static JsonDataStore getInstent(Context context){
        if (instant==null){
            instant = new JsonDataStore(context);
        }
        return instant;
    }


    //添加数据
    public void addEntity(String key,String value){
        jsonMap.put(MD5Util.getStringMD5(key),value);
    }
    //添加数据
    public void addEntity(String key,String value,boolean f){
        if (f){
            addEntity(key,value);
        }else{
            jsonMap.put(key,value);
        }
    }

    @Override
    public void Execute(String param) {

        if (jsonStoreDir==null || jsonStoreDir.equals("")){
            return;
        }
        Log.i("","json 保存目录: "+ jsonStoreDir);
        clearPreviousCache(jsonStoreDir);
        readJsonMapToSdcard(jsonStoreDir);
    }
    /**
     * 清理 上一次保存的文件
     * @param sdcard_save_dir
     */
    private void clearPreviousCache(String sdcard_save_dir){
        String bakPath = sdcard_save_dir.substring(0,sdcard_save_dir.lastIndexOf("/"));
        bakPath = bakPath.substring(0,bakPath.lastIndexOf("/"));
        bakPath = bakPath+"/jsonSchuduleBak/";
        Log.i("","备份文件夹路径 : "+bakPath);
        try {
            SdCardTools.backupFileDir(sdcard_save_dir,bakPath);
            SdCardTools.deleteTargetDir(sdcard_save_dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //写入文件
    private void readJsonMapToSdcard(String sdcard_save_dir){
       Iterator iter = jsonMap.entrySet().iterator();
        Object key = null;
        Object val = null;
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            key = entry.getKey();
            val = entry.getValue();
            SdCardTools.writeJsonToSdcard(sdcard_save_dir,(String)key,(String)val);

        }
        Logs.d("","---------------存储数据完成--------------------");
        //发送 读取排期 的广播
        //带上排期保存的目录
        sendCompeleteBroad(sdcard_save_dir);

    }

    private Intent intent =null;
    private Bundle bundle = null;
    /**
     * 发送 广播 到 排期读取
     * @param sdcard_save_dir json文件保存路径
     */
    private void sendCompeleteBroad(String sdcard_save_dir) {
        if (c==null){
            return;
        }
        if (intent == null){
            intent = new Intent();
        }
        if (bundle == null){
            bundle = new Bundle();
        }
        bundle.clear();
        intent.setAction(ScheduleReadBroad.ACTION);
        bundle.putString(ScheduleReadBroad.PARAM, sdcard_save_dir);
        intent.putExtras(bundle);
        c.sendBroadcast(intent);
    }
}
