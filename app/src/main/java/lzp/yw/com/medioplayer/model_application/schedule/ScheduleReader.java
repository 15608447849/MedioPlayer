package lzp.yw.com.medioplayer.model_application.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;


/**
 * Created by user on 2016/11/8.
 */
public class ScheduleReader {
    private static final String TAG = "_ScheduleRead";
    private ReentrantLock lock = new ReentrantLock();
    private static ScheduleReader reader = null;
    private Context  c;
    private String filedirPath;
    private LocalScheduleObject current = null;//当前正在使用的排期
    /*
    * 构造
    * */
    private ScheduleReader(Context c,String filedirpath){
        this.c = c;
        this.filedirPath = filedirpath;
        initScheduleMap();
        initNotToTimeSchuduleMap();
        initTimeTask();
    }

    //重新更新排期广播任务
    private void initTimeTask() {
        if (timerTask == null){

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //发送广播 -> 排期读取广播
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.clear();
                    intent.setAction(ScheduleReadBroad.ACTION);
                    bundle.putString(ScheduleReadBroad.PARAM, filedirPath);
                    intent.putExtras(bundle);
                    c.sendBroadcast(intent);
                }
            };
        }
    }

    //单例
    private TimerTask timerTask = null;
    //获取单例
    public static ScheduleReader getReader(Context c,String filedirpath){
        if (reader == null){
            reader = new ScheduleReader(c,filedirpath);

        }
        return  reader;
    }



    //根据类型 获取 string
    private String getScheduleType(Integer type){
        String var = null;
        switch (type){
            case 1:
                var = "[轮播]";
                break;
            case 2:
                var = "[点播]";
                break;
            case 3:
                var = "[重复]";
                break;
        }
        return var==null?"[未知]":var;
    }
    /**
     * 所有排期的集合
     */
    private Map<Integer,ArrayList<ScheduleBean>> scheduleMap = null;
    private void initScheduleMap(){
        scheduleMap = Collections.synchronizedMap(new HashMap<Integer, ArrayList<ScheduleBean>>());
        //轮播排期
        scheduleMap.put(1,new ArrayList<ScheduleBean>());
        //点播
        scheduleMap.put(2,new ArrayList<ScheduleBean>());
        //重复
        scheduleMap.put(3,new ArrayList<ScheduleBean>());
        //插播
        //默认
    }

    /**
     * 删除指定排期组
     */
    private void deleteOnlySchedule(ScheduleBean schedule){
        int type = schedule.getType();
        Logs.e(TAG,"删除 \n id =="+schedule.getId()+"\n节目名 == "+schedule.getProgram().getTitle()+ " \n类型:"+getScheduleType(type));
        scheduleMap.get(type).clear();
        Logs.i(TAG,scheduleMap.toString());
    }

    /**未到开始时间的排期*/
    private  Map<Integer,ArrayList<LocalScheduleObject>> notToTimeSchudule = null;
    private void initNotToTimeSchuduleMap(){
        notToTimeSchudule = Collections.synchronizedMap(new HashMap<Integer, ArrayList<LocalScheduleObject>>());
        //点播
        notToTimeSchudule.put(2,new ArrayList<LocalScheduleObject>());
        //重复
        notToTimeSchudule.put(3,new ArrayList<LocalScheduleObject>());
    }


    /**
     * 清理全部排期
     */
    private void  clearScheduleMap (){
        //循环
        for (Map.Entry<Integer, ArrayList<ScheduleBean>> entry : scheduleMap.entrySet()) {
            Logs.i(TAG,"全部排期 - 清理 类型 " + getScheduleType(entry.getKey())+ " - 大小 " + entry.getValue().size());
            entry.getValue().clear();
        }
    }
    /**
     * 清理 未到时间的排期
     */
    private void clearNotToTimeSchuduleMap(){
        //循环
        for (Map.Entry<Integer, ArrayList<LocalScheduleObject>> entry : notToTimeSchudule.entrySet()) {

            for (LocalScheduleObject lschedule : entry.getValue()){
                lschedule.stopTimer();
            }
            Logs.i(TAG,"未到时间 排期 - 清理 类型 " + getScheduleType(entry.getKey())+ " - 大小 " + entry.getValue().size());
            entry.getValue().clear();
        }
    }

    /**
     * 添加全部排期
     *
     *
     */
    private boolean addScheduleToMap(ScheduleBean entity){
            //判断是否包含类型
            if (scheduleMap.containsKey(entity.getType())){
                scheduleMap.get(entity.getType()).add(entity);
                Logs.i(TAG,"全部排期 - 添加 - "+getScheduleType(entity.getType()) +"\n id -"+ entity.getId());
                return true;
            }
            return false;
    }

    /**
     * 添加未到时间的排期
     * @param entity
     * @return
     */
    private boolean addNotToTimeSchdule(LocalScheduleObject entity){

        //判断是否包含类型
        if (notToTimeSchudule.containsKey(entity.getType())){
            notToTimeSchudule.get(entity.getType()).add(entity);
            Logs.i(TAG,"未到时间排期 - 添加 - "+getScheduleType(entity.getType()));
            return true;
        }
        return false;
    }
    private void stopWork(){
        Logs.i(TAG,"---------------清理中-----------");
        //清理当前正在使用的排期
        if(current!=null){
            current.stopTimer();
            current = null;
        }
        // 清理 未到时间的排期
        clearNotToTimeSchuduleMap();
        //清理全部排期
        clearScheduleMap();
    }

    private boolean isExit(){
        if (scheduleMap.get(3).size()>0){
            return false;
        }
        if (scheduleMap.get(2).size()>0){
            return false;
        }
        if (scheduleMap.get(1).size()>0){
            return false;
        }
        return true;
    }

    /**
     * 解析-----------------------------------------------------
     * */
    private LocalScheduleObject parseing(){
        LocalScheduleObject sche  = null;
        //过滤 (某一个 播放类型 的数组) // type,ArrayList<ScheduleBean>
        Object[] objarr = filterScheduleList();
        if (objarr==null){
            Logs.e(TAG,"当前无排期信息");
        }else{
            sche = filterOnlySchudule(objarr);
        }
        if (sche==null && !isExit()){
            deleteOnlySchedule((ScheduleBean) objarr[1]);
            sche = parseing();
        }
        return sche;
    }

    /*------------------------------------分组过滤---------------------------------------------------------------------------------*/
    private Object[] filterScheduleList(){
    //优先级 : 重复 3> 点播 2> 轮播 1
    if(scheduleMap.get(3).size()>0){
        Logs.i(TAG,"返回全部的 重复 排期");
        return new Object[]{3,scheduleMap.get(3)};
    }
    if (scheduleMap.get(2).size()>0){
        Logs.i(TAG,"返回全部的 点播 排期");
        return new Object[]{2,scheduleMap.get(2)};
    }
    if(scheduleMap.get(1).size()>0){
        Logs.i(TAG,"返回全部的 轮播 排期");
        return new Object[]{1,scheduleMap.get(1)};
    }
    return null;
}
    /**
     * 过滤一个
     * */
    private LocalScheduleObject filterOnlySchudule(Object[] arr) {
        LocalScheduleObject scheBean = null;
        try {
            int type = (Integer) arr[0];
            ArrayList<ScheduleBean> schduleList = (ArrayList<ScheduleBean>) arr[1];
           switch (type){
               case 3://重复
               case 2://点播
                   scheBean = filete(schduleList);
                   break;
               case 1://轮播
                   scheBean = new LocalScheduleObject(
                           TimeOperator.dateToStamp(TimeOperator.getToday()+" "+"00:00:00"),
                           TimeOperator.dateToStamp(TimeOperator.getToday()+" "+"23:59:59"),
                           schduleList.get(0));
                   deleteOnlySchedule(schduleList.get(0));
                   break;
           }
        }catch (Exception e){
            e.printStackTrace();
        }
        return scheBean;
    }

    //点播  每天 某个时间段 - 忽略 年月日
    //重复
    private LocalScheduleObject filete(ArrayList<ScheduleBean> schduleList) {
        return sortList(forechSchulistAndJust(schduleList));
    }
    //循环遍历
    private ArrayList<LocalScheduleObject> forechSchulistAndJust(ArrayList<ScheduleBean> schduleList){
        ArrayList<LocalScheduleObject> storeList = new ArrayList<>();
        int result = -1;
        for (ScheduleBean entity:schduleList){
            result = justTime(entity);
            deleteOnlySchedule(entity);
            if (result == 1 || result == -1){
                continue;
            }
            if (result == 3){
                addNotToTimeSchdule(new LocalScheduleObject(entity.getType(),
                        timerTask,
                        TimeOperator.getMillisecond(entity.getStartTime())));//添加到未到时间 排期列表

            }
            if (result == 2){
                storeList.add(new LocalScheduleObject(entity.getStartTime(),entity.getEndTime(),entity));
            }
        }
        return storeList;
    }
    // 快速 排序 1
    private LocalScheduleObject sortList(ArrayList<LocalScheduleObject> storeList){
        if (storeList.size()>0){
            if (storeList.size()==1){
                return storeList.get(0);
            }else{
                //排序 - 按 - 开始时间大小
                storeList = sortSchudule(storeList);
                return storeList.get(0);
            }
        }
        return null;
    }

    // 快速 排序 2
    private ArrayList<LocalScheduleObject> sortSchudule(ArrayList<LocalScheduleObject> playList) {
        Collections.sort(playList, new Comparator<LocalScheduleObject>() {
            @Override
            public int compare(LocalScheduleObject sa, LocalScheduleObject sb) {
                long a = Long.valueOf(sa.getStart());
                long b = Long.valueOf(sb.getEnd());
                TimeOperator.printTargetTimeStamp(a);
                TimeOperator.printTargetTimeStamp(b);
                return a-b>0 ? -1: a-b==0 ? 0:1;  //-1代表前者小，0代表两者相等，1代表前者大。
            }
        });
        for (int i = 0;i<playList.size();i++){
            System.out.println("index : "+ i +" - "+playList.get(i).getStart());
        }
        return playList;
    }
    /**
     * 判断 时间
     * @param entity
     *  //如果开始时间 和结束时间 在当前时间 之前 - 继续 1
        //如果开始时间 < 当前时间 < 结束时间  保留 ->返回      2
        //如果 当前时间 < 开始时间 < 结束时间  保留 -> 添加到未到时间的排期 3
        //以上规则全部不符合 -1
     */
    private int justTime(ScheduleBean entity) {
        int result = -1 ;
        int type = entity.getType();
        if (type == 3){
            //重复
            if (entity.getRules()!=null){
                result = JustRepeteType.just(entity);//
            }
        }
        if (type == 2){
            //点播
            result = JustPointPlay.Just(entity);
        }
        return result;
    }

    /**
     * 获取到一个 排期 列表对象
     * @param scheduleList
     * 1 轮播
     * 2 点播
     * 3 重复
     * //获取优先级 高的 -> 分类型验证时间 (开始时间 结束时间)-> 当前时间 -> 分发任务
     */
    public void startWork(List<ScheduleBean> scheduleList) {
        try{
            lock.lock();
            stopWork();
            Logs.i(TAG," ----------------------开始解析排期----------------------------- ");
            //分组
            for (ScheduleBean entity : scheduleList){
                addScheduleToMap(entity);
            }
            Logs.i(TAG,"---分组 完成---  \n"+scheduleMap.toString());
            current = parseing();
            Logs.i(TAG,"---解析 完成---  \n"+current.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }


    }
}
