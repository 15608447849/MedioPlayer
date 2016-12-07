package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lzp.yw.com.medioplayer.model_application.ui.UiFactory.UiLocalBroad;
import lzp.yw.com.medioplayer.model_application.ui.UiHttp.UiHttpProxy;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IAdvancedComponent;
import lzp.yw.com.medioplayer.model_application.ui.UiThread.LoopLocalSourceThread;
import lzp.yw.com.medioplayer.model_application.ui.UiThread.LoopSuccessInterfaces;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;
import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.tool.MD5Util;

/**
 * Created by user on 2016/12/1.
 * 资讯 维护一个 listview
 */

public class CNews extends FrameLayout implements IAdvancedComponent, LoopSuccessInterfaces {

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
    private boolean isRegestBroad = false; //是否注册广播
    //资源轮询线程
    private LoopLocalSourceThread loopSoureceThread;


    private CListView listView;
    private ListViewAdpter adpter;
    private CshowLayout showLayout;

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
        initSubComponet();//初始化组件
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
    private void addDataSource(List<DataObjsBean> dataObjs) {
        for (DataObjsBean data : dataObjs) {
            handerData(data);
        }
    }

    //分发数据
    private void handerData(DataObjsBean data) {
        String key = UiTools.getUrlTanslationFilename(data.getUrl());
        if (UiTools.fileIsExt(key)) {
            //资源存在 - 生成dataBean对象
            sendListAdapter(NewsDataBeans.generteDataSource(
                    data.getFormat(),
                    data.getTitle(),
                    data.getCreatedBy(),
                    data.getUpdtimeStr(),
                    key,
                    data.getUrls() == null ? null : data.getUrls().split(",")));
        } else {
            //资源不存在 1 存资源 2 开始轮询线程
            sendListAdapter(NewsDataBeans.generteDataSource(
                    data.getFormat(),
                    data.getTitle(),
                    data.getCreatedBy(),
                    data.getUpdtimeStr(),
                    key,
                    data.getUrls() == null ? null : data.getUrls().split(",")));
        }
    }

    //有效数据源加入 适配器
    private void sendListAdapter(NewsDataBeans newsDataBeans) {
        if (adpter == null) {
            return;
        }
        adpter.addUdataBean(newsDataBeans);
    }

    //无效数据源-送入轮询机制
    private void sendLoopThread(NewsDataBeans newsDataBeans) {
        if (adpter == null) {
            return;
        }
        adpter.addNdataBean(newsDataBeans);
        //开轮询线程
//      ... 未实现
    }

    //初始化 组件
    @Override
    public void initSubComponet() {
        showLayout = new CshowLayout(context, this, new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 结束图层上面的内容
                //隐藏图层
                hindShowLayout();
                listView.setVisibility(View.VISIBLE);
            }
        });
        adpter = new ListViewAdpter(context);
        listView = new CListView(context);
        listView.init(this, adpter, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击list 子项
                showShowLayout(position);
                listView.setVisibility(View.GONE);
            }
        });

    }


    //弹出 放大的 视图层
    private void showShowLayout(int position) {
        if (showLayout.getRootView().getVisibility() == View.GONE) {
            showLayout.getRootView().setVisibility(View.VISIBLE);
            showLayout.setData(adpter.getUdata(position));
        }
    }

    //隐藏放大的视图层
    private void hindShowLayout() {
        if (showLayout.getRootView().getVisibility() == View.VISIBLE) {
//            showLayout.destoryData();
            showLayout.getRootView().setVisibility(View.GONE);
        }
    }

    //设置属性
    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
    }

    //加载布局
    @Override
    public void layouted() {
        if (isLayout) {
            return;
        }
        layout.addView(this);
        isLayout = true;
    }

    //取消加载布局
    @Override
    public void unLayouted() {
        if (isLayout) {
            layout.removeView(this);
            isLayout = false;
        }
    }

    //开始执行
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

    //结束执行
    @Override
    public void stopWork() {
        try {
            cancelBroad();//取消广播
            unLayouted();
            unLoadContent();
//            if (loopSoureceThread != null) {
//                loopSoureceThread.stopLoop();
//                loopSoureceThread = null;
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //循环线程资源存在回传
    @Override
    public void SourceExist(Object data) {
        //未实现
    }





    //创建广播
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

    //取消广播
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
            UiHttpProxy.getContent(url, mBroadAction);
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
