package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers.EActivityGrallyAdpter;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.DragImageView;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;

public class EpaperActivity extends BaseActivity {

    public static final String TAG = "EpaperActivity";
    public static final String PATHKEY = "paperFilepath";

    private String path = "";
    private DragImageView image;
    private Gallery grallery;
    private EActivityGrallyAdpter adpter;

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
//        mFile = new File(path);
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
        image = (DragImageView) findViewById(R.id.epaper_act_show);

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
    }

    //选择文件 填充 imageView
    private void selectFile(int position) {
        String contentPath = adpter.getContentImagePath(adpter.getSource(position));
        if (image != null) {
            ImageAsyLoad.loadBitmap(contentPath, image);
        }
    }


}
