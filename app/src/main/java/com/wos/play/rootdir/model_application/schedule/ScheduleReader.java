package com.wos.play.rootdir.model_application.schedule;

import com.wos.play.rootdir.model_application.ui.UiFactory.UiDataFilter;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.MD5Util;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import cn.trinea.android.common.util.FileUtils;

import static com.wos.play.rootdir.model_application.schedule.TimeOperator.dateToStamp;
import static com.wos.play.rootdir.model_application.schedule.TimeOperator.printTargetTimeStamp;


/**
 * Created by user on 2016/11/8.
 */
public class ScheduleReader implements ListeningScheduleEventThread.OnScheduleEvent {
    private static final String TAG = "_ScheduleRead";
    private ReentrantLock lock = new ReentrantLock();
    private static ScheduleReader reader = null;

    //循环线程
    private ListeningScheduleEventThread looper;

    //创建循环线程
    private void createLooper() {
        closeLooper();
        if (looper == null) {
            looper = new ListeningScheduleEventThread();
            looper.setEvent(this);
            looper.setLoopTime(10);
            looper.mStart();
        }
    }

    //结束循环线程
    private void closeLooper() {
        if (looper != null) {
            looper.mStop();
            looper = null;
        }
    }

    private boolean isInit = false;
    private String filedirPath;
    private LocalScheduleObject current = null;//当前正在使用的排期


    /*
    * 构造
    * */
    private ScheduleReader() {

    }

    public void initSch(String filedirPath) {
        if (isInit) {
            return;
        }
        Logs.i(TAG, "- - - 初始化 排期读取 - - -\n" + filedirPath);
        this.filedirPath = filedirPath;
        initScheduleMap();
        initNotToTimeSchuduleMap();
        createLooper();
        isInit = true;
    }

    public void unInit() {
        //结束循环线程
        closeLooper();
        isInit = false;
    }

    //重新更新排期广播任务
    private TimerTask initTimeTask() {

        return new TimerTask() {
            @Override
            public void run() {
                startWork();
            }
        };
    }

    //获取单例
    public static ScheduleReader getReader() {
        if (reader == null) {
            reader = new ScheduleReader();
        }
        return reader;
    }


    //根据类型 获取 string
    public static String getScheduleType(Integer type) {
        String var = null;
        switch (type) {
            case 1:
                var = "[轮播]";
                break;
            case 2:
                var = "[点播]";
                break;
            case 3:
                var = "[重复]";
                break;
            case 4:
                var = "[插播]";
                break;
            case 5:
                var = "[默认]";
                break;
        }
        return var == null ? "[未知]" : var;
    }

    /**
     * 所有排期的集合
     */
    private Map<Integer, ArrayList<ScheduleBean>> scheduleMap = null;

    private void initScheduleMap() {
        if (scheduleMap == null) {
            scheduleMap = Collections.synchronizedMap(new HashMap<Integer, ArrayList<ScheduleBean>>());
        }
        //轮播
        scheduleMap.put(1, new ArrayList<ScheduleBean>());
        //点播
        scheduleMap.put(2, new ArrayList<ScheduleBean>());
        //重复
        scheduleMap.put(3, new ArrayList<ScheduleBean>());
        //插播
        scheduleMap.put(4, new ArrayList<ScheduleBean>());
        //默认
        scheduleMap.put(5, new ArrayList<ScheduleBean>());
    }

    /**
     * 删除指定排期组

     private void deleteOnlySchedule(ScheduleBean schedule){
     int type = schedule.getType();
     Logs.e(TAG,"删除 \n id =="+schedule.getId()+"\n节目名 == "+schedule.getProgram().getTitle()+ " \n类型:"+getScheduleType(type));
     scheduleMap.get(type).clear();
     Logs.i(TAG,scheduleMap.toString());
     }*/
    /**
     * 删除 一组 排期
     */
    private void deleteGroupSchedule(int type) {

        if (scheduleMap.containsKey(type)) {
            Logs.e(TAG, "删除一组排期 \n type ==" + getScheduleType(type) + " 组 _ 排期数量 : " + scheduleMap.get(type).size());
            scheduleMap.get(type).clear();
        }

    }

    /**
     * 未到开始时间的排期
     */
    private Map<Integer, ArrayList<LocalScheduleObject>> notToTimeSchudule = null;

    private void initNotToTimeSchuduleMap() {
        notToTimeSchudule = Collections.synchronizedMap(new HashMap<Integer, ArrayList<LocalScheduleObject>>());
        //点播
        notToTimeSchudule.put(2, new ArrayList<LocalScheduleObject>());
        //重复
        notToTimeSchudule.put(3, new ArrayList<LocalScheduleObject>());
        //插播
        notToTimeSchudule.put(4, new ArrayList<LocalScheduleObject>());
    }


    /**
     * 清理全部排期
     */
    private void clearScheduleMap() {
        //循环
        for (Map.Entry<Integer, ArrayList<ScheduleBean>> entry : scheduleMap.entrySet()) {
            Logs.i(TAG, "全部排期 -> 清理 -> 类型 " + getScheduleType(entry.getKey()) + " -> 大小 " + entry.getValue().size());
            entry.getValue().clear();
        }
    }

    /**
     * 清理 未到时间的排期
     */
    private void clearNotToTimeSchuduleMap() {
        //循环
        for (Map.Entry<Integer, ArrayList<LocalScheduleObject>> entry : notToTimeSchudule.entrySet()) {

            for (LocalScheduleObject lschedule : entry.getValue()) {
                lschedule.stopTimer();
            }
            Logs.i(TAG, "未到时间 排期 -> 清理 -> 类型 " + getScheduleType(entry.getKey()) + " -> 大小 " + entry.getValue().size());
            entry.getValue().clear();
        }
    }

    /**
     * 添加全部排期
     */
    private boolean addScheduleToMap(ScheduleBean entity) {
        //判断是否包含类型
        if (scheduleMap.containsKey(entity.getType())) {
            scheduleMap.get(entity.getType()).add(entity);
            Logs.i(TAG, "全部排期 -> 添加 -> " + getScheduleType(entity.getType()) + "\n id -" + entity.getId());
            return true;
        }
        return false;
    }

    /**
     * 添加未到时间的排期
     *
     * @param entity
     * @return
     */
    private boolean addNotToTimeSchdule(LocalScheduleObject entity) {

        //判断是否包含类型
        if (notToTimeSchudule.containsKey(entity.getType())) {
            notToTimeSchudule.get(entity.getType()).add(entity);
            Logs.i(TAG, "未到时间排期 - 添加 - " + getScheduleType(entity.getType()));
            return true;
        }
        return false;
    }

    private void stopWork() {
        Logs.i(TAG, "---------------排期停止中-----------");
        //清理当前正在使用的排期
        if (current != null) {
            current.stopTimer();
            current = null;
        }
        // 清理 未到时间的排期
        clearNotToTimeSchuduleMap();
        //清理全部排期
        clearScheduleMap();
    }

    //还有可用排期存在?
    private boolean isExit() {

        if (scheduleMap.get(1).size() > 0) {
            return true;
        }
        if (scheduleMap.get(2).size() > 0) {
            return true;
        }
        if (scheduleMap.get(3).size() > 0) {
            return true;
        }
        if (scheduleMap.get(4).size() > 0) {
            return true;
        }
        if (scheduleMap.get(5).size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 解析-----------------------------------------------------
     */
    private LocalScheduleObject parseing() {

        //过滤 (某一个 播放类型 的数组) // type,ArrayList<ScheduleBean>
        ParseScheTempObject temp = filterScheduleList();
        LocalScheduleObject sche = null;
        if (temp != null) {
            sche = filterOnlySchudule(temp);
            deleteGroupSchedule(temp.scheType);
        }
        if (sche == null && isExit()) {
            sche = parseing();
        }
        return sche;
    }

    /*------------------------------------分组过滤---------------------------------------------------------------------------------*/
    private ParseScheTempObject filterScheduleList() {

        //优先级 :插播4 >点播2 > 重复3 > 轮播1 >默认5
        if (scheduleMap.get(4).size() > 0) {
            Logs.i(TAG, "返回全部的 插播 排期");
            return new ParseScheTempObject(4, scheduleMap.get(4));
        }
        if (scheduleMap.get(2).size() > 0) {
            Logs.i(TAG, "返回全部的 点播 排期");
            return new ParseScheTempObject(2, scheduleMap.get(2));
        }
        if (scheduleMap.get(3).size() > 0) {
            Logs.i(TAG, "返回全部的 重复 排期");
            return new ParseScheTempObject(3, scheduleMap.get(3));
        }
        if (scheduleMap.get(1).size() > 0) {
            Logs.i(TAG, "返回全部的 轮播 排期");
            return new ParseScheTempObject(1, scheduleMap.get(1));
        }
        if (scheduleMap.get(5).size() > 0) {
            Logs.i(TAG, "返回全部的 默认 排期");
            return new ParseScheTempObject(5, scheduleMap.get(5));
        }
        return null;
    }

    /**
     * 过滤一个
     */
    private LocalScheduleObject filterOnlySchudule(ParseScheTempObject arr) {
        int type = arr.scheType;
        ArrayList<ScheduleBean> schduleList = arr.scheArr;
        LocalScheduleObject scheBean = null;
        try {
            switch (type) {
                case 1://轮播
                case 5://默认
                    scheBean = new LocalScheduleObject(
                            dateToStamp(TimeOperator.getToday() + " " + "00:00:00"),
                            dateToStamp(TimeOperator.getToday() + " " + "23:59:59"),
                            schduleList.get(0));
                    break;
                case 4://插播
                case 3://重复
                case 2://点播
                    scheBean = filete(schduleList);
                    break;
            }
        } catch (Exception e) {
            Logs.e(TAG,"filterOnlySchudule() - "+ e.getMessage());
        }
        return scheBean;
    }

    //插播
    //点播  每天 某个时间段 - 忽略 年月日
    //重复
    private LocalScheduleObject filete(ArrayList<ScheduleBean> schduleList) {
        return sortList(forechSchulistAndJust(schduleList));
    }

    //循环遍历
    private ArrayList<LocalScheduleObject> forechSchulistAndJust(ArrayList<ScheduleBean> schduleList) {
        ArrayList<LocalScheduleObject> storeList = new ArrayList<>();

        for (ScheduleBean entity : schduleList) {
            effiOnlySchduler(storeList, entity);
        }
        return storeList;
    }

    //检查单个排期
    private void effiOnlySchduler(ArrayList<LocalScheduleObject> storeList, ScheduleBean entity) {
        int result = Efficacy.justTime(entity);

        if (result == Efficacy.TYPE.error || result == Efficacy.TYPE.before_the_current_time) {//错误 或者 当前时间 之前 -> 下一次
            return;
        }
        if (result == Efficacy.TYPE.after_the_current_time) {//添加到未到时间 排期列表
            addNotToTimeSchdule(
                    new LocalScheduleObject(
                            entity.getType(),
                            initTimeTask(),
                            TimeOperator.getMillisecond(entity.getStartTime())
                    ));
        }
        if (result == Efficacy.TYPE.in_the_current_time) {
            storeList.add(new LocalScheduleObject(entity.getStartTime(), entity.getEndTime(), entity));
        }
    }

    // 快速 排序 1
    private LocalScheduleObject sortList(ArrayList<LocalScheduleObject> storeList) {
        try {
            if (storeList.size() > 0) {
                if (storeList.size() == 1) {
                    return storeList.get(0);
                } else {
                    //排序 - 按 - 开始时间大小
                    storeList = sortSchudule(storeList);
                    return storeList.get(0);
                }
            }
        } catch (Exception e) {
            Logs.e(TAG,"sortList() - "+e.getMessage());
        }
        return null;
    }

    // 快速 排序 2
    private ArrayList<LocalScheduleObject> sortSchudule(ArrayList<LocalScheduleObject> playList) {
        Collections.sort(playList, new Comparator<LocalScheduleObject>() {
            @Override
            public int compare(LocalScheduleObject sa, LocalScheduleObject sb) {
                long a = Long.valueOf(sa.getStart());
                long b = Long.valueOf(sb.getStart());
                printTargetTimeStamp(a);
                printTargetTimeStamp(b);
                return a - b > 0 ? -1 : a - b == 0 ? 0 : 1;  //-1代表前者小，0代表两者相等，1代表前者大。
            }
        });
//        for (int i = 0; i < playList.size(); i++) {
//            System.out.println("index : " + i + " - " + playList.get(i).getStart());
//        }
        return playList;
    }

    /**
     * 判断 时间
     *
     * @param entity //如果开始时间 和结束时间 在当前时间 之前 - 继续 1
     *               //如果开始时间 < 当前时间 < 结束时间  保留 ->返回      2
     *               //如果 当前时间 < 开始时间 < 结束时间  保留 -> 添加到未到时间的排期 3
     *               //以上规则全部不符合 -1
     */
    private int justTime(ScheduleBean entity) {
        int result = -1;
        int type = entity.getType();
        if (type == 3) {
            //重复
            if (entity.getRules() != null) {
                result = JustRepeteType.just(entity);//
            }
        }
        if (type == 2) {
            //点播
            result = JustPointPlay.Just(entity);
        }
        return result;
    }

    /**
     * 获取到一个 排期 列表对象
     * <p>
     * 1 轮播
     * 2 点播
     * 3 重复
     * 5 默认
     * //获取优先级 高的 -> 分类型验证时间 (开始时间 结束时间)-> 当前时间 -> 分发任务
     */
    private void startWork() {

        try {
            lock.lock();
            if (!isInit) {
                Logs.e(TAG, "请初始化 排期读取对象 ");
                return;
            }
            if (scheduleList == null) {
                Logs.e(TAG, "排期列表不正确,无法读取");
                return;
            }

            stopWork();
            Logs.i(TAG, " ----------------------开始解析排期----------------------------- ");
            //分组
            for (ScheduleBean entity : scheduleList) {
                addScheduleToMap(entity);
            }
            Logs.i(TAG, "---分组 完成---");
            try {
                current = parseing();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logs.i(TAG, "---解析 完成---");

            if (current==null){
                Logs.i(TAG,"---没有可播放排期任务---");
                return;
            }
            current.startTimer(initTimeTask(),TimeOperator.getMillisecond(current.getEnd()));
            Logs.i(TAG, "---设置时间完成---");

            //转换数据 - 发送 排期信息 -> ui制作
            UiDataFilter.getUiDataFilter().filter(current);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    private long temVar = 0;
    private long fileLastModifTime = 0;

    /**
     * 排期事件 监听
     */
    @Override
    public void loopEvent() {

        if (FileUtils.isFileExist(filedirPath + "main")) {
            //文件存在
            temVar = new File(filedirPath + "main").lastModified();
            if (fileLastModifTime != temVar) {
                //文件有改动 或者 第一次
                fileLastModifTime = temVar;
                scheduleExecuter();
            }
        }
    }

    private void scheduleExecuter() {
        //获取 <main> 文件内容 - > 得到 uri -> md5 ->文件名 -> 获取文本内容 -> 变成对象->scheduleReader
        String schdEntrance = SdCardTools.readerJsonToMemory(filedirPath + "main");
        Logs.i(TAG, "排期地址 - " + schdEntrance);
        schdEntrance = MD5Util.getStringMD5(schdEntrance);
        String[] filenames = new File(filedirPath).list();
        for (String filename : filenames) {
            if (filename.equals(schdEntrance)) {
                //找到了 ->变成对象
                tansSchdule(SdCardTools.readerJsonToMemory(filedirPath, filename));
            }
        }
    }

    private List<ScheduleBean> scheduleList = null;

    private void tansSchdule(String jsonContent) {
        if (jsonContent != null && !"".equals(jsonContent)) {
            scheduleList = AppsTools.parseJonToList(jsonContent, ScheduleBean[].class);
        }
        startWork();
    }

    @Override
    public void error(Exception e) {
        e.printStackTrace();
    }
}
