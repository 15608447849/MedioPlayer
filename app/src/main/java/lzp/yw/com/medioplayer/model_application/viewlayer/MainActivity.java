package lzp.yw.com.medioplayer.model_application.viewlayer;

import android.os.Bundle;
import android.view.View;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 初始化数据
     */
    private void initData() {
    }

    public void backTop(View v){
    }

    public void serverStart(View v){
    }

    public void serverStop(View v){
    }

}
