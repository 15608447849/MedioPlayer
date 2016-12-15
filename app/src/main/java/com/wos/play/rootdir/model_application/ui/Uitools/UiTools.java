package com.wos.play.rootdir.model_application.ui.Uitools;

import android.content.Context;
import android.util.Log;

import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.baselayer.SystemInitInfo;
import com.wos.play.rootdir.model_application.schedule.ScheduleReader;
import com.wos.play.rootdir.model_application.ui.UiFactory.UiDataFilter;
import com.wos.play.rootdir.model_application.ui.UiHttp.UiHttpProxy;
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
    private static boolean isInit = false;
    private static File contentDir = null;
    private static String basepath;
    private static String appicon;
    private static String weatherIconPath = null; //白天 day 黑夜 night
    private static String def_source_dir = null;

    public static void init(Context context) {
        final BaseActivity context1 = (BaseActivity) context;

        basepath = SystemInitInfo.get().getBasepath();
        appicon = SystemInitInfo.get().getAppicon();
        contentDir = new File(SystemInitInfo.get().getJsonStore());//json根目录


        //
        isInit = true;
        //解压缩
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isInit) {
                    try {
                        UiHttpProxy.getPeoxy().init(context1);
                        UiDataFilter.init(context1);// UI 过滤
                        unzipWeatherIcon(context1, appicon);
                        unzipDefSource(context1, basepath);
//                        Mvitamios.initStreamMedia(context1); //初始化vitimio
                        //初始化排期读取
//                     ScheduleReader.getReader().initSch(SystemInitInfo.get().getJsonStore());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    public static void uninit() {
        ScheduleReader.getReader().unInit();//关闭排期读取
        UiDataFilter.unInit();
        UiHttpProxy.getPeoxy().unInit();//ui下载关闭();//ui下载关闭
        isInit = false;
    }

    /**
     * 转换背景颜色代码
     *
     * @param colorValue
     * @return
     */
    public static String TanslateColor(String colorValue) {
        Log.i("", " - - color code param ->" + colorValue);
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
        Log.i("", " - - color code tanslate code ->" + color);
        return color;
    }

    //获取Uri的文件名
    public static String getUrlTanslationFilename(String url) {
        //      ftp://ftp:FTPmedia@172.16.0.17:21/content/1476427174433.jpg
//        System.out.println("转换 - url -> 本地文件路径 - " +url);
        return "".equals(url.substring(url.lastIndexOf("/") + 1)) ? null : basepath + url.substring(url.lastIndexOf("/") + 1);
    }

    //判断文件是否存在
    public static boolean fileIsExt(String filepath) {
        return FileUtils.isFileExist(filepath);
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
        if (content == null && content.equals("")) {
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
        if (fileIsExt(dirpath + "weathericon")) {
            weatherIconPath = dirpath + "weathericon/";
            //存在
            return true;
        }
        if (AppsTools.ReadAssectsDataToSdCard(context, dirpath, "weathericon.zip")) {
            //执行解压缩
            Logs.i("unzip", "路径- ->>>>>" + dirpath + "weathericon.zip");
            try {
                AppsTools.UnZip(dirpath + "weathericon.zip", dirpath.substring(0, dirpath.lastIndexOf("/")));
                //删除 .zip文件
                SdCardTools.DeleteFiles(dirpath + "weathericon.zip");
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
     * 解压  默认 资源  - 图片 -> def_image.png    视频 def_video.mp4
     *
     * @param context
     * @param basepath
     */
    private static void unzipDefSource(Context context, String basepath) {
        if (fileIsExt(basepath + "defSource")) {
            def_source_dir = basepath + "defSource/";
            //存在
            return;
        }
        if (AppsTools.ReadAssectsDataToSdCard(context, basepath, "defSource.zip")) {
            //执行解压缩
            Logs.i("unzip", "路径- ->>>>>" + basepath + "defSource.zip");
            try {
                AppsTools.UnZip(basepath + "defSource.zip", basepath.substring(0, basepath.lastIndexOf("/")));
                //删除 .zip文件
                SdCardTools.DeleteFiles(basepath + "defSource.zip");
                def_source_dir = basepath + "defSource/";
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


}
