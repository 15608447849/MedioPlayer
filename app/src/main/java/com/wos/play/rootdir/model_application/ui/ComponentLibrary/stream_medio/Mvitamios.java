package com.wos.play.rootdir.model_application.ui.ComponentLibrary.stream_medio;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_universal.tool.Logs;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by user on 2016/11/21.
 */

public class Mvitamios implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {
    private static final String TAG = "Vitamio";
    private BaseActivity activity;
    private ViewGroup layout; //


    private static boolean isInit = false;//是否初始化 c_Lib
    private View root;
    private boolean isLayout = false;





    private boolean isStardEnable = false;
    private Uri uri;
    private VideoView mVideoView;
    public Mvitamios(Context context, String path) {  // private String path; //"http://222.36.5.53:9800/live/xktv.m3u8";//"http://218.89.69.211:8088/streamer/yb01/yb01-500.m3u8";
        if (context instanceof BaseActivity) {
            this.activity = (BaseActivity) context;
            initStreamMedia();
            initView();
        }
        path = (path == null || path.equals("")) ? "http://222.36.5.53:9800/live/xktv.m3u8" : path;

//      this.path = "/mnt/external_sd/wosplayer/playlist/default.mp4";
        uri = Uri.parse(path);
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
                mVideoView = (VideoView) root.findViewById(R.id.buffer);
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
            playVideoStop();
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
        if (root != null && isLayout) {
            Logs.d(TAG,"removeLayout()");
            layout.removeView(root);
            isLayout = false;
        }
    }






    //播放
    private void playVideo() {
        if (isStardEnable) {//允许播放
            Logs.d(TAG,"playVideo() : "+ uri);
            try {
                mVideoView.setVideoURI(uri);
                mVideoView.setMediaController(new MediaController(activity));
                mVideoView.requestFocus();
                mVideoView.setOnInfoListener(this);
                mVideoView.setOnBufferingUpdateListener(this);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // optional need Vitamio 4.0
                        mediaPlayer.setPlaybackSpeed(1.0f);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //停止
    private void playVideoStop() {
        if (isInit && isLayout){
            Logs.d(TAG,"playVideoStop()");
            mVideoView.stopPlayback();
        }
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
               }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                Logs.d(TAG,"进度 : " + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Logs.d(TAG,"进度百分比 : " + percent + "%  ");
    }
}
