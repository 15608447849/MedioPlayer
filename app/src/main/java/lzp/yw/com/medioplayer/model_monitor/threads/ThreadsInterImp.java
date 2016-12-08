package lzp.yw.com.medioplayer.model_monitor.threads;

/**
 * Created by user on 2016/12/8.
 */

public interface ThreadsInterImp {

    //快挂了
    void overs();
    //发送广播    1 -> 界面显示
    public void sendBroadUi();
    //发送广播    2 - 监听server
    public void sendBroadWatchServer();
}
