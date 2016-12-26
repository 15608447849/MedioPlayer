package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_application.baselayer.SystemInitInfo;
import com.wos.play.rootdir.model_command_.kernel.CommandPostBroad;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.AdBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.LayoutBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.BaiduApiObject;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.WeathersBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.CONTENT_TYPE;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static com.wos.play.rootdir.model_universal.tool.AppsTools.uriTransionString;
import static com.wos.play.rootdir.model_universal.tool.CONTENT_TYPE.weather;

/**
 * Created by user on 2016/10/27.
 * lzp
 * 读取排期
 */
public class Command_UPSC implements iCommand {
    private static final String TAG = "_UPSC";
    private Context context;
    private String basePath;//资源存储路径
    private String storageLimits;//存储上限百分比
    private Intent intent;
    private Bundle bundle;
    private UrlList taskStore;
    public Command_UPSC(Context context) {
        this.context = context;
        bundle = new Bundle();
        intent = new Intent();
        basePath = SystemInitInfo.get().getBasepath();
        storageLimits = SystemInitInfo.get().getStorageLimits();
        taskStore = new UrlList();
    }

    private static ReentrantLock lock = new ReentrantLock();

    //结果
    private String res;

    /**
     * 入口-------------------------------------------------------------------------------------------------------------------------------------------------
     *
     * @param param
     */
    @Override
    public void Execute(String param) {
        Long startTime;//开始时间
        Long endTime;//结束时间
        try {
            lock.lock();
            Logs.d(TAG, "获取排期 uri : " + param);
            res = null;
            res = uriTransionString(param, null, null);
            if (res != null) {
                //保存json数据
                ICommand_SORE_JsonDataStore.getInstent(context).clearJsonMap();
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity("main", param, false); //main 保存主文件的 文件名
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity(param, res, true);//保存主文件内容
                //解析json
                List<ScheduleBean> list = AppsTools.parseJonToList(res, ScheduleBean[].class);

                if (list != null) {
                    taskStore.initLoadingList();
                    startTime = System.currentTimeMillis();
                    parseScheduleList(list);

                    endTime = System.currentTimeMillis();
                    Logs.e(TAG, "解析排期 用时 : " + (endTime - startTime) + " 毫秒 ");
                    startTime = System.currentTimeMillis();
                    clearSdcardSource();
                    endTime = System.currentTimeMillis();
                    Logs.e(TAG, "清理资源 用时 : " + (endTime - startTime) + " 毫秒 ");
                    sendDataSaveTask();
                    nonotifyDownLoad();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }




    /**
     * 解析 排期列表
     *
     * @param schedulelist
     */
    private void parseScheduleList(List<ScheduleBean> schedulelist) {
        Logs.d(TAG, "排期总数: " + schedulelist.size());
        for (ScheduleBean schedule : schedulelist) {
            parseSchedule(schedule);
            Logs.i(TAG, "-------------------------------------------------解析完一个排期----------------------------------------------------------");
        }
    }

    /**
     * 解析单个排期
     *
     * @param schedule
     */
    private void parseSchedule(ScheduleBean schedule) {
        parseProgram(schedule.getProgram());
    }

    /**
     * 解析节目
     *
     * @param program
     */
    private void parseProgram(ProgramBean program) {
        parseLayout(program.getLayout());
    }

    /**
     * 解析布局
     * 1 广告列表
     * 2 页面内标
     *
     * @param layout
     */
    private void parseLayout(LayoutBean layout) {
        if (layout.getAd() != null && layout.getAd().size() > 0) {
            for (AdBean ad : layout.getAd()) {
                parseAd(ad);
            }
        }
        if (layout.getPages() != null && layout.getPages().size() > 0) {
            for (PagesBean pages : layout.getPages()) {
                parsePages(pages);
            }
        }
    }

    /**
     * 解析广告
     *
     * @param ad
     */
    private void parseAd(AdBean ad) {
        Logs.d(TAG,"解析广告");
        if (ad.getComponents() != null && ad.getComponents().size() > 0) {
            for (ComponentsBean component : ad.getComponents()) {
                parseComponet(component);
            }
        }

    }

    /**
     * 解析 页面
     *
     * @param page
     */
    private void parsePages(PagesBean page) {
        Logs.d(TAG,"解析页面");
        taskStore.addTaskOnList(page.getBackground());
        if (page.getComponents() != null && page.getComponents().size() > 0) {
            for (ComponentsBean components : page.getComponents()) {
                parseComponet(components);
            }
        }

        if (page.getPages() != null && page.getPages().size() > 0) {
            for (PagesBean subpage : page.getPages()) {
                parsePages(subpage);
            }

        }
    }

    /**
     * 解析组件
     *
     * @param component
     */
    private void parseComponet(ComponentsBean component) {
        taskStore.addTaskOnList(component.getBackgroundPic());
        if (component.getContents() != null && component.getContents().size() > 0) {
            for (ContentsBean content : component.getContents()) {
                parseContent(content);
            }

        }
    }

    /**
     * 解析内容
     */
    private void parseContent(ContentsBean content) {
        parseContentSourece(content.getContentType(), content);
    }
/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------*/


    /**
     * 清理资源
     */
    private void clearSdcardSource() {
        //如果 true  清理!
        if (SdCardTools.justFileBlockVolume(basePath,storageLimits)) {
            SdCardTools.clearTargetDir(basePath, taskStore.getTaskListFileNames());
        }
    }

    /**
     * 解析具体内容 来源
    */
    private void parseContentSourece(String contentType, ContentsBean content) {

        Logs.d(TAG, ">>>> 解析具体内容 类型 - " + contentType );
        //图片
        if (contentType.equals(CONTENT_TYPE.image)) {
            taskStore.addTaskOnList(content.getContentSource());
        }
        //二维码
        if (contentType.equals(CONTENT_TYPE.qrCode)){

            taskStore.addTaskOnList(content.getContentSource());
        }
        //按钮
        if (contentType.equals(CONTENT_TYPE.button)) {
            //默认展示图片
            taskStore.addTaskOnList(content.getSourceUp());
            //点击时展示图片
            taskStore.addTaskOnList(content.getSourceDown());
        }
        //多媒体
        if (contentType.equals(CONTENT_TYPE.video)) {
            taskStore.addTaskOnList(content.getContentSource());
        }
        //图集
        if (contentType.equals(CONTENT_TYPE.gallary)) {
            getUrlSource(content.getContentSource(), CONTENT_TYPE.gallary);
        }
        //咨询
        if (contentType.equals(CONTENT_TYPE.news)) {
               getUrlSource(content.getContentSource(),CONTENT_TYPE.gallary);
        }
        //电子报
        if (contentType.equals(CONTENT_TYPE.epaper)) {
            parseEpaper(content.getContentSource());
        }
        //时钟
        if (contentType.equals(CONTENT_TYPE.clock)) {//swf
            taskStore.addTaskOnList(content.getContentSource());
        }

        //天气
        if (contentType.equals(weather)) {
            getUrlSource(content.getContentSource(), weather);
        }
        //文本控件
        if (contentType.equals(CONTENT_TYPE.text)) {
        }
        //流媒体
        if (contentType.equals(CONTENT_TYPE.media)) {
        }
        //跑马灯
        if (contentType.equals(CONTENT_TYPE.marquee)) {
        }
        //网页
        if (contentType.equals(CONTENT_TYPE.html)) {
        }
    }



    /**
     * 获取图集资源
     * 获取电子报资源
     *
     * @param contentSource
     */
    private void getUrlSource(String contentSource, String type) {//

        try {
            Logs.i(TAG, " content - url [ " + contentSource+" ]");
            res = null;
            res = AppsTools.uriTransionString(AppsTools.urlEncodeParam(contentSource), null, null);
            if (type.equals(CONTENT_TYPE.gallary) || type.equals(CONTENT_TYPE.news) || type.equals(CONTENT_TYPE.weather)) {  //咨询 图集 天气 ,base64解码

                res = AppsTools.justResultIsBase64decode(res);
            }
            if (res != null) {
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity(contentSource, res, true);//文件名,文件内容
                parseResult(res, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 解析内容url - 返回值
    private void parseResult(String res, String type) {
        if (type.equals(CONTENT_TYPE.gallary)) {
            GallaryBean gallaryBean = AppsTools.parseJsonWithGson(res, GallaryBean.class);
            if (gallaryBean != null) {
                parseContentGallarys(gallaryBean);
            }
        }

        if (type.equals(weather)) {
            WeathersBean weather = AppsTools.parseJsonWithGson(res, WeathersBean.class);
            if (weather != null) {
                parseWeatherToBaiduApi(weather.getWeatherData().getCurrentCity());
            }
        }
    }

    //访问 百度 api
    private void parseWeatherToBaiduApi(String currentCity) {
        //再次访问 百度 api 保存结果
        String baiduApiUrl = AppsTools.generWeateherContentUrl(currentCity);
        res = null;
        res = AppsTools.uriTransionString(baiduApiUrl, AppsTools.baiduApiMap(), null);
        if (res != null) {
            res = AppsTools.justResultIsUNICODEdecode(res);
            System.out.println(res);
            BaiduApiObject obj = AppsTools.parseJsonWithGson(res, BaiduApiObject.class);
            if (obj != null && obj.getErrNum() == 0 && obj.getErrMsg().equals("success")) {
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity(baiduApiUrl, res, true);//文件名,文件内容
            }
        }
    }


    /**
     * 解析内容下面的图集
     */
    private void parseContentGallarys(GallaryBean gallaryBean) {
        if (gallaryBean.getDataObjs() != null && gallaryBean.getDataObjs().size() > 0) {
            for (DataObjsBean dataobj : gallaryBean.getDataObjs()) {
                parseContentUrlData(dataobj);
            }
        }
    }

    /**
     * 内容下的url获取的数据集合
     *
     * @param dataobj
     */
    private void parseContentUrlData(DataObjsBean dataobj) {

        taskStore.addTaskOnList(dataobj.getUrl());

        if (dataobj.getUrls() != null && !dataobj.getUrls().equals("")) {
            //切割字符串
            parseContentsUrisContent(dataobj.getUrls());
        }
    }

    /**
     * 多个url
     *
     * @param urls
     */
    private void parseContentsUrisContent(String urls) {
        try {
            String[] urlarr = urls.split(",");
            for (int i = 0; i < urlarr.length; i++) {
                taskStore.addTaskOnList(urlarr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //解析电子报
    private void parseEpaper(String contentSource) {

    }










    /**发送数据保存广播*/
    private void sendDataSaveTask() {
        if (context != null) {
            bundle.clear();
            intent.setAction(CommandPostBroad.ACTION);
            bundle.putString(CommandPostBroad.PARAM1, CMD_INFO.SORE);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
            Logs.d(TAG,"sendDataSaveTask() success ");
        }
    }

    //通知服务器 可以下载了
    private void nonotifyDownLoad() {
        ICommand_DLIF.get(context).saveTaskList(taskStore.getList());
        ICommand_DLIF.get(context).downloadStartNotifiy();
    }
}
