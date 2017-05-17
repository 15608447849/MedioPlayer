package com.wos.play.rootdir.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.os.Handler;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.MediaInterface;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/11.
 * 播放图片的组件 - 多图片播放
 *
 */
public class CMorePictures extends FrameLayout implements IComponent,MediaInterface {
    private static final java.lang.String TAG = "CMorePictures";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private boolean isInitData;
    private boolean isLayout;
    public CMorePictures(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }

    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
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
    public void setAttribute() {
//        Logs.i(TAG,"- -setAttrbute()- -");
//        this.setBackgroundColor(Color.BLUE);
        this.setLayoutParams(layoutParams);
        currentIndex=0;//当前下标
    }
    @Override
    public void onLayouts() {
//        Logs.i(TAG,"- -layouted()- - "+isLayout);
//        Logs.i(TAG,"- --------------- - "+layout+"\n"+this);
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }
    @Override
    public void unLayouts() {
//        Logs.i(TAG,"- -unLayouted()- - "+isLayout);
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    @Override
    public void startWork() {
//        Logs.i(TAG,"- -startWork()- -");
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

    @Override
    public void stopWork() {
//        Logs.i(TAG,"stopWork()");
        try {
            unLoadContent();
            unLayouts(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//--------------------图片 自定义控件 数组------------------------------//
    private ArrayList<CImageView> imageArr = null;
    //添加 图片组件
    private void addImages(CImageView imageView){
        if (imageArr==null){
            imageArr = new ArrayList<>();
        }
        imageArr.add(imageView);
    }
    //创建 内容
    @Override
    public void createContent(Object object) {
        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            CImageView imageview = null;
            //只有图片内容
            for (ContentsBean content : contents){
                imageview = new CImageView(context,this,content);
                imageview.setMediaInterface(this);
                addImages(imageview);
            }
            playNumber = imageArr.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int playNumber = 0;
    private int currentIndex = 0;//当前循环的下标
    private CImageView currentImageView= null; //当前播放的图片
    private Handler handler = null;
    private final Runnable mTask = new Runnable() {

        @Override
        public void run() {
            loadContent();
        }
    };
    @Override
    public void loadContent() {

        if (imageArr!=null && imageArr.size()>0){
            if (handler==null){
                handler = new Handler();
            }
            unLoadContent();
            currentImageView =  imageArr.get(currentIndex);
            currentImageView.startWork();

            handler.postDelayed(mTask, imageArr.get(currentIndex).getLength() * 1000);
            currentIndex++;
            if (currentIndex==imageArr.size()){
                currentIndex = 0;
            }
        }
    }
    // 取消加载内容
    @Override
    public void unLoadContent() {

        if (handler!=null){
            handler.removeCallbacks(mTask);
        }
        if (currentImageView!=null){
            currentImageView.stopWork();
            currentImageView = null;
        }
    }


    @Override
    public void playOver(IView playView) {
        //子组件 资源不存在 或者 子组件err -> 播放一个内容
        playNumber--;
        if (playNumber>0){
            loadContent();
        }

    }
}












//计时器
   /*
               //创建计时器
//            startTimer(imageArr.get(currentIndex).getLength() * 1000);
    //        stopTimer();//停止计时间 停止当前内容
    private TimerTask timerTask= null;
    private Timer timer = null;

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
    }*/
