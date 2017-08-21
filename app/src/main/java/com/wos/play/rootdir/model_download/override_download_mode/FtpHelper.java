package com.wos.play.rootdir.model_download.override_download_mode;


import android.util.Log;

import com.wos.play.rootdir.model_universal.tool.Logs;

import org.apache.commons.net.ftp.FTP;
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
import java.util.Locale;

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
     * @throws IOException
     */
    private void openConnect() throws IOException {
        if (ftpClient==null){
            ftpClient = new FTPClient();
        }
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setDataTimeout(60000);       //设置传输超时时间为60秒
        ftpClient.setConnectTimeout(60000);       //连接超时为60秒
        int reply; // 服务器响应值
        ftpClient.connect(hostName, serverPort);     // 连接至服务器
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {// 断开连接
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
     */
    private void closeConnect(){
        try {
            if (ftpClient != null) {// 退出FTP
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
            }
        }
    }

    /**
     * 下载单个文件，可实现断点下载.
     * @param serverPath Ftp目录及文件全路径
     * @param localPath 本地目录
     * @param fileName 下载之后的文件名称
     * @param listener d 监听器
     * @throws IOException
     */
    public synchronized  void downloadSingleFile(String serverPath, String localPath, String fileName, int reconnectCount, OnFtpListener listener){
        String localFilePath = localPath+fileName ;
        String tem = ".tem"; //临时文件后缀
        String tmp_localPath = localPath + fileName+tem;
        try {
            this.openConnect();
            listener.ftpConnectState(FTP_CONNECT_SUCCESS,hostName,serverPort,userName,password,fileName);
        } catch (Exception e) {
            listener.error(e);
            reConnectionFTP(serverPath, localPath, fileName, reconnectCount, listener);
            return;
        }
        // 先判断服务器文件是否存在
        FTPFile[] files;
        try {
            files =  ftpClient.listFiles(serverPath+fileName); //远程服务器文件
            if (files==null || files.length == 0){
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
        if (!mkFile.exists() && mkFile.mkdirs()) {
            Log.d(TAG, "创建本地目录成功:"+ localPath);
        }
        boolean isLoad = true;
        // 接着判断下载的文件是否能断点下载
        long serverSize = files[0].getSize(); // 获取远程文件的长度
        File localFile = new File(localFilePath);//本地文件
        File tmp_localFile = new File(tmp_localPath);//临时文件
        if(localFile.exists()){//如果有同名文件
            long localFileSize = localFile.length(); // 获取文件的长度
            if( localFileSize == serverSize){
                listener.downLoadSuccess(localFile,serverPath,localPath,fileName,hostName,serverPort,userName,password);
                closeConnect();   // 下载完成之后关闭连接
                return;
            }else{
                if(localFile.delete()){
                    Log.i(TAG,"删除一个同名文件:"+localFile.getAbsolutePath());
                }
            }
        }

        long localSize = 0;
        if (tmp_localFile.exists()) { //如果 临时文件存在
            localSize = tmp_localFile.length(); // 获取 本地临时文件的长度
            if (localSize == serverSize) { //临时文件长度 和 服务器 的大小一样 不下载
                isLoad = false;
            }
            if (localSize > serverSize) {
                localSize = 0;
                Log.i(TAG,"临时文件大于服务器大小，删除:"+ new File(tmp_localPath).delete());
             }else if (localSize< serverSize){ //断点续传
                Log.i(TAG,"本地文件小于服务器，开启续传，剩余大小："+(serverSize -localSize));
             }
        }
        if (isLoad) {//如果　没有下载过　准备下载．
            //下载前　设置下载中所需值
            long step = serverSize / 100; //下标位置
            long process = 0; // 进度
            long currentSize = 0;//当前总大小
            long oldSize = 0, currentTime, oldTime;
            String speed ;
            OutputStream out;    // 输出到本地文件流
            try {
                out = new FileOutputStream(tmp_localFile, true); //本地文件输出流 - 临时文件
            } catch (FileNotFoundException e) {
                listener.error(e);
                listener.localNotFountFile(tmp_localFile.getAbsolutePath(),fileName);
                closeConnect();
                return;
            }
            //取出 ftp文件流
            InputStream input;
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
                int length;
                byte[] b = new byte[1024];//缓存大小
                double timeDiff,sizeDiff,speedTem;
                currentTime = System.currentTimeMillis();//当前毫秒数
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
                            sizeDiff = (currentSize-oldSize)/(1024 * 1.0);
                            speedTem = sizeDiff/timeDiff ;
                            oldSize = currentSize;
                            speed = String.format(Locale.CHINA, "%f kb/s",speedTem);
                            listener.downLoading(process, speed, fileName);
                        }
                    }
                }
                String nName = tmp_localPath.substring(0,tmp_localPath.lastIndexOf("."));//正式文件名
                FileUtils.renameFile(tmp_localPath,nName);//转换名字
                File Nf = new File(nName);
                listener.downLoadSuccess(Nf,serverPath,localPath,fileName,hostName,serverPort,userName,password);
            } catch (IOException e) {
                listener.error(e);
                listener.downLoadFail(serverPath,fileName);
            }finally {
                try {
                    out.close();
                    if (input!=null) input.close();
                    this.closeConnect(); // 下载完成之后关闭连接
                } catch (IOException e) {
                    //e.printStackTrace();
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
    private void reConnectionFTP(String serverPath, String localPath, String fileName
            , int reconnectCount, OnFtpListener listener) {
        closeConnect();
        if (reConnectCount++<reconnectCount){
            try {
                Thread.sleep(10*1000);
                Log.d(TAG, "重新连接FTP...\n当前第" + reConnectCount + "次尝试.\n服务器ip:"
                        + this.hostName + "端口:" + this.serverPort + "用户名:"
                        + this.userName + "密码:" + this.password);
            }catch (InterruptedException e1){
                e1.printStackTrace();
            }
           finally {
                downloadSingleFile(serverPath,localPath,fileName,reconnectCount,listener);
            }
        }else{
            listener.ftpConnectState(FTP_CONNECT_FAIL,hostName,serverPort,userName,password,fileName);
        }
    }

    public static final int FTP_CONNECT_SUCCESS = 1;//"ftp连接成功";
    public static final int FTP_CONNECT_FAIL = 2;   //"ftp连接失败";
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
        void ftpConnectState(int stateCode, String ftpHost, int port, String userName
                , String ftpPassword, String fileName);//1 success 2failt

        //ftp 不存在文件
        void ftpNotFountFile(String remoteFileName, String fileName);
        void localNotFountFile(String localFilePath, String fileName);

       /**
        *  下载进度监听
        */
       void downLoading(long downProcess, String speed, String fileName);

        /**
         * 下载成功
         * @param localFile
         */
        void downLoadSuccess(File localFile, String remotePath, String localPath, String fileName, String ftpHost, int port, String userName, String ftpPassword);

        /**
         * 下载失败
         */
        void downLoadFail(String remotePath, String fileName);

        /**
         * 错误
         * @param e
         */
        void error(Exception e);
    }

    /**
     * 上传单个文件.
     * @param localPath 本地文件路径
     * @return true上传成功, false上传失败
     */
    public boolean uploadingSingle(String localPath,String uploadFileAlias,String remotePath){
        boolean flag = false;
        try {
            openConnect(); // 打开FTP服务
            // 设置模式
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            if(!remotePath.endsWith("/")) remotePath += "/";
            if(uploadFileAlias == null || uploadFileAlias.equals("")){
                uploadFileAlias =FileUtils.getFileName(localPath);
            }
            // FTP下创建文件夹
            ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);
            InputStream inputStream = new FileInputStream(localPath);  // 创建输入流

            // 上传单个文件
            flag = ftpClient.storeFile(uploadFileAlias, inputStream);
            //关闭文件流
            inputStream.close();
            Logs.i(TAG,"远程文件目录- ["+remotePath +"] -"+(flag?" upload success":" upload fail"));
            //删除文件
            FileUtils.deleteFile(localPath);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭ftp服务器
            closeConnect();
        }
        return flag;
    }


























}
