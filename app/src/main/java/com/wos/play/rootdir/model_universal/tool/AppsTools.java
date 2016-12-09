package com.wos.play.rootdir.model_universal.tool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
     * 把url转化为文本格式数据
     *
     * @param urlString
     * @return the xml data or "" if catch Exception
     */
    public static String uriTransionString(String urlString,Map<String,String> header,Map<String,String> ParamMap) {
        URL url;
        String result = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return result;
        }
        System.out.println("URL success :"+urlString);
        HttpURLConnection httpUrlConnection;
        OutputStream out = null;
        BufferedReader br = null;
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();

            if (header!=null){
                Iterator iter = header.entrySet().iterator();
                String key;
                String val;
                while (iter.hasNext())
                {
                    Map.Entry<String,String> entry = (Map.Entry) iter.next();
                    key = entry.getKey();
                    val = entry.getValue();
                    System.out.println(key+" = "+val);
                    httpUrlConnection.setRequestProperty(key,val);
                }
            }

            // HttpURLConnection是基于HTTP协议的，其底层通过socket通信实现。如果不设置超时（timeout），在网络异常的情况下，可能会导致程序僵死而不继续往下执行。可以通过以下两个语句来设置相应的超时：
           // System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
           //System.setProperty("sun.net.client.defaultReadTimeout", "30000");
           httpUrlConnection.setConnectTimeout(30000);
           httpUrlConnection.setReadTimeout(60000);
            httpUrlConnection.setRequestProperty("Accept-Charset", "GBK");  //设置编码语言
            httpUrlConnection.setRequestProperty("Connection", "keep-alive");  //设置连接的状态
        // 设置是否从httpUrlConnection读入，默认情况下是true;
            httpUrlConnection.setDoInput(true);
        // 设定传送的内容类型是可序列化的java对象
        // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
       // httpUrlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            httpUrlConnection.setRequestProperty("Content-type", "text/html");
        //设置主体参数
        if (ParamMap!=null){
            //获取map 生成json
            String json = mapToJson(ParamMap);
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            httpUrlConnection.setDoOutput(true);
            // 设定请求的方法为"POST"，默认是GET
            httpUrlConnection.setRequestMethod("POST");// 可以根据需要 提交 GET、POST、DELETE、PUT等http提供的功能
            // Post 请求不能使用缓存
            httpUrlConnection.setUseCaches(false);

            httpUrlConnection.setRequestProperty("X-Auth-Token", "token");  //设置请求的token
            httpUrlConnection.setRequestProperty("Transfer-Encoding", "chunked");//设置传输编码
            httpUrlConnection.setRequestProperty("Content-Length", String.valueOf(json.getBytes().length));//设置文件请求的长度
            //对connection对象的一切配置（那一堆set函数）
            //都必须要在connect()函数执行之前完成。而对outputStream的写操作，又必须要在inputStream的读操作之前。
            // 此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，
            //所以在开发中不调用上述的connect()也可以)。
            httpUrlConnection.connect();//只是建立了一个与服务器的tcp连接没有实际发送http请求。
            //写数据
            out = httpUrlConnection.getOutputStream();
            out.write(json.getBytes());
            out.flush();
        }else{
            httpUrlConnection.connect();//只是建立了一个与服务器的tcp连接没有实际发送http请求。
        }
            System.out.println("httpUrlConnection connect()");
            //连接
            if (httpUrlConnection.getResponseCode()==200){
            br = new BufferedReader( new InputStreamReader(httpUrlConnection.getInputStream(),"UTF-8"));//<===注意，实际发送请求的代码段就在这里
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = br.readLine()) != null){
                sb.append(temp);
            }
              result = sb.toString();
            }else{
                System.err.println("请求失败 - "+httpUrlConnection.getResponseCode());
            }
            //断开连接
            httpUrlConnection.disconnect();
            System.out.println("httpUrlConnection disconnect()");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (br != null)
                    br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将Map转化为Json
     *
     * @param map
     * @return String
     */
    public static <T> String mapToJson(Map<String, T> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
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
     * 判断后缀是不是md5
     */
    public static boolean isMD5Suffix(String url){
        return isValidSuffix(url,".md5");
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

    //生成城市url
    public static String generWeateherContentUrl(String city) {
        System.out.println("当前城市: "+city);
        try {
            city = URLEncoder.encode(city,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "http://apis.baidu.com/apistore/weatherservice/recentweathers?cityname="+city;
    }

    private static Map<String,String> baiduApiMap;
    //百度api 天气 appkey
    public static Map<String,String> baiduApiMap(){
        if (baiduApiMap==null){
            baiduApiMap = new HashMap<>();
            baiduApiMap.put("apikey","3a36d13c23c8065dcf1a1f584f5a5092");
        }
       return baiduApiMap;
    }

    //unicode 解码
    public static String justResultIsUNICODEdecode(String res) {
        return UnicodeUtils.decodeUnicode(res);
    }



    //url encode
    public static String urlEncodeParam(String url){
        //http://172.16.0.216:9000/epaper/dyannews/page?stairId=3&categoryId=-1&sortBy=allSorts asc,upDate desc&filter=Base64
        //先截取?
        //在截取&
        //再截取=
        String var1 ;
        try {
            var1 = url.substring(0,url.indexOf("?")+1);
            String var3[] = url.substring(url.indexOf("?")+1).split("&");
            if (var3!=null && var3.length>0){
                for (int i=0;i<var3.length;i++){
                    if (var3[i].contains("=")){
                        var1= var1 + var3[i].substring(0,var3[i].indexOf("=")+1) + URLEncoder.encode(var3[i].substring(var3[i].indexOf("=")+1),"UTF-8");
                    }
                    if (i!=var3.length-1){
                        var1+="&";
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
        return var1;
    }


}
