package lzp.yw.com.medioplayer.model_download.singedownload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import lzp.yw.com.medioplayer.model_communication.CommuniReceiverMsgBroadCasd;
import lzp.yw.com.medioplayer.model_download.FtpTools.ActiveFtpUtils;
import lzp.yw.com.medioplayer.model_download.FtpTools.fileUtils;
import lzp.yw.com.medioplayer.model_universal.Logs;
import lzp.yw.com.medioplayer.model_universal.MD5Util;
import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/11/3.
 *lzp
 */
public class Loader {

    private static final String TAG = " _download_loader";
    private static ReentrantLock locker = new ReentrantLock();//同步锁
    private static ReentrantLock lock_repeat = new ReentrantLock();// 重复队列 同步锁
    private static ReentrantLock waitlistLock = new ReentrantLock();//等待队列 同步锁
    //工作线程
    private final static Scheduler.Worker ioThread = Schedulers.io().createWorker();
    private final static Scheduler.Worker notifyThread = Schedulers.io().createWorker();
    public static int loadcount = 2 ;//每次下载的数量


    private Context context ;

    //资源保存的路径
    private String savepath ;
    //终端id
    private String terminalNo ;

    //构造
    public Loader(Context context,String savepath,String terminalNo){
        this.context = context;
       this.savepath = savepath;
       this.terminalNo =terminalNo;
    }

    /**
     * 调用者实现的回调
     */
    private LoaderCaller other_caller;
    /**
     * 设置数据回调
     * @param calle
     */
    public void settingCaller(LoaderCaller calle){
        this.other_caller = calle;
    }

    /**
     * 回调次数
     */
    private static int callCount = 0;

    private LoaderCaller caller = new LoaderCaller() {
        @Override
        public void Call(String filePath) {

            Logs.i(TAG, "Call: 当前一个回调结果[ "+Loader.this.muri+" ]");

            Logs.i(TAG, "loader Call(): 执行线程"+ Thread.currentThread().getName()+" \n thread size:"+ Thread.getAllStackTraces().size());

            if (other_caller!=null){
                try {
                    Logs.i(TAG,"Call:传递到 子监听回调 , count:"+ callCount++);
                    other_caller.Call(filePath);

                }catch (Exception e){
                    Logs.e(TAG,"传递子监听回调err:"+e.toString());
                }
            }
            if (muri!=null){
                if (!existRepeatList){ // 不存在 重复列表
                    Logs.i(TAG, "不在重复任务队列");
                    complateTask(muri,filePath);//完成任务
                }
                existRepeatList = false;
            }
            //通知等待队列
            notifyWaitList();
        }
    };

    private String muri = null;
    private boolean existRepeatList = false;//执行重复任务队列?
    /**
     * 下载中 任务队列****************************************************************************************************************************************************
     */
    private static List<String> loadingTaskList = Collections.synchronizedList(new LinkedList<String>());
    /**
     * 重复任务队列
     */
    private static Map<String,ArrayList<Loader>> repeatTaskList = Collections.synchronizedMap(new HashMap<String,ArrayList<Loader>>());

    /**
     * 任務過多 等待隊列
     */
    private static List<Loader> waitList = Collections.synchronizedList(new LinkedList<Loader>());

    /**
     * 添加一个进行任务
     * @param Task
     */
    private boolean addTask(String Task){
        Logs.i(TAG," 當前进行中 - 任務隊列數量:"+loadingTaskList.size()+" \n -> 添加任務:["+Task+"]");

        if (!loadingTaskList.contains(Task)){
            loadingTaskList.add(Task);
            Logs.i(TAG," (添加success)" );
            return true;
        }
        else {
            Logs.i(TAG,"(添加faild)");
            addRepeatTask(this);
            return false;
        }
    }
    /**
     * 添加重复任务
     */
    private void addRepeatTask(Loader l){
        try{
            if(repeatTaskList.containsKey(l.muri)){
                ArrayList<Loader> list = repeatTaskList.get(l.muri);
                Logs.w(TAG,"repeat list key:"+l.muri+"\n  value array:"+ list.toString());
                list.add(l);

            }else{
                ArrayList<Loader> arr = new ArrayList<Loader>();
                arr.add(l);
                Logs.w(TAG,"not fount key :"+l.muri+"\n create array:"+ arr.toString());
                repeatTaskList.put(l.muri,arr);
            }

            l.existRepeatList = true;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除一个进行中任务
     */
    private  void complateTask(String Task, final String filepath){
        Logs.i(TAG,"一個任務完成["+ Task+"] - 準備刪除 ,當前 下載中 任務數量:"+loadingTaskList.size());
        if (loadingTaskList.contains(Task)){
            loadingTaskList.remove(Task);
            Logs.i(TAG, "loading 任务队列 (移除) :"+Task);
        }
        else {
            Logs.i(TAG,Task + "不在 进行中 任务队列 ,不需要删除");
            Logs.i(TAG,"当前 重复任务 队列size :"+repeatTaskList.size());
        }

        notifyThread.schedule(new Action0() {
            @Override
            public void call() {
                //异步通知所有人 一个任务完成
                notifyRepatList(Loader.this.muri,filepath);
            }
        });
    }

    /**
     * 异步通知所有人
     */
    private static void notifyRepatList(final String uri, final String filepath){

        try{
            lock_repeat.lock();
            Logs.w(TAG,  " 开始通知 "+Thread.currentThread().getName()+" <->thread size: "+Thread.getAllStackTraces().size());

            final ArrayList<Loader> arr = repeatTaskList.get(uri);

            if (arr!=null){
                Logs.w(TAG, uri + " 在 重复列表中,映射的value:"+ arr.toString());
                repeatTaskList.remove(uri);

                for (final Loader l:arr){
                    Schedulers.newThread().createWorker().schedule(new Action0() {
                        @Override
                        public void call() {
                            l.receivedNotifi(uri,filepath);
                        }
                    });
                }

            }else{
                Logs.i(TAG, uri+ "  不在 重复列表");
            }
            Logs.w(TAG,  "结束通知 ");

        }catch (Exception e){
            Logs.e(TAG,  " 下载完成后  通告 重复任务队列 Err :"+ e.toString());
        }finally {
            lock_repeat.unlock();
        }
    }
    /**
     * 重复任务
     * 接收一个通告广播
     * @param uri
     * @param filePath
     */
    private void receivedNotifi(String uri, String filePath){

        String u = muri.trim();
        String t = uri.trim();
        boolean f =  u.equals(t);
        Logs.w(TAG,  u+ " < - >"+t+"结果:{"+f+"}");
        if(f){
            caller.Call(filePath);
        }
    }
    /**
     * 加入等待隊列
     */
    private static void addWaitList(Loader loader){
        Logs.i(TAG,"加入等待队列 : [" + loader.muri + "] ");
        waitList.add(loader);
    }
    /**
     * 通知 等待隊列 執行
     */
    private static void notifyWaitList(){
        try{
        waitlistLock.lock();
            //如果存在 每次只執行 至多 5 個
            if (loadingTaskList.size()==0){
                if (waitList.size()>0){
                    Iterator<Loader> itr = waitList.iterator();
                    int i = 0;
                    while(itr.hasNext()){
                        Loader o = itr.next();
                        o.LoadingUriResource(o.muri,null);
                        itr.remove();
                        i++;
                        if (i==loadcount){
                            break;
                        }
                    }

                    Logs.i(TAG," ----------------------- 完成一次 等待隊列的執行 ----------------------------------------");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            waitlistLock.unlock();
        }
    }


/************************************************************************************下载具体执行*********************************************************************************/

    /**
     * 判断一个文件是不是已经存在
     */
    public boolean fileIsExist(String filename){
        return   fileUtils.checkFileExists(filename);
    }

    /**
     * 生成 状态
     *
     * 2 3 4
     *
     */
    private void nitifyMsg(String filename, int type){
//        FTPS:100000001;1004562123.jpg;2

            sendMsgToServer("FTPS:"+terminalNo+";" + filename+ ";"+type);

    }

    /**
     * 下载结果回传
     * @param Filepath 当前任务下载的文件名
     */
    private void loadFileRecall(final String Filepath) {
        Logs.i("#& loadFileRecall() 接受到一个文件路径: " + Filepath);

        if (Filepath.equals("loaderr")) {
            Logs.e("load faild 文件名:" + Filepath);
            notifyThread.schedule(new Action0() {
                @Override
                public void call() {
                    caller.Call("404");
                }
            });
        } else {
            //load success
            notifyThread.schedule(new Action0() {
                @Override
                public void call() {
                    caller.Call(Filepath);
                }
            });
        }
    }


    /**
     * 请放入io 线程
     * @param uri 下载地址
     *  @param mySettingFileName 我设置的文件名
     */
    public void LoadingUriResource(final String uri, String mySettingFileName) {
        try {
            locker.lock();
            muri = uri;
            //是否 加入 等待 ,如果进行中的任务数量已经满足了
            if (loadingTaskList.size()>loadcount){
                addWaitList(this);
                return;
            }
            //是否 是 重复任务
            boolean f = addTask(uri);
            if(!f){
                Logs.e("重复任务");
                return;
            }
            String fns = uri.substring(uri.lastIndexOf("/") + 1);//文件名
            String localFileDir =  savepath;
            String fps = localFileDir + fns;//全路径

            final String finalFps = fps;
            if (fileIsExist(fps)) {
                Logs.i(TAG, " -- 文件存在 -- \n 任务:" + uri + "\n" +
                        " 本地所在路径 " + finalFps );
                ioThread.schedule(new Action0() {
                    @Override
                    public void call() {
                        caller.Call(finalFps);
                        nitifyMsg(uri.substring(uri.lastIndexOf("/") + 1), 1);
                        nitifyMsg(uri.substring(uri.lastIndexOf("/") + 1), 2);
                        nitifyMsg(uri.substring(uri.lastIndexOf("/") + 1), 3);
                    }
                });

                return;
            }

            Logs.e(TAG," ------------------------------开始访问网络---------------------- " );

            //判断路径i
            if (uri.startsWith("http://")) {
                HttpLoad(uri, finalFps);

            } else if (uri.startsWith("ftp://")) {
                // ftp://ftp:FTPmedia@21.89.68.163:21/uploads/1466573392435.png
                String str = uri.substring(uri.indexOf("//") + 2);
                final String name = str.substring(0, str.indexOf(":"));
                final String password = str.substring(str.indexOf(":") + 1, str.indexOf("@"));
                final String host = getHost(str.substring(str.indexOf("@") + 1, str.indexOf("/")));
                final int port = getPort(str.substring(str.indexOf("@") + 1, str.indexOf("/")));
                final String path = str.substring(str.indexOf("/"), str.lastIndexOf("/") + 1);
                final String filename = str.substring(str.lastIndexOf("/") + 1);
                final String localPath = localFileDir;
                ioThread.schedule(new Action0() {
                    @Override
                    public void call() {
                        FTPload(host,port, name, password, path, filename, localPath,null);
                    }
                });
            }
        }catch (Exception e){
            Logs.e(TAG,e.getMessage());
        }finally {
            locker.unlock();
        }
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

    /************************************************************************************H T T P DOWN LOAD*********************************************/
    private static HttpUtils http = new HttpUtils();
    /**
     * Http
     */
    private synchronized void HttpLoad(final String url, final String Filepath){
       http.download(
                url,
                Filepath,
                false,// 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                false,//如果从请求返回信息中获取到文件名，下载完成后自动重命名
                new RequestCallBack<File>() {
                    long currentTime = 0;
                    long oldTime = 0;
                    long oldLoadingSize = 0;
                    String speed =null;
                    @Override
                    public void onStart() {
                        Logs.i(TAG," 启动http下载:\n"+ url+" >>\n on Thread : "+Thread.currentThread().getName());
                        nitifyMsg(url.substring(url.lastIndexOf("/")+1),1);
                        nitifyMsg(url.substring(url.lastIndexOf("/")+1),2);
                        currentTime = System.currentTimeMillis();
                    }
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        Logs.e(url +"# 当前下载总量:" + current);
                        oldTime = currentTime;
                        currentTime = System.currentTimeMillis();
                        long temSize = current-oldLoadingSize;
                        Logs.e(url +"# 当前下载量:" + temSize);
                        oldLoadingSize = current;
                        double speedTem = (temSize/(1024 * 1.0))/((currentTime-oldTime)/(1000*1.0)) ;
                        Logs.e(url +"# 当前速度:" + speedTem);
                        speed = String.format("%f",speedTem)+"kb/s";
                        notifyProgress(url.substring(url.lastIndexOf("/")+1),(current/total)+"",(speedTem/(1024 * 1.0))+" kb");
                    }
                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        Logs.i(TAG,"http 下载完成:[" + url + "]当前所在线程 : "+Thread.currentThread().getName()+"-Thread count :"+Thread.getAllStackTraces().size());
                        final String path  =responseInfo.result.getPath();
                        loadFileRecall(path);
                        nitifyMsg(url.substring(url.lastIndexOf("/")+1),3);
                    }
                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Logs.e(TAG,"http 下载失败:"+msg +" url:[" + url +"],当前所在线程:"+Thread.currentThread().getName());
                        loadFileRecall("loaderr");
                        nitifyMsg(url.substring(url.lastIndexOf("/")+1),4);
                    }
                }
        );
    }
    //生成 下载进度
    private void notifyProgress(String filename, String process, String speed){
        //文件下载进度上报(PRGS:10000001,1004562123.jpg,0.56,200kb/s)
        sendMsgToServer( "PRGS:" +terminalNo + ","+filename+ ","+ process + "," + speed);
    }


    /**************FTP DOWN LOAD **********************************************************************************************************************************************-*/

    /**
     *
     * @param host 服务器ip
     * @param user 用户名
     * @param pass  密码
     * @param remotePath 远程目录
     * @param fileName  要下载的文件名
     * @param localPath 本地路径
     */
    private synchronized void FTPload(final String host, int port,final String user, final String pass, final String remotePath, final String fileName, final String localPath, final Object ob){
        Log.d(TAG,"host:"+host+"port:"+port);

        Logs.i(TAG, "FTP任务["+fileName+"]\n >>所在线程:"+ Thread.currentThread().getName());

        final ActiveFtpUtils ftp = new ActiveFtpUtils(host,port,user,pass);
        ftp.downloadSingleFile(remotePath + fileName,
                localPath,
                fileName,
                3,//重新链接次数
                new ActiveFtpUtils.DownLoadProgressListener() {
                    @Override
                    public void onDownLoadProgress(String currentStep, long downProcess, String speed, File file) {

                        if(currentStep.equals(ActiveFtpUtils.FTP_DOWN_SUCCESS)){
                            //成功
                            Logs.i(TAG, "ftp下载succsee -["+fileName+"] - 线程 - "+ Thread.currentThread().getName());

                            if (fileName.contains(".apk")){
                                //APK文件
                                //caller.Call(file.getAbsolutePath());
                                loadFileRecall(file.getAbsolutePath());
                                nitifyMsg(fileName,3);
                                return;
                            }

                            if (fileName.contains(".md5")){
                                //MD5文件
                                Logs.i(TAG,"资源文件 在服务器上 对应md5文件 : "+ file.getAbsolutePath());
                                String sp = ((File)ob).getAbsolutePath();//源文件
                                String dp = file.getAbsolutePath();//源文件MD5文件
                                int sut =  MD5Util.FTPMD5(sp,dp);

                                if (sut==0){
                                    Logs.e(TAG,"文件:"+((File)ob).getName()+ " md5 效验成功");
                                    loadFileRecall(sp);
                                    nitifyMsg(((File)ob).getName(),3);
                                }else{
                                    Logs.e(TAG,"文件:"+((File)ob).getName()+ " md5 效验失败!");
                                    loadFileRecall("loaderr");
                                    nitifyMsg(((File)ob).getName(),4);
                                }

                            }else{
                                //资源文件
                                Logs.i(TAG,"资源文件 : "+ file.getAbsolutePath());
                                File msd5file = MD5Util.getFileMD5String(file);
                                if (msd5file == null){
                                    Logs.e(TAG,"文件:"+((File)ob).getName()+ " 资源文件生成md5文件 失败 ");
                                    loadFileRecall("loaderr");
                                    nitifyMsg(((File)ob).getName(),4);
                                    return;
                                }
                                //下载 md5
                                FTPload(host,port,user,pass,remotePath,fileName+".md5",localPath,msd5file);
                            }
                        }
                        if (currentStep.equals(ActiveFtpUtils.FTP_DOWN_LOADING)){
                            //下载中
                            notifyProgress(fileName,downProcess+"",speed);
                        }

                        //ftp远程文件不存在
                        if (currentStep.equals(ActiveFtpUtils.FTP_FILE_NOTEXISTS)){
                            Logs.e(TAG,"ftp服务器 不存在文件 <<" + fileName+">>");
                            loadFileRecall("loaderr");
                            nitifyMsg(fileName,4);
                        }
                        //连接失败
                        if(currentStep.equals(ActiveFtpUtils.FTP_CONNECT_FAIL)){
                            Logs.e(TAG,"ftp 连接失败 ");
                            loadFileRecall("loaderr");
                            nitifyMsg(fileName,4);
                        }
                        //下载失败
                        if (currentStep.equals(ActiveFtpUtils.FTP_DOWN_FAIL)){
                            Logs.e(TAG,"ftp 下载失败 :"+fileName);
                            loadFileRecall("loaderr");
                            nitifyMsg(fileName,4);

                        }

                        if (currentStep.equals(ActiveFtpUtils.FTP_CONNECT_SUCCESSS)){
                            Logs.i(TAG,"ftp 连接成功 - "+ fileName);
                            nitifyMsg(fileName,1);
                            nitifyMsg(fileName,2);
                        }
                    }
                });
    }

    /**
     * ---------------------------------------------------发送信息到通讯服务
     */
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    private void sendMsgToServer(String param){
        if (context!=null && terminalNo!=null){
            bundle.clear();
            intent.setAction(CommuniReceiverMsgBroadCasd.ACTION);
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM1, "fileDownloadSpeedOrState");
            bundle.putString(CommuniReceiverMsgBroadCasd.PARAM2, param);
            intent.putExtras(bundle);
            context.sendBroadcast(intent);
        }
    }


}
