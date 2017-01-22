package com.wos.play.rootdir.model_application.ui.Uitools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.button.MeImageButton;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.DoubleScaleImageView;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.UiStore.ImageStore;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by user on 2016/11/11.
 */

public class ImageUtils {

    public static Bitmap getBitmap(String filepath) {
        Bitmap bitmap = ImageStore.getInstants().getBitmapCache(filepath);
        if (bitmap==null || bitmap.isRecycled()){
            bitmap = getBitmap(new File(AppsTools.isMp4Suffix(filepath)?AppsTools.tanslationMp4ToPng(filepath):filepath));
            ImageStore.getInstants().addBitmapCache(filepath,bitmap);
        }
        return bitmap;
    }
    /**
     * 获取一个 bitmap
     * 成功返回turn
     *
     * */
    public static Bitmap getBitmap(File file) {

        FileInputStream is = null;
        Bitmap bitmap = null;
        try{
            if (file != null && file.exists()) {
                is = new FileInputStream(file);
                bitmap = createImageThumbnail(is);
                if (bitmap == null){
                    throw new Exception("无法获取bitmap 原因未知");
                }
            }
        }catch (Exception e){
           e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }
    private static Bitmap createImageThumbnail(FileInputStream is){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(),
                    null, opts);

            opts.inSampleSize = computeSampleSize(opts, -1, 1920*1080);
            opts.inJustDecodeBounds = false;

            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inDither = false;
            opts.inTempStorage = new byte[12 * 1024];

            bitmap =  BitmapFactory.decodeFileDescriptor(is.getFD(),
                    null, opts);
        }catch (Exception e) {
            // TODO: handle exception
            Log.e(""," create bitmap err: "+e.getMessage());
        }

        return bitmap;
    }
    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {// 最小边长 最大像素
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }
    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    //放入主线程 移除资源
    public static void removeImageViewDrawable(ImageView imageView) {
        if (!AppsTools.checkUiThread()){
            return;
        }
        Logs.i("imageUtils","回收图片bitmap...");
        //资源回调的地方
        Bitmap bitmap = null;
        Drawable drawable = imageView.getDrawable();
        if (drawable == null){
            drawable = imageView.getBackground();
            if (drawable == null){
                imageView.setDrawingCacheEnabled(true);
                bitmap = imageView.getDrawingCache();
                imageView.setDrawingCacheEnabled(false);
                if (bitmap==null){
                    return;
                }
            }
        }
        if (drawable != null && drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            drawable.setCallback(null);
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        imageView.setBackgroundResource(0);
        imageView.setImageDrawable(null);
        Logs.i("imageUtils","回收图片bitmap...完成");
    }
    //创建 imageview
    public static MeImageView createImageView(Context context){
        return new MeImageView(context);
    }
    //创建 imageview
    public static MeImageView createImageViewScale(Context context){
       // return new MeImageView(context);
        return new DoubleScaleImageView(context);
    }
    //创建imagebutton
    public static ImageButton createImagerButton(Context context){
        return new MeImageButton(context);
    }










}