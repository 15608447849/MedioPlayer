package com.wos.play.rootdir.model_download.override_download_mode;

import android.os.Parcel;
import android.os.Parcelable;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by user on 2016/11/25.
 */

public class Task implements Parcelable {

   public interface Type {
        int HTTP = 0;
        int FTP = 1;
        int FILE = 2;
        int NONE = 4;
    }

    interface State {
        int FINISHED = 0;
        int NEW = 1;
        int RUNNING = 2;
    }

    //资源保存的路径
    private String savePath;
    //终端id
    private String terminalNo;
    //文件名
    private String fileName;

    private int state;//执行状态

    private String url; //http://资源需要
    //文件本地路径
    private String localPath;//file:// 需要
    //文件下载类型
    private int type = Type.FTP;//默认

    //文件ftp ip
    private String ftpAddress;
    //文件ftp port
    private int ftpPort;
    //文件ftp 用户名
    private String ftpUser;
    //文件ftp 密码
    private String ftpPass;
    //文件远程路径
    private String remotePath;

    private TaskCall call;//接口回调

    /**
     *
     * @param savePath 文件存放路径 - 不存在会自动创建
     * @param terminalNo 终端id
     */
    public Task(String terminalNo) {
        state = State.NEW;
        this.terminalNo = terminalNo;
    }

    public void setOnDownLoadCall(TaskCall call) {
        this.call = call;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFtpAddress() {
        return ftpAddress;
    }

    public void setFtpAddress(String ftpAddress) {
        this.ftpAddress = ftpAddress;
    }

    public int getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(int ftpPort) {
        this.ftpPort = ftpPort;
    }
    public void setFtpPort(String ftpPort) {
        this.ftpPort = Integer.parseInt(ftpPort);
    }

    public String getFtpUser() {
        return ftpUser;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public String getFtpPass() {
        return ftpPass;
    }

    public void setFtpPass(String ftpPass) {
        this.ftpPass = ftpPass;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public int getState() {
        return state;
    }

    public String getUrl() {
        return url;
    }

    public TaskCall getCall() {
        return call;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCall(TaskCall call) {
        this.call = call;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
        try {
            if(!FileUtils.isFolderExist(savePath)){
                FileUtils.makeDirs(savePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTerminalNo() {
        return terminalNo;
    }

    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.savePath);
        dest.writeString(this.terminalNo);
        dest.writeString(this.fileName);
        dest.writeInt(this.state);
        dest.writeString(this.url);
        dest.writeString(this.localPath);
        dest.writeInt(this.type);
        dest.writeString(this.ftpAddress);
        dest.writeInt(this.ftpPort);
        dest.writeString(this.ftpUser);
        dest.writeString(this.ftpPass);
        dest.writeString(this.remotePath);
        dest.writeParcelable(this.call, flags);
    }

    protected Task(Parcel in) {
        this.savePath = in.readString();
        this.terminalNo = in.readString();
        this.fileName = in.readString();
        this.state = in.readInt();
        this.url = in.readString();
        this.localPath = in.readString();
        this.type = in.readInt();
        this.ftpAddress = in.readString();
        this.ftpPort = in.readInt();
        this.ftpUser = in.readString();
        this.ftpPass = in.readString();
        this.remotePath = in.readString();
        this.call = in.readParcelable(TaskCall.class.getClassLoader());
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this.type == Type.FTP && ((Task)o).getType()==Type.FTP){
            return this.fileName.equals(((Task)o).getFileName());
        }else {
            return super.equals(o);
        }
    }

    public String printInfo(){

        StringBuffer sb = new StringBuffer();
        if (type == Type.HTTP){
            sb.append("当前类型 - http , ")
                    .append("当前url - "+url);

        }
        if (type == Type.FILE){
            sb.append("当前类型 - file , ");
            sb.append("当前url - "+url);
        }

        if (type==Type.FTP){
            sb.append("当前类型 - ftp [");
            sb.append(ftpAddress +" ; ");
            sb.append(ftpPort +" ; ");
            sb.append(ftpUser +" ; ");
            sb.append(ftpPass +"]");
            sb.append("资源远程路径 - ["+ remotePath+fileName+"]");
            sb.append("资源本地路径 - ["+ savePath+fileName+"]");
        }
        return sb.toString()+" - hashcode ["+this.hashCode()+"]";
    }
}
