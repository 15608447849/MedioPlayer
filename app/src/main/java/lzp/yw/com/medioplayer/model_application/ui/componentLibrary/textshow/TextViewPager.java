package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.textshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponentUpdate;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IContentView;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/15.
 * 文本滑动最外层
 */
public class TextViewPager extends ViewPager implements IComponentUpdate {
    private TextViewPagerAdapter adapter;
    private TextViewPagerAction action;
    private Context context;
    private AbsoluteLayout layout;
    private int x,y,width,height;

    private int backgroundAlpha;
    private String backgroundColor;
    private  String bgImageUrl;
    private Bitmap bgimage;

    private ArrayList<View> viewList = null;
    private boolean isInitData;
    private boolean isLayout;

    //添加视图
    public void addIView(View view){
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

            this.backgroundAlpha = cb.getBackgroundAlpha();
            this.bgImageUrl = UiTools.getUrlTanslationFilename(cb.getBackgroundPic());
            if (bgImageUrl==null){
                backgroundColor = cb.getBackgroundColor();
            }
            if (cb.getContents()!=null && cb.getContents().size()>0){
                createContent(cb.getContents());
                adapter = new TextViewPagerAdapter(viewList);
                this.setAdapter(adapter); //适配器
                action = new TextViewPagerAction();
                this.addOnPageChangeListener(action);//滑动监听
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setAttrbute() {
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
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
            //只有图片内容
            for (ContentsBean content : contents){
                text = new TextScrollView(context,content);
                addIView((View)text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void loadContent() {

    }

    @Override
    public void unLoadContent() {

    }

}
