package com.wos.play.rootdir.model_application.ui.UiFactory;

import android.os.Handler;

import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.schedule.LocalScheduleObject;
import com.wos.play.rootdir.model_application.ui.UiElements.page.IviewPage;
import com.wos.play.rootdir.model_application.ui.UiElements.page.pagesView;
import com.wos.play.rootdir.model_application.ui.UiStore.ImageStore;
import com.wos.play.rootdir.model_application.ui.UiStore.PagerStore;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.List;

/**
 * Created by user on 2016/11/10.
 * Ui 数据过滤
 */
public class UiDataFilter {
    private static final String TAG = "_uidataFilete";

    private  static boolean isInit = false;

    public static BaseActivity activity = null;

    private static int homeKey = -1;

    private static Handler handler ;



    //是否初始化
    public static boolean isInit() {
        return isInit;
    }

    public static void init(BaseActivity activity){
        UiDataFilter.activity = activity;
        UiManager.getInstans().initData();
        handler = new Handler();
        isInit = true;
    }

    public static void unInit(){
        UiDataFilter.activity = null;
        handler = null;
        ImageAsyLoad.clear();
        ImageStore.getInstants().clearCache();
        isInit = false;
    }

    public static void  filter(final LocalScheduleObject current){
        Logs.i(TAG," 转换数据中 ...");
        if (!isInit){
            Logs.e(TAG," 未初始化 activity - 不可执行 ");
            return;
        }
//        if (!AppsTools.checkUiThread()){
//            Logs.e(TAG,"不在UI主线程 - 不可执行 ");
//            return;
//        }
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

    }



    // 创建 布局层
    private static void createLayout(ProgramBean program) {
        UiManager.getInstans().stopTask();
        homeKey =-1;
       // ViewStore.getInstant().pageTanslationCache();//页面 转存
        //循环创建所有页面
        PagerStore.getInstant().initPagesStore(); // 初始化
        repeatPageStore(program.getLayout().getPages());
        if (homeKey!=-1){
            UiManager.getInstans().exeMainTask(homeKey);
        }
    }

    //循环遍历所有页面存储
    private static void repeatPageStore(List<PagesBean> pages) {
        int key;
        IviewPage pageView;
        for (PagesBean page : pages){

            key = page.getId();
//            pageView = ViewStore.getInstant().getPageCache(key);
//            if (pageView==null){
//                 无缓存 - 创建
                pageView = new pagesView(activity,page);
//            }
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
