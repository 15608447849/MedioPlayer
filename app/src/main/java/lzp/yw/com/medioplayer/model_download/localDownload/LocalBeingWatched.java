package lzp.yw.com.medioplayer.model_download.localDownload;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by user on 2016/11/19.
 */

public class LocalBeingWatched extends Observable {
    public void excute(LocalDownloadQueue queueListManager,String savapath,String telminalid){
        setChanged();

        ArrayList<Object> arr = queueListManager.getTaskAndDelete();
        arr.add(savapath);
        arr.add(telminalid);
        notifyObservers(arr);  //取出任务
    }
}
