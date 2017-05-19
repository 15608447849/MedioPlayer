package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponentUpdate;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopMonitorFiles;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopSuccessInterfaces;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_application.viewlayer.EpaperActivity;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2017/1/4.
 */

public class CEpapersMain extends FrameLayout implements IComponentUpdate, LoopSuccessInterfaces{
    private static final String TAG = "_epapersMain";
    private Context context;
    private Handler handler ;
    private AbsoluteLayout layout;
    private int x,y,width,height;
    private AbsoluteLayout.LayoutParams layoutParams;
    private String epaperPathDir;
    private int itemNum = -1;
    private String epaperName;
    private MGridView mGrid;
    private MGridAdapter adapter;
    private ProgressBar progress;
    private boolean isInitData = false;
    private boolean isLayout = false;

    private int backgroundAlpha;
    private String backgroundColor;
    private  String bgImageUrl;
    private Bitmap bgImage;

    public CEpapersMain(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        handler = new Handler();
        this.layout = layout;
        initData(component);
    }

    //初始化数据 - 1 解压电子报zip文件
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);

            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);

            //--------背景-----------
            this.backgroundAlpha = getAlpha(cb.getBackgroundAlpha());
            if (cb.getBackgroundPic()!=null && !cb.getBackgroundPic().equals("")){
                this.bgImageUrl = UiTools.getUrlTanslationFilename(cb.getBackgroundPic());
                if (bgImageUrl==null){
                    backgroundColor = cb.getBackgroundColor();
                }
            } else {
                backgroundColor = cb.getBackgroundColor();
            }

            if (cb.getContents()!=null && cb.getContents().size()>0){
                createContent(cb.getContents().get(0));
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 百分比转换透明度（0-255）
     */
    private int getAlpha(int cent) {
        if(cent< 0)   cent=0;
        if(cent> 100) cent=100;
        return Math.round(cent * 255 / 100);
    }

    /**
     * 获取颜色值（包括透明度）
     */
    private int getColor(String colorString) {
        if (colorString!=null && colorString.charAt(0) == '#') {
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                color |= ( backgroundAlpha << 24 ); //color |=  0x0000000000000000;
                return (int) color;
            }
            return (int) color;
        }
        return Color.TRANSPARENT;
    }


    //加载背景
    @Override
    public void loadBg() {
        Bitmap bitmap = ImageUtils.getBitmap(bgImageUrl);
        if(bitmap!=null) this.setBackgroundDrawable(new BitmapDrawable(bitmap));

    }

    //不加载背景
    @Override
    public void unloadBg() {
        ImageUtils.removeCache(bgImageUrl);
        this.setBackgroundDrawable(null);
    }

    //创建content
    @Override
    public void createContent(Object object) {
        try {
            ContentsBean content = (ContentsBean) object;
            epaperPathDir = UiTools.getEpapers() + content.getContentSource();
            itemNum = content.getDaysKeep();
            epaperName = content.getContentName();//电子报名字
            //创建视图层
            mGrid = new MGridView(context);
            adapter = new MGridAdapter(context);
            progress = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
            progress.setIndeterminate(false);
            progress.setLayoutParams(new FrameLayout.LayoutParams(150, 150, Gravity.CENTER) );
            //循环遍历目录下面的文件   - 文件名 是 -> 年-月-日  -> 先查看.zip 如果存在,查看 是否有对应文件夹 ,如果没有 - 解压缩到当前目录
            setGrids();
            showDirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //设置视图层
    private void setGrids() {
        if (mGrid!=null){
            mGrid.setTtile(epaperName);
            mGrid.setAdapete(adapter);
            mGrid.setButtonOnclick(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //刷新
                    showDirs();
                }
            });
            mGrid.setItemOnclick(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //打开activity - 传过去一个 file  - 这个activity 是用来显示 那个啥的
                    startEpaperActivity(adapter.getItemFileAbsPath(position));
                }
            });
        }
    }

    //打开 电子报的入口!!
    private void startEpaperActivity(String itemFileAbsPath) {
        //打开一个当前电子报的列表视图
        if (itemFileAbsPath!=null && context!=null){
            Intent intent =new Intent(context, EpaperActivity.class);
            intent.putExtra(EpaperActivity.PATHKEY,itemFileAbsPath);
            context.startActivity(intent);
        }
    }


    //设置属性
    @Override
    public void setAttribute() {
        this.setLayoutParams(layoutParams);

        if (bgImageUrl==null){
            //设置背景颜色
            this.setBackgroundColor(getColor(backgroundColor));
            //this.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(backgroundColor)));
        } else {
            loadBg();
        }
        this.setAlpha(backgroundAlpha);
    }
    //设置布局
    @Override
    public void onLayouts() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
        if(progress!=null){
            this.removeView(progress);
            this.addView(progress);
        }
    }
    //取消布局
    @Override
    public void unLayouts() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
        unloadBg();
    }
    //加载内容
    @Override
    public void loadContent() {
        try {
          if (mGrid!=null){
              mGrid.setLayout(this);
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //取消加载内容
    @Override
    public void unLoadContent() {
        try {
           if (mGrid!=null){
               mGrid.unLayout();
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //开始工作
    @Override
    public void startWork() {
        try {
            if (!isInitData){
                return;
            }
            setAttribute();
            onLayouts();
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止工作
    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouts(); //移除布局
            LoopMonitorFiles.getInstance().clearMonitor(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------//
    private ArrayList<File> sourceList ;

    //添加 电子报 文件 通知适配器 更新数据
    private void addEpaperFileDir(File fileDir){
        if (sourceList==null){
            sourceList = new ArrayList<>();
        }
        if (effectFiles(fileDir.getAbsolutePath())){
            //判断文件名是不是相同?
            sourceList.add(fileDir);
            Collections.sort(sourceList, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return rhs.getName().compareTo(lhs.getName());
                }
            });
            handler.post(new Runnable() {
                @Override
                public void run() {//设置适配器数据
                    if (sourceList!=null && sourceList.size()>0){
                        progress.setVisibility(View.GONE);
                        adapter.setDataSource(sourceList);
                    }
                }
            });

        }

    }
    //判断文件名是不是相同
    private boolean effectFiles(String fileDirAbsolutePath) {
        if (sourceList!=null && sourceList.size()>0){
            for (File file : sourceList){
                if (file.getAbsolutePath().equals(fileDirAbsolutePath)){
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 循环遍历目录下面的文件   - 文件名 是 -> 年-月-日  -> 先查看.zip 如果存在,查看 是否有对应文件夹 ,如果没有 - 解压缩到当前目录
     */


    private void  showDirs() {
        if (epaperPathDir!=null && itemNum>0){
            try {
                String filepath ;
                for (int i = 0;i<itemNum;i++){
                    filepath = epaperPathDir + TimeOperator.getTodayGotoDays(-i);
                    if (FileUtils.isFolderExist(filepath)){
                        addEpaperFileDir(new File(filepath));
                    }else if (FileUtils.isFileExist(filepath+".zip")){
                        LoopMonitorFiles.getInstance().addMonitorFile(this, filepath);
                        unZipFiles(filepath+".zip", filepath, false);
                    }else{
                        LoopMonitorFiles.getInstance().addMonitorFile(this, filepath+".zip");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //解压电子报
    void unZipFiles(final String zip,final String dir, final boolean idDelete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                UiTools.unZipFiles(zip,dir,idDelete);
            }
        }).start();
    }

    @Override
    public void sourceExist(String data, boolean isFile) {
        if(isFile && data.endsWith(".zip")){
            String dir = data.replace(".zip","");
            LoopMonitorFiles.getInstance().addMonitorFile(this, dir);
            unZipFiles(data, dir, false);
        }else if(!isFile){
            addEpaperFileDir(new File(data));
        }
    }
}
