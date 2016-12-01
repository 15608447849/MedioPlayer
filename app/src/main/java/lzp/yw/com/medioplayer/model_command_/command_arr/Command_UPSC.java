package lzp.yw.com.medioplayer.model_command_.command_arr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.model_application.baselayer.DataListEntiyStore;
import lzp.yw.com.medioplayer.model_command_.kernel.CommandPostBroad;
import lzp.yw.com.medioplayer.model_download.entity.UrlList;
import lzp.yw.com.medioplayer.model_download.kernel.DownloadBroad;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.AdBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.LayoutBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.Rules;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ScheduleBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_weather.BaiduApiObject;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_weather.WeathersBean;
import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;
import lzp.yw.com.medioplayer.model_universal.tool.CMD_INFO;
import lzp.yw.com.medioplayer.model_universal.tool.CONTENT_TYPE;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.tool.SdCardTools;

import static lzp.yw.com.medioplayer.model_universal.tool.AppsTools.uriTransionString;
import static lzp.yw.com.medioplayer.model_universal.tool.CONTENT_TYPE.weather;

/**
 * Created by user on 2016/10/27.
 * lzp
 * 读取排期
 */
public class Command_UPSC implements iCommand {
    private static final String TAG = "_UPSC";
    private Context context;
    private DataListEntiyStore dl;
    private String basePath = null;
    private String terminalNo = null;
    private String storageLimits = null;
    private Long startTime;
    private Long endTime;
    private Intent intent;
    private Bundle bundle;
    public Command_UPSC(Context context) {
        this.context = context;
        bundle = new Bundle();
        intent = new Intent();
        dl = new DataListEntiyStore(context);
        dl.ReadShareData();
        basePath = dl.GetStringDefualt("basepath", "");
        terminalNo = dl.GetStringDefualt("terminalNo", "");
        storageLimits = dl.GetStringDefualt("storageLimits", "");
        taskStore = new UrlList();
    }

    private static ReentrantLock lock = new ReentrantLock();

    private UrlList taskStore;
    //结果
    private String res;

    /**
     * 入口-------------------------------------------------------------------------------------------------------------------------------------------------
     *
     * @param param
     */
    @Override
    public void Execute(String param) {

        try {
            lock.lock();
            Logs.d(TAG, " - - 同步访问  uri : " + param);
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

                    Logs.i(TAG, "----------------------------------任务队列大小 : " + taskStore.getListSize());
                    if (taskStore.getListSize() > 0) {
                        sendDownLoadTaskList();
                    }

                    startTime = System.currentTimeMillis();
                    clearSdcardSource();
                    endTime = System.currentTimeMillis();
                    Logs.e(TAG, "清理资源 用时 : " + (endTime - startTime) + " 毫秒 ");
                    sendDataSaveTask();
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
        Logs.d(TAG, "下发的排期总数: " + schedulelist.size());
        for (ScheduleBean schedule : schedulelist) {
            parseSchedule(schedule);
            Logs.e(TAG, "解析完一个排期\n\r-----------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     * 解析单个排期
     *
     * @param schedule
     */
    private void parseSchedule(ScheduleBean schedule) {
        Logs.d(TAG, "  排期 - " + schedule.getId() + " - type :" + schedule.getType() + " - allday:" + schedule.getAllDay());
        Logs.d(TAG, "  开始时间:" + schedule.getStartTime() + " - 结束时间:" + schedule.getEndTime());
        if (schedule.getType() == 3) {
            //重复
            if (schedule.getRules() != null) {
                parseRelus(schedule.getRules());
            }

        }
        parseProgram(schedule.getProgram());
    }

    private void parseRelus(Rules rules) {
        parseRelusRepeatRules(rules.getRepeatRules());

    }

    private void parseRelusRepeatRules(Rules.RepeatRulesBean repeatRules) {
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - code :" + repeatRules.getRepeatType().getCode());
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - text :" + repeatRules.getRepeatType().getText());
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - startday :" + repeatRules.getStartday());
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - endday :" + repeatRules.getEndday());
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - repeatWholeDay :" + repeatRules.isRepeatWholeDay());
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - startTime :" + repeatRules.getStartTime());
        Logs.d(TAG, " #重复类型 - repeatRules - repeatType - endTime :" + repeatRules.getEndTime());
    }

    /**
     * 解析重复类型的时间对象
     */


    /**
     * 解析节目
     *
     * @param program
     */
    private void parseProgram(ProgramBean program) {
        Logs.d(TAG, " #节目 id - " + program.getId());
        Logs.d(TAG, " #节目 title - " + program.getTitle());
        Logs.d(TAG, " #节目 width - " + program.getWidth() + "- height " + program.getHeight());
        Logs.d(TAG, " #节目 resolution(分辨率) - " + program.getResolution());
        Logs.d(TAG, " #节目 layoutId - " + program.getLayoutId());
        parseLayout(program.getLayout());
    }

    /**
     * 解析布局
     *
     * @param layout
     */
    private void parseLayout(LayoutBean layout) {
        Logs.d(TAG, "    ##布局 id - " + layout.getId());


        if (layout.getAd() != null && layout.getAd().size() > 0) {
            Logs.d(TAG, "    ##布局 ad size - " + layout.getAd().size());
            for (AdBean ad : layout.getAd()) {
                parseAd(ad);
                Logs.e(TAG, "    ##布局 解析完一个 广告 \n");
            }
        }

        if (layout.getPages() != null && layout.getPages().size() > 0) {
            Logs.d(TAG, "    ##布局 pages - size =  " + layout.getPages().size());
            for (PagesBean pages : layout.getPages()) {
                parsePages(pages);
                Logs.e(TAG, "    ##布局  解析完一个页面 \n");
            }
        }
    }

    /**
     * 解析广告
     *
     * @param ad
     */
    private void parseAd(AdBean ad) {
        Logs.d(TAG, "        ###广告 id -  " + ad.getId());
        Logs.d(TAG, "        ###广告 coordX:-  " + ad.getCoordX() + "- coordY: " + ad.getCoordY());
        Logs.d(TAG, "        ###广告 width : " + ad.getWidth() + " - Height : " + ad.getHeight());
        Logs.d(TAG, "        ###广告 backgroundColor -  " + ad.getBackgroundColor());
        Logs.d(TAG, "        ###广告 adEnabled -  " + ad.isAdEnabled());
        Logs.d(TAG, "        ###广告 waitTime -  " + ad.getWaitTime());
        if (ad.getComponents() != null && ad.getComponents().size() > 0) {
            Logs.d(TAG, "         ###广告  子组件 数量 -  " + ad.getComponents().size());
            for (ComponentsBean component : ad.getComponents()) {
                parseComponet(component);
                Logs.e(TAG, "         ###广告  解析完毕一个 广告下面 的组件 \n");
            }
        }

    }

    /**
     * 解析 页面
     *
     * @param page
     */
    private void parsePages(PagesBean page) {
        Logs.d(TAG, "        ### 页面 - id :" + page.getId());
        Logs.d(TAG, "        ### 页面 - home :" + page.isHome());
        Logs.d(TAG, "        ### 页面 - order :" + page.getOrder());
        Logs.d(TAG, "        ### 页面 - tweenEnabled :" + page.isTweenEnabled());
        Logs.d(TAG, "        ### 页面 - label :" + page.getLabel());
        Logs.d(TAG, "        ### 页面 - coordX :" + page.getCoordX() + " coordY :" + page.getCoordY());
        Logs.d(TAG, "        ### 页面 - width :" + page.getWidth() + " height :" + page.getHeight());
        Logs.d(TAG, "        ### 页面 - background :" + page.getBackground());
        taskStore.addTaskOnList(page.getBackground());
        Logs.d(TAG, "        ### 页面 - backgroundColor :" + page.getBackgroundColor());

        if (page.getComponents() != null && page.getComponents().size() > 0) {
            Logs.d(TAG, "        ### 页面 components List size:" + page.getComponents().size());
            for (ComponentsBean components : page.getComponents()) {
                parseComponet(components);
                Logs.e(TAG, "         ### 页面  解析完毕一个页面下面的 组件 \n");
            }
        }

        if (page.getPages() != null && page.getPages().size() > 0) {
            Logs.d(TAG, "        ### 页面下 子页面 page list size:" + page.getPages().size());
            for (PagesBean subpage : page.getPages()) {
                parsePages(subpage);
                Logs.e(TAG, "         ### 页面  解析完毕一个页面 下面的 子页面 \n");
            }

        }
    }

    /**
     * 解析组件
     *
     * @param component
     */
    private void parseComponet(ComponentsBean component) {
        Logs.d(TAG, "            组件 id " + component.getId());
        Logs.d(TAG, "            组件 coordX " + component.getCoordX() + " - coordY " + component.getCoordY());
        Logs.d(TAG, "            组件 width " + component.getWidth() + "- height " + component.getHeight());
        Logs.d(TAG, "            组件 componentTypeId " + component.getComponentTypeId());
        Logs.d(TAG, "            组件 componentTypeCode " + component.getComponentTypeCode());
        Logs.d(TAG, "            组件 interactEnabled 是否交互 " + component.isInteractEnabled());
        Logs.d(TAG, "            组件 order订单 " + component.getOrder());
        Logs.d(TAG, "            组件 backgroundAlpha 透明度 " + component.getBackgroundAlpha());
        Logs.d(TAG, "            组件 backgroundColor " + component.getBackgroundColor());
        Logs.d(TAG, "            组件 backgroundPic背景图片uri " + component.getBackgroundPic());
        taskStore.addTaskOnList(component.getBackgroundPic());
        Logs.d(TAG, "            组件 backgroundPicName 背景图片名字 " + component.getBackgroundPicName());
        Logs.d(TAG, "            组件 titleShowType " + component.getTitleShowType());
        Logs.d(TAG, "            组件 hasContent " + component.isHasContent());
        Logs.d(TAG, "            组件  linkId " + component.getLinkId());
        if (component.getContents() != null && component.getContents().size() > 0) {
            for (ContentsBean content : component.getContents()) {
                parseContent(content);
                Logs.d(TAG, "            组件 hasContent  解析完毕一个组件下面的内容 \n");
            }

        }
    }

    /**
     * 解析内容
     *
     * @param content Logs.d(TAG,"                * 内容 materialType 材料类型 ^ "+content.getMaterialType());
     *                Logs.d(TAG,"                * 内容 check State ^ "+content.getCheckState());
     *                Logs.d(TAG,"                * 内容 checked ^ "+content.isChecked());
     *                Logs.d(TAG,"                * 内容 categoryId 类别id  ^ "+content.getCategoryId());
     *                Logs.d(TAG,"                * 内容 subcategoryId 子类别id  ^ "+content.getSubcategoryId());
     *                Logs.d(TAG,"                * 内容 updateFreq 更新频率  ^ "+content.getUpdateFreq());
     *                Logs.d(TAG,"                * 内容 id ^ "+content.getId());
     *                Logs.d(TAG,"                * 内容 componentId ^ "+content.getComponentId());
     */
    private void parseContent(ContentsBean content) {
        Logs.d(TAG, "                * 内容 contentType ^ " + content.getContentType());
        Logs.d(TAG, "                * 内容 contentSource ^ " + content.getContentSource());
        parseContentSourece(content.getContentType(), content);
    }
/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------*/


    /**
     * 清理资源
     */
    private void clearSdcardSource() {
        //如果 true  清理!
        if (SdCardTools.justFileBlockVolume(basePath
                , storageLimits)) {

            List<String> keepList = new ArrayList<String>();
            for (CharSequence car : taskStore.getList()) {
                keepList.add(car.toString());
            }

            SdCardTools.clearTargetDir(basePath, keepList);

        }
    }


    /**
     * 解析具体内容 来源
     * *"id": "1", "code": "image", "label": "图片",
     * "id": "2","code": "text","label": "文本控件",
     * "id": "3","code": "video","label": "视频",
     * "id": "4","code": "media""label": "流媒体",
     * "id": "5","code": "clock","label": "时钟控件",
     * "id": "6","code": "weather","label": "天气控件",
     * "id": "7","code": "epaper","label": "电子报",
     * "id": "8","code": "image","label": "商品信息",
     * "id": "11","code": "image","label": "二维码",
     * "id": "12","code": "marquee","label": "跑马灯",
     * "id": "13","code": "gallary", "label": "图集",
     * "id": "14", "code": "news", "label": "资讯",
     */
    private void parseContentSourece(String contentType, ContentsBean content) {

        Logs.d(TAG, ">>>> 解析具体内容 来源 " + contentType);
        //图片
        if (contentType.equals(CONTENT_TYPE.image)) {
            taskStore.addTaskOnList(content.getContentSource());
        }
        //按钮
        if (contentType.equals(CONTENT_TYPE.button)) {
            //默认展示图片
            taskStore.addTaskOnList(content.getSourceUp());
            //点击时展示图片
            taskStore.addTaskOnList(content.getSourceDown());
        }
        //视频
        if (contentType.equals(CONTENT_TYPE.video)) {
            taskStore.addTaskOnList(content.getContentSource());
        }
        //图集
        if (contentType.equals(CONTENT_TYPE.gallary)) {
            getUrlSource(content.getContentSource(), CONTENT_TYPE.gallary);
        }
        //电子报
        if (contentType.equals(CONTENT_TYPE.news)) {
            //   getUrlSource(content.getContentSource());
        }
        if (contentType.equals(CONTENT_TYPE.epaper)) {
        }
        if (contentType.equals(CONTENT_TYPE.clock)) {//swf
            taskStore.addTaskOnList(content.getContentSource());
        }
        if (contentType.equals(CONTENT_TYPE.text)) {
        }
        if (contentType.equals(weather)) {
            getUrlSource(content.getContentSource(), weather);
        }
        if (contentType.equals(CONTENT_TYPE.media)) {
        }
        if (contentType.equals(CONTENT_TYPE.marquee)) {
        }
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
            Logs.i(TAG, " 内容 url :" + contentSource);
            res = null;
            res = AppsTools.uriTransionString(AppsTools.urlEncodeParam(contentSource), null, null);
            if (type.equals(CONTENT_TYPE.gallary) || type.equals(CONTENT_TYPE.news)) {  //咨询 图集
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
     *
     * @param gallaryBean
     */
    private void parseContentGallarys(GallaryBean gallaryBean) {
        Logs.i(TAG, "content _ grallry > totalItems " + gallaryBean.getTotalItems());
        Logs.i(TAG, "content _ grallry > desc " + gallaryBean.getDesc());
        Logs.i(TAG, "content _ grallry > pageIndex " + gallaryBean.getPageIndex());
        Logs.i(TAG, "content _ grallry > pageSize " + gallaryBean.getPageSize());
        Logs.i(TAG, "content _ grallry > totalPageCount " + gallaryBean.getTotalPageCount());
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
        Logs.i(TAG, "content _ URL _data > id " + dataobj.getId());
        Logs.i(TAG, "content _ URL _data > cretime " + dataobj.getCretime());
        Logs.i(TAG, "content _ URL _data > updtime " + dataobj.getUpdtime());
        Logs.i(TAG, "content _ URL _data > creatorUserId " + dataobj.getCreatorUserId());
        Logs.i(TAG, "content _ URL _data > title " + dataobj.getTitle());
        Logs.i(TAG, "content _ URL _data > isShowTitle " + dataobj.getIsShowTitle());
        Logs.i(TAG, "content _ URL _data > usedStatus " + dataobj.getUsedStatus());
        Logs.i(TAG, "content _ URL _data > format " + dataobj.getFormat());
        Logs.i(TAG, "content _ URL _data > url " + dataobj.getUrl());
        taskStore.addTaskOnList(dataobj.getUrl());
        Logs.i(TAG, "content _ URL _data > media " + dataobj.getMedia());
        Logs.i(TAG, "content _ URL _data > newName " + dataobj.getNewName());
        Logs.i(TAG, "content _ URL _data > fileName " + dataobj.getFileName());
        Logs.i(TAG, "content _ URL _data > createdBy " + dataobj.getCreatedBy());
        Logs.i(TAG, "content _ URL _data > upDate " + dataobj.getUpDate());
        Logs.i(TAG, "content _ URL _data > selectType " + dataobj.getSelectType());
        Logs.i(TAG, "content _ URL _data > typeId " + dataobj.getTypeId());
        Logs.i(TAG, "content _ URL _data > updtimeStr " + dataobj.getUpdtimeStr());
        if (dataobj.getUrls() != null && !dataobj.getUrls().equals("")) {
            Logs.i(TAG, "content _ grallry _data > urls " + dataobj.getUpdtimeStr());
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



    /**
     * 发送任务到下载服务广播
     */
    private void sendDownLoadTaskList() {
        if (context != null && dl != null) {
            //发送任务->下载服务
            bundle.clear();
            intent.setAction(DownloadBroad.ACTION);
            bundle.putCharSequenceArrayList(DownloadBroad.PARAM1, taskStore.getList());
            bundle.putString(DownloadBroad.PARAM2, terminalNo);
            bundle.putString(DownloadBroad.PARAM3, basePath);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);

        }
    }

    private void sendDataSaveTask() {
        if (context != null) {
            bundle.clear();
            intent.setAction(CommandPostBroad.ACTION);
            bundle.putString(CommandPostBroad.PARAM1, CMD_INFO.SORE);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }

    }
}
