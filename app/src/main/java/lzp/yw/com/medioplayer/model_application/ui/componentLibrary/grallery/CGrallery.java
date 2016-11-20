package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.grallery;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageSwitcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.ui.UiFactory.UiLocalBroad;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IAdvancedComponent;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_application.ui.componentLibrary.video.MyVideoView;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.tool.MD5Util;

/**
 * Created by user on 2016/11/17.
 */

public class CGrallery extends FrameLayout implements IAdvancedComponent {
    private static final java.lang.String TAG = "CGrallery";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private String url;
    private int updateTime;

    private String mBroadAction; //组件 id+hashcode+mad5
    private UiLocalBroad broad = null;
    private boolean isInitData;
    private boolean isLayout;
    private boolean isRegestBroad = false; //是否注册广播
    public CGrallery(Context context,AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }


    @Override
    public void initData(Object object) {
        ComponentsBean cb = ((ComponentsBean)object);
        this.componentId = cb.getId();
        this.mBroadAction = MD5Util.getStringMD5(this.hashCode()+"."+componentId);

        this.width = (int)cb.getWidth();
        this.height = (int)cb.getHeight();
        this.x = (int)cb.getCoordX();
        this.y = (int)cb.getCoordY();
        if (cb.getContents()!=null && cb.getContents().size()==1) {
            createContent(cb.getContents());
        }
        initSubComponet();
        this.isInitData = true;
    }

    private ImageSwitcher ishow;
    private Gallery gallery;
    private AbsoluteLayout absLayout;
    private MyVideoView video;
    @Override
    public void initSubComponet() {
        if (bitmapList==null){
            return;
        }
        try {
            View root = LayoutInflater.from(context).inflate(R.layout.imageswitcherpage,null);
            absLayout = (AbsoluteLayout) root.findViewById(R.id.frame_abslayout);
            video = new MyVideoView(context);//视频播放器
            ishow = (ImageSwitcher) root.findViewById(R.id.switcher);
            initImageSwitcher();
            gallery = (Gallery)root.findViewById(R.id.gallery);
            initGallery();
            this.addView(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void createBroad() {

        if (!isRegestBroad){
            broad = new UiLocalBroad(mBroadAction,this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(mBroadAction);
            context.registerReceiver(broad, filter); //只需要注册一次
            this.isRegestBroad = true;
        }
    }

    @Override
    public void cancelBroad() {
        if (isRegestBroad) {
            //取消注册
            if (broad != null) {
                try {
                    context.unregisterReceiver(broad);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                broad = null;
            }
        }
    }

    @Override
    public void broadCall() {
        Logs.i(TAG," 广播 - "+mBroadAction+" - 收到,执行!");
        //更新资源文件名
        tanslationUrl(url);
        if (AppsTools.checkUiThread()){
            //更新适配器
            if (adapter!=null){
                adapter.settingBitmaps(bitmapList);
            }

        }
    }
    private ImagerSwitchFactory factory ;//图片工厂 (停止使用请调用 stop)
    //初始化 图片选择器
    private void initImageSwitcher() {
        try {
            factory = new ImagerSwitchFactory(context);
            ishow.setFactory(factory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //动画
        ishow.setInAnimation(AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in));
        ishow.setOutAnimation(AnimationUtils.loadAnimation(context,
                android.R.anim.fade_out));
    }
    private GralleryAdapter adapter ;
    //初始化 画廊
    private void initGallery() {
        //设置适配器
        adapter = new GralleryAdapter(context);
        adapter.settingBitmaps(bitmapList);
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectItem(position);
                justVideos(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //判断是视频素材还是图片素材
    private void justVideos(int position) {
        if (absLayout.getVisibility() == View.VISIBLE){
            //有视频显示中
//                    video.start(layout,videoPath,false);
            video.stopMyPlayer();
            absLayout.setVisibility(View.GONE);
        }
        if (AppsTools.isMp4Suffix(imageNameArr.get(position))){
            if (absLayout.getVisibility() == View.GONE){
                absLayout.setVisibility(View.VISIBLE);
                //视频显示
                video.start(absLayout,imageNameArr.get(position),true);
            }
        }else {
            ishow.setImageDrawable(adapter.getDrawable(position));
        }
    }

    @Override
    public void setAttrbute() {
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
//        this.setBackgroundColor(Color.GREEN);
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
        if (isLayout){
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
            createBroad();
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            unLoadContent();
            cancelBroad();
            unLayouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void createContent(Object object) {
        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            for (ContentsBean content : contents){
                updateTime = content.getUpdateFreq();
                if (content.getContentSource()!=null ){
                    this.url = content.getContentSource();
                    tanslationUrl(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> imageNameArr = null;
    //添加 图片文件名
    public void addImageName(String name){
        if (imageNameArr == null){
            imageNameArr = new ArrayList<>();
        }
        if (!imageNameArr.contains(name)){
            imageNameArr.add(name);
            if (AppsTools.isMp4Suffix(name)){
                name = AppsTools.tanslationMp4ToPng(name);
            }
            if (UiTools.fileIsExt(name)){
                Bitmap bitmap = ImageUtils.getBitmap(name);
                if (bitmap!=null){
                    addBitmaps(bitmap);
                }
            }

        }
    }
    /*1 获取文件名
    2 获取文件内容
    3 变成图集对象
    4 获取所有文件名*/
    private void tanslationUrl(String contentSource) {
      String jsonContent = UiTools.fileTanslationObject(contentSource);
       if (jsonContent!=null){
           GallaryBean gallaryBean = new GallaryBean();
           try {
               Gson gson=new Gson();
               Type type = new TypeToken<GallaryBean>(){}.getType();
               gallaryBean=gson.fromJson(jsonContent, type);
           }catch (Exception e){
               e.printStackTrace();
           }
           if (gallaryBean!=null && gallaryBean.getDataObjs()!=null && gallaryBean.getDataObjs().size()>0){

               getImageFilename(gallaryBean.getDataObjs());
           }
       }
    }

    //获取 所有文件名
    private void getImageFilename(List<DataObjsBean> listBean) {
            for (DataObjsBean data : listBean){
                addImageName(UiTools.getUrlTanslationFilename(data.getUrl()));//同时生成bitmap
            }
    }

    /**
     * bitmap list
     */
    private ArrayList<Bitmap> bitmapList = null;
    //添加bitmap
    private  void  addBitmaps(Bitmap bitmap){
        if (bitmapList==null){
            bitmapList= new ArrayList<>();
        }
            bitmapList.add(bitmap);
    }
    //清理bitmap
    private void removeBitmaps(){
        if (bitmapList!=null){
            for (Bitmap bitmap :bitmapList){
                bitmap.recycle();
            }
            bitmapList.clear();
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



    @Override
    public void loadContent() {
        //开始计时器
        unLoadContent();
        //UiHttpProxy.getContent(url,mBroadAction);
        startTimer(updateTime*1000);
    }

    @Override
    public void unLoadContent() {
        //结束计时器
        stopTimer();//停止计时间 停止当前内容
    }

    private void startTimer(long millisecond){
        timerTask = new TimerTask() {
            @Override
            public void run() {
               loadContent();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,millisecond);
    }





}
