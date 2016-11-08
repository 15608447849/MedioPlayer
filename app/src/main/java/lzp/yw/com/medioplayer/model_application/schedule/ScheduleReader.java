package lzp.yw.com.medioplayer.model_application.schedule;

import java.util.List;

import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;

/**
 * Created by user on 2016/11/8.
 */

public class ScheduleReader {
    private static final String TAG = "_ScheduleRead";
    private static ScheduleReader reader = null;

    private ScheduleReader(){}
    public static ScheduleReader getReader(){
        if (reader == null){
            reader = new ScheduleReader();

        }
        return  reader;
    }

    /**
     * 获取到一个 排期 列表对象
     * @param scheduleList
     */
    public void startWork(List<ScheduleBean> scheduleList) {
        Logs.i(TAG," 开始解析排期 ");
    }
}
