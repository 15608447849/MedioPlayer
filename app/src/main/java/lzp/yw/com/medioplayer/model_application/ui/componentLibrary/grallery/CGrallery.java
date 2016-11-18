package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.grallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.ui.UiHttp.UiHttpProxy;
import lzp.yw.com.medioplayer.model_application.ui.UiHttp.UiHttpResult;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IAdvancedComponent;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;

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

    private boolean isInitData;
    private boolean isLayout;
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
    @Override
    public void initSubComponet() {
        if (bitmapList==null){
            return;
        }
        try {
            View root = LayoutInflater.from(context).inflate(R.layout.imageswitcherpage,null);
            ishow = (ImageSwitcher) root.findViewById(R.id.switcher);
            initImageSwitcher();
            gallery = (Gallery)root.findViewById(R.id.gallery);
            initGallery();
            this.addView(root);
        } catch (Exception e) {
            e.printStackTrace();
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
        adapter.tansBitmapToDraw(bitmapList);
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ishow.setImageDrawable(adapter.getDrawable(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void setAttrbute() {
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
        this.setBackgroundColor(Color.GREEN);
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
            loadContent();
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
    @Override
    public void createContent(Object object) {

        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            for (ContentsBean content : contents){
                if (content.getContentSource()!=null ){
                    this.url = content.getContentSource();
                    tanslationUrl(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private UiHttpResult call = new UiHttpResult() {
        @Override
        public Void HttpResultCall() {
            System.err.println("chen - gong");
            return null;
        }
    };

    @Override
    public void loadContent() {
        //开始计时器
        System.err.println("-**********-*************-***********-*************-**************-***************");
        UiHttpProxy.getContent(url,call);
    }

    @Override
    public void unLoadContent() {
    //结束计时器
    }


    private ArrayList<String> imageNameArr = null;
    //添加 图片文件名
    public void addImageName(String name){
        if (imageNameArr == null){
            imageNameArr = new ArrayList<>();
        }
        if (!imageNameArr.contains(name)){
            imageNameArr.add(name);
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
                addImageName(UiTools.getUrlTanslationFilename(data.getUrl()));
            }
        getBitmpArr();//获取所有bitmap
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
    /**
     * bitmap 数组
     */
    private void getBitmpArr(){
        if (imageNameArr!=null && imageNameArr.size()>0){
            Bitmap bitmap = null;
            for (String imagePath :imageNameArr){
                if (UiTools.fileIsExt(imagePath)){
                    bitmap = ImageUtils.getBitmap(imagePath);
                }
                if (bitmap!=null){
                    addBitmaps(bitmap);
                }
            }
        }
    }


}
