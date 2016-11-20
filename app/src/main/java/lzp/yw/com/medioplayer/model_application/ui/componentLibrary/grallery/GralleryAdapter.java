package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.grallery;

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

import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;

/**
 * Created by user on 2016/11/18.
 */

public class GralleryAdapter extends BaseAdapter {

    private ArrayList<Bitmap> bitmaps = null;
    private Context context ;
    private int selectItem;

    GralleryAdapter(Context context){
        this.context = context;
        this.bitmaps = bitmaps;
    }

    public void setSelectItem(int selectItem) {

        if (this.selectItem != selectItem) {
            this.selectItem = selectItem;
            notifyDataSetChanged();
        }
    }
    //设置bitmap
    public void settingBitmaps(ArrayList<Bitmap> bitmapList){
        if (bitmaps==null){
            bitmaps = new ArrayList<>();
        }
        for (Bitmap bitmap:bitmapList){
            if (!bitmaps.contains(bitmap)){
                bitmaps.add(bitmap);
            }
        }
        notifyDataSetChanged();
    }
    public Drawable getDrawable(int position){
        if (getBitmap(position)!=null){
            return new BitmapDrawable(getBitmap(position));
        }
        return null;
    }
    public Bitmap getBitmap(int position){
        if (bitmaps !=null){
            return bitmaps.get(position);
        }
        return null;
    }
   @Override
    public int getCount() {
       if (bitmaps ==null || bitmaps.size()==0){
           return 0;
       }else {
           return bitmaps.size();
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

        iv.setImageBitmap(bitmaps.get(position));
        if(selectItem==position){
            iv.setBackgroundColor(Color.BLACK);
        }
        else{//未选中
            iv.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}
