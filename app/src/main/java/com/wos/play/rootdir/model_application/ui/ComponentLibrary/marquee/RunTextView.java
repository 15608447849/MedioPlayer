package com.wos.play.rootdir.model_application.ui.ComponentLibrary.marquee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wos.play.rootdir.model_application.ui.UiInterfaces.RunTextInterface;


/**
 * Created by Administrator on 2016/11/24.
 * SurfaceView的使用
 * 首先继承SurfaceView，并实现SurfaceHolder.Callback接口，实现它的三个方法：surfaceCreated，surfaceChanged，surfaceDestroyed。
 * <p/>
 * <p/>
 * surfaceCreated(SurfaceHolder holder)：surface创建的时候调用，一般在该方法中启动绘图的线程。
 * surfaceChanged(SurfaceHolder holder, int format, int width,int height)：surface尺寸发生改变的时候调用，如横竖屏切换。
 * surfaceDestroyed(SurfaceHolder holder) ：surface被销毁的时候调用，如退出游戏画面，一般在该方法中停止绘图线程。
 * 还需要获得SurfaceHolder，并添加回调函数，这样这三个方法才会执行。
 */

public class RunTextView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "SurfaceText";
    //////////////////////////////////////////属性
    private RunTextInterface interImp;

    private Context mContext = null;
    /**
     * 是否滚动
     */
    private boolean isMove = false;
    /**
     * 移动方向
     */
    private int orientation = MOVE_RIGHT;
    /**
     * 向左
     */
    public static final int MOVE_LEFT = 0;
    /**
     * 向右
     */
    public static final int MOVE_RIGHT = 1;
    /**
     * 移动速度 1.5s 一次
     */
    private long speed = 1500;
    /**
     * 字幕内容
     */
    private String content = "暂无内容";
    /**
     * 字幕背景色
     */
    private String bgColor = "#E7E7E7";
    /**
     * 背景透明度
     */
    private int bgalpha = 60;
    /**
     * 字体颜色
     */
    private String fontColor = "#000000";//"#FFFFFF";
    /**
     * 字体透明度
     * 0-255 透明->不透明
     */
    private int fontAlpha = 255;
    /**
     * 字体大小20
     */
    private float fontSize = 20f;
    /**
     * 字体类型
     * serif 是有衬线字体，意思是在字的笔画开始、结束的地方有额外的装饰，而且笔画的粗细会有所不同。
     * sans serif 就没有这些额外的装饰，而且笔画的粗细差不多
     * monospace
     */
    private Typeface fontType = null;
    /**
     * 字体 风格
     * BOLD 加粗
     * BOLD_ITALIC 倾斜加粗
     * ITALIC  倾斜
     * NORMAL 正常
     */
    private int fontStyle = Typeface.BOLD;
    /**
     * 容器
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * 线程控制 循环绘制
     */
    private boolean loop = false;
    /**
     * 内容滚动位置
     * 起始坐标
     */
    private float x = 0;

    /**
     * 构造 1
     *
     * @param context
     */
    public RunTextView(Context context) {
      this(context,null);
    }
    public RunTextView(Context context,String fontType) {
        super(context);
        this.mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        //设置画布背景不为黑色　继承Sureface时这样处理才能透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);//透明 前景
        setZOrderOnTop(true);//Order 顺序
        setTypeFace(fontType);
    }
    /**
     * -----------------------------------------------------------overriee-----------------------------------------------------------------------------------------------------
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surface created (): [" + this.getHeight()+","+this.getWidth()+"]");
        if (isMove) {//滚动效果
            if (orientation == MOVE_LEFT) {//向左
                x = getWidth();
            } else {//向右
                x = -(content.length() * 10);
            }
        } else {//不滚动只画一次
            mExecute();
        }
        startHelper();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged() 源宽高: [" + this.getWidth()+" - "+this.getHeight()+"] \n改变宽高 -" +width +" "+height);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed()");
        loop = false;
    }

    @Override
    public void run() {
        while (loop) {
            try {
                mExecute();
                Thread.sleep(speed);//休眠多少毫秒
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public synchronized void mExecute(){
        try {
            draw();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -----------------------------------------------------------overriee-----------------------------------------------------------------------------------------------------
     * <p/>
     * /* *
     * 绘制
     */
    private void draw() {
        //锁定画布
        if (getHolder()==null) return;
        Canvas canvas = getHolder().lockCanvas();

        if (canvas == null) {
            Log.e(TAG, "draw() : mSurfaceHolder or camvas - 不存在 - " + mSurfaceHolder+" - "+ canvas);
            return;
        }
        Paint paint = new Paint();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);   //清屏
        paint.setAntiAlias(true);//锯齿

        paint.setTypeface(fontType);//字体Typeface.SANS_SERIF

        paint.setTextSize(fontSize);//字体大小

        paint.setColor(Color.parseColor(fontColor));//字体颜色

        paint.setAlpha(fontAlpha);//字体透明度
        canvas.drawText(content, x, (getHeight() / 2 + 5), paint);//画文字

        mSurfaceHolder.unlockCanvasAndPost(canvas);//解锁显示
        //滚动
        if (isMove) {
            //内容所占像素
            float conlen = paint.measureText(content);
            //组件宽度
            int w = getWidth();
            //方向
            if (orientation == MOVE_LEFT) {//向左
                if (x < -conlen) {
                    x = w;
                    call();
                } else {
                    x -= 2*5;
                }

            } else if (orientation == MOVE_RIGHT) {//右边
                if (x >= w) {
                    x = -conlen;
                    call();
                } else {
                    x += 2*5;
                }
            }
        }
    }
    // 回调
    private void call(){
        if (interImp!=null){
            interImp.textEnd(this);
        }
    }



    //get - set-----------------------------------------------------------------------------------------

    public void setInterImp(RunTextInterface interImp) {
        this.interImp = interImp;
    }
    public void setMove(boolean isMove) {
        this.isMove = isMove;
    }
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    public void setContent(String content) {
        this.content = content;
    }
    /**
     * #ffffff 白色
     * #000000 黑色
     * @param bgColor
     */
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
        //背景颜色
        setBackgroundColor(Color.parseColor(bgColor));
    }
    //数值
    public void setBgalpha(int bgalpha) {
        this.bgalpha = bgalpha;
        //背景透明度
        getBackground().setAlpha(bgalpha);
    }
    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }
    public void setFontAlpha(int fontAlpha) {
        this.fontAlpha = fontAlpha;
    }
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
    public void setSpeed(long speed) {
        this.speed = speed;
    }
    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }
    public void setTypeFace(String fonttype) {
        try {
            String FONTTYPE = fonttype == null ? "华文行楷.ttf" : fonttype+".ttf";
            fontType = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + FONTTYPE);//Typeface.MONOSPACE;
            fontType = Typeface.create(fontType, fontStyle);
        } catch (Exception e) {
            fontType = Typeface.SERIF;
           e.printStackTrace();
        }
    }
    //-----------------------------------------------
    private Thread helper = null;

    public void startHelper(){
        stopHelper();
        if (helper==null){
            helper = new Thread(this);
            loop = true;
            helper.start();
        }
    }
    public void stopHelper(){
        if (helper!=null){
            loop = false;
            helper.interrupt();
            helper = null;
        }
    }



}
