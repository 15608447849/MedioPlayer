package com.wos.play.rootdir.model_download.entity;

import com.wos.play.rootdir.model_application.baselayer.SystemInitInfo;
import com.wos.play.rootdir.model_download.override_download_mode.Task;

/**
 * Created by user on 2016/12/14.
 */

public class TaskFactory {



    /**
     * 根据 一个 url -> 变成一个 task  对象
     *
     * @param uri 完整路径
     * @param ftpip 地址
     * @param ftpport 端口
     * @param ftpusr 用户
     * @param ftppass 密码
     * @param file 文件名
     * @return
     */
    public static Task gnrTask(String uri,String ftpip,String ftpport,String ftpusr,String ftppass,String file){

        Task task = new Task(SystemInitInfo.get().getBasepath(),SystemInitInfo.get().getTerminalNo());
        task.setUrl(uri);

        if(uri.startsWith("http")){
           task.setType(Task.Type.HTTP);
           return task;
        }
        if(uri.startsWith("file")){
            task.setType(Task.Type.FILE);
            return task;
        }
        if (uri.startsWith("ftp")){
            task.setType(Task.Type.FTP);
            uri = uri.substring(uri.indexOf("/")+2);
        }
        if(uri.contains(":") && uri.contains("@")){
            //获取用户名密码
            String name =  uri.substring(0, uri.indexOf(":"));
            task.setFtpUser(name);
            String pass = uri.substring(uri.indexOf(":")+1,uri.indexOf("@"));
            task.setFtpPass(pass);
            uri = uri.substring(uri.indexOf("@")+1);
        }else{
            task.setFtpUser(ftpusr!=null?ftpusr:SystemInitInfo.get().getFtpUser());
            task.setFtpPass(ftppass!=null?ftppass:SystemInitInfo.get().getFtpPass());
        }
        if(uri.contains(":") && uri.contains("/")){
            //获取ip - 端口
            String ip =  uri.substring(0, uri.indexOf(":"));
            task.setFtpAddress(ip);
            String port = uri.substring(uri.indexOf(":")+1,uri.indexOf("/"));
            task.setFtpPort(port);

            uri = uri.substring(uri.indexOf("/"));
        }else{
            task.setFtpAddress(ftpip!=null?ftpip:SystemInitInfo.get().getFtpAddress());
            task.setFtpPort(ftpport!=null?ftpport:SystemInitInfo.get().getFtpPort());
        }


        if(uri.contains("/") && uri.contains(".")){
            //获取文件名
            String filename = uri.substring(uri.lastIndexOf("/")+1);
            task.setFileName(filename);
            uri = uri.substring(0,uri.lastIndexOf("/")+1);
        }else{
            task.setFileName(file!=null?file:"ERR_FILE");
        }


        if(!uri.startsWith("/")){
            uri= "/"+uri;
        }
        if(!uri.endsWith("/")){
            uri = uri+"/";
        }
        task.setRemotePath(uri);
        task.setSavePath(SystemInitInfo.get().getBasepath());
        return task;
    }


    public static Task gnrTask(Task oldTask,String newTaskName){
        Task task = new Task(SystemInitInfo.get().getBasepath(),SystemInitInfo.get().getTerminalNo());











        return task;

    }
    public static Task gnrTask(String uri){
        return gnrTask(uri,null,null,null,null,null);
    }

    public static Task gnrTask(String uri,String file){
        return gnrTask(uri,null,null,null,null,file);
    }

    /**
     *
     * @param uri 完整url
     * @param ftpip ftp地址
     * @param ftpport ftp端口
     * @param ftpusr ftp用户
     * @param ftppass ftp 密码
     * @return
     */
    public static Task gnrTask(String uri,String ftpip,String ftpport,String ftpusr,String ftppass) {
        return gnrTask(uri,ftpip,ftpport,ftpusr,ftppass,null);
    }
}
