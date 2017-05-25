package com.wos.play.rootdir.model_universal.tool;

import java.util.MissingFormatArgumentException;

import io.vov.vitamio.BuildConfig;

/**
 * Created by user on 2016/10/26.
 * lzp
 * 日志类
 */
public class Logs {
    private static final String TAG = "_Logs";
    public static boolean isDebug = true;//是否开启

    public static void v(String msg) {

        if (isDebug) {
            android.util.Log.v(TAG, msg);
        }
    }
    public static void w(String msg) {

        if (isDebug) {
            android.util.Log.w(TAG, msg);
        }
    }
    public static void d(String msg) {

        if (isDebug) {
            android.util.Log.d(TAG, msg);
        }
    }
    public static void i(String msg) {

        if (isDebug) {
            android.util.Log.i(TAG, msg);
        }
    }
    public static void e(String msg) {

        if (isDebug) {
            android.util.Log.e(TAG, msg);
        }
    }



    public static void v(String tag, String msg) {

        if (isDebug) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {

        if (isDebug) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void d(String tag, String msg) {

        if (isDebug) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {

        if (isDebug) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {

        if (isDebug) {
            android.util.Log.e(tag, msg);
        }
    }


    public static void ii(String tag, String msg, Object... args) {
        try {
            if (isDebug) android.util.Log.i(tag, String.format(msg, args));
        } catch (MissingFormatArgumentException e) {
            android.util.Log.e(tag, "Log", e);
            android.util.Log.i(tag, msg);
        }
    }














}
