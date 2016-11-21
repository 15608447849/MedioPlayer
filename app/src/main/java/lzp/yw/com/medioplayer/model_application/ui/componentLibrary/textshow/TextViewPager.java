package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.textshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponentUpdate;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IContentView;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by user on 2016/11/15.
 * 文本滑动最外层
 */
public class TextViewPager extends ViewPager implements IComponentUpdate {
    private static final java.lang.String TAG = "ViewPager";
    private TextViewPagerAdapter adapter;
    private TextViewPagerAction action;
    private Context context;
    private AbsoluteLayout layout;
    private int x,y,width,height;

    private int backgroundAlpha;
    private String backgroundColor;
    private  String bgImageUrl;
    private Bitmap bgimage;

    private ArrayList<TextScrollView> viewList = null;
    private boolean isInitData;
    private boolean isLayout;

    //添加视图
    public void addIView(TextScrollView view){
        if(viewList==null){
            viewList = new ArrayList<>();
        }
        viewList.add(view);
    }

    //返回视图集合大小
    public int getListSize(){
        if (viewList==null){
            return 0;
        }
        return viewList.size();
    }
    //返回指定的下标 对应的时长
    public int getIndexLength(int index){
        if (viewList==null){
            return 0;
        }
        return  ((IContentView)viewList.get(index)).getLength();
    }


    //构造
    public TextViewPager(Context context, AbsoluteLayout layout, ComponentsBean componentsBean) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(componentsBean);
    }


    @Override
    public void initData(Object object) {


        try {
            ComponentsBean cb = ((ComponentsBean)object);
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            this.backgroundAlpha = cb.getBackgroundAlpha();
            this.bgImageUrl = UiTools.getUrlTanslationFilename(cb.getBackgroundPic());
            if (bgImageUrl==null){
                backgroundColor = cb.getBackgroundColor();
            }
            if (cb.getContents()!=null && cb.getContents().size()>0){
                createContent(cb.getContents());
                action = new TextViewPagerAction(this);
                this.addOnPageChangeListener(action);//滑动监听
                adapter = new TextViewPagerAdapter(viewList);
                this.setAdapter(adapter); //适配器
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private AbsoluteLayout.LayoutParams layoutParams;
    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
        this.setAlpha(backgroundAlpha);
        if (bgImageUrl==null){
            //设置背景颜色
            this.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(backgroundColor)));
           }else{
            loadBg();
        }
    }

    @Override
    public void layouted() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }

    }

    @Override
    public void unLayouted() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
        unloadBg();
    }

    //加载背景
    @Override
    public void loadBg() {
        if (UiTools.fileIsExt(bgImageUrl)){
            //文件存在
            bgimage = ImageUtils.getBitmap(bgImageUrl);
        }
        if (bgimage!=null){
            this.setBackgroundDrawable(new BitmapDrawable(bgimage));
        }
    }
    //不加载背景
    @Override
    public void unloadBg() {
        if (bgimage!=null){
            bgimage.recycle();
            bgimage = null;
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
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //滑动事件
    @Override
    public void scrollTo(int x, int y) {
        if(getListSize()<=1){
            return;
        }
        super.scrollTo(x, y);
    }

    @Override
    public void createContent(Object object) {

        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            TextScrollView text = null;
            for (ContentsBean content : contents){
                text = new TextScrollView(context,content);
                addIView(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  boolean flag = true;
    private int currentIndex = 0;
    public void setCurretnIndex(int position){
        currentIndex = position;
//        flag = true;
//        loadContent();
    }
    private int direction = 0; //0 ->   ;1 <-
    @Override
    public void loadContent() {
        if (viewList != null && viewList.size() > 1) {
            int length = 5;
            unLoadContent();

            if (!flag) {
                if (direction == 0) {
                    currentIndex++;
                    if (currentIndex == viewList.size()) {
                        currentIndex = currentIndex - 2;
                        direction = 1;
                    }
                }else{
                    currentIndex--;
                    if (currentIndex < 0) {
                        currentIndex = currentIndex + 2;
                        direction = 0;
                    }
                }

                this.setCurrentItem(currentIndex);
            }
            flag = false;
            length = viewList.get(currentIndex).getLength();
            startTimer(length * 1000);
        }
    }

    @Override
    public void unLoadContent() {
        if (viewList!=null && viewList.size()>1){
            stopTimer();//停止计时间 停止当前内容
        }
    }


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
    }






}
