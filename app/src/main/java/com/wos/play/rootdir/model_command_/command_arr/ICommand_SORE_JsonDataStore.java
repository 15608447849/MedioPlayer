package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_application.baselayer.DataListEntiyStore;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.MD5Util;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by user on 2016/11/8.
 */

public class ICommand_SORE_JsonDataStore implements iCommand {
    private static final String TAG = " ICommand_SORE_JsonDataStore";
    private ConcurrentMap<String, String> jsonMap = new ConcurrentHashMap<>();
    private static ICommand_SORE_JsonDataStore instant = null;
    private DataListEntiyStore dls = null;
    private String jsonStoreDir = null;
    private Context c;
    private ICommand_SORE_JsonDataStore(Context c){
        this.c  = c;
        dls = new DataListEntiyStore(c);
        dls.ReadShareData();
        jsonStoreDir = dls.GetStringDefualt("jsonStore","");
    }

    public static ICommand_SORE_JsonDataStore getInstent(Context context){
        if (instant==null){
            instant = new ICommand_SORE_JsonDataStore(context);
        }
        return instant;
    }


    //添加数据
    private void addEntity(String key,String value){

        jsonMap.put(MD5Util.getStringMD5(key),value);
    }

    /**
     *
     * @param key 文件名
     * @param value 文件内容
     * @param f false->文件名不加密
     */
    public void addEntity(String key,String value,boolean f){
        Logs.i(TAG,"添加json文件名 - "+key);
        if (f){
            addEntity(key,value);
        }else{
            jsonMap.put(key,value);
        }
    }

    @Override
    public void Execute(String param) { //无参数

        if (jsonStoreDir==null || jsonStoreDir.equals("")){
            return;
        }
        Logs.i("TAG","json 保存目录 : "+ jsonStoreDir);
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
        Logs.i("TAG","备份文件夹路径 : "+bakPath);
        try {
            SdCardTools.backupFileDir(sdcard_save_dir,bakPath);
            SdCardTools.DeleteTargetDir(sdcard_save_dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //写入文件
    private void readJsonMapToSdcard(String sdcard_save_dir){
        if (!SdCardTools.MkDir(sdcard_save_dir)){
            Logs.i("TAG","---------------存储数据失败--------------------"+sdcard_save_dir);
            return;
        }
        Logs.e(TAG,"jsonMap size: "+jsonMap.size());
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
        Logs.i("TAG","---------------存储数据完成--------------------");
        //发送 读取排期 的广播
    }
    public void clearJsonMap(){
        if (jsonMap!=null){
            jsonMap.clear();
        }
    }
    private Intent intent =null;
    private Bundle bundle = null;

}
