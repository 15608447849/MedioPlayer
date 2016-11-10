package lzp.yw.com.medioplayer.model_application.ui.UiFactory;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.schedule.LocalScheduleObject;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.layoutView;
import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;

/**
 * Created by user on 2016/11/10.
 * Ui 数据过滤
 */
public class UiDataFilter {
    private static final String TAG = "_uidataFilete";

    private  static boolean isInit = false;

    private static BaseActivity activity = null;
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
    }
}
