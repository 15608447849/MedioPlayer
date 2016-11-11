package lzp.yw.com.medioplayer.model_application.ui.componentLibrary;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by user on 2016/11/11.
 * 播放图片的组件 - 多图片播放
 */
public class CMorePictures extends FrameLayout implements Iview{

    private static final java.lang.String TAG = "CMorePictures";

    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private int linkId;
    private Context context;
    private AbsoluteLayout layout;

    private boolean isInitData;
    private boolean isLayout;
    private boolean isAttr;
    public CMorePictures(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        initData(component);
        this.layout = layout;
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
            this.linkId = (int)cb.getLinkId();
            if (cb.isHasContent()){
                createContent(cb.getContents());
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void setInitSuccess(boolean flag) {
    }
    @Override
    public boolean isInitData() {
        return false;
    }
    @Override
    public void setAttrbuteSuccess(boolean flag) {
    }
    @Override
    public boolean isSetAttrbute() {
        return false;
    }
    @Override
    public void setAttrbute() {
        if (!isAttr){
            this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
            this.setBackgroundColor(Color.BLUE);
        }
    }

    @Override
    public void layouted() {
        if (isLayout){
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    @Override
    public void unLayouted() {
        if (isLayout = false){
            layout.removeView(this);
            isLayout = false;
        }
    }

    @Override
    public void startWork() {
        try {
            if (!isInitData){
                return;
            }
            setAttrbute();
            layouted();
            loadsource();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            unLayouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//--------------------------------------------------//
    private ArrayList<CImageView> imageArr = null;

    //添加 图片组件
    private void addImage(CImageView imageview){
        if (imageArr==null){
            imageArr = new ArrayList<>();
        }
        imageArr.add(imageview);
    }



    //创建 内容
    private void createContent(List<ContentsBean> contents) {
        CImageView imageview = null;
        //只有图片内容
        for (ContentsBean content : contents){
        imageview = new CImageView(context,this,content);
        addImage(imageview);
        }
    }


    private int currentIndex = 0;
    private CImageView currentImageView= null;
    private void loadsource() {

        if (imageArr.size()>0){
            Log.i("","loadsource 组件()");
            if (currentImageView!=null){
                currentImageView.stopWork();
                currentImageView = null;
            }
            currentImageView =  imageArr.get(currentIndex);
            currentImageView.startWork();
            //创建计时器
            startTimer(imageArr.get(currentIndex).getLength() * 1000);
            currentIndex++;
            if (currentIndex==imageArr.size()){
                currentIndex = 0;
            }
        }

    }







    private TimerTask timerTask= null;
    private Timer timer = null;

    public void stopTimer(){
        if (timerTask!=null){
            timerTask.cancel();
            timerTask = null;
        }
        if (timer!=null){
            timer.cancel();
            timer = null;
        }

    }

    public void startTimer(long millisecond){
        stopTimer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        loadsource();
                    }
                });

            }
        };
        timer = new Timer();
        timer.schedule(timerTask,millisecond);
    }



}
