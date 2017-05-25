package com.wos.play.rootdir.model_application.ui.ComponentLibrary.marquee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.view.MotionEvent;
import android.widget.AbsoluteLayout;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IComponent;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.List;

/**
 * Created by leolaurel.e.l on 2017/5/16.
 * 简单实现跑马灯效果
 */

public class CScrollTextView extends AppCompatTextView implements IComponent {

    public final static String TAG = CScrollTextView.class.getSimpleName();
    private static final float INTERVAL =  50f; // 滑动的最小距离
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private int count,width,height,x,y,currCount,currIndex;
    private boolean isInitData,isLayout,isUpDown,isStarting;//是否开始滚动
    private float textLength, textX = 0f, textY = 0f;   //文本长度/文字的横坐标/文字的纵坐标
    private float offset = 1.0f, lastX=0f, lastY=0f; // 记录触摸位置
    private Paint paint = new Paint();//绘图样式
    private String text = "默认展示";//文本内容
    private List<ContentsBean> contents;
    private int alpha = Color.TRANSPARENT, background = Color.TRANSPARENT;


    public CScrollTextView(Context context, AbsoluteLayout layout,ComponentsBean component) {
        super(context);
        this.isStarting = true;
        this.context = context;
        this.layout = layout;
        initData(component);
        setClickable(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        bringToFront();
        canvas.drawColor(background);
        canvas.drawText(text, textX, textY, paint);
        if(!isStarting) return;
        if((textX -= offset) +textLength < 0) nextContent();
        invalidate();
    }



//    public void startScroll(){
//        isStarting = true;
//        invalidate();
//    }
//
//    public void stopScroll(){
//        isStarting = false;
//        invalidate();
//    }

//    @Override
//    public void onClick(View v) {
//        Logs.d(TAG, "跑马灯点击");
//        if(isStarting) stopScroll();
//        else startScroll();
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {//当手指按下的时候
            lastX = event.getX();
            lastY = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {//当手指离开的时候
            float x2 = event.getX() - lastX;
            float y2 = event.getY() - lastY;
            if(isUpDown && y2 > INTERVAL ){
                nextContent(true);
            }else if(isUpDown && y2 < -INTERVAL ){
                prevContent();
            }else if(!isUpDown && x2 > INTERVAL ){
                nextContent(true);
            }else if(!isUpDown && x2 < -INTERVAL ){
                prevContent();
            }else{
                Logs.d(TAG, "未定义");
            }
        }
        return super.onTouchEvent(event);
    }
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
            this.isUpDown = "upDown".equals(cb.getTransition());
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            if (cb.getContents()!=null && cb.getContents().size()>0) {
                createContent(cb.getContents());
            }
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setAttribute() {
        this.setLayoutParams(layoutParams);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createContent(Object object) {
        try {
            contents = (List<ContentsBean>)object;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放下一条跑马灯（优先执行本条次数,次数满了跳转下一条）
     */
    private void nextContent(){
        nextContent(false);
    }

    /**
     * 播放下一条跑马灯
     * @param isForce  是否强制跳转下一条
     */
    private void nextContent(boolean isForce){
        if(!isForce && ++currCount < count){
            textX = width;
            return;
        }
        if(++currIndex >= contents.size()) currIndex = 0;
        showContent(contents.get(currIndex));
    }

    /**
     * 播放下一条跑马灯（执行本条次数）
     */
    private void prevContent(){
        if(--currIndex < 0) currIndex = contents.size() - 1;
        showContent(contents.get(currIndex));
    }


    private void showContent(ContentsBean content) {
        currCount = 0;
        // 初始化控件参数
        setTypeface(getTypeFace("幼圆"));
        alpha = getAlpha(content.getBackgroundAlpha());
        background = getColor(content.getBackgroundColor());
        // 初始化画笔参数
        if(paint == null) paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.parseColor(content.getFontColor()));

        // 初始化基本参数
        text = content.getContentSource();          // 滚动的文本
        count = content.getRollingTimes();          // 滚动的次数
        offset = content.getRollingSpeed() / 100f;  // 滚动的速度
        textLength  = paint.measureText(text);
        Paint.FontMetrics fm = paint.getFontMetrics();
        textY = (height - fm.descent + fm.ascent)/2 + fm.leading - fm.ascent;
        textX = width;
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
                color |= ( alpha << 24 ); //color |=  0x0000000000000000;
                return (int) color;
            }
            return (int) color;
        }
        return Color.TRANSPARENT;
    }
    /**
     * 获取字体
     */
    public Typeface getTypeFace(String fontType) {
        Typeface typeface;
        try {
            String fileName = fontType == null ? "华文行楷.ttf" : fontType+".ttf";
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fileName);
            typeface = Typeface.create(typeface, Typeface.BOLD);
        } catch (Exception e) {
            typeface = Typeface.SERIF;
            e.printStackTrace();
        }
        return typeface;
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
    public void loadContent() {
        showContent(contents.get(currIndex));
    }


    @Override
    public void unLoadContent() {
        currIndex = 0;
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
