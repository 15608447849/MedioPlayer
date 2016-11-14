package lzp.yw.com.medioplayer.model_application.ui.Uitools;

import android.content.Context;
import android.util.Log;

import lzp.yw.com.medioplayer.model_application.baselayer.DataListEntiyStore;
import lzp.yw.com.medioplayer.model_download.singedownload.Loader;

/**
 * Created by user on 2016/11/11.
 */

public class UiTools {
    private static boolean isInit = false;
    private static DataListEntiyStore dle = null;
    public static void  init(Context context){
        dle = new DataListEntiyStore(context);
        dle.ReadShareData();
        isInit = true;
    }
    public static void  uninit(){
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



}
