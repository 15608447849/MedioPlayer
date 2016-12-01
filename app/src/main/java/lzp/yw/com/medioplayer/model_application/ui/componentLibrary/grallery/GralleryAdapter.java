package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.grallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;

/**
 * Created by user on 2016/11/18.
 */

public class GralleryAdapter extends BaseAdapter {


    //文件名 下标
    private ArrayList<String> imageNameList = null;
    /**
     * bitmap list
     */
    private LinkedHashMap<String,Bitmap> bitmapMap = null;
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
    public void addBitmaps(String fileName,Bitmap bitmap) {
        if (bitmapMap == null) {
            bitmapMap = new LinkedHashMap<>();
        }
        if (imageNameList==null){
            imageNameList = new ArrayList<>();
        }
        //如果 不包含 这个文件名 添加
        if (!bitmapMap.containsKey(fileName)){

            imageNameList.add(fileName);
            bitmapMap.put(fileName,bitmap);
        }
        notifyDataSetChanged();
    }

    //清理bitmap
    public void removeBitmaps() {
        if (bitmapMap != null && imageNameList!=null) {
            for (Iterator<Map.Entry<String, Bitmap>> it = bitmapMap.entrySet().iterator(); it.hasNext();){
                Map.Entry<String, Bitmap> item = it.next();
                imageNameList.remove(item.getKey());
                item.getValue().recycle();
                it.remove();
            }
        }
    }
    //获取bitmap
    public Bitmap getBitmap(int position){
        if (bitmapMap !=null && imageNameList!=null){
            return bitmapMap.get(imageNameList.get(position));
        }
        return null;
    }

    public String getImageName(int position){
        if (imageNameList!=null){
            return imageNameList.get(position);
        }
        return null;
    }

    public Drawable getDrawable(int position){
        if (getBitmap(position)!=null){
            return new BitmapDrawable(getBitmap(position));
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
        ImageView iv = null;
        if (convertView==null){
            iv= ImageUtils.createImageView(context);
            iv.setAdjustViewBounds(true);
//            iv.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
            Gallery.LayoutParams params = new Gallery.LayoutParams(150, 150);
            iv.setLayoutParams(params);
            iv. setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setPadding(2,2,2,2);
            convertView = iv;
        }else{
            iv = (ImageView) convertView;
        }

        iv.setImageBitmap(bitmapMap.get(imageNameList.get(position)));
        if(selectItem==position){
            iv.setBackgroundColor(Color.BLACK);
        }
        else{//未选中
            iv.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}
