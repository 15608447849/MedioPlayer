package com.wos.play.rootdir.model_application.baselayer;

import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.DataListEntiy;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.util.HashMap;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2016/12/13.
 */

public class SystemInitInfo {

    private boolean isConfig = false;

    public boolean isConfig() {
        return isConfig;
    }

    public void setConfig(boolean config) {
        isConfig = config;
    }

    //连接类型
    private  String connectionType = "HTTP";
    //终端编号
    private String terminalNo = "0000";
    //服务器ip
    private String serverip = "127.0.0.1";
    //服务器端口
    private String serverport = "9000";
    //公司id
    private String companyid = "999";
    //心跳时间
    private String heartBeatInterval = "30";
    //重启时间
    private String sleepTime = "30";
    //容量达到多少时 会清理资源
    private String storageLimits = "50";
    //资源保存路径
    private String basepath = "/mnt/sdcard/wosplayer/source/";
    //json保存路径
    private String jsonStore = "/mnt/sdcard/wosplayer/jsoninfo/";
    // 图标存储路径
    private String appicon = "/mnt/sdcard/wosplayer/appicon/";


    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }



    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getServerport() {
        return serverport;
    }

    public void setServerport(String serverport) {
        this.serverport = serverport;
    }

    public String getHeartBeatInterval() {
        return heartBeatInterval;
    }

    public void setHeartBeatInterval(String heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getStorageLimits() {
        return storageLimits;
    }

    public void setStorageLimits(String storageLimits) {
        this.storageLimits = storageLimits;
    }

    public String getBasepath() {
        return basepath;
    }

    public void setBasepath(String basepath) {
        this.basepath = basepath;
    }

    public String getJsonStore() {
        return jsonStore;
    }

    public void setJsonStore(String jsonStore) {
        this.jsonStore = jsonStore;
    }

    public String getAppicon() {
        return appicon;
    }

    public void setAppicon(String appicon) {
        this.appicon = appicon;
    }


    //构造
    private SystemInitInfo() {
        contentEntity = new DataListEntiy();
        initRead();
    }
    private static final String infos = "/mnt/sdcard/wos.conf";
    //初始化 读取 - > sdcard 目录下的 一个配置文件 ->
    private void initRead() {
        if (FileUtils.isFileExist(infos)){
            //读取  内容转成 map
          readInfo();
        }

         //创建
        //如果不可以创建 ->
    }

    //读取信息
    private void readInfo() {
        String content =  SdCardTools.readerJsonToMemory(infos);
        if (content!=null && !content.equals("")){
            try {
                contentEntity.setMap(AppsTools.jsonTxtToMap(content));
//                赋值
                initValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //保存信息
    public void saveInfo(){
        initContent();
    }

    private void initContent() {
        try {
            HashMap map = contentEntity.getMap();
            map.clear();
            map.put("connectionType",connectionType);
            map.put("terminalNo",terminalNo);
            map.put("serverip",serverip);
            map.put("serverport",serverport);
            map.put("companyid",companyid);
            map.put("heartBeatInterval",heartBeatInterval);
            map.put("serverport",serverport);
            map.put("sleepTime",sleepTime);
            map.put("storageLimits",storageLimits);
            map.put("basepath",basepath);
            map.put("jsonStore",jsonStore);
            map.put("appicon",appicon);
            String content = AppsTools.mapToJson(map);
            SdCardTools.writeJsonToSdcard(infos,content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private  static  SystemInitInfo sysInfo;
    private DataListEntiy contentEntity;
    public static SystemInitInfo get(){
        if (sysInfo==null){
            sysInfo = new SystemInitInfo();
        }
        return sysInfo;
    }

    private void initValue() {
        try {
            //连接类型
            connectionType = contentEntity.GetStringDefualt("connectionType","HTTP");
            //终端编号
            terminalNo = contentEntity.GetStringDefualt("terminalNo","0000");
            //服务器ip
            serverip = contentEntity.GetStringDefualt("serverip","172.16.0.216");
            //服务器端口
            serverport = contentEntity.GetStringDefualt("serverport","9000");
            //公司id
            companyid = contentEntity.GetStringDefualt("companyid","999");
            //心跳时间
            heartBeatInterval = contentEntity.GetStringDefualt("heartBeatInterval","300000");
            //重启时间
            sleepTime = contentEntity.GetStringDefualt("sleepTime","99999");
            //容量达到多少时 会清理资源
            storageLimits = contentEntity.GetStringDefualt("storageLimits","50");
            //资源保存路径
            basepath = contentEntity.GetStringDefualt("basepath","/mnt/sdcard/wosplayer/source/");
            //json保存路径
            jsonStore = contentEntity.GetStringDefualt("jsonStore","/mnt/sdcard/wosplayer/jsoninfo/");
            // 图标存储路径
            appicon = contentEntity.GetStringDefualt("appicon","/mnt/sdcard/wosplayer/appicon/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        isConfig = true;
    }


}
