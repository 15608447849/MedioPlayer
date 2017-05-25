package com.wos.play.rootdir.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wos.play.rootdir.model_application.baselayer.SystemInfos;
import com.wos.play.rootdir.model_application.schedule.ScheduleReader;
import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_command_.kernel.CommandPostBroad;
import com.wos.play.rootdir.model_command_.kernel.iCommand;
import com.wos.play.rootdir.model_download.entity.TaskFactory;
import com.wos.play.rootdir.model_download.entity.UrlList;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.AdBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.LayoutBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallery.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallery.GalleryBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.OtWeatherBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_weather.WeathersBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.CMD_INFO;
import com.wos.play.rootdir.model_universal.tool.CONTENT_TYPE;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.MD5Util;
import com.wos.play.rootdir.model_universal.tool.SdCardTools;
import com.wos.play.rootdir.model_universal.tool.UnImpl;

import java.io.File;
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
    private String fileDirPath;
    public Command_UPSC(Context context) {
        this.context = context;
        bundle = new Bundle();
        intent = new Intent();
        basePath = SystemInfos.get().getBasepath();
        storageLimits = SystemInfos.get().getStorageLimits();
        fileDirPath = SystemInfos.get().getJsonStore();
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
        Logs.d(TAG, "Command_UPSC - uri [ " + param +" ]");
        try {
            lock.lock();
            res = null;
            res = uriTransionString(param, null, null);
            if (res != null && !res.equals("") && !res.equals("[]")) {
                if(check(res)) { Logs.i(TAG, "排期数据相同");return; }
                //保存json数据
                ICommand_SORE_JsonDataStore.getInstent(context).clearJsonMap();
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity("main", param, false); //main 保存主文件的 文件名
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity(param, res, true);//保存主文件内容
                //解析json
                List<ScheduleBean> list = AppsTools.parseJonToList(res, ScheduleBean[].class);
                if (list.size()>0) {
                    mExecuteMother(list);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private boolean check(String res) {
        String entrance = SdCardTools.readerJsonToMemory(fileDirPath + "main");
        if(entrance==null) return false;
        entrance = MD5Util.getStringMD5(entrance);
        String[] filenames = new File(fileDirPath).list();
        for (String filename : filenames) {
            if (filename.equals(entrance)) { //找到了 ->变成对
                String old = SdCardTools.readerJsonToMemory(fileDirPath, filename);
                Logs.i(TAG,res.equals(old)+"=="+res.equalsIgnoreCase(old));
                return res.equalsIgnoreCase(old);
            }
        }
        return false;
    }

    //执行处
    private void mExecuteMother(List<ScheduleBean> list) {
        Long startTime = System.currentTimeMillis();
        taskStore.initLoadingList();
        parseScheduleList(list);//解析入口
        clearSdcardSource();
        sendDataSaveTask();
        notifyDownLoad();
        Long endTime = System.currentTimeMillis();
        Logs.i(TAG, "解析排期 总用时 : " + (endTime - startTime) + " 毫秒 ");
    }


    /**
     * 解析 排期列表
     *
     * @param scheduleList
     */
    private void parseScheduleList(List<ScheduleBean> scheduleList) {
        Logs.d(TAG, "排期总数 : " + scheduleList.size());
        for (ScheduleBean schedule : scheduleList) {
            parseSchedule(schedule);
        }
    }

    /**
     * 解析单个排期
     *
     * @param schedule
     */
    private void parseSchedule(ScheduleBean schedule) {
        Logs.d(TAG, "开始解析 排期 - [ "+ schedule.getId()+" ] - 类型 - "+ ScheduleReader.getScheduleType(schedule.getType()));
       if (UnImpl.func_CB(schedule.getType())){
           return;
       }
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
     * 解析布局 ->
     * 1 广告列表
     * 2 页面内标
     *
     * @param layout
     */
    private void parseLayout(LayoutBean layout) {
        parseLayout_ad(layout);
        parseLayout_page(layout);
    }

    //广告入口
    private void parseLayout_ad(LayoutBean layout) {
        if (layout.getAd() != null && layout.getAd().size() > 0) {
            for (AdBean ad : layout.getAd()) {
                parseAd(ad);
            }
        }
    }
    //页面入口
    private void parseLayout_page(LayoutBean layout) {
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
        if (ad.getComponents() != null && ad.getComponents().size() > 0) {
            for (ComponentsBean component : ad.getComponents()) {
                parseComponent(component);
            }
        }

    }

    /**
     * 解析 页面
     *
     * @param page
     */
    private void parsePages(PagesBean page) {
        Logs.d(TAG,"解析页面 - << "+page.getId() +" - "+ page.getLabel()+" 主页 - +"+page.isHome()+">>");
        taskStore.addTaskOnList(page.getBackground()); //添加页面背景图片
        if (page.getComponents() != null && page.getComponents().size() > 0) {
            for (ComponentsBean components : page.getComponents()) {
                parseComponent(components);//解析内容
            }
        }

        if (page.getPages() != null && page.getPages().size() > 0) {
            for (PagesBean subPage : page.getPages()) {
                parsePages(subPage);//页面下面的页面
            }
        }
    }

    /**
     * 解析组件
     *
     * @param component
     */
    private void parseComponent(ComponentsBean component) {
        taskStore.addTaskOnList(component.getBackgroundPic());//添加内容背景图
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
        parseContentSource(content.getContentType(), content);
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
    private void parseContentSource(String contentType, ContentsBean content) {

        Logs.d(TAG, " 解析具体内容 - 类型 - >>>>  [ " + contentType +" ]" );
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
            getUrlSource(content.getContentSource(),CONTENT_TYPE.news);
        }
        //电子报
        if (contentType.equals(CONTENT_TYPE.epaper)) {
            parseEpaper(content);
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
            parseWebSource(content);
        }
    }
    //解析 网页
    private void parseWebSource(ContentsBean content) {
        if (content.getHtmlType().equals("local")){
            //下载zip包
            taskStore.addTaskOnList(content.getContentSource());
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
        if (type.equals(CONTENT_TYPE.gallary) || type.equals(CONTENT_TYPE.news)) {
            GalleryBean galleryBean = AppsTools.parseJsonWithGson(res, GalleryBean.class);
            if (galleryBean != null) {
                parseContentGallery(galleryBean);
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
        res = AppsTools.uriTransionString(baiduApiUrl,null, null);
        if (res != null) {
            res = AppsTools.getJsonStringFromGZIP(res);
            OtWeatherBean obj = AppsTools.parseJsonWithGson(res, OtWeatherBean.class);
            if (obj != null && obj.getStatus() == 1000 && obj.getDesc().equals("OK")) {
                ICommand_SORE_JsonDataStore.getInstent(context).addEntity(baiduApiUrl, res, true);//文件名,文件内容 - 保存数据
            }
        }
    }


    /**
     * 解析内容下面的图集
     */
    private void parseContentGallery(GalleryBean galleryBean) {
        if (galleryBean.getDataObjs() != null && galleryBean.getDataObjs().size() > 0) {
            for (DataObjsBean infoBean : galleryBean.getDataObjs()) {
                parseContentUrlData(infoBean);
            }
        }
    }

    /**
     * 内容下的url获取的数据集合
     *
     * @param dataObj
     */
    private void parseContentUrlData(DataObjsBean dataObj) {
        taskStore.addTaskOnList(dataObj.getUrl());
        // 添加缩略图下载
        if(dataObj.getImageUrl()!=null && !dataObj.getImageUrl().equals("")){
            taskStore.addTaskOnList(dataObj.getImageUrl());
        }
        if (dataObj.getUrls() != null && !dataObj.getUrls().equals("")) {
            //切割字符串
            parseContentsUrisContent(dataObj.getUrls());
        }
    }

    /**
     * 多个url
     *
     * @param urls
     */
    private void parseContentsUrisContent(String urls) {
        try {
            String[] urlArr = urls.split(",");
            for (int i = 0; i < urlArr.length; i++) {
                taskStore.addTaskOnList(urlArr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*解析电子报 - 规则 从今天开始 - 获取 向前的 keepDays 数量
    *
    *
    * */
    private void parseEpaper(ContentsBean contentSource) {

        //获取 1 - 内容的路径
        // 内容的保持天数
        //拼接下载内容 - 日期.zip
        //文件存放路径

        String contentPath = contentSource.getContentSource();
        String localPath = SystemInfos.get().getEpaperSourcePath() + contentPath;
        int keepDays = contentSource.getDaysKeep();
        Logs.d(TAG,"电子报 - 本地路径 ["+localPath+"]");
        for (int i = 0;i<keepDays;i++){
            //根据天数 获取 日期
            taskStore.addTaskOnList(TaskFactory.gnrTask(contentPath,localPath, TimeOperator.getTodayGotoDays(-i)+".zip"));
        }
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
    private void notifyDownLoad() {
        ICommand_DLIF.get(context).saveTaskList(taskStore.getList());
        ICommand_DLIF.get(context).downloadStartNotifiy();
    }
}
