package com.wos.play.rootdir.model_application.ui.ComponentLibrary.video;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.CImageView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IContentView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.MediaInterface;
import com.wos.play.rootdir.model_application.ui.Uitools.GestureHelper;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.tool.CONTENT_TYPE;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/14.
 * 多媒体 播放组件
 */

public class CMedio extends FrameLayout implements IComponent,MediaInterface
        ,GestureHelper.OnSlidingListener{
    private static final java.lang.String TAG = "CMorePictures";
    private int componentId;
    private int width,height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private boolean isInitData, isLayout;
    private GestureHelper mGestureHelper;
    public CMedio(Context context,AbsoluteLayout layout, ComponentsBean component) {
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
            this.componentId = cb.getId();
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            if (cb.getContents()!=null && cb.getContents().size()>0) {
                createContent(cb.getContents());
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private ArrayList<IContentView> contentArr = null;
    //添加 图片组件-视频组件
    private void addContentImp(IContentView content){
        if (contentArr==null){
            contentArr = new ArrayList<>();
        }
        contentArr.add(content);
    }

    //创建内容
    @Override
    public void createContent(Object object) {
        try {
            String type = null;
            List<ContentsBean> contents = (List<ContentsBean>)object;
            IContentView imp = null;
            //只有图片 或者 视频内容
            for (ContentsBean content : contents){
                type=content.getMaterialType() ==null?content.getContentType():content.getMaterialType();
                if(content.getContentName().endsWith(".png") || content.getContentName().endsWith(".jpg")){
                    type = CONTENT_TYPE.image;
                }
                if (type.equals(CONTENT_TYPE.image)){
                     imp = new CImageView(context,this,content);
                }
                if (type.equals(CONTENT_TYPE.video)){
                    imp = new MyVideoViewHolder(context,this,content);
                }
                if (imp!=null){
                    addContentImp(imp);
                    imp.setMediaInterface(this);
                    imp = null;
                }
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
    private IContentView currentIView= null; //当前播放的图片
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
            currentIView =  contentArr.get(currentIndex);
            currentIView.startWork();
            handler.postDelayed(mTask, contentArr.get(currentIndex).getLength() * 1000);
            currentIndex++;
            if (currentIndex==contentArr.size()){
                currentIndex = 0;
            }
        }
    }

    @Override
    public void unLoadContent() {
        if (handler!=null){
            handler.removeCallbacks(mTask);
        }
        if (currentIView!=null){
            currentIView.stopWork();
            currentIView = null;
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
}
