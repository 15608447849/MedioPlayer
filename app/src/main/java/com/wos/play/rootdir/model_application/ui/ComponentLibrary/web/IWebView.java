package com.wos.play.rootdir.model_application.ui.ComponentLibrary.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponentUpdate;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by user on 2016/11/17.
 */

public class IWebView extends WebView implements IComponentUpdate {
    private static final java.lang.String TAG = "IWebView";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private String url;
    private boolean isInitData;
    private boolean isLayout;
    private boolean isLink;
    private String preUrl;
    private String backgroundColor;
    private  String bgImageUrl;
    private Bitmap bgimage;
    private  String domian;
    public IWebView(Context context, AbsoluteLayout layout, ComponentsBean component) {
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

            this.bgImageUrl = UiTools.getUrlTanslationFilename(cb.getBackgroundPic());
            if (bgImageUrl==null){
                backgroundColor = cb.getBackgroundColor();
            }

            if (cb.getContents()!=null && cb.getContents().size()==1) {
                if (cb.getContents().get(0).getHtmlType().equals("url")){
                    this.url=cb.getContents().get(0).getUrl();
                }else{
                    this.url=cb.getContents().get(0).getContentSource(); //本地
                }
            }else {
                this.url = "www.baidu.com";
            }
            this.url  = url.startsWith("http")?url:"http://" + url;
            domian = url.substring(url.indexOf("/")+2);
           if (domian.contains("/")){
               domian =  domian.substring(0,domian.indexOf("/"));
           }
            if (domian.contains("www.")){
                domian =  domian.substring(4);
            }
            Log.i(TAG,"加载外链:"+isLink+" 域名:"+domian);
            isLink = cb.getContents().get(0).isOutsideChain();
            this.getSettings().setDefaultTextEncodingName("UTF-8");//编码
            this.getSettings().setJavaScriptEnabled(true);//js
//            this.getSettings().setPluginState(WebSettings.PluginState.ON);//flash 有关系
            this.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.i(TAG,"链接:"+url);
                    if (!isLink && !url.contains(domian)) {
                        loadUrl(preUrl);
                        return true;
                    }
                    preUrl = url;
                    return super.shouldOverrideUrlLoading(view, url);
                }
            });
            this.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        // 加载完成
                        Logs.i(TAG, "页面加载完成");
                    } else {
                        // 加载进度
                        //Logs.i(TAG, "页面加载中..." + newProgress);
                    }
                }
            });
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setAttrbute() {
        try {
            this.setLayoutParams(layoutParams);
            if (bgImageUrl==null){
                //设置背景颜色
                if (backgroundColor!=null){
                    this.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(backgroundColor)));
                }
            }else{
                loadBg();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
//            this.destroy();
            unLoadContent();
            unLayouted(); //移除布局

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void unloadBg() {
        if (bgimage!=null){
//            bgimage.recycle();
            bgimage = null;
        }
    }

    @Override
    public void createContent(Object object) {
        //
    }

    @Override
    public void loadContent() {
        Logs.i(TAG,"网页 - URL :[ "+ url+" ]");

        this.loadUrl(url);
    }

    @Override
    public void unLoadContent() {
        //
    }
}
