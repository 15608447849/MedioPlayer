package com.wos.play.rootdir.model_application.ui.Uitools;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by user on 2016/12/9.
 */

public class ImageAsyLoad {
    public static void loadBitmap(String filePath, final ImageView imageview){
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
}
