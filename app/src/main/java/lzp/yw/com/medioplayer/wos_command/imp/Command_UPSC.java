package lzp.yw.com.medioplayer.wos_command.imp;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.baselayer.BaseApplication;
import lzp.yw.com.medioplayer.baselayer.CONTENT_TYPE;
import lzp.yw.com.medioplayer.baselayer.Logs;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.AdBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.ContentsBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.LayoutBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.PagesBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.ProgramBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.cmd_upsc.ScheduleBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.resultEntitys.content_gallary.GallaryBean;
import lzp.yw.com.medioplayer.rxjave_retrofit.serverProxy.HttpProxy;
import lzp.yw.com.medioplayer.wos_command.iCommand;
import lzp.yw.com.medioplayer.wosappserver.LoaderServer;
import rx.Subscriber;

/**
 * Created by user on 2016/10/27.
 * lzp
 * 读取排期
 *"id": "1", "code": "image", "label": "图片",
 *"id": "2","code": "text","label": "文本控件",
 *"id": "3","code": "video","label": "视频",
 *"id": "4","code": "media""label": "流媒体",
 *"id": "5","code": "clock","label": "时钟控件",
 *"id": "6","code": "weather","label": "天气控件",
 *"id": "7","code": "epaper","label": "电子报",
 *"id": "8","code": "image","label": "商品信息",
 *"id": "11","code": "image","label": "二维码",
 *"id": "12","code": "marquee","label": "跑马灯",
 *"id": "13","code": "gallary", "label": "图集",
 *"id": "14", "code": "news", "label": "资讯",
 */
public class Command_UPSC implements iCommand {

    private static final String TAG = "_UPSC";

    private ReentrantLock lock = new ReentrantLock();

    private List<String> loadingList = null;
    private void initLoadingList(){
        if (loadingList==null){
            loadingList = new ArrayList<String>();
        }else{
            loadingList.clear();
            Logs.d(TAG,"任务清空,当前数量:" + loadingList.size());
        }
        initContentArray();
    }
    /**
     * 添加任务
     * @param url
     */
    private void addTaskOnList(String url){
        if (loadingList==null){
            return;
        }
        if (url==null || url.equals("") || url.equals("null")){
            Logs.e(TAG," add task is failt url = " + url);
            return;
        }
        if(!loadingList.contains(url)){
            loadingList.add(url);
            //sendTask(url);
            Logs.w(TAG," add task is succsee !");
        }else{
            Logs.e(TAG," add task failt ,because is exist !!!");
        }
    }

    /**
     * 发送任务到 下载服务广播
     * @param url
     */
    private void sendTask(String url) {
        if (BaseApplication.appContext!=null){
            Intent intent = new Intent();
            intent.setAction(LoaderServer.LoaderServerReceiveNotification.ACTION);
            intent.putExtra(LoaderServer.LoaderServerReceiveNotification.key,url);
            BaseApplication.appContext.sendBroadcast(intent);
        }
    }

    /**
     *  组件具体内容 具体分析 队列
     *
     */
    private ArrayList<String []> contentArray = null;
    //初始化
    private void initContentArray(){
      if (contentArray == null){
          contentArray = new ArrayList<String []>();
      }else{
          contentArray.clear();
          Logs.i(TAG,"contentArray 清空");
      }
    }
    //添加
    public void addContentOnArr(String [] contentArr){
        if (contentArr==null || contentArray == null){
            return;
        }
        if (contentArr.length!=2){
            return;
        }
        if (!contentArray.contains(contentArr)){
            contentArray.add(contentArr);
        }
    }


    @Override
    public void Execute(String param) {

        try{
            Logs.d(TAG," uri >> "  + param);
              String id = getId(param,2);

            HttpProxy.getInstance().getSchedule(new Subscriber<List<ScheduleBean>>() {
                @Override
                public void onCompleted() {
                    Logs.d(TAG,"----  get Schedule is ok  ----");
                }

                @Override
                public void onError(Throwable e) {
                    Logs.d(TAG," " +e.getMessage());
                }

                @Override
                public void onNext(List<ScheduleBean> scheduleBeen) {
                                initLoadingList();
                                parseScheduleList(scheduleBeen);
                                loopContentArray();

                }
            },id);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 获取 排期id
     * @param param
     * @param count
     * @return
     */
    private String getId(String param,int count){

        param= param.substring(0,param.trim().lastIndexOf("/"));
        param = param.substring(param.trim().lastIndexOf("/")+1);
        return param;
    }

    /**
     * 解析 排期列表
     * @param scheduleBeen
     */
    private void parseScheduleList(List<ScheduleBean> scheduleBeen) {
        Logs.d(TAG,"下发的排期总数: "+scheduleBeen.size());
        for (ScheduleBean schedule:scheduleBeen){
            parseSchedule(schedule);
            Logs.e(TAG,"解析完一个排期\n\r-----------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     * 解析单个排期
     * @param schedule
     */
    private void parseSchedule(ScheduleBean schedule) {
        Logs.d(TAG,"  排期 - " + schedule.getId() + " - type :"+schedule.getType() +" - allday:"+schedule.getAllDay());
        Logs.d(TAG,"  开始时间:"+schedule.getStartTime() +" - 结束时间:"+schedule.getEndTime());
        parseProgram(schedule.getProgram());
    }

    /**
     * 解析节目
     * @param program
     */
    private void parseProgram(ProgramBean program) {
        Logs.d(TAG," #节目 id - "+program.getId());
        Logs.d(TAG," #节目 title - "+program.getTitle());
        Logs.d(TAG," #节目 width - "+program.getWidth() + "- height "+program.getHeight());
        Logs.d(TAG," #节目 resolution(分辨率) - "+ program.getResolution());
        Logs.d(TAG," #节目 layoutId - "+ program.getLayoutId());
        parseLayout(program.getLayout());
    }

    /**
     * 解析布局
     * @param layout
     */
    private void parseLayout(LayoutBean layout) {
        Logs.d(TAG,"    ##布局 id - "+ layout.getId());


        if (layout.getAd()!=null && layout.getAd().size()>0){
            Logs.d(TAG,"    ##布局 ad size - "+  layout.getAd().size());
            for (AdBean ad : layout.getAd()){
                parseAd(ad);
                Logs.e(TAG, "    ##布局 解析完一个 广告 \n" );
            }
        }

        if (layout.getPages()!=null && layout.getPages().size()>0){
            Logs.d(TAG,"    ##布局 pages - size =  "+  layout.getPages().size());
            for(PagesBean pages : layout.getPages()){
                parsePages(pages);
                Logs.e(TAG,"    ##布局  解析完一个页面 \n");
            }
        }

    }

    /**
     * 解析广告
     * @param ad
     */
    private void parseAd(AdBean ad) {
        Logs.d(TAG,"        ###广告 id -  "+  ad.getId());
        Logs.d(TAG,"        ###广告 coordX:-  "+  ad.getCoordX() + "- coordY: "+ad.getCoordY());
        Logs.d(TAG,"        ###广告 width : "+  ad.getWidth()+" - Height : "+ad.getHeight());
        Logs.d(TAG,"        ###广告 backgroundColor -  "+  ad.getBackgroundColor());
        Logs.d(TAG,"        ###广告 adEnabled -  "+  ad.isAdEnabled());
        Logs.d(TAG,"        ###广告 waitTime -  "+  ad.getWaitTime() );
       if (ad.getComponents()!=null && ad.getComponents().size()>0){
           Logs.d(TAG,"         ###广告  子组件 数量 -  "+  ad.getComponents().size() );
           for (ComponentsBean component : ad.getComponents()){
                parseComponet(component);
                Logs.e(TAG,"         ###广告  解析完毕一个 广告下面 的组件 \n");
           }
       }

    }

    /**
     *解析 页面
     * @param page
     */
    private void parsePages(PagesBean page) {
        Logs.d(TAG,"        ### 页面 - id :"+page.getId());
        Logs.d(TAG,"        ### 页面 - home :"+page.isHome());
        Logs.d(TAG,"        ### 页面 - order :"+page.getOrder());
        Logs.d(TAG,"        ### 页面 - tweenEnabled :"+page.isTweenEnabled());
        Logs.d(TAG,"        ### 页面 - label :"+page.getLabel());
        Logs.d(TAG,"        ### 页面 - coordX :"+page.getCoordX() +" coordY :"+page.getCoordY());
        Logs.d(TAG,"        ### 页面 - width :"+page.getWidth() +" height :"+page.getHeight());
        Logs.d(TAG,"        ### 页面 - background :"+ page.getBackground());
        addTaskOnList(page.getBackground());
        Logs.d(TAG,"        ### 页面 - backgroundColor :"+page.getBackgroundColor());

        if (page.getComponents()!=null && page.getComponents().size()>0){
            Logs.d(TAG,"        ### 页面 components List size:" + page.getComponents().size());
            for(ComponentsBean components:page.getComponents()){
                parseComponet(components);
                Logs.e(TAG,"         ### 页面  解析完毕一个页面下面的 组件 \n");
            }
        }
        
        if (page.getPages()!=null && page.getPages().size() > 0){
            Logs.d(TAG,"        ### 页面下 子页面 page list size:" + page.getPages().size());
            for(PagesBean subpage:page.getPages()){
                parsePages(subpage);
                Logs.e(TAG,"         ### 页面  解析完毕一个页面 下面的 子页面 \n");
            }
            
        }
    }

    /**
     * 解析组件
     * @param component
     */
    private void parseComponet(ComponentsBean component) {
        Logs.d(TAG,"            组件 id " + component.getId());
        Logs.d(TAG,"            组件 coordX " + component.getCoordX()+" - coordY "+component.getCoordY());
        Logs.d(TAG,"            组件 width " + component.getWidth()+"- height "+ component.getHeight() );
        Logs.d(TAG,"            组件 componentTypeId " + component.getComponentTypeId());
        Logs.d(TAG,"            组件 componentTypeCode " + component.getComponentTypeCode());
        Logs.d(TAG,"            组件 interactEnabled 是否交互 " + component.isInteractEnabled());
        Logs.d(TAG,"            组件 order订单 " + component.getOrder());
        Logs.d(TAG,"            组件 backgroundAlpha 透明度 " + component.getBackgroundAlpha());
        Logs.d(TAG,"            组件 backgroundColor " + component.getBackgroundColor());
        Logs.d(TAG,"            组件 backgroundPic背景图片uri " + component.getBackgroundPic());
        addTaskOnList(component.getBackgroundPic());
        Logs.d(TAG,"            组件 backgroundPicName 背景图片名字 " + component.getBackgroundPicName());
        Logs.d(TAG,"            组件 titleShowType " + component.getTitleShowType());
        Logs.d(TAG,"            组件 hasContent " + component.isHasContent());
        Logs.d(TAG,"            组件  linkId "+component.getLinkId());
        if (component.getContents()!=null && component.getContents().size()>0){
            for (ContentsBean content : component.getContents()){
                parseContent(content);
                Logs.d(TAG,"            组件 hasContent  解析完毕一个组件下面的内容 \n");
            }

        }
    }

    /**
     * 解析内容
     * @param content
     *
     */
    private void parseContent(ContentsBean content) {
        Logs.d(TAG,"                * 内容 id ^ "+content.getId());
        Logs.d(TAG,"                * 内容 componentId ^ "+content.getComponentId());
        Logs.d(TAG,"                * 内容 contentType ^ "+content.getContentType());
        Logs.d(TAG,"                * 内容 materialType 材料类型 ^ "+content.getMaterialType());
        Logs.d(TAG,"                * 内容 check State ^ "+content.getCheckState());
        Logs.d(TAG,"                * 内容 checked ^ "+content.isChecked());
        Logs.d(TAG,"                * 内容 contentSource ^ "+content.getContentSource());
        Logs.d(TAG,"                * 内容 categoryId 类别id  ^ "+content.getCategoryId());
        Logs.d(TAG,"                * 内容 subcategoryId 子类别id  ^ "+content.getSubcategoryId());
        Logs.d(TAG,"                * 内容 updateFreq 更新频率  ^ "+content.getUpdateFreq());
//        parseContentSourece(content.getContentType(),content.getContentSource());

        if (content.getContentType()!=null && !content.getContentType().equals("") && content.getContentSource()!=null && !content.getContentSource().equals("")){
            addContentOnArr(new String[]{content.getContentType(),content.getContentSource()});
        }

    }
/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    private Object obj = new Object();
    private void  loopContentArray(){

        if (contentArray!=null && contentArray.size()>0){
                Logs.d(TAG,"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                indexFlags = 0;

                for (String[] arr : contentArray){
                        try {
                            parseContentSourece(arr[0],arr[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

        }
//        Logs.d(TAG,"----------------------------------任务队列大小 : "+loadingList.size()+"\n"+loadingList);
    }

    /**
     * 解析具体内容 来源
     * *"id": "1", "code": "image", "label": "图片",
     *"id": "2","code": "text","label": "文本控件",
     *"id": "3","code": "video","label": "视频",
     *"id": "4","code": "media""label": "流媒体",
     *"id": "5","code": "clock","label": "时钟控件",
     *"id": "6","code": "weather","label": "天气控件",
     *"id": "7","code": "epaper","label": "电子报",
     *"id": "8","code": "image","label": "商品信息",
     *"id": "11","code": "image","label": "二维码",
     *"id": "12","code": "marquee","label": "跑马灯",
     *"id": "13","code": "gallary", "label": "图集",
     *"id": "14", "code": "news", "label": "资讯",
     */
    private void parseContentSourece(String contentType, String contentSource) {


        if (contentType.equals(CONTENT_TYPE.gallary)){
            //图集
            getGallarySource(contentSource);
        }
        if (contentType.equals(CONTENT_TYPE.news)){
            getNewsSource(contentSource);
        }
        if (contentType.equals(CONTENT_TYPE.image)){
            addTaskOnList(contentSource);
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.video)){
            addTaskOnList(contentSource);
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.epaper)){
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.clock)){
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.text)){
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.weather)){
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.media)){
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.marquee)){
            collectTaskComplete();
        }
        if (contentType.equals(CONTENT_TYPE.html)){
            collectTaskComplete();
        }
    }




    /**
     * 判断是否时base64 加密
     * 去除baseUri
     *
     */
    private String justUriIsBase64GetUrl(String url){


        if (url.trim().lastIndexOf("=Base64")!=-1){
            url=url.trim().substring(0,url.lastIndexOf("=Base64"));
            Logs.e(TAG,"  url >>> "+url);
        }
        return url.trim().substring(HttpProxy.getInstance().getBaseUri().length());
    };
    /**
     * 获取图集资源
     * @param contentSource
     */
    private void getGallarySource(String contentSource) {

        try {
            contentSource = justUriIsBase64GetUrl(contentSource);

            HttpProxy.getInstance().getGrallarys(new Subscriber<GallaryBean>() {
                @Override
                public void onCompleted() {
                    Logs.e(TAG,"gallary or news -- ok -- ");
                    collectTaskComplete();
                }
                @Override
                public void onError(Throwable e) {
                    Logs.e(TAG,"gallary or news -- err -- " + e.getMessage());
                }
                @Override
                public void onNext(GallaryBean gallaryBean) {
                    parseContentGallarys(gallaryBean);
                }
            },contentSource);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 解析内容下面的图集
     * @param gallaryBean
     */
    private void parseContentGallarys(GallaryBean gallaryBean) {
        Logs.i(TAG,"content _ grallry > totalItems " +gallaryBean.getTotalItems() );
        Logs.i(TAG,"content _ grallry > desc " +gallaryBean.getDesc() );
        Logs.i(TAG,"content _ grallry > pageIndex " +gallaryBean.getPageIndex() );
        Logs.i(TAG,"content _ grallry > pageSize " +gallaryBean.getPageSize() );
        Logs.i(TAG,"content _ grallry > totalPageCount " +gallaryBean.getTotalPageCount() );
        if (gallaryBean.getDataObjs()!=null && gallaryBean.getDataObjs().size()>0){
            for (DataObjsBean dataobj : gallaryBean.getDataObjs()){
                parseGallarysData(dataobj);
            }
        }
    }

    /**
     * 图集下的数据集合
     * @param dataobj
     */
    private void parseGallarysData(DataObjsBean dataobj) {
        Logs.i(TAG,"content _ grallry _data > id " +dataobj.getId());
        Logs.i(TAG,"content _ grallry _data > cretime " +dataobj.getCretime());
        Logs.i(TAG,"content _ grallry _data > updtime " +dataobj.getUpdtime());
        Logs.i(TAG,"content _ grallry _data > creatorUserId " +dataobj.getCreatorUserId());
        Logs.i(TAG,"content _ grallry _data > title " +dataobj.getTitle());
        Logs.i(TAG,"content _ grallry _data > isShowTitle " +dataobj.getIsShowTitle());
        Logs.i(TAG,"content _ grallry _data > usedStatus " +dataobj.getUsedStatus());
        Logs.i(TAG,"content _ grallry _data > format " +dataobj.getFormat());
        Logs.i(TAG,"content _ grallry _data > url " +dataobj.getUrl());
        addTaskOnList(dataobj.getUrl());
        Logs.i(TAG,"content _ grallry _data > media " +dataobj.getMedia());
        Logs.i(TAG,"content _ grallry _data > newName " +dataobj.getNewName());
        Logs.i(TAG,"content _ grallry _data > fileName " +dataobj.getFileName());
        Logs.i(TAG,"content _ grallry _data > createdBy " +dataobj.getCreatedBy());
        Logs.i(TAG,"content _ grallry _data > upDate " +dataobj.getUpDate());
        Logs.i(TAG,"content _ grallry _data > selectType " +dataobj.getSelectType());
        Logs.i(TAG,"content _ grallry _data > typeId " +dataobj.getTypeId());
        Logs.i(TAG,"content _ grallry _data > updtimeStr " +dataobj.getUpdtimeStr());
        if (dataobj.getUrls()!=null && !dataobj.getUrls().equals("")){
            Logs.i(TAG,"content _ grallry _data > urls " + dataobj.getUpdtimeStr());
                //切割字符串
                parseGallaryNewsUris(dataobj.getUrls());
        }
    }

    /**
     *  多个url
     * @param urls
     */
    private void parseGallaryNewsUris(String urls) {
        try {
            String [] urlarr = urls.split(",");
            for(int i=0;i<urlarr.length;i++){
                addTaskOnList(urlarr[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // news
    private void getNewsSource(String contentSource) {
          getGallarySource(contentSource);

    }

    private int indexFlags = 0;
    /**
     * 收集任务完成
     */
    private void collectTaskComplete() {

        indexFlags++;
        Logs.e(TAG,"indexflags = "+indexFlags +" -- "+contentArray.size());
        if (indexFlags == contentArray.size()){
            Logs.d(TAG,"----------------------------------任务队列大小 : "+loadingList.size()+"\n"+loadingList);
        }
    }

}
