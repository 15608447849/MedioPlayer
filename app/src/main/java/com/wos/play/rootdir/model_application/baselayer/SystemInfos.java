package com.wos.play.rootdir.model_application.baselayer;

import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.DataListEntiy;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.HashMap;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2016/12/13.
 */

public class SystemInfos {
    private  static  final String TAG = "系统配置";
    public boolean isConfig() {
        //加载一次数据
        readInfo();
        //然后判断端口号是否为空
        Logs.i(TAG,"SystemInfos()_isConfig() 终端号:"+terminalNo);
        return !terminalNo.equals("");
    }



    //连接类型
    private  String connectionType = "HTTP";
    //终端编号
    private String terminalNo = "";
    //服务器ip
    private String serverip = "172.16.0.216";//"192.168.7.3";
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
    //电子报zip文件保存路径
    private String epaperSourcePath = "/mnt/sdcard/wosplayer/epapers/";
    //主ftp-IP地址
    private String ftpAddress = "192.168.7.3";

    //主ftp-端口
    private String ftpPort = "21";

    //主ftp 用户名
    private String ftpUser = "ftp";

    //住ftp 密码
    private String ftpPass = "FTPmedia";

    public String getFtpAddress() {
        return ftpAddress;
    }

    public void setFtpAddress(String ftpAddress) {
        this.ftpAddress = ftpAddress;
    }

    public String getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(String ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public String getFtpPass() {
        return ftpPass;
    }

    public void setFtpPass(String ftpPass) {
        this.ftpPass = ftpPass;
    }

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

    public String getEpaperSourcePath() {
        return epaperSourcePath;
    }

    public void setEpaperSourcePath(String epaperSourcePath) {
        this.epaperSourcePath = epaperSourcePath;
    }

    //构造
    private SystemInfos() {
        contentEntity = new DataListEntiy();
    }
    private static final String infos = "/mnt/sdcard/wosplayer/wos.conf";
    //初始化 读取 - > sdcard 目录下的 一个配置文件 ->





    //读取信息  -> 读取内容转成map
    private void readInfo() {
        try {
        String content =  FileUtils.readFile(infos,"utf-8").toString();
        Logs.i(TAG,"读取系统配置信息: [\n"+content+"\n]");
        if (content!=null && !content.equals("")){
            contentEntity.setMap(AppsTools.jsonTxtToMap(content));
//                                  赋值
            //连接类型
            connectionType = contentEntity.GetStringDefualt("connectionType");
            //终端编号
            terminalNo = contentEntity.GetStringDefualt("terminalNo");
            //服务器ip
            serverip = contentEntity.GetStringDefualt("serverip");
            //服务器端口
            serverport = contentEntity.GetStringDefualt("serverport");
            //公司id
            companyid = contentEntity.GetStringDefualt("companyid");
            //心跳时间
            heartBeatInterval = contentEntity.GetStringDefualt("heartBeatInterval");
            //重启时间
            sleepTime = contentEntity.GetStringDefualt("sleepTime");
            //容量达到多少时 会清理资源
            storageLimits = contentEntity.GetStringDefualt("storageLimits");
            //资源保存路径
            basepath = contentEntity.GetStringDefualt("basepath");
            //电子报资源路径
            epaperSourcePath = contentEntity.GetStringDefualt("epaperPath");
            //json保存路径
            jsonStore = contentEntity.GetStringDefualt("jsonStore");
            // 图标存储路径
            appicon = contentEntity.GetStringDefualt("appicon");
            //ftp地址
            ftpAddress = contentEntity.GetStringDefualt("ftpAddress");
            //ftp端口号
            ftpPort = contentEntity.GetStringDefualt("ftpPort");
            //ftp用户名
            ftpUser = contentEntity.GetStringDefualt("ftpUser");
            //ftp密码
            ftpPass = contentEntity.GetStringDefualt("ftpPass");
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //保存信息
    public boolean saveInfo(){
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
            map.put("epaperPath",epaperSourcePath);
            map.put("jsonStore",jsonStore);
            map.put("appicon",appicon);
            map.put("ftpAddress",ftpAddress);
            map.put("ftpPort",ftpPort);
            map.put("ftpUser",ftpUser);
            map.put("ftpPass",ftpPass);

            String content = AppsTools.mapToJson(map);

           boolean flag =  FileUtils.writeFile(infos,content);
           Logs.i(TAG,"保存的系统配置文件内容:[\n"+content +"\n]\n 存储结果:"+flag);
            if (!flag){
                clearValue();
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void clearValue() {
        terminalNo = "";
    }


    private  static SystemInfos sysInfo;
    private DataListEntiy contentEntity;
    public static SystemInfos get(){
        if (sysInfo==null){
            sysInfo = new SystemInfos();
            sysInfo.readInfo();
        }
        return sysInfo;
    }


}
