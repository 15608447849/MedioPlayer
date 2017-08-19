package com.wos.play.rootdir.model_application.baselayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.UiFactory.UiManager;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_communication.CommunicationServer;
import com.wos.play.rootdir.model_monitor.kernes.WatchServer;
import com.wos.play.rootdir.model_universal.tool.Logs;
import java.util.ArrayList;

/**
 * bind communication server
 */
public class BaseActivity extends Activity {
    private static final String TAG = "_BaseActivity";
    public static ArrayList<Activity> atyArr = new ArrayList<>();
    private Handler mHandler =  new Handler();
    private int adViewId, waitTime; // 无人值守页面ID以及无人值守触发时间，单位S

    //添加 create 添加
    public void addActivityOnArr(Activity a) {
        //是否存在, 存在删除 ;添加到队列末尾
        removeActivityOnArr(a);
        atyArr.add(a);
        Logs.i("添加 activity [ " + a.toString() + " ]");
    }

    //删除 destroy調用
    public boolean removeActivityOnArr(Activity a) {
        boolean f = false;
        if (atyArr.contains(a)) {
            atyArr.remove(a);
            f = true;
            Logs.i("刪除 activity [ " + a.toString() + " ]");
        }
        return f;
    }

    //finish 一個activity stop調用
    public void stopActivityOnArr(Activity a) {
        if (atyArr.contains(a)) {
            a.finish();
            Logs.i("停用 activity [ " + a.toString() + " ]");
            removeActivityOnArr(a);
        }
    }
/*----------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFullScreen(true);//设置全屏
        setContentView(R.layout.activity_base);
        addActivityOnArr(this);
        initAllServer("all");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAdDutyTiming();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean isStopOnDestroy = true;

    public void setStopOnDestroy(boolean stopOnDestroy) {
        isStopOnDestroy = stopOnDestroy;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isStopOnDestroy){
            stopActivityOnArr(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopActivityOnArr(this);
    }
    /*------------------------------------------------------ 生命周期 ----------------------------------------------------------------------*/

    /**
     * 初始化全部的服务
     */
    public void initAllServer(String serverName) {
        ((BaseApplication) getApplication()).initStartServer(serverName);
        Logs.d(TAG, "=============== 初始化服务完成 ===============");
    }

    /**
     * 关闭服务
     */
    public void closeAllServer(String serverName) {
        ((BaseApplication) getApplication()).closeServer(serverName);
    }

    //发送消息的监控服务
    private void sendBroadToWatchServer() {
        this.startService(new Intent(this, WatchServer.class));
    }

    /**
     * true为设置全屏
     * false非全屏
     */
    public void setFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    protected Toast mToast = null;

    /**
     * 显示一个Toast信息
     *
     * @param content
     */
    public void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(this, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

    private boolean isOnBack = false;

    protected void setIsOnBack(boolean flag) {
        isOnBack = flag;
    }

    // 返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !isOnBack) { // 如果是手机上的返回键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected AppMessageBroad appReceive;

    /**
     * 停止广播 destroy call
     */
    protected void unregisterBroad() {
        if (appReceive != null) {
            try {
                getApplicationContext().unregisterReceiver(appReceive);
            } catch (Exception e) {
                e.printStackTrace();
            }
            appReceive = null;
            Logs.i(TAG, "注销 activity 接受通讯服务消息 广播");
        }

    }

    /**
     * @param type 1 msg  2schedule
     */
    protected void registerBroad(int type) {
        unregisterBroad();
        if (type == 1) {
            appReceive = new AppMessageBroad(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(AppMessageBroad.ACTION);
            getApplicationContext().registerReceiver(appReceive, filter); //只需要注册一次
            Logs.i(TAG, "已注册 activity 接受通讯服务消息 广播");
        }
    }


    /**发送消息 到通讯服务 (如果要接受消息 -请打开 接受消息通知)*/
    protected void sendMsgCommServer(String cmd, String param) {
        try{
            Logs.i(TAG, "--通过通讯服务，发送消息:" + cmd);
            Intent intent = new Intent(getAppContext(), CommunicationServer.class);
            intent.putExtra("cmd", cmd);
            intent.putExtra("param", param);
            getApplication().startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //通讯服务发来的消息 接收处
    protected void receiveService(final String result, final String result1) {

    }

    /**
     * 与 UI 元素 通讯  ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
     */

    //返回底层当前 layout
    public ViewGroup getActivityLayout() {
        return null;
    }

    //初始化 UI
    protected void initUI() {
        Logs.d(TAG, "初始化UI - 开始");
        UiTools.init(this);
        Logs.d(TAG, "初始化UI - 结束");
    }

    protected void unInitUI() {
        Logs.d(TAG, "注销UI - 开始");
        UiTools.uninit();
        Logs.d(TAG, "注销UI - 结束");
    }

    private FragmentTransaction fragmentTransaction;

    /**
     * 返回 fragment 事务
     */
    private FragmentTransaction getFragmentTransaction() {
        return this.getFragmentManager().beginTransaction();
    }

    /**
     * 替换 一个 view -> fragment
     */
    public void replaceViewToFragment(ViewGroup view, Fragment fragment) {
        Logs.i(TAG, "- 替换 View To Fragment -  view id:[ " + view.getId() + " ] - fragment:[ " + fragment + " ]");

        fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.replace(view.getId(), fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    //删除一个fragments
    public void deleteFragments(Fragment fragment) {
        Logs.i(TAG, "- 删除 Fragment - " + fragment);
        fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    //返回全局context
    public Context getAppContext() {
        return this.getApplicationContext();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(MotionEvent.ACTION_UP == ev.getAction()){
            startAdDutyTiming();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开始无人值守计时
     */
    private void startAdDutyTiming() {
        if(adViewId > 0 && waitTime >0){
            mHandler.removeCallbacks(jumpAdDutyPage);
            UiManager.getInstance().exeAdTask(0);
            mHandler.postDelayed(jumpAdDutyPage, waitTime*1000);
        }
    }

    /**
     * 跳转无人值守广告
     */
    private Runnable jumpAdDutyPage = new Runnable() {
        @Override
        public void run() {
            UiManager.getInstance().exeAdTask(adViewId);
        }
    };
    /**
     * 无人值守广告 布局ID
     */
    public void onHasAdDuty(int adViewId, int waitTime) {
        this.adViewId = adViewId;
        this.waitTime = waitTime;
        startAdDutyTiming();
    }
}
