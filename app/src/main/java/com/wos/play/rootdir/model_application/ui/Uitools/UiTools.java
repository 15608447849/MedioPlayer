package com.wos.play.rootdir.model_application.ui.Uitools;

import android.content.Context;

import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_application.schedule.ScheduleReader;
import com.wos.play.rootdir.model_application.ui.UiFactory.UiDataFilter;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.MD5Util;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.io.File;
import java.util.Calendar;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2016/11/11.
 */

public class UiTools {
    private static final String TAG = "UI-工具";
    private static boolean isInit = false;
    private static File contentDir = null;
    private static String basepath;
    private static String appicon;
    private static String weatherIconPath = null; //白天 day 黑夜 night
    private static String def_source_dir = null;
    private static String epaper_path_dir = null;

    public static void init(final BaseActivity activity) {
        if (!isInit){
            UiDataFilter.getUiDataFilter().init(activity);// UI 过滤
            basepath = SystemInfos.get().getBasepath();//资源路径
            appicon = SystemInfos.get().getAppicon();//天气图标
            epaper_path_dir = SystemInfos.get().getEpaperSourcePath();//电子报
            contentDir = new File(SystemInfos.get().getJsonStore());//json根目录
            //解压缩
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isInit) {
                        try {
                            unzipWeatherIcon(activity, appicon);
                            unzipDefSource(activity, basepath);
                            //初始化排期读取
                            ScheduleReader.getReader().initSch(SystemInfos.get().getJsonStore());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            isInit = true;
            Logs.i(TAG,"初始化 - UI工具类 - 完成");
        }
    }


    public static void uninit() {
        if (isInit){
            Logs.i(TAG,"注销 - UI 工具类");
            ScheduleReader.getReader().unInit();//关闭排期读取
            UiDataFilter.getUiDataFilter().unInit();
            isInit = false;
        }

    }

    /**
     * 转换背景颜色代码
     *
     * @param colorValue
     * @return
     */
    public static String TanslateColor(String colorValue) {
        String color = null;
        try {
            if (colorValue.startsWith("#") && colorValue.length() == 7) {
                color = colorValue;
            } else {
                if (colorValue.contains("0x")) {
                    color = "#" + colorValue.substring(2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            color = "#FFFFFF";
        }
        return color;
    }

    //获取Uri的文件名
    public static String getUrlTanslationFilename(String url) {
        if (url==null) return null;
        //      ftp://ftp:FTPmedia@172.16.0.17:21/content/1476427174433.jpg
        System.out.println("转换 - url -> 本地文件路径 - " + url);
        String var = "".equals(url.substring(url.lastIndexOf("/") + 1)) ? null : basepath + url.substring(url.lastIndexOf("/") + 1);
        System.out.println("转换 - url -> 本地文件路径 > " + var);
        return var;
    }

    //判断文件是否存在
    public static boolean fileIsExt(String filepath) {
        return FileUtils.isFileExist(filepath);
    }

    //判断文件架夹是否存在
    public static boolean fileDirIsExt(String filepath) {
        return FileUtils.isFolderExist(filepath);
    }

    /*1 获取文件名
        2 获取文件内容
        3 变成图集对象
        4 获取所有文件名*/
    //图集 :文件名->获取文件信息
    public static String urlTanslationJsonText(String filename) {
        if (!contentDir.exists()) {
            return null;
        }
        String[] filenames = contentDir.list();
        if (filenames == null || filenames.length == 0) {
            return null;
        }
        filename = MD5Util.getStringMD5(filename);
        String textContent = null;
        for (String file : filenames) {
            if (file.equals(filename)) {
                textContent = SdCardTools.readerJsonToMemory(contentDir.getAbsolutePath() + "/", filename);
                break;
            }
        }
        return textContent;
    }

    //存入数据 内容目录下  文件名请进行md5加密
    public static boolean storeContentToDirFile(String filename, String content) {
        if (content == null || content.equals("")) {
            return false;
        }
        if (!contentDir.exists()) {
            return false;
        }
        SdCardTools.writeJsonToSdcard(contentDir.getAbsolutePath() + "/", MD5Util.getStringMD5(filename), content);
        return true;
    }
    /**
     * 天气图片 解压缩  源目录 - assets - weathericon.zip -> 根目录下的 appicon->weathericon.zip ->解压缩->删除zip
     */
    private static boolean unzipWeatherIcon(Context context, String dirpath) {
        if (fileDirIsExt(dirpath + "weathericon/")) {
            weatherIconPath = dirpath + "weathericon/";
            //存在
            return true;
        }
        if (AppsTools.ReadAssectsDataToSdCard(context, dirpath, "weathericon.zip")) {

            try {
                unZipFiles(dirpath + "weathericon.zip",dirpath,true); //.substring(0, dirpath.lastIndexOf("/"))
                weatherIconPath = dirpath + "weathericon/";
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    //判断白天还是晚上
    public static String getDateSx() {
        String val = null;
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 8) {
            val = "day/";
        } else if (hour >= 8 && hour < 11) {
            val = "day/";
        } else if (hour >= 11 && hour < 13) {
            val = "day/";
        } else if (hour >= 13 && hour < 18) {
            val = "day/";
        } else {
            val = "night/";
        }
        return val;
    }

    //获取 天气图片 跟路径
    public static String getWeatherIconPath() {
        return weatherIconPath;
    }

    /**
     * 解压  默认 资源 ->
     *      图片 -> def_image.png    视频 def_video.mp4
     *
     * @param context
     * @param basepath
     */
    private static void unzipDefSource(Context context, String basepath) {
//        String outputdir =  basepath.substring(0, basepath.lastIndexOf("/"));
//        outputdir =  outputdir.substring(0, outputdir.lastIndexOf("/")+1);
        final String outputdir = basepath;
        if (fileDirIsExt(outputdir + "defSource/")) {
            //判断文件是否存在
            if (fileIsExt(outputdir + "defSource/def_image.png") && fileIsExt(outputdir + "defSource/def_video.mp4")){
                def_source_dir = outputdir + "defSource/";
                //存在
                return;
            }
            //删除资源
            FileUtils.deleteFile(outputdir + "defSource/def_image.png");
            FileUtils.deleteFile(outputdir + "defSource/ef_video.mp4");
        }
        if (AppsTools.ReadAssectsDataToSdCard(context, outputdir, "defSource.zip")) {
            //执行解压缩
            try {
                unZipFiles(outputdir + "defSource.zip",outputdir,true); //unzipFilePath.substring(0, unzipFilePath.lastIndexOf("/"))
                def_source_dir = outputdir + "defSource/";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //获取 默认图片
    public static String getDefImagePath() {
        return def_source_dir + "def_image.png";
    }

    //获取 默认视频
    public static String getDefVideoPath() {
        return def_source_dir + "def_video.mp4";
    }

    //获取电子报路径
    public static String getEpapers(){
        return epaper_path_dir;
    }

    //解压缩文件
    public static String unZipFiles(String unzipFilePath,String outputDir,boolean isDelete){

        //执行解压缩
        Logs.i("解压缩",unzipFilePath + " -> " + outputDir + (isDelete?" 删除zip包":" 不删除zip包"));
        try {
            AppsTools.UnZip(unzipFilePath, outputDir);
            if (isDelete)  SdCardTools.DeleteFiles(unzipFilePath);   //删除 .zip文件
            return outputDir;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
