package lzp.yw.com.medioplayer.model_application.baselayer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Iterator;
import java.util.Map;

import lzp.yw.com.medioplayer.model_universal.tool.DataListEntiy;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/2.
 */

public class DataListEntiyStore extends DataListEntiy{
    private static String TAG = " _DataListEntiyStore";
    private static final String SHARED_FILE_NAME = "_Data";
    private static final String SHARED_STTING_FILE_NAME = "_appOneStting";
    private static final String oneSttingKey = "onesFlag";


    /**
     * 获取一个shared prefernces
     */
    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SHARED_FILE_NAME,  Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
                + Context.MODE_MULTI_PROCESS);//Context.MODE_WORLD_WRITEABLE);
    }

    /**
     * 写数据到 shared
     * @param context
     * @param key
     * @param value
     */
    public static void writeShareData(Context context, String key, String value) {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     * 读取share存储的数据
     * @param key
     * @return
     */
    public static String readShareData(Context context,String key) {
        SharedPreferences preferences = getSharedPreferences(context);
        String result = preferences.getString(key, "");
        return result;
    }
    /**
     * 获取key，没有获取就使用默认的
     * @param key
     * @param defualtValue
     * @return
     */
    public static String GetKey(Context c,String key,String defualtValue)
    {
        String value= readShareData(c,key).trim();
        return  (value!="")?value:defualtValue;
    }

    private String GetKey(String key,String defualtValue){
        return GetKey(context,key,defualtValue);
    }

    /**
     * 配置服务器信息
     * 成功 true
     * 失败 false
     * @param flag
     */
    public static void settingServerInfo(Context context,boolean flag){
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(oneSttingKey, flag);
        editor.commit();
    }
    /**
     * 读取 是否设置服务器信息
     */
    public static boolean isSettingServerInfo(Context context){
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(oneSttingKey, false);
    }


    private Context context = null;
    public DataListEntiyStore(Context context){
        this.context = context;
    }

    /**
     * 保存数据到本地app的 "_Data"
     */
    public void SaveShareData()
    {
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            writeShareData(context,key.toString(), val.toString());
        }
        Logs.d(TAG,"ToolsDataListEntity_SaveShareData() completion ");
    }

    /**
     * 读取封装配置文档
     */
    public void ReadShareData ()
    {   map.put("connectionType",GetKey("connectionType", "HTTP"));
        //ui配置项
        map.put("terminalNo",GetKey("terminalNo", ""));//终端编号
        map.put("serverip",  GetKey("serverip", "172.16.0.17"));//服务器ip
        map.put("serverport",  GetKey("serverport", "9000"));//服务器端口
        map.put("companyid",  GetKey("companyid", "999"));//公司id
        map.put("HeartBeatInterval",  GetKey("HeartBeatInterval", "30"));//心跳时间
        map.put("sleepTime",GetKey("sleepTime", "30"));//重启时间
        map.put("storageLimits",GetKey("storageLimits","50"));//sdka 容量达到多少时 会清理资源
        map.put("basepath",  GetKey("basepath", "sourceDir"));//资源保存路径
        map.put("jsonStore",GetKey("jsonStore","schuduleList"));//json保存路径
        map.put("appicon",GetKey("appicon","appicon"));// 图标存储路径
        Logs.i(TAG,"ToolsDataListEntity_ReadShareData() \n --------------------------------------------------读取配置信息------------------------------- \n 成功");
    }
    }
