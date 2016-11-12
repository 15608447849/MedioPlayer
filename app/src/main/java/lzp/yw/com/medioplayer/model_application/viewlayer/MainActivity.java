package lzp.yw.com.medioplayer.model_application.viewlayer;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

/**
 * Created by user on 2016/10/26.
 */
public class MainActivity extends BaseActivity {
    private AbsoluteLayout main_layout;//底层 主布局
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_layout = (AbsoluteLayout) findViewById(R.id.main_layout);
        initUI();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unInitUI();
    }

    @Override
    public ViewGroup getActivityLayout() {
        return main_layout;
    }
}
