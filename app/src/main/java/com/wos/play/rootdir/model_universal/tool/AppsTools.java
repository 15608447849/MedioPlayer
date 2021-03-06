package com.wos.play.rootdir.model_universal.tool;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import cn.trinea.android.common.util.FileUtils;


/**
 * Created by user on 2016/10/26.
 */
public class AppsTools {
    private static final String TAG = AppsTools.class.getSimpleName();
    //随机数
    public static int randomNum(int min, int max) {
        return (int) (min + Math.random() * max);
    }

    private static String callCmd(String cmd, String filter) {
        String result = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(is);

            String line;
            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && !line.contains(filter)) {
                //result += line;
            }
            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取 mac 地址
    public static String getMacAddress(Context context) {

        String mac = getLocalMacAddressFromWifiInfo(context);
        if (mac == null || "".equals(mac))
            mac = getMacAddress();

        return mac;
    }

    //本地以太网mac地址文件
    private static String getMacAddress() {
        String strMacAddress = "";
        byte[] b;
        try {
            NetworkInterface NIC = NetworkInterface.getByName("eth0");
            b = NIC.getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0 || i != b.length - 1) {
                    buffer.append('-');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddress = buffer.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return strMacAddress;
    }

    //根据Wifi信息获取本地Mac
    public static String getLocalMacAddressFromWifiInfo(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    /**
     * ip
     *
     * @return
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = networkInterface
                        .getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && (inetAddress instanceof Inet4Address)) {
                        Logs.i("getLocalIpAddress() _ local IP : " + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Logs.e("", "WifiPreference IpAddress :" + ex.toString());
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
     * 获取软件版本号
     *
     * @return
     */
    public static String getLocalVersionName(Context context) {
        String versionCode = "1.0.0";
        try {
            // 获取软件版本号
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    /**
     * 获取屏幕宽,高
     */
    public static int[] getScreenSize(Context context) {
        int arr[] = null;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        if (width > 0 && height > 0) {
            Logs.d("screen [" + width + "," + height + "]");
            arr = new int[]{width, height};
        }
        return arr;
    }


    /**
     * map -> uri
     */
    public static String mapTanslationUri(String ip, String port, Map<String, String> map) {

        StringBuilder sb = new StringBuilder("http://");
        sb.append(ip).append(":").append(port).append("/").append("terminal/apply").append("?");
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Object val = entry.getValue();
            sb.append(key.toString()).append("=").append(val.toString()).append("&");
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf("&"));
    }

    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    //json -> list<T>
    public static <T> List<T> parseJonToList(String json, Class<T[]> clazz) {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }

    /**
     * 判断是否时base64 加密
     * 去除baseUri
     */
    public static String justUriIsBase64GetUrl(String url) {
        if (url.trim().lastIndexOf("=Base64") != -1) {
            url = url.trim().substring(0, url.lastIndexOf("=Base64"));
            Logs.e(TAG, " delete Base64 -> url  " + url);
        }
        return url.trim();
    }

    ;

    /**
     * 内容 base64 解密
     */
    public static String justResultIsBase64decode(String result) {

        try {
            byte[] byteIcon = Base64.decode(result, Base64.DEFAULT);
            return new String(byteIcon, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void urlFileUpload(String uploadUrl, String filepath) {
        if (!FileUtils.isFileExist(filepath)) {
            return;
        }
        FileInputStream inputStream = null;
        OutputStream outStream = null;
        try {
            URL url = new URL(uploadUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            con.setRequestProperty("Charset", "UTF-8");
            con.setConnectTimeout(30000);
            con.setReadTimeout(60000);
            con.setRequestProperty("Connection", "keep-alive");  //设置连接的状态
            con.setRequestProperty("Content-Type", "application/octet-stream");
            // 头字段
//            con.setRequestProperty("Accept", "*/*");
//            con.setRequestProperty("Accept-Charset", "UTF-8,*;q=0.5");
//            con.setRequestProperty("Accept-Encoding", "gzip,deflate");
//            con.setRequestProperty("Accept-Language", "zh-CN");
//            con.setRequestProperty("User-Agent", "Android");

            inputStream = new FileInputStream(filepath);
            outStream = con.getOutputStream();
            byte[] cache = new byte[1024];
            int len;
            while ((len = inputStream.read(cache)) != -1) {
                outStream.write(cache, 0, len);
            }
            outStream.flush();
            if ((len = con.getResponseCode()) == 200) {
                System.out.println("文件上传成功 - [" + filepath + "]");
            } else {
                System.err.println("文件上传失败 - 错误码 :[" + len + "]");
            }

            //断开连接
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.w(TAG,e);
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    Log.w(TAG,e);
                }
            }
        }
    }

    /**
     * 把url转化为文本格式数据
     *
     * @param urlString trans
     * @return the xml data or "" if catch Exception
     */
    public static String uriTransString(String urlString, Map<String, String> header, Map<String, String> ParamMap) {
        if (urlString == null) return null;

        URL url;
        String result = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e1) {
            System.out.println("URL connect fail :" + urlString);
            System.err.println(e1.getMessage());
            return null;
        }

        HttpURLConnection httpUrlConnection;
        OutputStream out = null;
        BufferedReader br = null;
        try {
            httpUrlConnection = (HttpURLConnection) url.openConnection();

            if (header != null) {
                Iterator iterator = header.entrySet().iterator();
                String key;
                String val;
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                    key = entry.getKey();
                    val = entry.getValue();
                    System.out.println(key + " = " + val);
                    httpUrlConnection.setRequestProperty(key, val);
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
            if (ParamMap != null) {
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
            } else {
                httpUrlConnection.connect();//只是建立了一个与服务器的tcp连接没有实际发送http请求。
            }
//            System.out.println("httpUrlConnection connect()");
            //连接
            if (httpUrlConnection.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));//<===注意，实际发送请求的代码段就在这里
                StringBuilder sb = new StringBuilder();
                String temp;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp);
                }
                result = sb.toString();
            } else {
                System.err.println("请求失败 - " + httpUrlConnection.getResponseCode());
            }
            //断开连接
            httpUrlConnection.disconnect();
//            System.out.println("httpUrlConnection disconnect()");
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
     * 将Map转化为Json文本
     *
     * @param map
     * @return String
     */
    public static <T> String mapToJson(Map<String, T> map) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }

    /**
     * 函数名称: parseData
     * 函数描述: 将json字符串转换为map
     *
     * @param data
     * @return
     */
    public static HashMap<String, String> jsonTxtToMap(String data) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        HashMap<String, String> map = g.fromJson(data, new TypeToken<HashMap<String, String>>() {
        }.getType());
        return map;
    }

    //检查是不是ui线程
    public static boolean checkUiThread() {
        // UI主线程 非UI主线程
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 判断指定类型 是不是 数组中的某个类型
     *
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
    public static boolean isMD5Suffix(String url) {
        return isValidSuffix(url, ".md5");
    }

    /**
     * 判断后缀是否是 mp4
     */
    public static boolean isMp4Suffix(String url) {
        return isValidSuffix(url, ".mp4");
    }

    /**
     * mp4 后缀->png后缀
     */
    public static String tanslationMp4ToPng(String url) {
        return url.substring(0, url.lastIndexOf(".mp4")) + ".png";
    }


    /**
     * SDCard/ xxx / xx.txt
     * <p>
     * 读取 acesst路径下的文件 -> 保存到sd卡中
     */

    public static boolean ReadAssectsDataToSdCard(Context c, String dirPath, String fileName) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // 得到资源中的assets数据流
            inputStream = c.getResources().getAssets().open(fileName);
            int length = inputStream.available();
            if (length == 0) {
                return false;
            }
            if (SdCardTools.MkDir(dirPath)) {
                //目录创建 或者 存在
                fileOutputStream = new FileOutputStream(dirPath + fileName);
                byte[] buffer = new byte[1024];
                length = 0;
                while ((length = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                fileOutputStream.flush();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
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


    public static String mUrlEncode(String param) {
        if (justParamIsURLEncode(param)) {
            try {
                param = URLEncoder.encode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return param;
    }

    //判断参数 是否 encode
    public static boolean justParamIsURLEncode(String param) {
        try {
            if (param.contains("+")) { //需要编码
                return true;
            }
            String var1 = URLDecoder.decode(param, "UTF-8");

            if (var1.equals(param)) {
                return true;  //需要encode
            } else {
                return false;//不需要
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return true;
    }

    //生成->城市url
    public static String generWeateherContentUrl(String city) {
        System.out.println("-当前城市 [" + city + "]");
        city = mUrlEncode(city);
//        return "http://apis.baidu.com/apistore/weatherservice/recentweathers?cityname="+city;
        return "http://wthrcdn.etouch.cn/weather_mini?city=" + city;

    }


    //unicode 解码
    public static String justResultIsUNICODEdecode(String res) {
        return UnicodeUtils.decodeUnicode(res);
    }


    //url encode
    public static String urlEncodeParam(String url) {
        //http://172.16.0.216:9000/epaper/dyannews/page?stairId=3&categoryId=-1&sortBy=allSorts asc,upDate desc&filter=Base64
        //先截取?
        //在截取&
        //再截取=
        String var1;
        try {
            var1 = url.substring(0, url.indexOf("?") + 1);
            String var3[] = url.substring(url.indexOf("?") + 1).split("&");
            if (var3.length > 0) {
                for (int i = 0; i < var3.length; i++) {
                    if (var3[i].contains("=")) {
                        var1 = var1 + var3[i].substring(0, var3[i].indexOf("=") + 1)
                                + mUrlEncode(var3[i].substring(var3[i].indexOf("=") + 1));
                    }
                    if (i != var3.length - 1) {
                        var1 += "&";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
        return var1;
    }


    public static String printTimes(long millisecond) {
        StringBuilder sb = new StringBuilder();

        sb.append("时间差约等-:");
        String val = "";
        if (millisecond < 1000) {
            val = " 毫秒";
        } else if ((millisecond = millisecond / 1000) > 0 && millisecond < 60) {
            //毫秒 -> 秒
            val = " 秒";
        } else if ((millisecond = millisecond / 60) > 0 && millisecond < 60) {
            val = " 分钟";
        } else if ((millisecond = millisecond / 60) > 0 && millisecond < 12) {
            val = " 小时";
        } else if ((millisecond = millisecond / 24) > 0) {
            val = " 天";
        }
        sb.append(millisecond).append(val);
        return sb.toString();
    }


    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //获取短整形
    private static int getShort(byte[] data) {
        return  ((data[0] << 8) | data[1] & 0xFF);
    }

    //天气api结果解析
    public static String getJsonStringFromGZIP(String content) {
        String jsonString = null;
        try {
            InputStream is = new ByteArrayInputStream(content.getBytes());
            BufferedInputStream bis = new BufferedInputStream(is);
            bis.mark(2);
            // 取前两个字节
            byte[] header = new byte[2];
            int result = bis.read(header);
            // reset输入流到开始位置
            bis.reset();
            // 判断是否是GZIP格式
            int headerData = getShort(header);
            if (result != -1 && headerData == 0x1f8b) {
                is = new GZIPInputStream(bis);
            } else {
                is = bis;
            }
            InputStreamReader reader = new InputStreamReader(is, "utf-8");
            char[] data = new char[100];
            int readSize;
            StringBuilder sb = new StringBuilder();
            while ((readSize = reader.read(data)) > 0) {
                sb.append(data, 0, readSize);
            }
            jsonString = sb.toString();
            bis.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    //获取域名
    public static String getDomain(String url) {

        if (url == null) return null;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            url = url.substring(url.indexOf("/") + 2);
        }
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf("/"));
        }
        int length = url.split("\\.").length;
        if(url.endsWith("com.cn")  ||url.endsWith("org.cn")
                || url.endsWith("net.cn")  ||url.endsWith("gov.cn")){
            if(length==4) url = url.substring(url.indexOf(".")+1);
        }else {
            if(length==3) url = url.substring(url.indexOf(".")+1);
        }
        return url;
    }


}
