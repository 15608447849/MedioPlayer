package lzp.yw.com.medioplayer.baselayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import lzp.yw.com.medioplayer.R;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends Activity {
    private static final String TAG = "_BaseActivity";
    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    protected CompositeSubscription mCompositeSubscription;

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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFullScreen(true);
        setContentView(R.layout.activity_base);
        mCompositeSubscription = new CompositeSubscription();
        addActivityOnArr(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopActivityOnArr(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //一旦调用了 CompositeSubscription.unsubscribe()，这个CompositeSubscription对象就不可用了,
        // 如果还想使用CompositeSubscription，就必须在创建一个新的对象了。
        /*
         Subscription subscription = wrapper.getSmsCode2("15813351726")
        .subscribe(newSubscriber(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i(TAG, "call " + s);
            }
        }));
        mCompositeSubscription.add(subscription);
         */
        mCompositeSubscription.unsubscribe();
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

    private DialogLoading loading;
    protected void showLoadingDialog() {
        if (loading == null) {
            loading = new DialogLoading(this);
        }
        loading.show();
    }

    protected void hideLoadingDialog() {
        if (loading != null) {
            loading.dismiss();
        }

    }

    /**
     * 创建观察者,订阅者
     *
     * @param onNext
     * @param <T>
     * @return
     */
    protected <T> Subscriber newSubscriber(final Action1<? super T> onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                Logs.d("onCompleted()");
              //  hideLoadingDialog();

            }
            @Override
            public void onError(Throwable e) {
                Logs.e(TAG, String.valueOf(e.getMessage()));
                hideLoadingDialog();
            }
            @Override
            public void onNext(T t) {
                hideLoadingDialog();
                if (!mCompositeSubscription.isUnsubscribed()) {
                    Logs.d("onNext()");
                    onNext.call(t);
                }
            }

        };
    }



    // 返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 如果是手机上的返回键
           Logs.e(TAG," 点击了 back Key ");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }






}
