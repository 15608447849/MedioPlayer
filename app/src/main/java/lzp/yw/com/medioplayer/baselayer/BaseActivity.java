package lzp.yw.com.medioplayer.baselayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFullScreen(true);
        setContentView(R.layout.activity_base);
        mCompositeSubscription = new CompositeSubscription();
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
     * 创建观察者
     *
     * @param onNext
     * @param <T>
     * @return
     */
    protected <T> Subscriber newSubscriber(final Action1<? super T> onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                hideLoadingDialog();
            }
            @Override
            public void onError(Throwable e) {
                Logs.e(TAG, String.valueOf(e.getMessage()));
                hideLoadingDialog();
            }
            @Override
            public void onNext(T t) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.call(t);
                }
            }

        };
    }









}
