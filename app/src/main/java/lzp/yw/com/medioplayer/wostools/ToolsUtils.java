package lzp.yw.com.medioplayer.wostools;

import android.content.Context;
import android.content.SharedPreferences;

import lzp.yw.com.medioplayer.baselayer.BaseApplication;

/**
 * Created by user on 2016/10/26.
 */
public class ToolsUtils {
    private static final String SHARED_FILE_NAME = "_Data";
    private static final String SHARED_STTING_FILE_NAME = "_appOneStting";
    public static void writeShareData(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     * 读取share存储数据
     * @param key
     * @return
     */
    public static String readShareData(String key) {
        SharedPreferences preferences =BaseApplication.appContext.getSharedPreferences(SHARED_FILE_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        String result = preferences.getString(key, "");
        return result;
    }
    /**
     * 获取key，没有获取就使用默认的
     * @param key
     * @param defualtValue
     * @return
     */
    public static String GetKey(String key,String defualtValue)
    {
        String value= readShareData(key).trim();
        return  (value!="")?value:defualtValue;
    }

    private static final String oneSttingKey = "oneflag";
    /**
     * 配置服务器信息
     * 成功 true
     * 失败 false
     * @param flag
     */
    public static void settingServerInfo(boolean flag){
        SharedPreferences preferences = BaseApplication.appContext.getSharedPreferences(SHARED_STTING_FILE_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(oneSttingKey, flag);
        editor.commit();
    }
    /**
     * 读取 是否设置服务器信息
     */
    public static boolean isSettingServerInfo(){
        SharedPreferences preferences =BaseApplication.appContext.getSharedPreferences(SHARED_STTING_FILE_NAME, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
        return preferences.getBoolean(oneSttingKey, false);
    }


}
