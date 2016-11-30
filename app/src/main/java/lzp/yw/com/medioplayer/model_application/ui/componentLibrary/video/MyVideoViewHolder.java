package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.FrameLayout;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IContentView;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.MedioInterface;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/14.
 * 视频控件 二次封装
 */
public class MyVideoViewHolder implements IContentView{
    private static final String TAG = "MyVideoViewHolder";
    private MyVideoView video = null;
    private Context mCcontext;
    private FrameLayout layout ;
    private int length;
    private String videoPath ;
    public int getLength() {
        return length;
    }
    private boolean isInitData;
    private boolean isLayout ;

    //多媒体控件播放
    private MedioInterface medioEr = null;
    public void setMedio(MedioInterface medio){
        this.medioEr = medio;
    }

    //构造
    public MyVideoViewHolder(Context context, FrameLayout layout, ContentsBean content) {
        mCcontext =context;
        this.layout = layout;
        video = new MyVideoView(context);//视频播放器
        initData(content);
    }

    @Override
    public void initData(Object object) {
        try {
            ContentsBean content = ((ContentsBean)object);
            this.videoPath = UiTools.getUrlTanslationFilename(content.getContentSource());
            this.length = content.getTimeLength();
            video.setOnPreparedListener_(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Logs.i(TAG,"当前设置时长 - "+length +"获取到视频真实时长 - "+video.getDuration()/1000);
                    if (length!=video.getDuration()/1000){
                        length = video.getDuration()/1000;
                    }
                }
            });
            video.setOnCompletionListener_ (new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Logs.i(TAG,"播放完成");
                }
            });
            video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Logs.e(TAG,"播放错误");
                    if (medioEr!=null){
                        medioEr.playOver(MyVideoViewHolder.this);
                    }
                    return false;
                }
            });
            isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setAttrbute() {
        //
    }

    @Override
    public void layouted() {
        //
    }

    @Override
    public void unLayouted() {
        //
    }

    @Override
    public void startWork() {
        //启动播放器
        if (videoPath!=null && UiTools.fileIsExt(videoPath)){
            video.start(layout,videoPath,false);
        }else{
//            if (medioEr!=null){
//                medioEr.playOver(this);
//            }
            video.start(layout,UiTools.getDefVideoPath(),false);
        }

    }

    @Override
    public void stopWork() {
        //停止播放器
        video.stopMyPlayer();
    }


}
