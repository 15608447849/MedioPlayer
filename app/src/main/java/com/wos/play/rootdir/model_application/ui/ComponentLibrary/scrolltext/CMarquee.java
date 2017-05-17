package com.wos.play.rootdir.model_application.ui.ComponentLibrary.scrolltext;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/24.
 */

public class CMarquee extends ViewPager implements IComponent{

    private static final java.lang.String TAG = "CMarquee";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private boolean isInitData;
    private boolean isLayout;

    private MarqueeAdpter adapter;
    private MarqueeAction action;
    private ArrayList<RunTextView> viewList = null;
    private void addSubView(RunTextView view){
        if (viewList==null){
            viewList = new ArrayList<>();
        }
        viewList.add(view);
    }

    //滑动事件
    @Override
    public void scrollTo(int x, int y) {
        if(viewList.size()<=1){
            return;
        }
        super.scrollTo(x, y);
    }
    public CMarquee(Context context, AbsoluteLayout layout,ComponentsBean component) {
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
            if (cb.getContents()!=null && cb.getContents().size()>0) {
                createContent(cb.getContents());
                action = new MarqueeAction();
                this.addOnPageChangeListener(action);//滑动监听
                adapter = new MarqueeAdpter(viewList);
                this.setAdapter(adapter); //适配器
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void createContent(Object object) {
        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            RunTextView text = null;
            for (ContentsBean content : contents){
                text = new RunTextView(context);
                //设置
                text.setBgColor(content.getBackgroundColor());
                text.setBgalpha(content.getBackgroundAlpha());
                text.setMove(true);
                text.setLoop(true);
                text.setFontColor(content.getFontColor());
                text.setFontAlpha(100);
                text.setFontSize(20);
                text.setContent("<<"+content.getContentName()+">>  "+content.getContentSource());
                text.setOrientation(RunTextView.MOVE_LEFT);
                text.setSpeed(content.getRollingSpeed());
                text.setFontStyle( Typeface.BOLD);
                text.setTypeFace("幼圆");
                text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                addSubView(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setAttribute() {
        this.setLayoutParams(layoutParams);
    }
    @Override
    public void onLayouts() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }
    @Override
    public void unLayouts() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    @Override
    public void loadContent() {
    }
    @Override
    public void unLoadContent() {
    }
    @Override
    public void startWork() {
        try {
            if (!isInitData){
                return;
            }
            setAttribute();
            onLayouts();
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouts(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
