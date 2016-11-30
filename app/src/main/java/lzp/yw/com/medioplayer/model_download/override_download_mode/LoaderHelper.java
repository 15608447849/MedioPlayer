package lzp.yw.com.medioplayer.model_download.override_download_mode;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.tool.MD5Util;


/**
 * Created by user on 2016/11/25.
 */

public class LoaderHelper implements Observer {//观察者
    public static final int DOWNLOAD_MODE_SERIAL = 0;
    public static final int DOWNLOAD_MODE_CONCURRENT = 1;
    private static final String TAG = "LoaderHelper";
    private DownloadCallImp caller = null;
    private ExecutorService executor;
    private HttpUtils http = null;
    public LoaderHelper() {

    }
    public void initWord(Context context,int downLoadType){
//        singleThreadExecutor = Executors.newSingleThreadExecutor();
        executor =downLoadType==DOWNLOAD_MODE_SERIAL? Executors.newSingleThreadExecutor(): Executors.newFixedThreadPool(15);
        http = new HttpUtils();
        caller = new DownloadCallImp();
        caller.setContext(context);
    }

    public void unInitWord(){
        if (executor!=null){
            try {
                executor.shutdown();
                if(!executor.awaitTermination(2 * 1000, TimeUnit.MILLISECONDS)){
                    // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                    executor.shutdownNow();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        if (http!=null){
            http = null;
        }
        if (caller!=null){
            caller.unSetContext();
        }
    }

    /**
     * 执行下载任务
     */
    private void excuteDownLoad(final Task task) {
        executor.execute(new Runnable() {
            public void run() {
                try {
                    parseDatas(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 解析数据
     * @param task
     */
    private void parseDatas(Task task){
        if (fileIsExist(task.getSavePath()+task.getFileName())){
            caller.downloadResult(task,0);
            return;
        }

        int type = task.getType();
        if (type == Task.Type.HTTP){
            httpDownload(task);
        }else
        if (type == Task.Type.FTP){
            ftpDownloadSetting(task);
        }else
        if (type== Task.Type.FILE){
            cpFile(task);
        }else {
            caller.downloadResult(task, 1);
        }
    }
    //复制本地文件到 -app 资源目录下
    private void cpFile(Task task) {
        File jhFile = null;
        try {
            jhFile = new File( new URI(task.getUrl()));
            if (jhFile.exists()){
                Log.i(TAG,"复制建行资源文件到app资源目录 - \n"+jhFile.getAbsolutePath()+" -> "+task.getSavePath()+task.getFileName());
                FileUtils.copyFile(jhFile, new File(task.getSavePath()+task.getFileName()));
                caller.downloadResult(task,0);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         Log.e(TAG,"建行资源文件不存在 或者 复制失败 - "+jhFile);
         caller.downloadResult(task,1);
    }

    /**
     * 获取fto 主机
     * @param str
     */
    private String getHost(String str) {
        return str.substring(0,str.indexOf(":"));
    }
    private int getPort(String str){
        int port = 0;
        str = str.substring(str.indexOf(":")+1);
        try {
            port = Integer.parseInt(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return port;
    }
    //设置ftp值
    private void ftpDownloadSetting(final Task task) {
        String uri = task.getUrl();
        String str = uri.substring(uri.indexOf("//") + 2);
        String user = str.substring(0, str.indexOf(":"));
        String pass = str.substring(str.indexOf(":") + 1, str.indexOf("@"));
        String host = getHost(str.substring(str.indexOf("@") + 1, str.indexOf("/")));
        int port = getPort(str.substring(str.indexOf("@") + 1, str.indexOf("/")));
        String remotePath = str.substring(str.indexOf("/"), str.lastIndexOf("/") + 1);
        String remoteFileName = str.substring(str.lastIndexOf("/") + 1);
        String localPath = task.getSavePath();
        ftpDownloadImp(task,host,port,user,pass,remotePath,remoteFileName,localPath);
    }

    //Ftp 下载助手
    private void ftpDownloadImp(final Task task, String host,int port,final String user, String pass, String remotePath, String fileName, String localPath) {
        new FtpHelper(host,port,user,pass)
                .downloadSingleFile(
                        remotePath,
                        localPath,
                        fileName,
                        3,
                        new FtpHelper.OnFtpListener() {

                            @Override
                            public void ftpConnectState(int stateCode, String ftpHost, int port, String userName, String ftpPassword, String fileName) {
                                Logs.i(TAG,"连接服务器 : ip:"+ ftpHost+" port:"+port  +"\nuser:"+userName+" password:"+ftpPassword);
                                if (stateCode==FtpHelper.FTP_CONNECT_SUCCESSS){
                                    Logs.i(TAG,"ftp 连接成功");
                                    caller.nitifyMsg(task.getTerminalNo(),fileName,1);
                                    caller.nitifyMsg(task.getTerminalNo(),fileName,2);
                                }
                                if (stateCode==FtpHelper.FTP_CONNECT_FAIL){
                                    Logs.e(TAG,"ftp 连接失败");
                                    caller.nitifyMsg(task.getTerminalNo(),fileName,4);
                                    caller.downloadResult(task,1,fileName,".md5");
                                }
                            }

                            @Override
                            public void ftpNotFountFile(String remoteFileName, String fileName) {
                                Logs.e(TAG,"ftp 服务器未发现文件 : "+remoteFileName+fileName);
                                caller.downloadResult(task,1);
                                caller.nitifyMsg(task.getTerminalNo(),fileName,4);
                            }

                            @Override
                            public void localNotFountFile(String localFilePath, String fileName) {
                                Logs.e(TAG,"本地文件不存在或无法创建 : "+localFilePath);
                                caller.nitifyMsg(task.getTerminalNo(),fileName,4);
                                caller.downloadResult(task,1,fileName,".md5");

                            }

                            @Override
                            public void downLoading(long downProcess, String speed, String fileName) {
                                caller.notifyProgress(task.getTerminalNo(),fileName,downProcess+"%",speed);
                            }



                            @Override
                            public void downLoadFailt(String remotePath, String fileName) {
                                Logs.e(TAG,"ftp 下载失败 : "+fileName);
                                caller.nitifyMsg(task.getTerminalNo(),fileName,4);
                                caller.downloadResult(task,1,fileName,".md5");
                            }

                            @Override
                            public void error(Exception e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void downLoadSuccess(File localFile, String remotePath, String localPath, String fileName, String ftpHost, int port, String userName, String ftpPassword) {
                                Logs.i(TAG, "ftp下载succsee -"+ localFile.getAbsolutePath() +" - 线程 - "+ Thread.currentThread().getName());
                                if (caller.downloadResult(task,0,fileName,".md5")){

                                        //获取源文件 code
                                        String sPath = localPath+fileName.substring(0,fileName.lastIndexOf("."));
                                        Logs.e(TAG,"文件 - "+sPath +" 比较 MD5值");
                                        String sCode = MD5Util.getFileMD5String(new File(sPath));
                                        if (sCode!=null){
                                            //比较md5
                                            if(MD5Util.FTPMD5(sCode, localFile.getAbsolutePath()) == 1){
                                                //md5 效验失败 删除文件
                                                cn.trinea.android.common.util.FileUtils.deleteFile(sPath);
                                                Logs.e(TAG,"文件 - "+sPath +" 比较 MD5值 失败 已删除");
                                            }
                                        }
                                }else{
                                    ftpDownloadImp(task,ftpHost,port,userName,ftpPassword,remotePath,fileName+".md5",localPath);
                                }

                            }
                        }
                );
    }

    // http xiazai
    private void httpDownload(final Task task) {
       final String url = task.getUrl();
        final String filePath = task.getSavePath()+task.getFileName();
      http.download(url,
              filePath,
              false,
              false,
              new RequestCallBack<File>() {
                  long currentTime = 0;
                  long oldTime = 0;
                  long oldLoadingSize = 0;
                  String speed =null;
                  @Override
                  public void onStart() {
                      Logs.i(TAG,"启动http下载:"+ url+" on Thread : "+Thread.currentThread().getName());
                      caller.nitifyMsg(task.getTerminalNo(),url.substring(url.lastIndexOf("/")+1),1);
                      caller.nitifyMsg(task.getTerminalNo(),url.substring(url.lastIndexOf("/")+1),2);
                      currentTime = System.currentTimeMillis();
                  }
                  @Override
                  public void onLoading(long total, long current, boolean isUploading) {
                      oldTime = currentTime;
                      currentTime = System.currentTimeMillis();
                      long temSize = current-oldLoadingSize;
                      oldLoadingSize = current;
                      double speedTem = (temSize/(1024 * 1.0))/((currentTime-oldTime)/(1000*1.0)) ;
                      speed = String.format("%f",speedTem)+"kb/s";
                      caller.notifyProgress(task.getTerminalNo(),url.substring(url.lastIndexOf("/")+1),(current/total)+"",(speedTem/(1024 * 1.0))+" kb");
                  }
                  @Override
                  public void onSuccess(ResponseInfo<File> responseInfo) {
                      final String path  =responseInfo.result.getPath();
                      caller.downloadResult(task,0);
                      caller.nitifyMsg(task.getTerminalNo(),url.substring(url.lastIndexOf("/")+1),3);
                  }
                  @Override
                  public void onFailure(HttpException e, String s) {
                      caller.downloadResult(task,1);
                      caller.nitifyMsg(task.getTerminalNo(),url.substring(url.lastIndexOf("/")+1),4);
                  }
              });
    }


    /**
     * 被观察者
     * 数据
     */
    @Override
    public void update(Observable observable, Object data) {
//        Logs.i(TAG,"update - "+data);
        //获取到一个任务 -> 执行一个线程
        if (data!=null){
            excuteDownLoad((Task) data);
        }
    }

    /**
     * 判断文件是不是存在
     */
    public boolean fileIsExist(String filename){
        return   fileUtils.checkFileExists(filename);
    }








}
