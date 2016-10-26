package lzp.yw.com.medioplayer.baselayer;

import android.app.Application;
import android.content.Context;

/**
 * Created by user on 2016/10/26.
 */
public class BaseApplication extends Application{
    public static Context appContext = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Logs.i("###################### app start ######################");
        appContext = this.getApplicationContext();
    }
}
