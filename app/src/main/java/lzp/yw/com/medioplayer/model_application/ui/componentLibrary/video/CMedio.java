package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.video;

import android.content.Context;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponent;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IContentView;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.MedioInterface;
import lzp.yw.com.medioplayer.model_application.ui.componentLibrary.image.CImageView;
import lzp.yw.com.medioplayer.model_universal.tool.CONTENT_TYPE;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by user on 2016/11/14.
 * 多媒体 播放组件
 */

public class CMedio extends FrameLayout implements IComponent,MedioInterface{
    private static final java.lang.String TAG = "CMorePictures";
    private int componentId;
    private int width,height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private boolean isInitData;
    private boolean isLayout;
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
            this.componentId = cb.getId();
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            if (cb.getContents()!=null && cb.getContents().size()>0) {
                createContent(cb.getContents());
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private ArrayList<IContentView> contentArr = null;
    //添加 图片组件
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
                    imp = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置属性
    @Override
    public void setAttrbute() {
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
    }

    //布局
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
    //开始
    @Override
    public void startWork() {
        Logs.i(TAG,"- -startWork()- -");
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
    //停止
    @Override
    public void stopWork() {
        Logs.i(TAG,"stopWork()");
        try {
            unLoadContent();
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int currentIndex = 0;//当前循环的下标
    private IContentView currentIView= null; //当前播放的图片


    //加载内容
    @Override
    public void loadContent() {
        if (contentArr!=null && contentArr.size()>0){
            unLoadContent();
            currentIView =  contentArr.get(currentIndex);
            currentIView.startWork();
            //创建计时器
            startTimer(currentIView.getLength() * 1000);
            currentIndex++;
            if (currentIndex==contentArr.size()){
                currentIndex = 0;
            }
        }
    }

    @Override
    public void unLoadContent() {
        stopTimer();//停止计时间 停止当前内容
        if (currentIView!=null){
            currentIView.stopWork();
            currentIView = null;
        }
    }
    //和 视频控件 通讯
    @Override
    public void playOver() {
        loadContent();
    }




    private TimerTask timerTask= null;
    private Timer timer = null;
    //停止计时器
    private void stopTimer(){
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }
    //开始计时器
    private void startTimer(long millisecond){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        loadContent();
                    }
                });

            }
        };
        timer = new Timer();
        timer.schedule(timerTask,millisecond);
    }


}
