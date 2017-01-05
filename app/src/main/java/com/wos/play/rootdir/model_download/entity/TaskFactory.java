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
     *             @param sourceLocalPath 本地资源路径
     * @return
     */
    public static Task gnrTask(String uri,String ftpip,String ftpport,String ftpusr,String ftppass,String file,String sourceLocalPath){

        Task task = new Task(SystemInitInfo.get().getTerminalNo());//设置终端id
        task.setSavePath(sourceLocalPath==null?SystemInitInfo.get().getBasepath():sourceLocalPath); //设置本地路径
        task.setUrl(uri); //设置url

        //判断类型
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
        //设置 ftp 用户名密码
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
        //设置ftp端口号
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
        //设置文件名
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
        task.setRemotePath(uri); //设置远程路径

        return task;
    }


    public static Task gnrTask(String uri){
        return gnrTask(uri,null,null,null,null,null,null);
    }

    // url - 可以是服务器 相对资源路径 file - 资源在服务器上面的文件名
    public static Task gnrTask(String uri,String file){
        return gnrTask(uri,null,null,null,null,file,null);
    }

    // serverRelativePath - 服务器 相对资源路径 localpath本地相对路径 filename - 资源在服务器上面的文件名
    public static Task gnrTask(String serverRelativePath,String localSource,String filename){
        return gnrTask(serverRelativePath,null,null,null,null,filename,localSource);
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
        return gnrTask(uri,ftpip,ftpport,ftpusr,ftppass,null,null);
    }
}
