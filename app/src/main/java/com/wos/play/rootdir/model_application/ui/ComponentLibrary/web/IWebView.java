package com.wos.play.rootdir.model_application.ui.ComponentLibrary.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponentUpdate;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import cn.trinea.android.common.util.FileUtils;

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
                if (cb.getContents().get(0).getHtmlType().equals("url")){ //网络
                    this.url=cb.getContents().get(0).getUrl();
                    this.url  = url.startsWith("http")?url:"http://" + url;
                    isLink = cb.getContents().get(0).isOutsideChain();
                    domian = AppsTools.getDomian(url);
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
                }

                if (cb.getContents().get(0).getHtmlType().equals("local")){
                    //本地静态页面
                  String file =  UiTools.getUrlTanslationFilename(cb.getContents().get(0).getContentSource());
                    if (FileUtils.isFileExist(file)){
                        try {
                            file =  UiTools.unZipFiles(file,file.substring(0,file.lastIndexOf("."))+"_unzip",false);//解压缩

                        } catch (Exception e) {
                            file = null;
                        }
                        if (file!=null){
                            url = "file://"+file+"/index.html";   //获取压缩的文件路径
                        }
                    }else{
                        Log.i(TAG,"文件不存在");
                        url = "about:blank";
                    }
                    this.setWebViewClient(new WebViewClient());
                }
            }else {
                this.url = "about:blank";
            }
            WebSettings webSettings = this.getSettings();
            webSettings.setDefaultTextEncodingName("UTF-8");//编码
            webSettings.setJavaScriptEnabled(true);//js
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
            //页面支持缩放：
            webSettings.setJavaScriptEnabled(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setSupportZoom(true);
            //设置此属性，可任意比例缩放
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);

            webSettings.setAllowFileAccess(true);  //设置可以访问文件
            webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存

            this.setWebChromeClient(new WebChromeClient());
            this.requestFocusFromTouch();//支持获取手势焦点
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
