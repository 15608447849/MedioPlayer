package lzp.yw.com.medioplayer.model_download.downloadTools;

import java.util.Observable;

/**
 * Created by user on 2016/11/3.
 * 被观察者
 */

public class BeingWatched extends Observable{
    public void excute(DownloadQueueTask queueListManager,String savapath,String telminalid){
        setChanged();
        notifyObservers(new Object[]{queueListManager.getTaskAndDelete(),savapath,telminalid});  //取出任务
     }
}
