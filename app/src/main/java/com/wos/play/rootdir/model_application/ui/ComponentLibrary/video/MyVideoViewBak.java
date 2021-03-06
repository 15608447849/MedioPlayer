package com.wos.play.rootdir.model_application.ui.ComponentLibrary.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.MediaController;

import com.wos.play.rootdir.model_universal.tool.Logs;


/**
 * Created by user on 2016/7/6.
 */
public class MyVideoViewBak extends SurfaceView implements MediaController.MediaPlayerControl{
    public static final String TAG = "MyVideoView";
    private Context mContext;//上下文
    private ViewGroup layout;//父布局
    private String filename;//播放的文件
    private Uri mUri;
    private int mDuration;//持续时长
    /**
     * Surface 表层
     *  持有者
     */
    private SurfaceHolder mSurfaceHolder = null;
    /**
     * 音频播放者
     */
    private MediaPlayer mMediaPlayer = null;
    /**
     * 是否准备完成
     */
    private boolean mIsPrepared;
    private int mVideoWidth; //视频宽
    private int mVideoHeight;  // 视频高
    private int mSurfaceWidth;  //表层宽
    private int mSurfaceHeight; //表层高
    private boolean mStartWhenPrepared;//准备完成何时开始
    private int mSeekWhenPrepared;//准备完成何时设置点
    /**
     * 是否布局过
     */
    private boolean isLayouted = false;
    /**
     * 是不是销毁了
     */
    private boolean isDestroy = false;


    /**
     *  setMyLayout 设置布局
     *  loadRouce 设置文件路径
     * @param context
     */
    public MyVideoViewBak(Context context) {
        super(context);
        mContext = context;
        Logs.d(TAG," my videoview create");
    }

    //
    /**
     * 设置 布局属性
     */
    public void setMyLayout(){
        Logs.d(TAG,"setMyLayout() - "+isLayouted);
        try {
            if (!isLayouted){
                Logs.d(TAG,"视频播放器 -设置布局中......");
                    //如果没有被布局过
                if (layout instanceof AbsoluteLayout){
                    this.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT,AbsoluteLayout.LayoutParams.MATCH_PARENT,0,0));
                }else{
                    this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                layout.addView(this);
                isLayouted = true;
            }
        } catch (Exception e) {
            Logs.e(TAG,"视频播放器 -设置布局失败：" + e.getMessage());
        }
    }
    //设置布局
    public void setLayout(ViewGroup gp){
        Logs.d(TAG,"setLayout() - " + gp);
        if (layout!=null){
          stopMyPlayer();
        }
        this.layout = gp;
        //设置布局
        this.setMyLayout();
    }

    /**
     * 加载本地资源
     */
    public void loadLocalSource(final String filename) {
        Logs.d(TAG,"loadLocalSource() - "+filename);
        this.filename = filename;
        try {
            setVideoPath(filename);
            setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Logs.i(TAG,"错误的文件播放:"+filename);
                    return false;
                }
            });
        } catch (Exception e) {
            Logs.e(TAG,"视频播放加载资源错误：" + e.getMessage());
        }
    }
    //设置视频路径
    public void setVideoPath(String path) {
        Logs.d(TAG,"setVideoPath - "+path);
        if (null == path || "".equals(path)) {
            return;
        }
        setVideoURI(Uri.parse(path));
    }
    /**
     * 加载数据
     *  自动打开视频
     * @param uri
     */
    public void setVideoURI(Uri uri) {
        Logs.d(TAG,"setVideoURI() - "+ uri);
        if (null == uri) {
            return;
        }
        try{
            mUri = uri;
            mStartWhenPrepared = false;//准备完成后 就开始吗? 不
            mSeekWhenPrepared = 0;//准备完后从那个点开始播放
            openVideo();//打开视频
//            if (!isLayouted) { //如果没布局
//                requestLayout(); //请求布局
//                invalidate();    //刷新视图
//                Logs.d(TAG,"布局完成");
//            }
        }catch (Exception e){
            Logs.e(TAG,"setVideoURI() err- "+e.getMessage());
        }

    }


    /**
     * 开始播放
     */
    public void start(){
        try {
            Logs.d(TAG,"视频播放器  start()被调用 , 开始播放 >>>");
            if (layout==null){
                Logs.d(TAG,"视频播放器请设置布局...");
                return;
            }
            if (!isLayouted) {  //如果没布局
                requestLayout(); //请求布局
                invalidate();    //刷新视图
                Logs.d(TAG,"布局完成");
            }
            if (mMediaPlayer != null && mIsPrepared) {
                Logs.d(TAG,"开启视频播放中,请稍后...");
                mMediaPlayer.start();
                mStartWhenPrepared = false;
//                Logs.d(TAG,"mStartWhenPrepared is false");
            } else {
                mStartWhenPrepared = true;
//                Logs.d(TAG,"mStartWhenPrepared is true");
            }
        } catch (Exception e) {
            Logs.e(TAG,"start() err - " + e.getMessage());
        }
    }

    /**
     * 设置布局 开始播放 入口!!!
     */
   public void start(ViewGroup layout,String filepath,boolean isloop){
        Logs.d(TAG,"start(layout,filepath,isloop) >>> "+layout+" - " +filepath+" - "+ isloop);
        setLayout(layout); //设置布局对象
        initVideoView(isloop);//初始化
        loadLocalSource(filepath);//设置文件路径
//        start();//开始
    }

    /**
     * 暂停
     */
    public void pause() {
        if (isDestroy){
            Logs.e(TAG,"isDestroy is true");
            return;
        }
        if (mMediaPlayer != null && mIsPrepared) {//如果媒体播放器存在,并且准备完成的
            if (mMediaPlayer.isPlaying()) {//如果正在播放中
                mMediaPlayer.pause(); //暂停
            }
        }
        mStartWhenPrepared = false;//设置 未准备完成
    }

    /**
     * 停止播放
     */
    public void stopMyPlayer() {

        pause();
        if (mMediaPlayer == null){
            return;
        }

        if (!isDestroy){
            Logs.i(TAG,filename+"视频停止播放");
            isDestroy = true;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (isLayouted){
            layout.removeView(this);//父布局移除视图
            isLayouted = false;
        }
    }
    /**
     * 初始化
     * isloop 是否循环
     */
    public void initVideoView(boolean isLoop) {
        Logs.d(TAG,"initVideoView() - " + isLoop );
        mVideoWidth = 0; //视频宽度
        mVideoHeight = 0;   //视频高度
        getHolder().addCallback(mySurfaceHolderCallback); //表层回调 - 创建viewHolder
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//表面型推缓冲区
        setFocusable(false);//设置焦点
        setFocusableInTouchMode(false);//设置焦点触摸

        if (isLoop){
//      setting - 循环播放
           setOnCompletionListener_(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Logs.d(TAG,"video play over ... Completion");
                    //设置循环播放
                    setVideoPath(filename);
                    start();
                    Logs.d(TAG,"initVideoView this.setOnCompletionListener complete!");
                }
            });
            Logs.d(TAG,"设置循环成功");
        }else{
            Logs.d(TAG,"未设置循环播放");
        }
    }

    /**
     * 打开播放器
     */
    private synchronized  void openVideo() {
        Logs.d(TAG,"---------------------------------------------  openVideo()  ----------------------------------------");
        //如果 播放路径不存在 或者 表层持有者 不存在
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later (没有准备好回放,稍后会再试一次)
            Logs.e(TAG,"播放路径-mUri-[ "+mUri+" ]\n视图层-mSurfaceHolder = [ "+mSurfaceHolder+" ]");
            Logs.d(TAG,"==================================打开视频失败========================================");
            return;
        }

        try {
            // 发送广播，关掉系统的音乐播放器
            Intent i = new Intent("com.android.music.musicservicecommand");
            i.putExtra("command", "pause");
            mContext.sendBroadcast(i);

            // 播放器存在
            if (mMediaPlayer != null) { //释放
                Logs.e(TAG,"视频播放器 已存在") ;
                mMediaPlayer.stop();
                mMediaPlayer.reset(); //重置释放
                mMediaPlayer.release();
                mMediaPlayer = null; //置为空
                Logs.e(TAG,"释放底层播放器 成功");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        try {
            Logs.d(TAG,"#### 设置视频中..... ####");
            mIsPrepared = false; //是否准备完成 no!

//            mMediaPlayer = MediaPlayer.create(mContext,mUri);
//            Logs.d(TAG," 视屏播放器 同步执行");
            mMediaPlayer = new MediaPlayer(); //新建播放器
            Logs.d(TAG," 创建视频播放器 ");
            mMediaPlayer.setDataSource(mContext, mUri); //设置数据源
            Logs.d(TAG," 设置数据源完成");


            mMediaPlayer.setOnPreparedListener(mPreparedListener); //准备监听
            Logs.d(TAG," 设置 准备完成 监听");

            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);//改变监听
            Logs.d(TAG," 设置 视频大小改变 监听");

            mDuration = -1; //持续时间
            mMediaPlayer.setOnErrorListener(mErrorListener);//错误监听
            Logs.d(TAG," 设置 错误 监听");

            mMediaPlayer.setOnCompletionListener(mCompletionListener);//播放完成
            Logs.d(TAG," 设置 播放结束 监听");

            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);//缓冲更新
            Logs.d(TAG," 设置 缓冲更新 监听");
            mCurrentBufferPercentage = 0;//缓冲百分比 per cent age

            mSurfaceHolder.setSizeFromLayout();//设置surface大小来自布局
            Logs.d(TAG," 设置在布局上的大小完成");
            mMediaPlayer.setDisplay(getHolder());//设置播放器图层显示在哪
            Logs.d(TAG," 设置视频图片显示层对象完成");
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放器音频流
            Logs.d(TAG," 设置音频流完成");
            mMediaPlayer.setScreenOnWhilePlaying(true);//设置屏幕回放
            Logs.d(TAG," 设置 设置是否使用 SurfaceHolder 显示 (true)");
            mMediaPlayer.setLooping(true);
            Logs.d(TAG," 设置是否循环播放(true)");

            mMediaPlayer.prepareAsync();//异步准备
            Logs.d(TAG," 视屏播放器 异步执行");

            //start();//开始
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Logs.e(TAG,"新建视频播放器时错误 ->>> ");
            e.printStackTrace();
        }
    }







    /**
     * 得到时长
     * @return
     */
    public int getDuration() {
        Logs.d(TAG,"getDuration()");
        if (mMediaPlayer != null && mIsPrepared) {
            if (mDuration > 0) { //如果时长大于初始点
                return mDuration;
            }
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        Logs.e(TAG,"视频时长:"+mDuration);
        return mDuration;
    }

    /**
     * 返回 : 当前播放下标
     */
    public int getCurrentPosition() {
        try {
            if (mMediaPlayer != null) {
                // 返回的播放的视频 一帧要多少毫秒
                return mMediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            Logs.e(TAG,"获取帧图失败 " + e.getMessage());
        }
        return 0;
    }

    /**
     * 设置播放点
     * @param msec
     */
    public void seekTo(int msec) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    /**
     * 是否在播放中
     * @return
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 得到缓冲百分比 percentage
     * @return
     */
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    public boolean canPause() {
        return false;
    }
    public boolean canSeekBackward() {
        return false;
    }
    public boolean canSeekForward() {
        return false;
    }
    public int getAudioSessionId() {
        return 0;
    }

    /**
     * 点击事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    //大小改变监听
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Logs.d(TAG,"视频大小变化监听 width,geight - ("+width+","+height+")");
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            Logs.d(TAG,"视频大小变化监听 Video - Width,Height- ("+mVideoWidth+","+mVideoHeight+")");
            if (mVideoWidth != 0 && mVideoHeight != 0) {

                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
        }
    };

    /**
     * 当前 缓冲 百分比[pə'sentɪdʒ]
     */
    private int mCurrentBufferPercentage;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };
    /**
     * 准备完成监听
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener(){

        public void onPrepared(MediaPlayer mp) {
            Logs.d(TAG,"准备 事件监听 中... -> 开始执行");
            //设置视频宽高
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            Logs.d(TAG,"设置视频宽高 ("+mVideoWidth+","+mVideoHeight+")");

            if (mVideoWidth != 0 && mVideoHeight != 0) { //宽高不等于0
                Logs.d(TAG,"固定 SurfaceHolder 大小");
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);//fixed 固定

                if (mSurfaceWidth == mVideoWidth  && mSurfaceHeight == mVideoHeight){ //如果 表层宽高 等于 视频宽高
                    Logs.d(TAG,"表层宽高 == 视频宽高");
                    if (mSeekWhenPrepared != 0) {//准备完成播放点不在初始点 设置当前点播放 并还原初始点
                        Logs.d(TAG,"准备完成 - 播放点不在初始点 - 设置当前点播放并还原初始点");
                        mMediaPlayer.seekTo(mSeekWhenPrepared);
                        mSeekWhenPrepared = 0;
                    }
                    if (mStartWhenPrepared) {//准备完成何时开始 == true
                        Logs.d(TAG,"mStartWhenPrepared is true");
//                        mMediaPlayer.start();
                        mStartWhenPrepared = false;
                        Logs.d(TAG,"mStartWhenPrepared setting is false");
                    } else if (!isPlaying() && (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {//如果 不在播放中 并且 准备完成播放点 不是初始点 或者 现在的播放点大于初始点
                        Logs.e(TAG,"mPreparedListener 当前不在播放中 ,播放点不在初始点");
                    }
                }
            } else {
                /**
                 * We don't know the video size yet, but should start anyway.
                 我们还不知道视频的大小,但无论如何应该开始。
                 The video size might be reported to us later.
                 以后视频的大小可能会报告给我们。
                 */
                Logs.d(TAG,"未知的视频大小");
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    Logs.d(TAG,"mMediaPlayer seekTo mSeekWhenPrepared ,"+mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                if (mStartWhenPrepared) {
//                    Logs.d(TAG,"mPreparedListener restart 视频准备完成监听: 重启");
//                    mMediaPlayer.start();
                    mStartWhenPrepared = false;
                }
            }

            mIsPrepared = true; //准备完成
            Logs.d(TAG,"mPreparedListener 执行完毕");

            if (mOnPreparedListener != null) {
                //Logs.d(TAG,"mOnPreparedListener(用户自定义的准备完成监听事件) - 存在");
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            start();
        }
    };

    //设置准备完成监听对象
    public void setOnPreparedListener_(MediaPlayer.OnPreparedListener l) {
//        Logs.d(TAG,"准备完成 setOnPreparedListener()");
        mOnPreparedListener = l;
    }

    /**
     * 播放完成监听事件
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = null;
    private MediaPlayer.OnCompletionListener mCompletionListener=new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
//            Logs.e(TAG,"video mCompletionListener() _ onCompletion() "+ mOnCompletionListener);

            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };
    //设置播放完成自定义监听
    public void setOnCompletionListener_ (MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * 绘制 重写
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        try {
            Logs.i(TAG, "视频播放器 onMeasure() 绘制 w:"+width+" - h:"+height);
            setMeasuredDimension(width, height); //设置绘制大小
            Logs.d(TAG,"----------绘制完成---------------");
        }catch (Exception e){
            Logs.e(TAG," onMeasure err : "+e.getMessage());
        }

    }

    /**
     * 错误监听
     */
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {

            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,impl_err)) {
                    return true;
                }
            }
            if (getWindowToken() != null) {
                Logs.e(TAG,"getWindowToken() is not null");
            }
            return true;
        }
    };
    /**
     * 设置错误监听
     * @param l
     */
    private MediaPlayer.OnErrorListener mOnErrorListener;
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * 表层视图 回调接口
     * 创建的时候会尝试打开视频
     */
    SurfaceHolder.Callback mySurfaceHolderCallback = new SurfaceHolder.Callback() {

        //创建时候
        public void surfaceCreated(SurfaceHolder holder) {
            Logs.d(TAG,"surfaceCreated() - "+holder);
            isDestroy = false;
            mSurfaceHolder = holder; //设置 表层视图持有者
            openVideo(); //打开播放器
        }
        //改变时
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            //如果 媒体播放器存在 ,已经准备完成, 视频宽度和高度 与布局宽度高度相同
            if (mMediaPlayer != null && mIsPrepared && mVideoWidth == width
                    && mVideoHeight == height) {
                //如果 准备完成后播放点 不等于0 ,设置从这个点开始播放, 还原播放点
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                //媒体播放器开始播放
//                mMediaPlayer.start();
            }
        }

        //销毁时候
        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (mMediaPlayer != null) {
                isDestroy  = true;
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    };

    /**
     * 获取当前帧
     * @return
     */
    public Bitmap getCurrentFrame(){
        Bitmap bitmap = null;
        MediaMetadataRetriever mmr = null;
        try {
            if (mMediaPlayer!=null && mMediaPlayer.isPlaying() &&  null != filename && !"".equals(filename)){
                mmr = new MediaMetadataRetriever();
                mmr.setDataSource(filename);
                long pos = mMediaPlayer.getCurrentPosition();
                bitmap = mmr.getFrameAtTime(pos * 1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//              Logs.e(TAG,"截图 视频 大小 ( "+mMediaPlayer.getVideoWidth()+" , "+mMediaPlayer.getVideoHeight()+" )");
//              Logs.e(TAG,"截图 视频 大小 ( "+bitmap.getWidth()+" , "+bitmap.getHeight()+" )");
//              Logs.e(TAG,"截图 视频 大小 ( "+this.getWidth()+" , "+this.getHeight()+" )");
                bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(),
                        this.getHeight(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mmr.release();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }


}
