package lzp.yw.com.medioplayer.model_application.ui.UiFactory;

import java.util.List;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.schedule.LocalScheduleObject;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.page.IviewPage;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.page.pagesView;
import lzp.yw.com.medioplayer.model_application.ui.UiStore.ViewStore;
import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;

/**
 * Created by user on 2016/11/10.
 * Ui 数据过滤
 */
public class UiDataFilter {
    private static final String TAG = "_uidataFilete";

    private  static boolean isInit = false;

    public static BaseActivity activity = null;

    private static int homeKey = -1;

    //是否初始化
    public static boolean isInit() {
        return isInit;
    }

    public static void init(BaseActivity activity){
        UiDataFilter.activity = activity;
        UiManager.getInstans().initData();
        isInit = true;
    }

    public static void unInit(){
        UiDataFilter.activity = null;
        isInit = false;
    }

    public static void  filter(LocalScheduleObject current){
        Logs.i(TAG," 转换数据中 ...");
        if (!isInit){
            Logs.e(TAG," 未初始化 activity - 不可执行 ");
            return;
        }
        createLayout(current.getSchedule().getProgram());
    }



    // 创建 布局层
    private static void createLayout(ProgramBean program) {
        UiManager.getInstans().stopTask();
        homeKey =-1;
       // ViewStore.getInstant().pageTanslationCache();//页面 转存
        //循环创建所有页面
        ViewStore.getInstant().initPagesStore(); // 初始化
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
            ViewStore.getInstant().addPage(key,pageView); //添加页面
            if (page.getPages()!=null && page.getPages().size()>0){
                repeatPageStore(page.getPages());
            }
        }
    }
}
