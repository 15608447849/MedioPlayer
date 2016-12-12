package com.wos.play.rootdir.model_application.baselayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_communication.CommuniReceiverMsgBroadCasd;
import com.wos.play.rootdir.model_monitor.kernes.WatchServer;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;

/**
 *  bind communication server
 */
public class BaseActivity extends Activity {
    private static final String TAG = "_BaseActivity";
    public static ArrayList<Activity> atyArr = new ArrayList<Activity>();

    //添加 create 添加
   public void addActivityOnArr(Activity a){

       //是否存在, 存在删除 ;添加到队列末尾
       removeActivityOnArr(a);
       atyArr.add(a);
       Logs.i("添加 activity [ "+ a.toString() +" ]");
   }
    //删除 destory調用
    public boolean removeActivityOnArr(Activity a){
        boolean f = false;
        if (atyArr.contains(a)){
            atyArr.remove(a);
            f = true;
            Logs.i("刪除 activity [ "+ a.toString() +" ]");
        }
        return f;
    }
    //finish 一個activity stop調用
    public void stopActivityOnArr(Activity a){

        if (atyArr.contains(a)){
            atyArr.get(atyArr.indexOf(a)).finish();
            Logs.i("停用 activity [ "+ a.toString() +" ]");
        }
    }
/*----------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFullScreen(true);
        setContentView(R.layout.activity_base);
        addActivityOnArr(this);
        initAllServer("all");
    }

    /**
     * 初始化全部的服务
     */
    public void initAllServer(String serverName){
        ((BaseApplication)getApplication()).initStartServer(serverName);
    }

    /**
     * 关闭服务
     */
    public void closeAllServer(String serverName){
        ((BaseApplication)getApplication()).closeServer(serverName);
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopActivityOnArr(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivityOnArr(this);
    }

    private void sendBroadToWatchServer() {
        this.startService(new Intent(this, WatchServer.class));
    }


    /**
     * true为设置全屏
     * false非全屏
     * */
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

    // 返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 如果是手机上的返回键
           Logs.e(TAG,"--- 点击了 back Key ---");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected AppMessageBroad appReceive;
    /**
     * 停止广播 destory call
     */
    protected void unregistBroad() {
        if (appReceive != null) {
            try {
                getApplicationContext().unregisterReceiver(appReceive);
            } catch (Exception e) {
                e.printStackTrace();
            }
            appReceive = null;
            Logs.i(TAG, "注销 activity 消息 广播");
        }

    }

    /**
     *
     * @param type 1 msg  2schedule
     */
    protected void registBroad(int type){
        unregistBroad();
        if (type==1) {
            appReceive = new AppMessageBroad(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(AppMessageBroad.ACTION);
            getApplicationContext().registerReceiver(appReceive, filter); //只需要注册一次
            Logs.i(TAG, "已注册 activity 消息 广播");
        }
    }
    private Intent intent = null;
    private Bundle bundle = null;
    protected void sendMsgCommServer(String methodsName,String methodsParam){
        if (intent == null) {
            intent = new Intent();
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.clear();
        intent.setAction(CommuniReceiverMsgBroadCasd.ACTION);
        bundle.putString(CommuniReceiverMsgBroadCasd.PARAM1, methodsName);
        bundle.putString(CommuniReceiverMsgBroadCasd.PARAM2, methodsParam);
        intent.putExtras(bundle);
        this.sendBroadcast(intent);
    }
    protected void receiveService(final String result) {

    }


    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * 与 UI 元素 通讯  ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
     *
     *
     *
     */

    //返回底层当前 layout
    public ViewGroup getActivityLayout(){
        return null;
    }

        //初始化 UI
        protected void initUI(){
            Logs.i(TAG,"--------------初始化 UI 元素--------------");
            UiTools.init(this);
        }

        protected void unInitUI(){
            UiTools.uninit();
            Logs.i(TAG,"-----------------注销 UI 元素-----------------");
        }


    private FragmentTransaction fragmentTransaction;
    /**
     * 返回 fragment 事务
     */
    private FragmentTransaction getFragmentTransaction(){
        return this.getFragmentManager().beginTransaction();
    }

    /**
     * 替换 一个 view -> fragment
     */
    public void repleaceViewToFragment(ViewGroup view, Fragment fragment){
        Logs.i(TAG," repleace View To Fragment - \n view id: "+view.getId() +" \n fragment:"+fragment);
        fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.replace(view.getId(), fragment);
        fragmentTransaction.commit();
    }

    //删除一个fragments
    public void deleteFragments(Fragment fragment){
        Logs.i(TAG,"- delete Fragment - "+ fragment);
        fragmentTransaction = getFragmentTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();

    }

    //返回全局context
    public Context getAppContext(){
        return this.getApplicationContext();
    }









}
