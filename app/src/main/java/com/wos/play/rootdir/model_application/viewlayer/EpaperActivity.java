package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers.EActivityGrallyAdpter;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;

public class EpaperActivity extends BaseActivity {
    public static final String TAG = "EpaperActivity";
    public static final String PATHKEY = "paperFilepath";
    private String path = "";
    private Gallery grallery;
    private EActivityGrallyAdpter adpter;

    private MeImageView imageView;

    //适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epaper);
        setIsOnBack(true);
        initIntent();
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
        if (arrList.size() > 0) {
            initView(arrList);
        } else {
            stopActivityOnArr(this);
        }
    }

    //初始化视图
    private void initView(List<File> list) {
        grallery = (Gallery) findViewById(R.id.epaper_act_gallery);
        adpter = new EActivityGrallyAdpter(this, list);
        grallery.setAdapter(adpter);
        grallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adpter.setSelectItem(position);
                selectFile(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ViewGroup vp = (ViewGroup) findViewById(R.id.epaper_act_layout);
        imageView = ImageUtils.createImageView(this,3);
        vp.addView(imageView,new FrameLayout.LayoutParams(-1,-1));
    }

    //选择文件 填充 imageView
    private void selectFile(int position) {
        String contentPath = adpter.getImagePath(adpter.getSource(position));
        Log.i(TAG,"显示大图:"+contentPath);
        ImageAsyLoad.loadBitmap(contentPath,imageView);
    }


}
