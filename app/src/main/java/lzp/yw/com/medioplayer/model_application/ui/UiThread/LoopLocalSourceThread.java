package lzp.yw.com.medioplayer.model_application.ui.UiThread;

import java.util.List;

import cn.trinea.android.common.util.FileUtils;
import lzp.yw.com.medioplayer.model_universal.tool.AppsTools;

/**
 * Created by user on 2016/11/30.
 */

public class LoopLocalSourceThread extends Thread{
    private boolean isStart = false;
    public void startLoop(){
        isStart = true;

    }
    public void stopLoop(){
        isStart = false;
    }

    public LoopLocalSourceThread(List<String> fileList, LoopSuccessInterfaces binder) {
        this.fileList = fileList;
        this.binder = binder;
    }

    //放入一个资源列表
    private List<String> fileList = null;
    private LoopSuccessInterfaces binder;

    private void relase() {
        if (binder!=null){
            binder = null;
        }
        if (fileList!=null){
            fileList=null;
        }
    }

    @Override
    public void run() {
        while (isStart){
            if (fileList!=null && fileList.size()>0){
                System.out.println(fileList.get(0)+" - looping");
                if (FileUtils.isFileExist(fileList.get(0))){
                    //已存在
                    //通知绑定的接口
                    binder.SourceExist(fileList.get(0));
                    //删除
                    fileList.remove(0);
                }
            }else{
                stopLoop();
            }
            try {
                Thread.sleep(AppsTools.randomNum(1,2)*500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopLoop();
            }
        }
        relase();
    }
}
