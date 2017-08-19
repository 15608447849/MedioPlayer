package com.wos.play.rootdir.model_application.ui.ComponentLibrary.gallery;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.video.CVideoView;
import com.wos.play.rootdir.model_application.ui.UiFactory.UiLocalBroad;
import com.wos.play.rootdir.model_application.ui.UiHttp.UiHttpProxy;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IAdvancedComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponentUpdate;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopMonitorFiles;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopSuccessInterfaces;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_report.Report;
import com.wos.play.rootdir.model_report.ReportHelper;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallery.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallery.GalleryBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;
import com.wos.play.rootdir.model_universal.tool.MD5Util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

/**
 * Created by user on 2016/11/17.
 */

public class CGallery extends FrameLayout implements IAdvancedComponent, IComponentUpdate, LoopSuccessInterfaces {
    private static final java.lang.String TAG = "CGallery";
    private int componentId;
    private int width;
    private int height;
    private int x, y;
    private Context context;
    private AbsoluteLayout layout;
    // 布局参数
    private AbsoluteLayout.LayoutParams layoutParams;
    private String url;
    private int updateTime;

    private String mBroadAction; //组件 id+hashcode+mad5
    private UiLocalBroad broad = null;
    private boolean isInitData;
    private boolean isLayout;
    private boolean isRegisterBroad = false; //是否注册广播

    private MImageSwitcher iShow;
    private Gallery gallery;
    private FrameLayout frameLayout;
    private CVideoView cVideoView;
    private ImageSwitchFactory factory;//图片工厂 (停止使用请调用 stop)
    private GalleryAdapter adapter;

    private int backgroundAlpha;
    private String backgroundColor;
    private  String bgImageUrl;
    //private Bitmap bgImage;


    public CGallery(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }

    @Override
    public void initData(Object object) {
        ComponentsBean cb = ((ComponentsBean) object);
        this.componentId = cb.getId();
        this.mBroadAction = MD5Util.getStringMD5(this.hashCode() + "." + componentId);

        this.width = (int) cb.getWidth();
        this.height = (int) cb.getHeight();
        this.x = (int) cb.getCoordX();
        this.y = (int) cb.getCoordY();
        layoutParams = new AbsoluteLayout.LayoutParams(width, height, x, y);

        //---------------背景-----------------
        Logs.e(TAG, "BackgroundPic: --->>>" + cb.getBackgroundPic());
        this.backgroundAlpha = getAlpha(cb.getBackgroundAlpha());
        if (cb.getBackgroundPic()!=null && !cb.getBackgroundPic().equals("")){
            this.bgImageUrl = UiTools.getUrlTanslationFilename(cb.getBackgroundPic());
            if (bgImageUrl==null){
                backgroundColor = cb.getBackgroundColor();
            }
        } else {
            backgroundColor = cb.getBackgroundColor();
        }

        initSubComponent();
        if (cb.getContents() != null && cb.getContents().size() == 1) {
            createContent(cb.getContents().get(0));
        }
        this.isInitData = true;
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

   /* //加载背景
    @Override
    public void loadBg() {
        if (UiTools.fileIsExt(bgImageUrl)){
            //文件存在
            bgImage = ImageUtils.getBitmap(bgImageUrl);
            Logs.e(TAG, "是否回收：  " + bgImage.isRecycled());
        }
        if (bgImage!=null  && !bgImage.isRecycled()){
            gallery.setBackgroundDrawable(new BitmapDrawable(bgImage));
        }
    }

    //不加载背景
    @Override
    public void unloadBg() {
        if (bgImage!=null && !bgImage.isRecycled()){
            bgImage.recycle();
            bgImage = null;
        }
        gallery.setBackgroundDrawable(null);
    }*/

    //加载背景
    @Override
    public void loadBg() {
        Bitmap bitmap = ImageUtils.getBitmap(bgImageUrl);
        bitmap = ImageUtils.getTransparentBitmap(bitmap, backgroundAlpha);
        if(bitmap!=null) gallery.setBackgroundDrawable(new BitmapDrawable(bitmap));

    }

    //不加载背景
    @Override
    public void unloadBg() {
        ImageUtils.removeCache(bgImageUrl);
        gallery.setBackgroundDrawable(null);
    }

    //创建内容
    @Override
    public void createContent(Object object) {
        try {
            ContentsBean content = (ContentsBean) object;
            updateTime = content.getUpdateFreq();//更新频率
            if (content.getContentSource() != null) {
                this.url = content.getContentSource();
                translationUrl(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化 子组件
    @Override
    public void initSubComponent() {
        try {
            View root = LayoutInflater.from(context).inflate(R.layout.image_switcher_page, null);
            frameLayout = (FrameLayout) root.findViewById(R.id.frame_video);
            cVideoView = new CVideoView(context);//视频播放器
            cVideoView.setLayout(frameLayout);
            iShow = (MImageSwitcher) root.findViewById(R.id.switcher);
            initImageSwitcher();
            gallery = (Gallery) root.findViewById(R.id.gallery);
            initGallery();
            this.addView(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void broadCall() {
        Logs.i(TAG, "图集 广播 - " + mBroadAction + " - 收到,执行!");
        //更新资源文件名
        translationUrl(url);
    }

    @Override
    public void createBroad() {
        if (!isRegisterBroad) {
            broad = new UiLocalBroad(mBroadAction, this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(mBroadAction);
            context.registerReceiver(broad, filter); //只需要注册一次
            this.isRegisterBroad = true;
        }
    }

    @Override
    public void cancelBroad() {
        if (isRegisterBroad) {
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

    //初始化 图片选择器
    private void initImageSwitcher() {
        try {
            factory = new ImageSwitchFactory(context);
            iShow.setFactory(factory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //动画
        iShow.setInAnimation(AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in));
        iShow.setOutAnimation(AnimationUtils.loadAnimation(context,
                android.R.anim.fade_out));
    }

    //初始化 画廊
    private void initGallery() {
        //设置适配器
        adapter = new GalleryAdapter(context);
        gallery.setAdapter(adapter);
        if (bgImageUrl==null){ //设置背景颜色
            gallery.setBackgroundColor(getColor(backgroundColor));
        } else {
            loadBg();
        }

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

        if (frameLayout.getVisibility() == View.VISIBLE) {
            //有视频显示中
            cVideoView.pause();
            frameLayout.setVisibility(View.GONE);
        }
        try {
            if (AppsTools.isMp4Suffix(adapter.getImageName(position))) {
                if (frameLayout.getVisibility() == View.GONE) {
                    frameLayout.setVisibility(View.VISIBLE); //视频显示
                    cVideoView.setVideoPath(adapter.getImageName(position), true);
                }
            } else {
                ImageAsyLoad.loadBitmap(adapter.getBitmapString(position), (MeImageView) iShow.getCurrentImageView());
            }
            ReportHelper.onEpaperNews(context, 0 , (int) adapter.getItemId(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置属性
    @Override
    public void setAttribute() {
        this.setLayoutParams(layoutParams);
        flag_ones = true;
    }

    @Override
    public void onLayouts() {
        if (isLayout) {
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    @Override
    public void unLayouts() {
        if (isLayout) {
            layout.removeView(this);
            isLayout = false;
        }
        unloadBg();
    }

    @Override
    public void startWork() {
        try {
            if (!isInitData) {
                return;
            }
            setAttribute();
            onLayouts();
            createBroad();//创建广播
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopWork() {
        try {
            cancelBroad();//取消广播
            unLoadContent();
            unLayouts();
            LoopMonitorFiles.getInstance().clearMonitor(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*1 获取文件名
    2 获取文件内容
    3 变成图集对象
    4 获取所有文件名*/
    private void translationUrl(String contentSource) {
        String jsonContent = UiTools.urlTanslationJsonText(contentSource);
        if (jsonContent != null) {
            try {
                GalleryBean galleryBean = AppsTools.parseJsonWithGson(jsonContent, GalleryBean.class);
                if (galleryBean != null && galleryBean.getDataObjs() != null
                        && galleryBean.getDataObjs().size() > 0) {
                    getImageFilename(galleryBean.getDataObjs());
                    //发送图集信息列表到适配器
                    adapter.setDataObjsBean(galleryBean.getDataObjs());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //获取 所有文件名
    private void getImageFilename(List<DataObjsBean> listBean) {
        for (DataObjsBean data : listBean) {
            addImageName(UiTools.getUrlTanslationFilename(data.getUrl()));
        }
    }

    //添加 图片文件名
    public void addImageName(String name) {
        if (UiTools.fileIsExt(name)) {
            //资源存在
            sendGralleryAdapter(name);
        } else {
            //资源不存在
            sendLoopThread(name);
        }
    }

    //发送资源到适配器
    private void sendGralleryAdapter(final String name) {
        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                adapter.addBitmaps(name);
            }
        });
    }

    //资源不存在发送给轮询线程
    private void sendLoopThread(String name) {
        LoopMonitorFiles.getInstance().addMonitorFile(this,name);
    }

    //轮询到资源存在了
    @Override
    public void sourceExist(String filePath, boolean isFile) {  //资源存在
        sendGralleryAdapter(filePath);
    }

    private boolean flag_ones = true;
    private TimerTask timerTask = null;
    private Timer timer = null;
    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public void loadContent() {
        //开始计时器
        unLoadContent();
        if (!flag_ones) {
            UiHttpProxy.getPeoxy().update(url, mBroadAction,UiHttpProxy.GALLERY_TYPE);
        }
        flag_ones = false;
        startTimer(updateTime * 1000);
    }

    @Override
    public void unLoadContent() {
        //结束计时器
        stopTimer();//停止计时间 停止当前内容
    }

    private void startTimer(long millisecond) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                loadContent();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, millisecond);
    }
}
