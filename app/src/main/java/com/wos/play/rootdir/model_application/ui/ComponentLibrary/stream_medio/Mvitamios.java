package com.wos.play.rootdir.model_application.ui.ComponentLibrary.stream_medio;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_universal.tool.Logs;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by user on 2016/11/21.
 */

public class Mvitamios implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {
    private static final String TAG = "Vitamio";
    private BaseActivity activity;
    private ViewGroup layout; //
    private String path; //"http://222.36.5.53:9800/live/xktv.m3u8";//"http://218.89.69.211:8088/streamer/yb01/yb01-500.m3u8";

    private static boolean isInit = false;//是否初始化 c_Lib
    private View root;
    private boolean isLayout = false;

    private boolean mIsVideoSizeKnown = false;//是否知道视频大小
    private boolean mIsVideoReadyToBePlayed = false;//视频准备播放?
    private io.vov.vitamio.MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private int mVideoWidth; //视频宽高
    private int mVideoHeight;
    private boolean isStardEnable = false;

    public Mvitamios(Context context, String path) {
        if (context instanceof BaseActivity) {
            this.activity = (BaseActivity) context;
            initStreamMedia();
            initView();
        }
        this.path = (path == null || path.equals("")) ? "http://222.36.5.53:9800/live/xktv.m3u8" : path;
        this.path  ="http://222.36.5.53:9800/live/xktv.m3u8";
//        this.path = "/mnt/external_sd/wosplayer/playlist/default.mp4";
    }

    //初始化  uiTools 去初始化它
    private void initStreamMedia() {
        if (activity != null) {
            isInit = io.vov.vitamio.Vitamio.isInitialized(activity.getApplicationContext());
        }
        Logs.d(TAG," - vitamio isInitialized() - " + isInit );
    }

    //初始化控件
    private void initView() {
        if (isInit) {
            if (activity != null) {
                root = LayoutInflater.from(activity).inflate(R.layout.stream_layout, null);
                mPreview = (SurfaceView) root.findViewById(R.id.surface);
                holder = mPreview.getHolder();
                holder.addCallback(this);
                holder.setFormat(PixelFormat.RGBA_8888);
                Logs.d(TAG," - vitamio initView() - ok");
            }
        }
    }

    //1 设置布局 2 设置开始播放状态 true
    private void settingLayout(ViewGroup vp) {
        if (isInit) {//已经初始化
            Logs.d(TAG," -settingLayout() - "+vp);
            if (layout != null) {
                removeLayout();
            }
            layout = vp;
            addLayout();
        }
    }

    public void allowPlay(ViewGroup layout) {
        if (isInit) {
            Logs.d(TAG, "allowPlay() - "+ layout);
            isStardEnable = true;
            settingLayout(layout);
            playVideo();
        }
    }

    public void unAllowPlay() {
        if (isInit) {
            Logs.d(TAG,"unAllowPlay()");
            isStardEnable = false;
            removeLayout();
        }
    }

    private void addLayout() {
        if (root != null && !isLayout) {
            Logs.d(TAG,"addLayout()");
            layout.addView(root);
            isLayout = true;
        }
    }

    private void removeLayout() {
        unPlayVideo();
        if (root != null && isLayout) {
            Logs.d(TAG,"removeLayout()");
            layout.removeView(root);
            isLayout = false;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logs.d(TAG, "surfaceCreated called - "+ holder);
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logs.d(TAG, "surfaceChanged called - "+ holder +" format - "+format +" width = "+width+",height = " +height );
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logs.d(TAG, "surfaceDestroyed called - "+ holder);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Logs.d(TAG,"onBufferingUpdate() - -"+mp+" - - "+percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Logs.d(TAG,"onCompletion() - -"+mp);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完成
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            Logs.d(TAG,"onPrepared() - -"+mp);
            reStartVideoPlayback();
        }
//        if (mp != null) {
//            mp.setBufferSize(512 * 1024);
//        }

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        //视频大小改变
        if (width == 0 || height == 0) {
            Logs.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            reStartVideoPlayback();
        }
    }

    //重新开始播放
    private void reStartVideoPlayback() {
        if (mMediaPlayer != null && isStardEnable) {
            Logs.d(TAG,"reStartVideoPlayback()");
            holder.setFixedSize(mVideoWidth, mVideoHeight);
            mMediaPlayer.start();
        }

    }

    //清理
    private void doCleanUp() {
        Logs.d(TAG,"doCleanUp() ");
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    //释放
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            Logs.d(TAG,"releaseMediaPlayer() ");
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //播放
    private void playVideo() {
        unPlayVideo();
        if (isStardEnable) {//允许播放
            Logs.d(TAG,"playVideo() : "+path);
            try {
                mMediaPlayer = new MediaPlayer(activity);//true
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnVideoSizeChangedListener(this);
                mMediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
                activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void unPlayVideo() {
        Logs.d(TAG,"unPlayVideo() ");
        releaseMediaPlayer();
        doCleanUp();
    }

}
