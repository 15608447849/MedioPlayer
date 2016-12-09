package com.wos.play.rootdir.model_download.override_download_mode;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by user on 2016/6/22.
 */
public class fileUtils {
    private static final String TAG =  fileUtils.class.getName();

    /**
     * 创建文件
     *
     * @param pathdir
     */
    public static void MkDir(String pathdir) {
        try {
            File file = new File(pathdir);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    /**
     * 获取文件大小
     *
     * @param filename
     * @return
     */
    public static long GetFileSize(String filename) {
        long result = 0;
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return 0;
            } else {
                try {
                    FileInputStream fis = null;
                    fis = new FileInputStream(file);
                    result = fis.available();
                } catch (Exception e) {
                  e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 获取文件名
     *
     * @param pathandname
     * @return
     */
    public static String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        int end = pathandname.length();// pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        }
        return null;
    }



    /**
     * 重命名
     * @param oldPath
     * @param newpath
     * @return
     */
    public static boolean renamefile(String oldPath, String newpath) {
        try {
            File file = new File(oldPath);
            file.renameTo(new File(newpath));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
