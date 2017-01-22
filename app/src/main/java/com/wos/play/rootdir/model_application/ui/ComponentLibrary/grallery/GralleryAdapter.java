package com.wos.play.rootdir.model_application.ui.ComponentLibrary.grallery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/18.
 */

public class GralleryAdapter extends BaseAdapter {


    //文件名 下标
    private ArrayList<String> imageNameList = null;
    /**
     * bitmap list
     */
    private Context context ;
    private int selectItem;
    //构造
    GralleryAdapter(Context context){
        this.context = context;
    }

    //设置选择项
    public void setSelectItem(int selectItem) {

        if (this.selectItem != selectItem) {
            this.selectItem = selectItem;
            notifyDataSetChanged();
        }
    }
    //添加bitmap
    public void addBitmaps(String fileName) {
        if (imageNameList==null){
            imageNameList = new ArrayList<>();
        }
        //如果 不包含 这个文件名 添加
        if (!imageNameList.contains(fileName)){
            imageNameList.add(fileName);
        }
        notifyDataSetChanged();
    }


    public String getImageName(int position){
        if (imageNameList!=null){
            return imageNameList.get(position);
        }
        return null;
    }

    public Drawable getDrawable(int position){
        if (imageNameList!=null){
            return new BitmapDrawable(ImageUtils.getBitmap(imageNameList.get(position)));
        }
        return null;
    }

    public String getBitmapString(int position){
        if (imageNameList!=null) {
            return imageNameList.get(position);
        }
        return null;
    }
   @Override
    public int getCount() {
       if (imageNameList ==null || imageNameList.size()==0){
           return 0;
       }else {
           return imageNameList.size();
       }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MeImageView iv = null;
        if (convertView==null){
            iv= ImageUtils.createImageView(context);
            iv.setAdjustViewBounds(true);
            Gallery.LayoutParams params = new Gallery.LayoutParams(150, 150);
            iv.setLayoutParams(params);
            iv. setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setPadding(2,2,2,2);
            convertView = iv;
        }else{
            iv = (MeImageView) convertView;
        }

        iv.setImageBitmap(ImageUtils.getBitmap(imageNameList.get(position)));
        ImageAsyLoad.loadBitmap(imageNameList.get(position),iv);
        if(selectItem==position){
            iv.setBackgroundColor(Color.BLACK);
        }
        else{//未选中
            iv.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}