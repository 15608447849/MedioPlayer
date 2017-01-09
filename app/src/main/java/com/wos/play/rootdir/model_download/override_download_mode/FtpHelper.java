package com.wos.play.rootdir.model_download.override_download_mode;


import com.wos.play.rootdir.model_universal.tool.Logs;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by Administrator on 2016/6/25.
 */

public class FtpHelper {

    private static final String TAG = "_ftp";
    /**
     * 服务器名.
     */
    private String hostName;
    /**
     * 端口号
     */
    private int serverPort;
    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;
    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    /**
     * FTP重新链接次数
     */
    private int reConnectCount = 0;
    public FtpHelper(String hostName, int serverPort, String userName, String password) {
        init(hostName,serverPort,userName,password);
    }
    private void init(String hostName, int serverPort, String userName, String password){
        this.reConnectCount = 0;
        this.hostName = hostName;
        this.serverPort = serverPort==0?21:serverPort;
        this.userName = userName;
        this.password = password;
    }
    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public void openConnect() throws IOException {

        if (ftpClient==null){
            ftpClient = new FTPClient();
        }
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setDataTimeout(60000);       //设置传输超时时间为60秒
        ftpClient.setConnectTimeout(60000);       //连接超时为60秒

        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);

        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("FTP connect fail code : " + reply );
        }
        // 登录到服务器
        ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config =new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh"); //设置配置类型
            ftpClient.configure(config);//配置
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }
    }
    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect(){
        try {
            if (ftpClient != null) {
                // 退出FTP
                ftpClient.logout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpClient != null) {
                    ftpClient.disconnect();
                    ftpClient=null;
                    System.out.println(" - 关闭 ftp 连接...");
                }
            } catch(Exception ioe) {
                // do nothing
//                ioe.printStackTrace();
            }
        }
    }








    /**
         * 下载单个文件，可实现断点下载.
         *
         * @param serverPath
         *            Ftp目录及文件全路径
         * @param localPath
         *            本地目录
         * @param fileName
         *            下载之后的文件名称
         * @param listener
         *            监听器
         * @throws IOException
         */
    public synchronized  void downloadSingleFile(String serverPath, String localPath, String fileName, int reconnectCount, OnFtpListener listener){
        String localFilePath = localPath+fileName ;
//        log.i(TAG,"准备下载 :["+serverPath+fileName"]\n 远程服务器文件本地路径 : "+localFilePath);
        String tem = ".tem"; //临时文件后缀
        String tmp_localPath = localPath + fileName+tem;
        try {
            // 打开FTP服务
//            log.i(TAG,"连接服务器 : FTP://"+ hostName+":"+serverPort  +"\n user : "+userName+"  password : "+password);
            this.openConnect();
            listener.ftpConnectState(FTP_CONNECT_SUCCESSS,hostName,serverPort,userName,password,fileName);
        } catch (Exception e) {
            listener.error(e);
            reConnectionFTP(serverPath, localPath, fileName, reconnectCount, listener);
            return;
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = null;
        try {
            files =  ftpClient.listFiles(serverPath+fileName); //远程服务器文件
            if (files==null || files.length == 0){
//                log.i(TAG,"服务器文件 不存在 : ["+ serverPath+"]");
                listener.ftpNotFountFile(serverPath,fileName);
                closeConnect();
                return;
            }
        } catch (Exception e) {
            listener.error(e);
            reConnectionFTP(serverPath, localPath, fileName, reconnectCount, listener);
            return;
        }



        //创建本地文件夹
        File mkFile = new File(localPath);
        if (!mkFile.exists()) {
            mkFile.mkdirs();
        }
        boolean isLoad = true;
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
//        log.i(TAG,"服务器文件长度 : "+ serverSize);
        File localFile = new File(localFilePath);//本地文件
        File tmp_localFile = new File(tmp_localPath);//临时文件
//        log.i("本地路径 :"+localFilePath);
//        log.i("临时文件路径:"+tmp_localPath);
        if(localFile.exists()){
            //如果有同名文件
            long localfile_Size = localFile.length(); // 获取文件的长度
//            log.i(TAG,"本地 已存在文件 长度 :"+ localfile_Size);
            if( localfile_Size == serverSize){
//                log.i(TAG,"文件已存在"+localFile.getName());
                listener.downLoadSuccess(localFile,serverPath,localPath,fileName,hostName,serverPort,userName,password);
                // 下载完成之后关闭连接
                closeConnect();
                return;
            }else{
//                log.i(TAG,"删除一个同名文件:"+localFile.getAbsolutePath());
                localFile.delete();
            }
        }

        long localSize = 0;
        if (tmp_localFile.exists()) { //如果 临时文件存在
            localSize = tmp_localFile.length(); // 获取 本地临时文件的长度
//            log.i(TAG,"临时文件长度: "+localSize);
            if (localSize == serverSize) { //临时文件长度 和 服务器 的大小一样 不下载
                isLoad = false;
//                log.i(TAG,"不下载 - "+tmp_localFile.getName());
            }if (localSize<serverSize){ //断点续传
//                log.i(TAG,"临时文件:"+tmp_localPath+" - 大小:"+localSize+" \n 服务器存在大小:"+serverSize);
            }else if (localSize>serverSize){
//                log.i(TAG,"删除一个临时文件:"+tmp_localPath+" - 大小:"+localSize+"\n 服务器存在大小:"+serverSize);
                new File(tmp_localPath).delete();
                localSize = 0;
            }
        }

        if (isLoad) {//如果　没有下载过　准备下载．

//            log.i(TAG,"准备下载文件 - "+serverPath +" -> "+ localFilePath );
            //下载前　设置下载中所需值
            long step = serverSize / 100; //下标位置
            long process = 0; // 进度
            long currentSize = 0;//当前总大小
            long oldSize = 0;
            long currentTime = 0;
            long oldTime = 0;
            String speed =null;
            // 输出到本地文件流
            OutputStream out = null;
            try {
                out = new FileOutputStream(tmp_localFile, true); //本地文件输出流 - 临时文件
            } catch (FileNotFoundException e) {
                listener.error(e);
                listener.localNotFountFile(tmp_localFile.getAbsolutePath(),fileName);
                closeConnect();
                return;
            }

            //取出 ftp文件流
            InputStream input  = null;
            try {
                ftpClient.setRestartOffset(localSize);  //设置下载点
                input = ftpClient.retrieveFileStream(serverPath+fileName);//远程ftp 文件输入流
            } catch (Exception e) {
                listener.error(e);
                reConnectionFTP(serverPath, localPath, fileName, reconnectCount, listener);
                return;
            }

            //下载中
            try {
                byte[] b = new byte[1024];//缓存大小
                int length = 0;
                currentTime = System.currentTimeMillis();//当前毫秒数
                double timeDiff = 0;
                double sizeDiff = 0;
                double speedTem = 0;
                while ((length = input.read(b)) != -1) {
                    out.write(b, 0, length);
                    out.flush();
                    currentSize += length; // 当前总大小 = 现在的大小 加上 写出来的大小
                    if (step<=0){
                        continue;
                    }
                    if (currentSize / step != process) { //如果 当前进度/下标 != 已有进度
                        process = currentSize / step;
                        if (process % 10 == 0) { //每隔%10的进度返回一次
                            oldTime = currentTime;//旧时间
                            currentTime = System.currentTimeMillis();//当前时间
                            timeDiff = (currentTime-oldTime) / (1000 * 1.0) ;
//                            log.d("时间差:"+ timeDiff +"秒");
                            sizeDiff = (currentSize-oldSize)/(1024 * 1.0);
//                            log.d(fileName +" - 当前下载量:" + sizeDiff + " kb");
                            speedTem = sizeDiff/timeDiff ;
                            oldSize = currentSize;
//                            log.d(fileName +" - 下载速度:" + speedTem +" kb/s");
                            speed = String.format("%f",speedTem) + "kb/s";
                            listener.downLoading(process, speed, fileName);
                        }
                    }
                }

                String nNmae = tmp_localPath.substring(0,tmp_localPath.lastIndexOf("."));//正式文件名
                fileUtils.renamefile(tmp_localPath,nNmae);//转换名字
                File Nf = new File(nNmae);
//                log.i(TAG,",转换前文件名["+tmp_localPath+"]\n新文件名: "+nNmae);
                listener.downLoadSuccess(Nf,serverPath,localPath,fileName,hostName,serverPort,userName,password);
            } catch (IOException e) {
                listener.error(e);
                listener.downLoadFailt(serverPath,fileName);
            }finally {
                try {
                    if (out!=null){
                        out.close();
                    }
                    if (input!=null){
                        input.close();
                    }
                    // 下载完成之后关闭连接
                    this.closeConnect();
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }

        }

    }




    /**
     * 重新链接
     * @param serverPath
     * @param localPath
     * @param fileName
     * @param reconnectCount
     * @param listener
     * @return
     */
    private void reConnectionFTP(String serverPath, String localPath, String fileName, int reconnectCount, OnFtpListener listener) {
        closeConnect();
        if (reConnectCount++<reconnectCount){
            try {
            Thread.sleep(10*1000);
           System.out.println("重新连接FTP...\n当前第"+reConnectCount+"次尝试.\n服务器ip:"+this.hostName+"端口:"+this.serverPort+"用户名:"+this.userName+"密码:"+this.password);
            }catch (InterruptedException e1){
                //listener.ftpConnectState(FTP_CONNECT_FAIL,hostName,serverPort,userName,password,fileName);
                e1.printStackTrace();
            }
           finally {
                downloadSingleFile(serverPath,localPath,fileName,reconnectCount,listener);
            }

        }else{
            listener.ftpConnectState(FTP_CONNECT_FAIL,hostName,serverPort,userName,password,fileName);
        }
    }



    public static final int FTP_CONNECT_SUCCESSS = 1;//"ftp连接成功";
    public static final int FTP_CONNECT_FAIL = 2;//"ftp连接失败";
    public static final int FTP_DISCONNECT_SUCCESS = 3;//"ftp断开连接";
    public static final int FTP_DOWN_LOADING = 4;//"ftp文件正在下载";
    public static final int FTP_DOWN_SUCCESS = 5;//"ftp文件下载成功";
    public static final int FTP_DOWN_FAIL = 6;//"ftp文件下载失败";
    public static final int FTP_FILE_NOTEXISTS = 7;//"ftp文件不存在";
    public static final int FTP_LOCAL_FILE_NOTEXISIS = 8;//"本地文件不存在或者创建失败";

    public interface OnFtpListener {
        /**
         * 连接成功
         * @param stateCode
         * @param ftpHost
         * @param port
         * @param userName
         * @param ftpPassword
         */
        public void ftpConnectState(int stateCode, String ftpHost, int port, String userName, String ftpPassword, String fileName);//1 success 2failt

        //ftp 不存在文件
        public void ftpNotFountFile(String remoteFileName, String fileName);
        public void localNotFountFile(String localFilePath, String fileName);

       /*
        *   下载进度监听
        */
       public void downLoading(long downProcess, String speed, String fileName);

        /**
         * 下载成功
         * @param localFile
         */
        public void downLoadSuccess(File localFile, String remotePath, String localPath, String fileName, String ftpHost, int port, String userName, String ftpPassword);

        /**
         * 下载失败
         */
        public void downLoadFailt(String remotePath, String fileName);

        /**
         * 错误
         * @param e
         */
        public void error(Exception e);





    }


    /**
     * 上传文件
     */
    /**
     * 上传单个文件.
     *
     * @param localPath
     *            本地文件路径
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    public void uploadingSingle(String localPath,String uploadFileAlias,String remotePath){
        try {
            // 打开FTP服务
            openConnect();

            // 设置模式
            ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
            remotePath = remotePath.endsWith("/")?remotePath:remotePath.substring(0,remotePath.lastIndexOf("/")+1);
            // FTP下创建文件夹
            ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);
            //不带进度的方式
            // 创建输入流
            InputStream inputStream = new FileInputStream(localPath);

            // 上传单个文件
           boolean flag = ftpClient.storeFile(
                    (uploadFileAlias==null||uploadFileAlias.equals(""))?localPath.substring(localPath.lastIndexOf("/")+1):uploadFileAlias, //上传的文件名
                    inputStream
            );
            //关闭文件流
            inputStream.close();
            Logs.i(TAG,"远程文件目录- ["+remotePath +"] -"+(flag?" upload success":" upload failt"));
            //删除文件
            FileUtils.deleteFile(localPath);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭ftp服务器
            closeConnect();
        }
    }


























}
