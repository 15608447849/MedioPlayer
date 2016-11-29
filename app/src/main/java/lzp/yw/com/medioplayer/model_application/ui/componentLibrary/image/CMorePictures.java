package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.image;

import android.content.Context;
import android.os.Handler;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponent;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.MedioInterface;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/11.
 * 播放图片的组件 - 多图片播放
 *
 */
public class CMorePictures extends FrameLayout implements IComponent,MedioInterface{
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
    public void setAttrbute() {
//        Logs.i(TAG,"- -setAttrbute()- -");
        this.setLayoutParams(layoutParams);
//        this.setBackgroundColor(Color.BLUE);
    }
    @Override
    public void layouted() {
//        Logs.i(TAG,"- -layouted()- - "+isLayout);
//        Logs.i(TAG,"- --------------- - "+layout+"\n"+this);
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }
    @Override
    public void unLayouted() {
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
            setAttrbute();
            layouted();
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
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//--------------------图片 自定义控件 数组------------------------------//
    private ArrayList<CImageView> imageArr = null;
    //添加 图片组件
    private void addImages(CImageView imageview){
        if (imageArr==null){
            imageArr = new ArrayList<>();
        }
        imageArr.add(imageview);
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
                imageview.setMedioInterface(this);
                addImages(imageview);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int currentIndex = 0;//当前循环的下标
    private CImageView currentImageView= null; //当前播放的图片
    private Handler hander = null;
    private final Runnable mTask = new Runnable() {

        @Override
        public void run() {
            loadContent();
        }
    };
    @Override
    public void loadContent() {

        if (imageArr!=null && imageArr.size()>0){
            if (hander==null){
                hander = new Handler();
            }
            unLoadContent();
            currentImageView =  imageArr.get(currentIndex);
            currentImageView.startWork();

            hander.postDelayed(mTask, imageArr.get(currentIndex).getLength() * 1000);
            currentIndex++;
            if (currentIndex==imageArr.size()){
                currentIndex = 0;
            }
        }
    }
    // 取消加载内容
    @Override
    public void unLoadContent() {

        if (hander!=null){
            hander.removeCallbacks(mTask);
        }
        if (currentImageView!=null){
            currentImageView.stopWork();
            currentImageView = null;
        }
    }


    @Override
    public void playOver(Iview playView) {
        //子组件 资源不存在 或者 子组件err -> 播放一个内容
        loadContent();
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
