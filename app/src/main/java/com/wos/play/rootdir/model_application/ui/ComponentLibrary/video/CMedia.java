package com.wos.play.rootdir.model_application.ui.ComponentLibrary.video;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.MediaInterface;
import com.wos.play.rootdir.model_application.ui.Uitools.GestureHelper;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_report.ReportHelper;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2016/11/14.
 * 多媒体 播放组件
 */

public class CMedia extends FrameLayout implements IComponent,MediaInterface
        ,GestureHelper.OnSlidingListener{
    private static final java.lang.String TAG = "CMorePictures";
    //private int componentId;
    private int width,height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private boolean isInitData, isLayout;
    private GestureHelper mGestureHelper;
    private FrameLayout root;
    private ImageView mImageView;
    private CVideoView  mVideoView;
    private int length;
    private String fileName;
    private boolean isShowTopLayer;
    private long startTime;
    private ContentsBean content;


    public CMedia(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }

    //初始化数据
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
            mGestureHelper = new GestureHelper(cb.getTransition(),this);
            //this.componentId = cb.getId();
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            initSubComponent();
            if (cb.getContents()!=null && cb.getContents().size()>0) {
                createContent(cb.getContents());
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initSubComponent() {
        root = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.media_layout_base,null);
        mImageView = (ImageView) root.findViewById(R.id.media_image);
        mVideoView = (CVideoView) root.findViewById(R.id.media_video);
        this.addView(root);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private ArrayList<ContentsBean> contentArr = null;
    //添加 图片组件-视频组件
    private void addContentImp(ContentsBean content){
        if (contentArr==null){
            contentArr = new ArrayList<>();
        }
        contentArr.add(content);
    }

    //创建内容
    @Override
    public void createContent(Object object) {
        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            //只有图片 或者 视频内容
            for (ContentsBean content : contents){
                addContentImp(content);
            }
            playNumber = contentArr.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置属性
    @Override
    public void setAttribute() {
        this.setLayoutParams(layoutParams);
    }


    //布局
    @Override
    public void onLayouts() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }

    }

    //取消布局
    @Override
    public void unLayouts() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    //开始
    @Override
    public void startWork() {
        try {
            if (!isInitData)return;
            setAttribute();
            onLayouts();
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止
    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouts(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int currentIndex = 0;//当前循环的下标
    private Handler handler = null;
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            loadContent();
        }
    };

    //加载内容
    @Override
    public void loadContent() {
        if (contentArr!=null && contentArr.size()>0){
            if (handler==null){
                handler = new Handler();
            }
            unLoadContent();
            startTime = System.currentTimeMillis();
            content = contentArr.get(currentIndex);
            length = content.getTimeLength();
            fileName = UiTools.getUrlTanslationFilename(content.getContentSource());
            if(isImage(content)){
                mImageView.setVisibility(View.VISIBLE);
                if (UiTools.fileIsExt(fileName)){
                    mImageView.setImageBitmap(ImageUtils.getBitmap(fileName));
                }else {
                    mImageView.setImageBitmap(ImageUtils.getBitmap(UiTools.getDefImagePath()));
                    playOver(this);
                }
            }else{
                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setZOrderOnTop(isShowTopLayer);
                if (FileUtils.isFileExist(fileName)) {
                    mVideoView.setVideoPath(fileName);
                } else {
                    this.playOver(this);
                    mVideoView.setVideoPath(UiTools.getDefVideoPath());
                }
                if(mVideoView.getDuration() >0 ) length = mVideoView.getDuration();
            }

            handler.postDelayed(mTask, length * 1000);
            currentIndex++;
            if (currentIndex==contentArr.size()){
                currentIndex = 0;
            }
        }
    }

    private boolean isImage(ContentsBean content) {
        //if(content==null || content.getContentName() ==null) return false;
        String fileName = content.getContentName();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg");
    }

    @Override
    public void unLoadContent() {
        if (handler!=null){
            handler.removeCallbacks(mTask);
        }
        mVideoView.pause();
        mVideoView.setVisibility(View.GONE);
        mImageView.setVisibility(View.GONE);
        if(content!=null && isImage(content)){
            ReportHelper.onImage(context, content.getId(), fileName, startTime);
        }else if(content!=null){
            ReportHelper.onVideo(context, content.getId(), fileName, startTime);
        }
    }

    private int playNumber = 0;
    //和 视频控件 图片控件 通讯
    @Override
    public void playOver(IView playView) {
        playNumber--;
        if (playNumber>0){
            loadContent();
        }
    }

    @Override
    public void onUpOrLeft() {
        Logs.i(TAG,"向上或向左滑动");
        if (playNumber< 2) return;
        currentIndex -= 2;
        if( currentIndex< 0) { //  当-1的时候减1;-2的时候减2
            currentIndex += contentArr.size();
        }
        loadContent();
    }

    @Override
    public void onDownOrRight() {
        Logs.i(TAG,"向下或向右滑动");
        if (playNumber< 2) return;
        loadContent();
    }

    public void setShowTopLayer(boolean isShowTopLayer) {
        this.isShowTopLayer = isShowTopLayer;
    }
}
