package com.wos.play.rootdir.model_download.override_download_mode;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cn.trinea.android.common.util.StringUtils;

/**
 * Created by user on 2016/6/22.
 */
public class FileUtils {
    private static final String TAG =  FileUtils.class.getName();

    /**
     * 创建文件
     * @param pathDir 路径
     */
    public static void MkDir(String pathDir) {
        try {
            File file = new File(pathDir);
            if (!file.exists() && file.mkdirs()) {
                Log.d(TAG, "创建本地目录:" + pathDir);
            }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     * @param path 文件全路径
     */
    public static long getFileSize(String path) {
        long result = 0;
        try {
            File file = new File(path);
            if (!file.exists()) return 0;
            try {
                FileInputStream fis = new FileInputStream(file);
                result = fis.available();
            } catch (Exception e) {
              e.printStackTrace();
            }
        } catch (Exception e) {
            Log.w(TAG, "读取文件长度失败:"+path);
        }
        return result;
    }

    /**
     * 获取文件名
     * @param path 文件全路径
     */
    public static String getFileName(String path) {
        if (path ==  null|| path.length() == 0)  return null;
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 重命名
     * @param oldPath 旧路径
     * @param newPath 新路径
     * @return 是否成功
     */
    public static boolean renameFile(String oldPath, String newPath) {
        try {
            File file = new File(oldPath);
            return file.renameTo(new File(newPath));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    public static void copyFile(File jhFile, File file) throws IOException {
       org.apache.commons.io.FileUtils.copyFile(jhFile, file);
    }

    public static void deleteFile(String localPath) {
        cn.trinea.android.common.util.FileUtils.deleteFile(localPath);
    }
}
