package com.wos.play.rootdir.model_application.viewlayer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.trinea.android.common.util.FileUtils;

public class EpaperActivity extends BaseActivity implements View.OnClickListener {

    private ProgressBar tasksCompletedView;//进度条

    public static final String TAG = "EpaperActivity";
    public static final String PATHKEY = "paperFilepath";
    private ArrayList<String> sourceList;
    private ArrayList<String> sourceNameList;
    private SubsamplingScaleImageView imageView;

    private ImageButton epaper_imageButton_back;//返回
    private ImageButton epaper_imageButton_previous;//上一页
    private ImageButton epaper_imageButton_next;//下一页
    private static int index = 0;//第几页
    private boolean isLoading = true;//本次加载是否完成
    private boolean lastClickTypeIsRight = true;//最近一次点击方向(上一次，下一次,默认是下一次(右))

    private ImageButton epaper_imageButton_pages;//显示页数
    private ListView epaper_page_listView;//页数
    private boolean showPage = true;

    //适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epaper);
        setIsOnBack(true);

        initIntent();
        initView();
        initData(0);
        initPagesMenu();
    }

    /**
     * 初始化页数
     */
    private void initPagesMenu() {
        epaper_page_listView = (ListView) findViewById(R.id.epaper_page_listView1);
        epaper_page_listView.setVisibility(View.GONE);
        Collections.sort(sourceNameList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        epaper_page_listView.setAdapter(new ArrayAdapter<>(this, R.layout.epaper_list_item_text, sourceNameList));

        epaper_page_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                epaper_page_listView.setVisibility(View.GONE);
                showPage = false;
                index = position;
                initData(0);
            }
        });
    }

    /**
     * 初始化数据
     *
     * @param type
     */
    private void initData(int type) {
        if (!isLoading) return;//上次加载未完成
        isLoading = false;
        showPage = true;
        if (epaper_page_listView != null) epaper_page_listView.setVisibility(View.GONE);
        tasksCompletedView.setVisibility(View.VISIBLE);
        close();
        switch (type) {
            //上一页就减
            case 1:
                index--;
                lastClickTypeIsRight = false;
                break;
            case 2:
                index++;
                lastClickTypeIsRight = true;
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
        sourceNameList = new ArrayList<>();
        for (File sFile : files) {
            if (sFile.isDirectory() && sFile.list().length > 0) {
                sourceList.add(UiTools.getImagePath(sFile));
                sourceNameList.add(sFile.getName());
            }
        }
        Collections.sort(sourceList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }

    // 返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return close();
    }

    //初始化视图
    private void initView() {
        tasksCompletedView = (ProgressBar) findViewById(R.id.tasksCompletedView);//进度条
        tasksCompletedView.setEnabled(false);

        imageView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
        imageState();

        epaper_imageButton_pages = (ImageButton) findViewById(R.id.epaper_image_button_pages);
        epaper_imageButton_back = (ImageButton) findViewById(R.id.epaper_imageButton_back);
        epaper_imageButton_previous = (ImageButton) findViewById(R.id.epaper_imageButton_previous);
        epaper_imageButton_next = (ImageButton) findViewById(R.id.epaper_imageButton_next);

        //设置透明度
        epaper_imageButton_back.setBackgroundColor(Color.TRANSPARENT);
        epaper_imageButton_previous.setBackgroundColor(Color.TRANSPARENT);
        epaper_imageButton_next.setBackgroundColor(Color.TRANSPARENT);
        epaper_imageButton_pages.setBackgroundColor(Color.TRANSPARENT);

        //设置监听器
        epaper_imageButton_next.setOnClickListener(this);
        epaper_imageButton_back.setOnClickListener(this);
        epaper_imageButton_previous.setOnClickListener(this);
        epaper_imageButton_pages.setOnClickListener(this);
    }

    /**
     * 图片加载情况
     */
    private void imageState() {
        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                Logs.d(TAG, "onReady");
            }

            @Override
            public void onImageLoaded() {
                Logs.i(TAG, "onImageLoaded");
                tasksCompletedView.setVisibility(View.GONE);
                isLoading = true;
            }

            @Override
            public void onPreviewLoadError(Exception e) {
                isLoading = true;
                Logs.i(TAG, "onPreviewLoadError");
            }

            @Override
            public void onImageLoadError(Exception e) {
                isLoading = true;
                Logs.i(TAG, "onImageLoadError==" + sourceList.size() + "--" + index + "--1--" + sourceList.get(index));
                UiTools.delete(sourceList, index);//删除该位置错误的图片
                UiTools.delete(sourceNameList, index);
                Logs.i(TAG, "onImageLoadError" + index + "--2--" + sourceList.size());

                showToast("网络不可用或者版面不正确");
                tasksCompletedView.setVisibility(View.GONE);
                //根据最近一次点击的方向
                if (lastClickTypeIsRight) initData(2);//加载下一页
                else initData(1);//加载上一页
            }

            @Override
            public void onTileLoadError(Exception e) {
                Logs.i(TAG, "onTileLoadError");
                isLoading = true;
            }

            @Override
            public void onPreviewReleased() {
                Logs.i(TAG, "onPreviewReleased");
                isLoading = true;
            }
        });
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
                index = 0;
                close();
                stopActivityOnArr(this);
                break;
            case R.id.epaper_imageButton_previous://上一页
                initData(1);
                break;
            case R.id.epaper_imageButton_next://下一页
                initData(2);
                break;
            case R.id.epaper_image_button_pages://显示页数
                if (showPage) {
                    showPage = false;
                    epaper_page_listView.setVisibility(View.VISIBLE);
                } else {
                    showPage = true;
                    epaper_page_listView.setVisibility(View.GONE);
                }
                break;
        }
    }

}
