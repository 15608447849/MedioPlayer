package lzp.yw.com.medioplayer.model_universal;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 *<p>Title:MD5Util </p>
 *<p>Description: </p>
 *<p>Company: </p> 
 * @author xieyg
 * @date 下午3:06:56
 */
public class MD5Util {
    private static String TAG = "MD5_UTILS";

	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 对外提供getMD5(String)方法
     * @author randyjia
     *
     */

    public static String getStringMD5(String val) {
        MessageDigest md5 = null;
        String str = null;
        try {
            md5 = MessageDigest.getInstance("MD5");

            md5.update(val.getBytes());
            byte[] m = md5.digest();//加密
            str = toHexString(m);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str==null?"null":str;
    }

    public static String getFileMD5String(File file) {
    	BufferedWriter bw = null;
        String md5Code = null;
        try{
            if (!file.exists()){
                Log.e(TAG,"生成MD5 的 资源文件不存在 :" + file.toString());
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            byte [] buffer =  new   byte [ 1024 ];
            int  numRead =  0 ;
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            while  ((numRead = in.read(buffer)) >  0 ) {
                messagedigest.update(buffer, 0 , numRead);
            }
            in.close();
            md5Code = toHexString(messagedigest.digest());
        } catch (Exception e){
            e.printStackTrace();
        }finally{
        	try {
                if (bw != null){
                    bw.close();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return md5Code;
    }

    public static String readFileByLines(String fileName) {
        StringBuffer sb = new StringBuffer();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                sb.append(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }
    public static int FTPMD5(String sourcefile_md5code, String dirFile) {
        String dstr = readFileByLines(dirFile);
        if (sourcefile_md5code.equals(dstr)){
            deleteFile(dirFile);
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 删除本地文件
     * @param fileName
     * @return
     */
    public static void deleteFile(String fileName) {
        try {
            Log.e(TAG," 删除文件 - "+fileName);
            File file = new File(fileName);
            if (file.exists()) {
                Log.e(TAG," 删除文件 - "+fileName +" * success");
                file.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, "delete file error with exception" + e.toString()
                    + "on file:" + fileName);
        }
    }
}
