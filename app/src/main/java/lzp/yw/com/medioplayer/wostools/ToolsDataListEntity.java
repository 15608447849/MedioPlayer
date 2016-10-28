package lzp.yw.com.medioplayer.wostools;

import java.util.Iterator;
import java.util.Map;

import lzp.yw.com.medioplayer.baselayer.BaseApplication;
import lzp.yw.com.medioplayer.baselayer.DataListEntiy;
import lzp.yw.com.medioplayer.baselayer.Logs;

/**
 * Created by user on 2016/10/26.
 */
public class ToolsDataListEntity extends DataListEntiy{



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
            ToolsUtils.writeShareData(BaseApplication.appContext,key.toString(), val.toString());
        }
        Logs.d("ToolsDataListEntity_SaveShareData() completion ");
    }

    /**
     * 读取封装配置文档
     */
    public void ReadShareData ()
    {   map.put("connectionType",ToolsUtils.GetKey("connectionType", "HTTP"));
        //ui配置项
        map.put("terminalNo",ToolsUtils.GetKey("terminalNo", ""));
        map.put("serverip",  ToolsUtils.GetKey("serverip", "172.16.0.17"));
        map.put("serverport",  ToolsUtils.GetKey("serverport", "9000"));
        map.put("companyid",  ToolsUtils.GetKey("companyid", "999"));
        map.put("HeartBeatInterval",  ToolsUtils.GetKey("HeartBeatInterval", "30"));
        map.put("basepath",  ToolsUtils.GetKey("basepath", "/mnt/sdcard/"));
        map.put("sleepTime",ToolsUtils.GetKey("sleepTime", "30"));

        Logs.i("ToolsDataListEntity_ReadShareData() \n --------------------------------------------------读取配置信息------------------------------- \n 成功");
    }






}
