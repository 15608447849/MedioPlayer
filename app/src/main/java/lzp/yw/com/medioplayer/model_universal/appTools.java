package lzp.yw.com.medioplayer.model_universal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
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

/**
 * Created by user on 2016/10/26.
 */
public class appTools {

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
     * http 同步请求
     * lzp
     *
     */

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














}
