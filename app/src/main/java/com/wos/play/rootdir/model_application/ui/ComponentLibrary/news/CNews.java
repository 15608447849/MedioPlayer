package com.wos.play.rootdir.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import com.wos.play.rootdir.model_application.ui.UiFactory.UiLocalBroad;
import com.wos.play.rootdir.model_application.ui.UiHttp.UiHttpProxy;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IAdvancedComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponentUpdate;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopMonitorFiles;
import com.wos.play.rootdir.model_application.ui.UiThread.LoopSuccessInterfaces;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
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
 * Created by user on 2016/12/1.
 * 资讯 维护一个 listview
 */

public class CNews extends FrameLayout implements IAdvancedComponent, IComponentUpdate, LoopSuccessInterfaces {

    private static final java.lang.String TAG = "CNews";
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

    private CListView listView;
    private ListViewAdapter adapter;
    private CShowLayout showLayout;

    private int backgroundAlpha;
    private String backgroundColor;
    private String bgImageUrl;

    public CNews(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }

    //初始化数据
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
        this.backgroundAlpha = getAlpha(cb.getBackgroundAlpha());

        //---------------背景----------------
        if (cb.getBackgroundPic()!=null && !cb.getBackgroundPic().equals("")){
            this.bgImageUrl = UiTools.getUrlTanslationFilename(cb.getBackgroundPic());
            if (bgImageUrl==null){
                this.backgroundColor = cb.getBackgroundColor();
            }
        } else {
            this.backgroundColor = cb.getBackgroundColor();
        }
        initSubComponent();//初始化组件
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

    //加载背景
    @Override
    public void loadBg() {
        Bitmap bitmap = ImageUtils.getBitmap(bgImageUrl);
        if(bitmap!=null) this.setBackgroundDrawable(new BitmapDrawable(bitmap));
        this.setAlpha(backgroundAlpha/100f);

    }

    //不加载背景
    @Override
    public void unloadBg() {
        ImageUtils.removeCache(bgImageUrl);
        this.setBackgroundDrawable(null);
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

    private void tanslationUrl(String contentSource) {
        String jsonContent = UiTools.urlTanslationJsonText(contentSource);
        if (jsonContent != null) {
            try {
                GallaryBean gallaryBean = AppsTools.parseJsonWithGson(jsonContent, GallaryBean.class);
                if (gallaryBean != null && gallaryBean.getDataObjs() != null && gallaryBean.getDataObjs().size() > 0) {
                    addDataSource(gallaryBean.getDataObjs());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //添加数据源
    private void addDataSource(List<DataObjsBean> list) {
        for (DataObjsBean data : list) {
            handlerData(data);
        }
    }

    //分发数据
    private void handlerData(DataObjsBean data) {
        String key = UiTools.getUrlTanslationFilename(data.getUrl());
        String key1 = UiTools.getUrlTanslationFilename(data.getImageUrl());
        // 没有缩略图 或有缩略图且存在文件
        if (UiTools.fileIsExt(key) &&  (key1!= null == UiTools.fileIsExt(key1))) {
            //资源存在 - 生成dataBean对象
            sendListAdapter(NewsDataBeans.generateDataSource(
                    data.getFormat(),
                    data.getTitle(),
                    data.getCreatedBy(),
                    data.getUpdtimeStr(),
                    key,
                    key1,
                    data.getUrls() == null ? null : data.getUrls().split(",")));
        } else {
            //资源不存在 1 存资源 2 开始轮询线程
            sendLoopThread(NewsDataBeans.generateDataSource(
                    data.getFormat(),
                    data.getTitle(),
                    data.getCreatedBy(),
                    data.getUpdtimeStr(),
                    key,
                    key1,
                    data.getUrls() == null ? null : data.getUrls().split(",")));
        }
    }

    //有效数据源加入 适配器
    private void sendListAdapter(NewsDataBeans newsDataBeans) {
        if (adapter == null) {
            return;
        }
        adapter.addUDataBean(newsDataBeans);
    }

    //无效数据源-送入轮询机制
    private void sendLoopThread(NewsDataBeans newsDataBeans) {
        if (adapter == null) {
            return;
        }
        adapter.addNDataBean(newsDataBeans);
        //开轮询线程
        String filePath = newsDataBeans.getFilePath();
       if (filePath!=null && !UiTools.fileIsExt(filePath)){
            LoopMonitorFiles.getInstance().addMonitorFile(this,filePath);
        }
        String thumbPath = newsDataBeans.getThumbPath();
        if (thumbPath!=null && !UiTools.fileIsExt(thumbPath)){
            LoopMonitorFiles.getInstance().addMonitorFile(this,thumbPath);
        }
    }

    //初始化 组件
    @Override
    public void initSubComponent() {
        showLayout = new CShowLayout(context, this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束图层上面的内容
                //隐藏图层
                hindShowLayout();
                listView.setVisibility(View.VISIBLE);
            }
        });
        adapter = new ListViewAdapter(context);
        listView = new CListView(context);
        listView.init(this, adapter, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击list 子项
                if (showShowLayout(position)){
                    listView.setVisibility(View.GONE);
                }
            }
        });

    }


    //弹出 放大的 视图层
    private boolean showShowLayout(int position) {
        NewsDataBeans data = adapter.getUData(position);
        if (data==null) return false;
        if (showLayout.getRootView().getVisibility() == View.GONE) {
            showLayout.getRootView().setVisibility(View.VISIBLE);
            showLayout.setData(data);
        }
        return true;
    }

    //隐藏放大的视图层
    private void hindShowLayout() {
        if (showLayout.getRootView().getVisibility() == View.VISIBLE) {
            showLayout.getRootView().setVisibility(View.GONE);
        }
    }

    //设置属性
    @Override
    public void setAttribute() {
        this.setLayoutParams(layoutParams);

        if (bgImageUrl==null){
            //设置背景颜色
            this.setBackgroundColor(getColor(backgroundColor));
            //this.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(backgroundColor)));
        } else {
            loadBg();
        }
        this.setAlpha(backgroundAlpha);
    }

    //加载布局
    @Override
    public void onLayouts() {
        if (isLayout) {
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    //取消加载布局
    @Override
    public void unLayouts() {
        if (isLayout) {
            layout.removeView(this);
            isLayout = false;
        }
        unloadBg();
    }

    //开始执行
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

    //结束执行
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

    //循环线程资源存在回传
    @Override
    public void sourceExist(final String filePath, boolean isFile) {
        AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                NewsDataBeans newsDataBeans = adapter.getNta(filePath);
                if(newsDataBeans!=null){
                    sendListAdapter(newsDataBeans);
                }
            }
        });

    }

    //创建广播
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

    //取消广播
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

    //广播回调
    @Override
    public void broadCall() {
        Logs.i(TAG, " 资讯 - 广播 -  " + mBroadAction + " - 收到,执行!");
        //更新资源文件名
        tanslationUrl(url);
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
    //加载内容
    @Override
    public void loadContent() {
        //开始计时器
        unLoadContent();
        if (!flag_ones) {
            UiHttpProxy.getPeoxy().update(url, mBroadAction,UiHttpProxy.NEWS_TYPE);
        }
        flag_ones = false;
        startTimer(updateTime * 1000);
    }

    //取消加载内容
    @Override
    public void unLoadContent() {
        stopTimer();//停止计时间 停止当前内容
    }
}
