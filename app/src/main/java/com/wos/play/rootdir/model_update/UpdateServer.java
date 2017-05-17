package com.wos.play.rootdir.model_update;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_download.override_download_mode.FtpHelper;
import com.wos.play.rootdir.model_download.override_download_mode.FtpHelper.OnFtpListener;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.trinea.android.common.util.FileUtils;
import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ShellUtils;

/**
 * Created by Administrator on 2017/4/28.
 */

public class UpdateServer extends IntentService {
    public static final String TAG ="更新app";
    public static final String UPDATE_KEY = "update_app";
    public static final String UPDATE_URI = "/setting/version/upgrade";
    public static final String LOCAL_PATH = "/mnt/sdcard/wosplayer/savepath/";
    private static String DOWN_ID = "0";
    private final String FILE_PATH = LOCAL_PATH + "/update.txt";
    private Gson gson = new Gson();

    public UpdateServer(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String ftpUrl, fileName;
            DOWN_ID = getDownId();
            String result = postJson(getUpdateUrl(), getDates());
            if (result == null) return;
            Logs.e("result:" + result);
            JsonObject object = new JsonParser().parse(result).getAsJsonObject();
            if ("success".equals(object.get("result").getAsString()) && object.get("dataObj") != null) {
                JsonObject dataObj = object.get("dataObj").getAsJsonObject();
                ftpUrl = dataObj.get("fpath").getAsString();
                fileName = dataObj.get("nname").getAsString();
                saveUpdateInfo(result);
                downloadApk(ftpUrl, fileName);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存后台返回数据
     * @param result
     */
    private void saveUpdateInfo(String result) {
        boolean flag =  FileUtils.writeFile(FILE_PATH,result);
        Logs.i(TAG,"保存文件内容:[\n"+result +"\n]\n 存储结果:"+flag);
    }

    /**
     * 读取之前保存的数据
     * @return
     */
    private String readUpdateInfo() {
        StringBuffer buff = new StringBuffer();
        try {
            String encoding = "GBK";
            File file = new File(FILE_PATH);
            if (!file.isFile() && !file.exists()) return null;
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                buff.append(lineTxt).toString();
            }
            read.close();
        } catch (Exception e) {
            Logs.e("读取文件内容出错");
        }
        return String.valueOf(buff);
    }

    private String getDownId(){
        String info = readUpdateInfo();
        if (info ==null || "".equals(info)) return "0";
        int localVersion = AppsTools.getLocalVersionCode(getApplicationContext());
        JsonObject upInfo = new JsonParser().parse(info).getAsJsonObject();
        if (upInfo == null) return DOWN_ID;
        JsonObject dataObj = upInfo.get("dataObj").getAsJsonObject();
        if (localVersion == Integer.parseInt(dataObj.get("version").getAsString())) {
            DOWN_ID = upInfo.get("downId").getAsString();
        }
        return DOWN_ID;
    }

    /**
     * 获取后台更新接口地址
     * @return
     */
    public String getUpdateUrl() {
        String ip = SystemInfos.get().getServerip();
        String port = SystemInfos.get().getServerport();
        return "http://"+ip + ":"+ port + UPDATE_URI;
    }

    public String getDates() {
        Map<String, String> map = new HashMap<>();
        map.put("downId", DOWN_ID);
        map.put("code", AppsTools.getLocalVersionName(getApplicationContext()));
        map.put("version", String.valueOf(AppsTools.getLocalVersionCode(getApplicationContext())));
        map.put("number", SystemInfos.get().getTerminalNo());
        Logs.e("map:" + map.toString());
        return gson.toJson(map);
    }

    /**
     * 下载更新包
     * @param url
     */
    private void downloadApk(String url, String name) {
        HashMap<String,String> map = parseFtpUrl(url);
        FtpHelper ftpHelper = new FtpHelper(map.get("host"), Integer.parseInt(map.get("port")), map.get("user"),map.get("pass"));
        ftpHelper.downloadSingleFile(map.get("remotePath"), LOCAL_PATH, name, 3, new OnFtpListener() {
            @Override
            public void ftpConnectState(int stateCode, String ftpHost, int port, String userName, String ftpPassword, String fileName) {
                Logs.i(TAG,"连接服务器 : ip:"+ ftpHost+" port:"+port  +"\nuser:"+userName+" password:"+ftpPassword);
                if (stateCode==FtpHelper.FTP_CONNECT_SUCCESSS){
                    Logs.i(TAG,"ftp 连接成功");
                }
                if (stateCode==FtpHelper.FTP_CONNECT_FAIL){
                    Logs.e(TAG,"ftp 连接失败");
                }
            }

            @Override
            public void ftpNotFountFile(String remoteFileName, String fileName) {
                Logs.e(TAG,"ftp 服务器未发现文件 : "+remoteFileName+fileName);
            }

            @Override
            public void localNotFountFile(String localFilePath, String fileName) {
                Logs.e(TAG,"本地文件不存在或无法创建 : "+localFilePath);
            }

            @Override
            public void downLoading(long downProcess, String speed, String fileName) {

            }

            @Override
            public void downLoadSuccess(File localFile, String remotePath, String localPath, String fileName, String ftpHost, int port, String userName, String ftpPassword) {
                Logs.i(TAG, "ftp下载succsee -"+ localFile.getAbsolutePath() +" - 线程 - "+ Thread.currentThread().getName());
                judgeApk(localFile.getAbsolutePath());
            }

            @Override
            public void downLoadFailt(String remotePath, String fileName) {
                Logs.e(TAG,"ftp 下载失败 : "+fileName);
            }

            @Override
            public void error(Exception e) {

            }
        });

    }

    //切割ftp字符串
    public static HashMap<String,String> parseFtpUrl(String uri){
        String str = uri.substring(uri.indexOf("//") + 2);
        String user = str.substring(0, str.indexOf(":"));
        String pass = str.substring(str.indexOf(":") + 1, str.indexOf("@"));
        String host = str.substring(str.indexOf("@") + 1, str.lastIndexOf(":"));
        String port = str.substring(str.lastIndexOf(":") + 1, str.indexOf("/"));
        String remotePath = str.substring(str.indexOf("/"), str.lastIndexOf("/") + 1);
        String remoteFileName = str.substring(str.lastIndexOf("/") + 1);
        //Logs.e("切割ftp路径",uri+"->"+host+" "+user+" "+pass+" "+remotePath+remoteFileName);
        HashMap<String,String> map = new HashMap<>();
        map.put("user",user);
        map.put("pass",pass);
        map.put("host",host);
        map.put("port",port);
        map.put("remotePath",remotePath);
        map.put("remoteFileName",remoteFileName);
        return map;
    }


    public String postJson(String urlString,String  json) {
        if (urlString == null) return null;

        URL url;
        String result = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            System.out.println("URL connect failt :"+urlString);
            System.err.println(e1.getMessage());
            return null;
        }

        HttpURLConnection httpUrlConnection;
        OutputStream out = null;
        BufferedReader br = null;
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(30000);
            httpUrlConnection.setReadTimeout(60000);
            httpUrlConnection.setRequestProperty("Accept-Charset", "GBK");  //设置编码语言
            httpUrlConnection.setRequestProperty("Connection", "keep-alive");  //设置连接的状态
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setRequestProperty("Content-type", "application/json");
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestProperty("X-Auth-Token", "token");  //设置请求的token
            httpUrlConnection.setRequestProperty("Transfer-Encoding", "chunked");//设置传输编码
            httpUrlConnection.setRequestProperty("Content-Length"
                    , String.valueOf(json.getBytes().length));//设置文件请求的长度
            httpUrlConnection.connect();//只是建立了一个与服务器的tcp连接没有实际发送http请求。
            out = httpUrlConnection.getOutputStream();
            out.write(json.getBytes());
            out.flush();
            //连接
            if (httpUrlConnection.getResponseCode()==200){
                br = new BufferedReader( new InputStreamReader(httpUrlConnection
                        .getInputStream(),"UTF-8"));//<===注意，实际发送请求的代码段就在这里
                StringBuilder sb = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null){
                    sb.append(temp);
                }
                result = sb.toString();
            }else{
                System.err.println("请求失败 - "+httpUrlConnection.getResponseCode());
            }
            //断开连接
            httpUrlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (br != null)
                    br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private synchronized void judgeApk(String apkLocalPath) {

        File apkfile = new File(apkLocalPath);
        int flag = -1;
        if (!apkfile.exists()) {
            Log.e(TAG,"install apk failt,path :"+apkLocalPath +", update package file not fount.\n");
        }else{
            //判断apk 版本号
            // 1 获取软件版本号 apk版本号
            int localVersion = AppsTools.getLocalVersionCode(getApplicationContext());
            int apkVersion = getApkVersionCode(getApplication(),apkLocalPath);
            //2 获取软件包名 apk包名
            String localPackageName = getApplication().getApplicationInfo().packageName;
            String apkPackageName = getApkPackageName(getApplication(),apkLocalPath);
            Log.e(TAG,"local package:"+localPackageName+";version:"+localVersion+".\n");
            Log.e(TAG,"apk package:"+apkPackageName+";version:"+apkVersion+".\n");
            if (localPackageName.equals(apkPackageName)){
                if (localVersion>=apkVersion){
                    Log.e(TAG,"update failt,apk version is invalid.\n");
                }
                else{
                    flag = 1;
                }
            }else{
                Log.e(TAG,"the apk file is not update.apk,the apk is other App.\n");
                flag = 2;
            }
        }
        if (flag>0){
            installApk(apkLocalPath,flag);
        }
    }
    /**
     * 安装APK文件
     */
    public  void installApk(String apkLocalPath,int flag) {

        ShellUtils.CommandResult result = ShellUtils.execCommand("chmod 777 "+apkLocalPath,true); // 赋予权限
        Log.e(TAG,"apk path : "+apkLocalPath+" excute 'chmod 777' result:"+result.result+"\n");
        if (result.result == 0){
            int code =  PackageUtils.install(getApplicationContext(),apkLocalPath); //包管理器安装
            Log.e(TAG," PackageUtils.install result code : "+code+"\n");
            if (code != PackageUtils.INSTALL_SUCCEEDED){
                Log.e(TAG,"usr shell command installing.\n");
                int res = runingInstallApk(apkLocalPath);
                if (res == 0){
                    flag = 3;
                    Log.e(TAG,"shell command install apk success.\n");
                }else{
                    Log.e(TAG,"shell command install apk failt.\n");
                }
            }else{
                flag = 3;
                Log.e(TAG,"package util install apk success.\n");
            }
        }
        if (flag == 3){
            String packageName = getApkPackageName(getApplicationContext(),apkLocalPath);
            String activityName = getTaragePackageLunchActivityName(getApplicationContext(),packageName);
            String command = "rm -rf "+apkLocalPath+"\n"+adbStartActivity(packageName,activityName);
            Log.e(TAG,command);
            //成功 - 打开 app
            ShellUtils.execCommand(command,true);
        }
    }

    //运行时安装apk
    public int runingInstallApk(String apkLocalPath){
        String command = getInstallAdb(apkLocalPath);
        Log.e(TAG,command);
        ShellUtils.CommandResult result = ShellUtils.execCommand(command,true,true);
        return result.result;
    }

    //安装app
    public static String getInstallAdb(String apkLocalPath) {
        return " chmod 777 "+apkLocalPath + "\n"+
                "pm install -r "+apkLocalPath ;
    }

    //获取apk包名
    public static String getApkPackageName(Context context, String apkPath){
        String name = "";
        try {
            name = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES).packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getTaragePackageLunchActivityName(Context context,String packagename){
        String lunchActivity = null;
        Intent mainIntent = new Intent();
        mainIntent.setPackage(packagename);
        mainIntent.setAction(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> mApps = context.getPackageManager().queryIntentActivities(mainIntent,0);
        if (mApps!=null && mApps.size()==1){
            lunchActivity = mApps.get(0).activityInfo.name;
        }
        return lunchActivity;
    }
    //打开一个activity
    public static String adbStartActivity(String packageName,String mainActivityClassPath){
        return "am start -a --user 0 android.intent.action.MAIN -c android.intent.category.LAUNCHER -n "+packageName+"/"+mainActivityClassPath;
    }

    //获取apk版本代码
    public static int getApkVersionCode(Context context,String apkPath){
        int code = -1;
        try {
            code = context.getPackageManager().getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
}
