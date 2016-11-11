package lzp.yw.com.medioplayer.model_application.ui.Uitools;

import android.content.Context;

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
        String color = null ;
        try {
            String tem = Integer.toHexString(Integer.parseInt(colorValue));
            if (tem.length() == 6) {
                color = tem;
            } else {
                StringBuffer addZeor = new StringBuffer();
                for (int i = 0; i < 6 - tem.length(); i++) {
                    addZeor.append("0");
                }
                color = addZeor + tem;
            }
            return "#" + color;
        }catch (Exception e){
            return null;
        }
    }

    //获取Uri的文件名
    public static String getUrlTanslationFilename(String url){
        //      ftp://ftp:FTPmedia@172.16.0.17:21/content/1476427174433.jpg
        return dle.GetStringDefualt("basepath","")+url.substring(url.lastIndexOf("/")+1);

    }

    //判断文件是否存在
    public static boolean fileIsExt(String filepath){
            return Loader.fileIsExist(filepath);
    }



}
