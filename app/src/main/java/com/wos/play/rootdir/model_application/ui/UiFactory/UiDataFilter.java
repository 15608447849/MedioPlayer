package com.wos.play.rootdir.model_application.ui.UiFactory;

import android.os.Handler;

import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.schedule.LocalScheduleObject;
import com.wos.play.rootdir.model_application.ui.UiElements.page.IViewPage;

import com.wos.play.rootdir.model_application.ui.UiHttp.UiHttpProxy;
import com.wos.play.rootdir.model_application.ui.UiStore.ImageStore;
import com.wos.play.rootdir.model_application.ui.UiStore.PagerStore;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.AdBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.UnImpl;

import java.util.List;

/**
 * Created by user on 2016/11/10.
 * Ui 数据过滤
 */
public class UiDataFilter {
    private static final String TAG = UiDataFilter.class.getSimpleName();

    private  boolean isInit = false;
    public  BaseActivity activity = null;
    private int homeKey = -1;
    private Handler handler ;
    private static UiDataFilter uiDataFilter;


    private UiDataFilter() {
    }

    public static UiDataFilter getUiDataFilter(){
        if (uiDataFilter==null){
            uiDataFilter = new UiDataFilter();
        }
        return uiDataFilter;
    }

    public void init(BaseActivity activity){
       if (!AppsTools.checkUiThread()){
            Logs.e(TAG,"不在UI主线程 - 不可执行 ");
            return;
        }
        if (!isInit){
            this.activity = activity;
            handler = new Handler();
            UiManager.getInstance().initData();
            UiHttpProxy.getPeoxy().init(activity);
            isInit = true;
            Logs.i(TAG,"初始化 - UI数据过滤类 - 完成");
        }

    }

    public void unInit(){
        if (isInit) {
            Logs.i(TAG, "注销 - UI 数据过滤类");
            UiManager.getInstance().unInitData();
            UiHttpProxy.getPeoxy().unInit();//ui下载关闭();
            ImageStore.getInstants().clearCache();
            ImageAsyLoad.clear();
            this.activity = null;
            handler = null;
            isInit = false;
        }
    }




    public void  filter(final LocalScheduleObject current){
        if (!isInit){
            Logs.e(TAG," 未初始化 activity - 不可执行 ");
            return;
        }
        Logs.i(TAG,"  = = = = = = = = = UI 转换数据中 = = = = = = = = = ");
        if (UnImpl.func_CB(current.getSchedule().getType())){
            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    createLayout(current.getSchedule().getProgram());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Logs.i(TAG,"  = = = = = = = = = UI 数据转换完成 = = = = = = = = = ");
    }



    // 创建 布局层
    private void createLayout(ProgramBean program) {
        UiManager.getInstance().stopTask();
        homeKey =-1;
        //循环创建所有页面
        PagerStore.getInstant().initPagesStore(); // 初始化
        repeatAdStore(program.getLayout().getAd());
        repeatPageStore(program.getLayout().getPages());
        if (homeKey!=-1){
            UiManager.getInstance().exeMainTask(homeKey);
        }
    }

    private void repeatAdStore(List<AdBean> ads) {
        IViewPage pageView;
        for (AdBean ad : ads){
            pageView = new IViewPage(activity, ad);
            if(pageView.isAd() && activity!=null){
                PagerStore.getInstant().addPage(ad.getId(),pageView); //添加页面
                activity.onHasAdDuty(ad.getId(), ad.getWaitTime());
                break;
            }
        }
    }

    //循环遍历所有页面存储
    private void repeatPageStore(List<PagesBean> pages) {
        int key;
        IViewPage pageView;
        for (PagesBean page : pages){
            key = page.getId();//创建
            pageView = new IViewPage(activity,page);
            if (pageView.isHome()){
                homeKey = key;
            }
            PagerStore.getInstant().addPage(key,pageView); //添加页面
            if (page.getPages()!=null && page.getPages().size()>0){
                repeatPageStore(page.getPages());
            }
        }
    }
}
