package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.wos.play.rootdir.model_application.schedule.TimeOperator;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_application.viewlayer.EpaperActivity;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2017/1/4.
 */

public class CEpapersMain extends FrameLayout implements IComponent{
    private static final String TAG = "_epapersMain";
    private Context context;
    private Handler handler ;
    private AbsoluteLayout layout;
    private int x,y,width,height;
    private AbsoluteLayout.LayoutParams layoutParams;
    private String epaperPathDir;
    private int itemNum = -1;
    private String epaperName;
    private MGridView mgrid;
    private MgridAdptes adpter;
    private boolean isInitData = false;
    private boolean isLayout = false;

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
            if (cb.getContents()!=null && cb.getContents().size()>0){
                createContent(cb.getContents().get(0));
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
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
            mgrid = new MGridView(context);
            adpter = new MgridAdptes(context);
            //循环遍历目录下面的文件   - 文件名 是 -> 年-月-日  -> 先查看.zip 如果存在,查看 是否有对应文件夹 ,如果没有 - 解压缩到当前目录
            setGrids();
            showDirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //设置视图层
    private void setGrids() {
        if (mgrid!=null){
            mgrid.setTtile(epaperName);
            mgrid.setAdapete(adpter);
            mgrid.setButtonOnclick(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //刷新
                    showDirs();
                }
            });
            mgrid.setItemOnclick(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //打开activity - 传过去一个 file  - 这个activity 是用来显示 那个啥的
                    startEpaperActivity(adpter.getItemFileAbsPath(position));
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
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
    }
    //设置布局
    @Override
    public void layouted() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }
    //取消布局
    @Override
    public void unLayouted() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    //加载内容
    @Override
    public void loadContent() {
        try {
          if (mgrid!=null){
              mgrid.setLayout(this);
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //取消加载内容
    @Override
    public void unLoadContent() {
        try {
           if (mgrid!=null){
               mgrid.unLayout();
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
            setAttrbute();
            layouted();
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
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------//
    private ArrayList<File> sourceList ;

    //添加 电子报 文件
    private void addEpaperFileDir(File filedir){
        if (sourceList==null){
            sourceList = new ArrayList<>();
        }
        if (effectFiles(filedir.getAbsolutePath())){
            //判断文件名是不是相同?
            sourceList.add(filedir);
        }

    }
    //判断文件名是不是相同
    private boolean effectFiles(String filedirAbsolutePath) {
        if (sourceList!=null && sourceList.size()>0){
            for (File file : sourceList){
                if (file.getAbsolutePath().equals(filedirAbsolutePath)){
                    return false;
                }
            }
        }
        return true;
    }


    //--------------------------------------------//
    private ReentrantLock lock = new ReentrantLock();
    /**
     * 循环遍历目录下面的文件   - 文件名 是 -> 年-月-日  -> 先查看.zip 如果存在,查看 是否有对应文件夹 ,如果没有 - 解压缩到当前目录
     */
    private void  showDirs() {

        if (epaperPathDir!=null && itemNum>0){
           new Thread(new Runnable() {
               @Override
               public void run() {

                   try {
                       lock.lock();
                       String filepath ;
                       for (int i = 0;i<itemNum;i++){
                           filepath = epaperPathDir + TimeOperator.getTodayGotoDays(-i);

                           if (FileUtils.isFileExist(filepath+".zip")){
                               if (!FileUtils.isFolderExist(filepath)){
                                   //解压缩
                                   UiTools.unZipFiles(filepath+".zip",filepath,false);
                               }
                               //保存文件到 数组
                               addEpaperFileDir(new File(filepath));
                           }
                       }
                       Logs.i(TAG,"============= 电子报 遍历 文件夹 完成 ============");

                       handler.post(new Runnable() {
                           @Override
                           public void run() {
                               //设置适配器数据
                               settingAdpterData();
                           }
                       });
                   } catch (Exception e) {
                       e.printStackTrace();
                   }finally {
                       lock.unlock();
                   }

               }
           }).start();
        }
    }
    //设置适配器
    private void settingAdpterData() {
        if (sourceList!=null && sourceList.size()>0){
            adpter.setDataSource(sourceList);
        }
    }


}
