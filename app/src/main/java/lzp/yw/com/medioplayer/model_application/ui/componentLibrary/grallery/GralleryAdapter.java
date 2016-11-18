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

    private ArrayList<Drawable> imageArr = null;
    private Context context ;
    GralleryAdapter(Context context){
        this.context = context;
        imageArr = new ArrayList<>();
    }

    //转换bitmap
    public void tansBitmapToDraw(ArrayList<Bitmap> bitmapList){
        if (imageArr==null){
            imageArr = new ArrayList<>();
        }
        imageArr.clear();
           for (Bitmap bitmap : bitmapList){
               imageArr.add(new BitmapDrawable(bitmap));
           }

        notifyDataSetChanged();
    }
    public Drawable getDrawable(int position){
        if (imageArr!=null){
            return imageArr.get(position);
        }
        return null;
    }
   @Override
    public int getCount() {
       if (imageArr==null || imageArr.size()==0){
           return 0;
       }else {
           return imageArr.size();
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
            iv.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
            iv.setBackgroundColor(Color.BLACK);
            convertView = iv;
        }else{
            iv = (ImageView) convertView;
        }
        iv.setImageDrawable(imageArr.get(position));
        return convertView;
    }
}
