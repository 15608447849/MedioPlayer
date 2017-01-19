package com.wos.play.rootdir.model_application.ui.ComponentLibrary.stream_medio;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_universal.tool.Logs;

import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.util.VLCInstance;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2017/1/19.
 */

public class MVlcs extends SurfaceView implements SurfaceHolder.Callback, IVideoPlayer {
    private static final String TAG = "MVlcs";
    private BaseActivity activity;
    private String uri ;
    public MVlcs(Context context, String path) {  // private String path; //"http://222.36.5.53:9800/live/xktv.m3u8";//"http://218.89.69.211:8088/streamer/yb01/yb01-500.m3u8";
       super(context);
        uri = (path == null || path.equals("")) ? "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8" : path;

        if (context!=null && context instanceof BaseActivity) {
            this.activity = (BaseActivity) context;
            try {
                initStreamMedia();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private LibVLC mMediaPlayer;
    private void initStreamMedia() throws Exception {
        mMediaPlayer = VLCInstance.getLibVlcInstance(activity.getApplicationContext());
        mSurfaceView = this;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        mSurfaceHolder.addCallback(this);
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private void start() {
        if (uri==null || uri.equals("")){
            Logs.e(TAG,"错误的直播路径: "+uri);
            return;
        }
        mMediaPlayer.eventVideoPlayerActivityCreated(true);
        mSurfaceView.setKeepScreenOn(true);
        mMediaPlayer.playMRL(uri);
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mSurfaceView.setKeepScreenOn(false);
            mMediaPlayer.eventVideoPlayerActivityCreated(false);
        }
    }




    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mMediaPlayer != null) {
            mSurfaceHolder = holder;
            mMediaPlayer.attachSurface(holder.getSurface(), this);
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
        if (mMediaPlayer != null) {
            mMediaPlayer.attachSurface(holder.getSurface(), this);//, width, height
        }
        if (width > 0) {
            mVideoHeight = height;
            mVideoWidth = width;
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mMediaPlayer != null) {
            mMediaPlayer.detachSurface();
        }
    }

    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
    }





    private ViewGroup viewGroup;
    private boolean isLayout = false;
    public void allowPlay(ViewGroup vp){
     //设置布局
        if (vp!=null && mMediaPlayer!=null ){
            if (!isLayout){
                viewGroup = vp;
                this.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,MATCH_PARENT));
                viewGroup.addView(this);
                isLayout = true;
            }
            //开始播放
            start();
        }
    }
    public void unAllowPlay(){
        if (viewGroup!=null && mMediaPlayer!=null && isLayout){
            //停止播放
            pause();
            viewGroup.removeView(this);
            isLayout  = false;
        }
    }









}
