package lzp.yw.com.medioplayer.model_command_.command_arr;

import android.content.Context;
import android.media.AudioManager;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseApplication;
import lzp.yw.com.medioplayer.model_universal.Logs;


/**
 * Created by user on 2016/7/30.
 *
 */
public class Command_VOLU implements iCommand {

    private static final String TAG = "_Command_VOLU";

    @Override
    public void Execute(String param) {
        Logs.d(TAG,"音量设置 param :["+ param +"]\nThreadName:"+Thread.currentThread().getName()+" ");
        int percent = Integer.valueOf(param);
        SetSystemVolume(percent);
    }
    public void SetSystemVolume(int percent)
    {
        AudioManager audioManager = (AudioManager) BaseApplication.appContext.getSystemService(Context.AUDIO_SERVICE);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (max*percent)/100, 0);
        Logs.d(TAG," 完成 ");
    }












}
