package lzp.yw.com.medioplayer.model_application.ui.UiFactory;

import java.util.List;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.schedule.LocalScheduleObject;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.layoutView;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.pagesView;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IviewPage;
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

    private static BaseActivity activity = null;

    private static int homeKey = -1;

    //是否初始化
    public static boolean isInit() {
        return isInit;
    }

    public static void init(BaseActivity activity){
        UiDataFilter.activity = activity;
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

        layoutView layout = new layoutView(activity,program);
        layout.startWork();
        homeKey =-1;
        //循环 创建所有页面
        repeatPageStore(program.getLayout().getPages(),layout);

        if (homeKey!=-1){
            if(ViewStore.getInstant().getPage(homeKey)!=null){
                ViewStore.getInstant().getPage(homeKey).startWork();
            }
        }
    }

    //循环遍历所有页面 存储
    private static void repeatPageStore(List<PagesBean> pages,layoutView layout) {
        ViewStore.getInstant().initPagesStore(); // 初始化

        int key = -1;
        IviewPage pageView = null;
        for (PagesBean page : pages){

            key = page.getId();
            pageView = ViewStore.getInstant().getPageCache(key);
            if (pageView==null){
                // 无缓存 - 创建
                pageView = new pagesView(activity,layout,page);
            }
            if (pageView.isHome()){
                homeKey = pageView.getmId();
            }
            ViewStore.getInstant().addPage(key,pageView); //添加页面

            if (page.getPages()!=null && page.getPages().size()>0){
                repeatPageStore(page.getPages(),layout);
            }
            //创建 页面的 内容组件 -> 告诉这个page
            pageView = null;
        }
    }


}
