package com.wos.play.rootdir.model_application.viewlayer;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

    private DragImageView imageview;
    private ListView listview;
    private EActivityGrallyAdpter adpter;
    Object[] arr = new Object[7];
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
        if (imageview!=null && imageview.getVisibility() == View.VISIBLE){
            //清楚bitmap
            imageview.clearBitmap();
            imageview.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
            Logs.i(TAG,"清理 image - bitmap");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //初始化视图
    private void initView() {

        imageview = (DragImageView) findViewById(R.id.imageview);
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adpter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = adpter.getImagePath(adpter.getSource(position));
                Logs.i(TAG,"选择: "+ path);
                if (imageview!=null && imageview.getVisibility() == View.GONE){
                    listview.setVisibility(View.GONE);
                    imageview.setVisibility(View.VISIBLE);
                    arr[1] = path;
                    ImageAsyLoad.regionBitmap(arr);
                }
            }
        });
        arr[0] = imageview;
        arr[2] = getWindowManager().getDefaultDisplay().getWidth();
        arr[3] = getWindowManager().getDefaultDisplay().getHeight();
        arr[4] = getResources().getDisplayMetrics().densityDpi;
        arr[5] = 2;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (imageview!=null && imageview.getVisibility() == View.VISIBLE){
            //清楚bitmap
            imageview.clearBitmap();
            Logs.i(TAG,"onStop 清理 image - bitmap");
        }
    }
}
