package com.wos.play.rootdir.model_application.viewlayer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;

import cn.trinea.android.common.util.FileUtils;

public class EpaperActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "EpaperActivity";
    public static final String PATHKEY = "paperFilepath";
    private ArrayList<String> sourceList;
    private SubsamplingScaleImageView imageView;

    private ImageButton epaper_imageButton_back;//返回
    private ImageButton epaper_imageButton_previous;//上一页
    private ImageButton epaper_imageButton_next;//下一页
    private static int index = 0;//第几页

    //适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epaper);
        setIsOnBack(true);

        initIntent();
        initView();
        initData(0);
    }

    /**
     * 初始化数据
     *
     * @param type
     */
    private void initData(int type) {
        close();
        switch (type) {
            case 1://上一页就减
                index--;
                break;
            case 2:
                index++;
                break;
        }
        if (index >= sourceList.size()) index = 0;
        else if (index < 0) index = sourceList.size() - 1;
        imageView.setImage(ImageSource.uri(sourceList.get(index)));
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

        sourceList = new ArrayList<>();//电子报图片路径list
        for (File sFile : files) {
            if (sFile.isDirectory() && sFile.list().length > 0) {
                sourceList.add(getSourceImagePath(sFile));
            }
        }
    }

    // 返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return close();
    }

    //初始化视图
    private void initView() {
        imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
        epaper_imageButton_back = (ImageButton) findViewById(R.id.epaper_imageButton_back);
        epaper_imageButton_previous = (ImageButton) findViewById(R.id.epaper_imageButton_previous);
        epaper_imageButton_next = (ImageButton) findViewById(R.id.epaper_imageButton_next);

        //设置透明度
        epaper_imageButton_back.setBackgroundColor(Color.TRANSPARENT);
        epaper_imageButton_previous.setBackgroundColor(Color.TRANSPARENT);
        epaper_imageButton_next.setBackgroundColor(Color.TRANSPARENT);

        //设置监听器
        epaper_imageButton_next.setOnClickListener(this);
        epaper_imageButton_back.setOnClickListener(this);
        epaper_imageButton_previous.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        close();
        stopActivityOnArr(this);
    }

    private boolean close() {
        if (imageView != null) {
            imageView.recycle();
            System.gc();
            return true;
        }
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.epaper_imageButton_back://返回
                close();
                stopActivityOnArr(this);
                break;
            case R.id.epaper_imageButton_previous://上一页
                initData(1);
                break;
            case R.id.epaper_imageButton_next://下一页
                initData(2);
                break;
        }
    }

    //获取电子报图片路径
    public String getSourceImagePath(File source) {
        if (source != null) {
            //循环遍历 - 找出 文件名 thumb_开头的文件
            String[] list = source.list();
            if (list != null && list.length > 0) {
                for (String aList : list) {
                    if (!aList.contains("thumb") && aList.contains(".png"))
                        return source + "/" + aList;
                }
            }
        }
        return UiTools.getDefImagePath();
    }
}
