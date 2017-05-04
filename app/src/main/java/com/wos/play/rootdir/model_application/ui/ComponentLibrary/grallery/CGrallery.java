package com.wos.play.rootdir.model_application.ui.ComponentLibrary.grallery;

import android.content.Context;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.video.MyVideoView;
import com.wos.play.rootdir.model_application.ui.UiFactory.UiLocalBroad;
import com.wos.play.rootdir.model_application.ui.UiHttp.UiHttpProxy;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IAdvancedComponent;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopLocalSourceThread;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopSuccessInterfaces;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallary.GallaryBean;
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

public class CGrallery extends FrameLayout implements IAdvancedComponent, LoopSuccessInterfaces {
    private static final java.lang.String TAG = "CGrallery";
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
    private boolean isRegestBroad = false; //是否注册广播
    //资源轮询线程
    private LoopLocalSourceThread loopSoureceThread;

    private MImageSwitcher ishow;
    private Gallery gallery;
    private AbsoluteLayout absLayout;
    private MyVideoView video;
    private ImagerSwitchFactory factory;//图片工厂 (停止使用请调用 stop)
    private GralleryAdapter adapter;


    public CGrallery(Context context, AbsoluteLayout layout, ComponentsBean component) {
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
        initSubComponet();
        if (cb.getContents() != null && cb.getContents().size() == 1) {
            createContent(cb.getContents().get(0));
        }
        this.isInitData = true;
    }
    //创建内容
    @Override
    public void createContent(Object object) {
        try {
            ContentsBean content = (ContentsBean) object;
            updateTime = content.getUpdateFreq();//更新频率
            if (content.getContentSource() != null) {
                this.url = content.getContentSource();
                tanslationUrl(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化 子组件
    @Override
    public void initSubComponet() {
        try {
            View root = LayoutInflater.from(context).inflate(R.layout.imageswitcherpage, null);
            absLayout = (AbsoluteLayout) root.findViewById(R.id.frame_abslayout);
            video = new MyVideoView(context);//视频播放器
            ishow = (MImageSwitcher) root.findViewById(R.id.switcher);
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
        tanslationUrl(url);
    }

    @Override
    public void createBroad() {
        if (!isRegestBroad) {
            broad = new UiLocalBroad(mBroadAction, this);
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

    //初始化 画廊
    private void initGallery() {
        //设置适配器
        adapter = new GralleryAdapter(context);
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

        if (absLayout.getVisibility() == View.VISIBLE) {
            //有视频显示中
            video.stopMyPlayer();
            absLayout.setVisibility(View.GONE);
        }
        try {
            if (AppsTools.isMp4Suffix(adapter.getImageName(position))) {
                if (absLayout.getVisibility() == View.GONE) {
                    absLayout.setVisibility(View.VISIBLE);
                    //视频显示
                    video.start(absLayout, adapter.getImageName(position), true);
                }
            } else {
//                ishow.setImageDrawable(adapter.getDrawable(position));
                ImageAsyLoad.loadBitmap(adapter.getBitmapString(position), (MeImageView) ishow.getCurrentImageView());
            }
        } catch (Exception e) {
            e.printStackTrace();
//            ishow.setImageDrawable(adapter.getDrawable(position));
        }
    }

    //设置属性
    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
        flag_ones = true;
    }

    @Override
    public void layouted() {
        if (isLayout) {
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    @Override
    public void unLayouted() {
        if (isLayout) {
            layout.removeView(this);
            isLayout = false;
        }
    }

    @Override
    public void startWork() {
        try {
            if (!isInitData) {
                return;
            }
            setAttrbute();
            layouted();
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
            unLayouted();
            unLoadContent();
            if (loopSoureceThread != null) {
                loopSoureceThread.stopLoop();
                loopSoureceThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*1 获取文件名
    2 获取文件内容
    3 变成图集对象
    4 获取所有文件名*/
    private void tanslationUrl(String contentSource) {
        String jsonContent = UiTools.urlTanslationJsonText(contentSource);
        if (jsonContent != null) {
            try {
                GallaryBean gallaryBean = AppsTools.parseJsonWithGson(jsonContent, GallaryBean.class);
                if (gallaryBean != null && gallaryBean.getDataObjs() != null && gallaryBean.getDataObjs().size() > 0) {
                    getImageFilename(gallaryBean.getDataObjs());
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
        if (loopSoureceThread == null) {
            //开启轮询线程 访问本地资源 添加到图集
            loopSoureceThread = new LoopLocalSourceThread(this);
            loopSoureceThread.startLoop();
            loopSoureceThread.start();
        }
        loopSoureceThread.addLoopSource(name);
    }

    //轮询到资源存在了
    @Override
    public void SourceExist(Object data) {
        final String filePath = (String) data;
        //资源存在
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
