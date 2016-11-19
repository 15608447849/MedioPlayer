package lzp.yw.com.medioplayer.model_application.ui.Uitools;

import android.content.Context;
import android.util.Log;

import java.io.File;

import lzp.yw.com.medioplayer.model_application.baselayer.DataListEntiyStore;
import lzp.yw.com.medioplayer.model_application.ui.UiHttp.UiDownload;
import lzp.yw.com.medioplayer.model_download.singedownload.Loader;
import lzp.yw.com.medioplayer.model_universal.tool.MD5Util;
import lzp.yw.com.medioplayer.model_universal.tool.SdCardTools;

/**
 * Created by user on 2016/11/11.
 */

public class UiTools {
    private static boolean isInit = false;
    private static DataListEntiyStore dle = null;
    private static File contentDir = null;
    public static void  init(Context context){
        dle = new DataListEntiyStore(context);
        dle.ReadShareData();
        contentDir = new File(dle.GetStringDefualt("jsonStore","")) ;//根目录
        UiDownload.init(context,dle.GetStringDefualt("basepath",""),dle.GetStringDefualt("terminalNo",""));
        isInit = true;
    }
    public static void  uninit(){
        UiDownload.unInit();
        dle =null;
        isInit = false;
    }

    /**
     * 转换背景颜色代码
     * @param colorValue
     * @return
     */
    public static String TanslateColor(String colorValue){
        Log.i(""," - - color code param ->"+colorValue);
        String color = null ;
        try {
           if (colorValue.startsWith("#") && colorValue.length()==7){
               color = colorValue;
           }else{
                if (colorValue.contains("0x")){
                    color= "#"+colorValue.substring(2);
                }
           }

        }catch (Exception e){
           e.printStackTrace();
            color= "#FFFFFF";
        }
        Log.i(""," - - color code tanslate code ->"+color);
        return color;
    }

    //获取Uri的文件名
    public static String getUrlTanslationFilename(String url){
        //      ftp://ftp:FTPmedia@172.16.0.17:21/content/1476427174433.jpg
        return "".equals(url.substring(url.lastIndexOf("/")+1))?null:dle.GetStringDefualt("basepath","")+url.substring(url.lastIndexOf("/")+1);

    }

    //判断文件是否存在
    public static boolean fileIsExt(String filepath){
            return Loader.fileIsExist(filepath);
    }

    /*1 获取文件名
        2 获取文件内容
        3 变成图集对象
        4 获取所有文件名*/
    //图集 :文件名->获取文件信息
    public static String fileTanslationObject(String filename){
        if(!contentDir.exists()){
            return null;
        }
        String [] filenames = contentDir.list();
        if (filenames==null || filenames.length==0){
            return null;
        }
        filename =  MD5Util.getStringMD5(filename);
        String textContent = null;
        for (String file : filenames){
            if (file.equals(filename)){
                textContent = SdCardTools.readerJsonToMemory(contentDir.getAbsolutePath()+"/",filename);
                break;
            }
        }
        return textContent;
    }
    //存入数据 内容目录下  文件名请进行md5加密
    public static boolean storeContentToDirFile(String filename,String content){
        if(!contentDir.exists()){
            return false;
        }
        SdCardTools.writeJsonToSdcard(contentDir.getAbsolutePath()+"/",MD5Util.getStringMD5(filename),content);
        return true;
    }



}
