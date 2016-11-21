package lzp.yw.com.medioplayer.model_universal.tool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by user on 2016/10/26.
 */
public class AppsTools {
    //随机数
    public static int randomNum(int min,int max){
        return (int)(min+Math.random()*max);
    }

    private static String callCmd(String cmd,String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine ()) != null && line.contains(filter)== false) {
                //result += line;
                Logs.i("line: "+line);
            }

            result = line;
            Logs.i("result "+result);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * get mac
     */
    public static String getLocalMacAddressFromBusybox(){
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig","HWaddr");

        //如果返回的result == null，则说明网络不可取
        if(result==null){
            return "网络出错，请检查网络";
        }

        //对该行数据进行解析
        //例如：eth0      Link encap:Ethernet  HWaddr 00:16:E8:3E:DF:67
        if(result.length()>0 && result.contains("HWaddr")==true){
            Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
            Logs.i("Mac:"+Mac+" Mac.length: "+Mac.length());

            if(Mac.length()>1){
                Mac = Mac.replaceAll(" ", "");
                result = "";
                String[] tmp = Mac.split(":");
                for(int i = 0;i<tmp.length;++i){
                    result +=tmp[i]+"-";
                }
            }
            result = Mac;
            Logs.i(result+" result.length: "+result.length());
        }
        return result;
    }

    /**
     * ip
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && (inetAddress instanceof Inet4Address)) {
                        Logs.i("getLocalIpAddress() _ local IP : "+ inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Logs.e("","WifiPreference IpAddress :"+ex.toString());
        }
        return "";
    }

    /**
     * 版本号
     */
    /**
     * 获取软件版本号
     *
     * @return
     */
    public static int getLocalVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取屏幕宽,高
     */
    public static int[] getScreenSize(Context context){
        int arr[] = null;
        WindowManager manager =(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        if (width > 0 && height >0){
            Logs.d("screen ["+width+","+height+"]");
            arr = new int[]{width,height};
        }
        return arr;
    }


    /**
     * map -> uri
     */
    public static String mapTanslationUri(String ip,String port,Map<String,String> map){

        StringBuffer sb = new StringBuffer("http://");

        sb.append(ip).append(":").append(port).append("/").append("terminal/apply").append("?");

        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Object val = entry.getValue();
            sb.append(key.toString()).append("=").append(val.toString()).append("&");
        }
        return sb.toString().substring(0,sb.toString().lastIndexOf("&"));
    }

            //将Json数据解析成相应的映射对象
         public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
                 Gson gson = new Gson();
                 T result = gson.fromJson(jsonData, type);
                 return result;
         }

        //json -> list<T>
        public static <T> List<T> parseJonToList(String json, Class<T[]> clazz)
        {
            Gson gson = new Gson();
            T[] array = gson.fromJson(json, clazz);
            return Arrays.asList(array);
        }

    /**
     * 判断是否时base64 加密
     * 去除baseUri
     *
     */
    public static String justUriIsBase64GetUrl(String url){


        if (url.trim().lastIndexOf("=Base64")!=-1){
            url=url.trim().substring(0,url.lastIndexOf("=Base64"));
            Logs.e(TAG," delete Base64 -> url  "+url);
        }
        return url.trim();
    };
    /**
     *  内容 base64 解密
     */
    public static String justResultIsBase64decode(String result){

        try {
            byte[] byteIcon = Base64.decode(result,Base64.DEFAULT);
            return new String(byteIcon,"UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };

    /**
     * 把url转化为xml格式数据
     *
     * @param urlString
     * @return the xml data or "" if catch Exception
     */
    public static String uriTranslationString(String urlString) {
        URL url;
        String result = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            Log.e("",""+ "url errer :" +urlString +" \n cause: "+ e1.getMessage());
            return result;
        }
        URLConnection urlConnection;
        try {
            urlConnection = url.openConnection();
        } catch (IOException ioe) {

            Log.e("",""+ "url connect failed: " +  ioe.getMessage());
            return result;
        }

        InputStreamReader in = null;
        BufferedReader br = null;
        try {
            in = new InputStreamReader(urlConnection.getInputStream());
            br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = br.readLine()) != null){
                sb.append(temp);
            }
              result = sb.toString();
            return result;
        } catch (IOException e) {
            Log.e("",""+ "uriTranslationString() get input stream error:" +  e.getMessage());
            return result;
        } finally {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {

                Log.e("",""+ "uriTranslationXml close input stream error:" +  urlString+"cause:" +e.getMessage());
                e.printStackTrace();
            }
        }
    }




    //检查是不是ui线程
    public static boolean checkUiThread(){
        if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                return true;
        } else { // 非UI主线程
                return false;
        }
    }

    /**
     * 判断指定类型 是不是 数组中的某个类型
     * @param contentType
     * @param allowTypes
     * @return
     */
    private static boolean isValidSuffix(String contentType, String... allowTypes) {
        if (null == contentType || "".equals(contentType)) {
            return false;
        }
        for (String type : allowTypes) {
            if (contentType.endsWith(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断后缀是否是 mp4
     */
    public static boolean isMp4Suffix(String url){
        return isValidSuffix(url,".mp4");
    }
    /**
     * mp4 后缀->png后缀
     */
    public static String tanslationMp4ToPng(String url){
        return url.substring(0,url.lastIndexOf(".mp4"))+".png";
    }


    /**
     *SDCard/ xxx / xx.txt
     *
     * 读取 acesst路径下的文件 -> 保存到sd卡中
     */

    public static boolean ReadAssectsDataToSdCard(Context c,String dirPath,String fileName) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 得到资源中的assets数据流
            inputStream = c.getResources().getAssets().open(fileName);
            int length = inputStream.available();
            if (length==0){
                return false;
            }
            if(SdCardTools.MkDir(dirPath)){
                //目录创建 或者 存在
                fileOutputStream = new FileOutputStream(dirPath+fileName);
                byte[] buffer = new byte[1024];
                length = 0;
                while((length = inputStream.read(buffer)) > 0){
                    fileOutputStream.write(buffer, 0 ,length);
                }
                fileOutputStream.flush();
                System.out.println("ReadAssectsDataToSdCard() ----------success--------------");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    /**
     * 解压缩
     */
    @SuppressWarnings("unchecked")
    public static void UnZip(String zipFileName, String outputDirectory)
            throws IOException {

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFileName);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = null;
            File dest = new File(outputDirectory);
            dest.mkdirs();
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
                String entryName = zipEntry.getName();
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        File f = new File(outputDirectory + File.separator
                                + name);
                        f.mkdirs();
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator
                                    + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator
                                    + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        File f = new File(outputDirectory + File.separator
                                + zipEntry.getName());
                        // f.createNewFile();
                        in = zipFile.getInputStream(zipEntry);
                        out = new FileOutputStream(f);
                        int c;
                        byte[] by = new byte[1024];
                        while ((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }
                        out.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new IOException("解压失败：" + ex.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("解压失败：" + ex.toString());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                }
            }
        }
    }






}
