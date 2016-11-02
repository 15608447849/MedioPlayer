package lzp.yw.com.medioplayer.model_application.baselayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_communication.CommunicationServer;
import lzp.yw.com.medioplayer.model_communication.ICallBackAIDL;
import lzp.yw.com.medioplayer.model_communication.ICommunicationAIDL;
import lzp.yw.com.medioplayer.model_universal.Logs;

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
    }

    /**
     * 初始化全部的服务
     */
    public void initAllServer(){
        ((BaseApplication)getApplication()).initStartServer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopActivityOnArr(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbindCommuniServer();
        removeActivityOnArr(this);
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

    //-----------------------------------------------------------绑定 通讯服务
    public ServiceConnection serConnImp = null;
    public Intent communiServerIntent = null;

    private ICallBackAIDL.Stub icall = new ICallBackAIDL.Stub() {
        @Override
        public void serverResult(String result) throws RemoteException {
            receiveService(result);
        }
    };
    private ICommunicationAIDL iService;


    /**
     * 绑定服务
     */
    public void mBindCommuniServer(){
        if (iService !=null){
            Logs.e(TAG," 远程通讯服务 和 activity("+ this.toString() +") 已经绑定 ..." );
            return;
        }
        if (serConnImp==null){
            serConnImp = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Logs.d(TAG," mBindCommuniServer() onServiceConnected() 通讯服务对象- Ibinder : "+ service);
                    iService = ICommunicationAIDL.Stub.asInterface(service);
                    try {
                        iService.receiveResult(icall);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    //无法获取服务对象时
                    Logs.e(TAG,"mBindCommuniServer() onServiceDisconnected() 无法获取通讯服务对象");
                    iService = null;
                }
            };
        }

        if (communiServerIntent == null){
            communiServerIntent = new Intent(this, CommunicationServer.class);
        }
        bindService(communiServerIntent, serConnImp, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解除bind
     */
    public void mUnbindCommuniServer(){
        if (serConnImp!=null){
            unbindService(serConnImp);
        }

    }
    /**
     * 发送消息去 服务
     */
    public void sendMsgCommServer(String cmd,String msg){
        if (iService!=null){
            try {
                iService.sendRequest(cmd,msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 收到服务器的消息
     */
    public void receiveService(String result){
        Logs.d(TAG," 服务器返回值: \n" + result);
    }
}
