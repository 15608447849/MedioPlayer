package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers.EActivityGrallyAdpter;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;

public class EpaperActivity extends BaseActivity {
    public static final String TAG = "EpaperActivity";
    public static final String PATHKEY = "paperFilepath";
    private GridView list;
    private EActivityGrallyAdpter adpter;
private SubsamplingScaleImageView imageView;
    //适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epaper);
        setIsOnBack(true);
        initIntent();
        initView();
    }

    //初始化路径
    private void initIntent() {
        String path = this.getIntent().getStringExtra(PATHKEY);
        if (path == null || !FileUtils.isFolderExist(path)) {
            stopActivityOnArr(this);
            return;
        }
        Logs.i(TAG, "打开电子报 - [ " + path + " ]");
        File[] files = new File(path).listFiles();
        List<File> arrList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            File sFile = files[i];
            if (sFile.isDirectory() && sFile.list().length > 0) {
                arrList.add(sFile);
            }
        }
        Logs.i(TAG, "列表 - [ " + arrList + " ]");
        if (arrList.size() > 0) {
            adpter = new EActivityGrallyAdpter(this, arrList);
        } else {
            stopActivityOnArr(this);
        }
    }


    // 返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
    //初始化视图
    private void initView() {
        list = (GridView) findViewById(R.id.grid);
        list.setAdapter(adpter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = adpter.getSourceImagePath(adpter.getSource(position));
                Logs.i(TAG,"选择: "+ path);
                if (imageView!=null && imageView.getVisibility()==View.GONE ){
                    list.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImage(ImageSource.uri(path));
                }
            }
        });

        imageView =  (SubsamplingScaleImageView)findViewById(R.id.imageView);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (imageView!=null && imageView.getVisibility() == View.VISIBLE){
                    imageView.recycle();
                    imageView.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
