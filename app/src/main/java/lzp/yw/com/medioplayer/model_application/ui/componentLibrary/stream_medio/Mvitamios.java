package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.stream_medio;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 2016/11/21.
 */

public class Mvitamios implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback{

    private BaseActivity activity ;
    private ViewGroup layout; //
    private String path ; //"http://222.36.5.53:9800/live/xktv.m3u8";//"http://218.89.69.211:8088/streamer/yb01/yb01-500.m3u8";

    private  static boolean isInit = false;//是否初始化 c_Lib
    private  View root;
    private  boolean isLayout = false;

    private boolean mIsVideoSizeKnown = false;//是否知道视频大小
    private boolean mIsVideoReadyToBePlayed = false;//视频准备播放?
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private int mVideoWidth; //视频宽高
    private int mVideoHeight;
    private boolean isStardEnable = false;

    public Mvitamios(Context context, String path) {
        if (context instanceof BaseActivity){
            this.activity = (BaseActivity) context;
        }
        this.path = (path==null || path.equals(""))?"http://222.36.5.53:9800/live/xktv.m3u8":path;
        initView();
    }

    //初始化  uiTools 去初始化它
   public static void initStreamMedia(Activity activityContext){
       if (activityContext!=null){
               if (!LibsChecker.checkVitamioLibs(activityContext)) {
                   isInit = false;
               }else {
                   Vitamio.isInitialized(activityContext.getApplication());
                   isInit = true;
               }
           }
   }

    //初始化控件
    private void initView(){
        if (isInit){
            if (activity!=null) {
                root = LayoutInflater.from(activity).inflate(R.layout.stream_layout,null);
                mPreview = (SurfaceView) root.findViewById(R.id.surface);
                holder = mPreview.getHolder();
                holder.addCallback(this);
                holder.setFormat(PixelFormat.RGBA_8888);
            }
        }
    }

    //1 设置布局 2 设置开始播放状态 true
    private void settingLayout(ViewGroup vp) {
        if (isInit){//已经初始化
           if (layout!=null){
             removeLayout();
           }
            layout = vp;
            addLayout();
        }
    }

    public void allowPlay(ViewGroup layout){
        if (isInit){
            isStardEnable = true;
            settingLayout(layout);
            playVideo();
        }
    }

    public void unAllowPlay(){
        if (isInit){
            isStardEnable = false;
            removeLayout();
        }
    }
    private void addLayout() {
        if (root!=null && !isLayout){
            layout.addView(root);
            isLayout = true;
        }
    }

    private void removeLayout() {
       unPlayVideo();
        if (root!=null && isLayout){
            layout.removeView(root);
            isLayout = false;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完成
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            reStartVideoPlayback();
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        //视频大小改变
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
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
        if (mMediaPlayer!=null && isStardEnable){
            holder.setFixedSize(mVideoWidth, mVideoHeight);
            mMediaPlayer.start();
        }

    }
    //清理
    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }
    //释放
    private void releaseMediaPlayer() {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    //播放
    private void playVideo() {
        unPlayVideo();
        if (isStardEnable){//允许播放
            try{
                mMediaPlayer = new MediaPlayer(activity);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.setDisplay(holder);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnVideoSizeChangedListener(this);
                activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void unPlayVideo(){
        releaseMediaPlayer();
        doCleanUp();
    }

}
