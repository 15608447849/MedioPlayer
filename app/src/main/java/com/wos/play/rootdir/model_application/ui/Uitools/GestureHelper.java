package com.wos.play.rootdir.model_application.ui.Uitools;

import android.view.MotionEvent;
import android.view.View;

import com.wos.play.rootdir.model_universal.tool.Logs;

/**
 * Created by Administrator on 2017/5/18.
 */

public class GestureHelper {
    private static final java.lang.String TAG = GestureHelper.class.getSimpleName();
    private static final float INTERVAL =  50f; // 滑动的最小距离
    private boolean isUpDown;
    private float lastX=0f, lastY=0f;           // 记录触摸位置
    private OnSlidingListener onSlidingListener;

    /**
     * 上下左右滑动构造方法
     */
    public GestureHelper(String flag, OnSlidingListener onSlidingListener) {
        this.isUpDown = "upDown".equals(flag);
        this.onSlidingListener = onSlidingListener;
        if(onSlidingListener instanceof View){
            initView((View)onSlidingListener);
        }
    }

    private void  initView(View view){
        view.setClickable(true);
    }

    /**
     * 上下左右滑动监听
     */
    public interface OnSlidingListener{
        void onUpOrLeft();      //向上滑或向左滑
        void onDownOrRight();   //向下滑或向右滑
    }

    public void onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {  //当手指按下的时候
            lastX = event.getX();
            lastY = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {    //当手指离开的时候
            execSliding(event.getX() - lastX, event.getY() - lastY);
        }
    }

    private void execSliding(float x, float y) {
        if(onSlidingListener != null){
            if(isUpDown && y > INTERVAL ){
                onSlidingListener.onDownOrRight();
            }else if(isUpDown && y < -INTERVAL ){
                onSlidingListener.onUpOrLeft();
            }else if(!isUpDown && x > INTERVAL ){
                onSlidingListener.onDownOrRight();
            }else if(!isUpDown && x < -INTERVAL ){
                onSlidingListener.onUpOrLeft();
            }else{
                Logs.d(TAG, "未定义");
            }
        }

    }

}
