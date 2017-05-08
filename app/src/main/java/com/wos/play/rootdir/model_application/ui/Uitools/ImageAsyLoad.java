package com.wos.play.rootdir.model_application.ui.Uitools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.graphics.BitmapFactory.decodeFile;
import static com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils.computeSampleSize;

/**
 * Created by user on 2016/12/9.
 */

public class ImageAsyLoad {
    public static void loadBitmap(String filePath, final MeImageView imageview){
        Subscription subscription = getSubscription(imageview);
        if (subscription!=null){
            subscription.unsubscribe();
        }
        subscription = Observable.just(filePath)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String imageFilePath) {

                        return ImageUtils.getBitmap(imageFilePath);
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        imageview.setImageBitmap(bitmap);
                    }
                });
        addSubscription(imageview,subscription);
    }


    private static HashMap<ImageView,Subscription> subscriptionMap = null;
    private static void addSubscription(ImageView key,Subscription value){
        if (subscriptionMap==null){
            subscriptionMap = new HashMap<>();
        }
       subscriptionMap.put(key,value);
    }
    
    private static Subscription getSubscription(ImageView iv){
        Subscription val = null;
        if (subscriptionMap!=null){
         
            Iterator iter = subscriptionMap.entrySet().iterator();
            Map.Entry entry ;
            ImageView key ;
            while (iter.hasNext())
            {
                try {
                    entry = (Map.Entry) iter.next();
                    key = (ImageView)entry.getKey();
                    if (key.equals(iv)){
                        val = (Subscription)entry.getValue();
                        iter.remove();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
        }
        return val;
    }

    public static void clear(){
        if (subscriptionMap!=null){
            Iterator iter = subscriptionMap.entrySet().iterator();
            Map.Entry entry ;
            while (iter.hasNext())
            {
                try {
                    entry = (Map.Entry) iter.next();
                  ((Subscription)entry.getValue()).unsubscribe();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    iter.remove();
                }
            }
        }
    }

    public static void regionBitmap(Object ... objects) {
        final String TAG = "大图";
        Subscription subscription = getSubscription((MeImageView)objects[0]);
        if (subscription!=null){
            subscription.unsubscribe();
        }
        subscription = Observable.just(objects)
                .map(new Func1<Object[], Object[]>() {
                    @Override
                    public Object[] call(Object[] objects) {
                        try {
                            String path = (String) objects[1];
                            int sw = (int) objects[2];
                            int sh = (int)objects[3]; // 屏幕宽高
                            int displayDpi = (int) objects[4];//像素密度
                            int scale = (int) objects[5];
                            Log.e(TAG,"像素密度:"+displayDpi);
                            Log.e(TAG,"屏幕 大小:"+sw+" "+sh);
                            Log.e(TAG,"采样率:"+scale);

                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;
                            decodeFile(path,opts);
                            Log.e(TAG,"bitmap 大小:"+opts.outWidth+" "+opts.outHeight);
                            scale = computeSampleSize(opts, -1, sw*sh) / scale; //计算采样率
                            Log.e(TAG,"bitmap 采样率计算结果:"+scale);
                            opts.inSampleSize = scale;
                            opts.inJustDecodeBounds = false;
                            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;//Bitmap.Config，默认为ARGB_8888
                            opts.inPurgeable = true;//当存储Pixel的内存空间在系统内存不足时是否可以被回收
                            opts.inInputShareable = true;//inPurgeable为true情况下才生效，是否可以共享一个InputStream
                            opts.inDither = false;//是否抖动，默认为false
                            opts.inTempStorage = new byte[16 * 1024]; //解码时的临时空间，建议16*1024
                            opts.inScreenDensity = displayDpi;
                            opts.inScaled = true;
                            opts.inPreferQualityOverSpeed = false;
                            Bitmap bitmap = BitmapFactory.decodeFile(path,opts);
                            return new Object[]{objects[0],bitmap};
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;

                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object[]>() {
                    @Override
                    public void call(Object[] obj) {

                        if (obj!=null){
                            Log.e(TAG,"obj - "+obj[0]+" "+obj[1]);
                            ((MeImageView)obj[0]).setImageBitmap((Bitmap) obj[1]);
                        }

                    }
                });
        addSubscription((MeImageView) objects[0],subscription);
    }


}
