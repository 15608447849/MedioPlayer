package com.wos.play.rootdir.model_application.ui.ComponentLibrary.gallery;

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
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.content_gallery.DataObjsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/18.
 */

public class GalleryAdapter extends BaseAdapter {


    //文件名 下标
    private ArrayList<String> imageNameList = null;

    //保存图集列表信息
    private List<DataObjsBean> list = null;
    /**
     * bitmap list
     */
    private Context context ;
    private int selectItem;
    //构造
    GalleryAdapter(Context context){
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
            iv= ImageUtils.createImageView(context,1);
            iv.setAdjustViewBounds(true);
            Gallery.LayoutParams params = new Gallery.LayoutParams(150, 150);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setPadding(2,2,2,2);
            convertView = iv;
        }else{
            iv = (MeImageView) convertView;
        }

        //---------图集缩略图修改----------
        String fileName = UiTools.getUrlTanslationFilename(list.get(position).getUrl());//图集文件
        String thunName = UiTools.getUrlTanslationFilename(list.get(position).getImageUrl());//缩略图文件
        iv.setImageBitmap(ImageUtils.getBitmap(fileName));
        if (thunName != null && !"".equals(thunName))
            ImageAsyLoad.loadBitmap(thunName,iv);
        else
            ImageAsyLoad.loadBitmap(fileName,iv);

        /*iv.setImageBitmap(ImageUtils.getBitmap(imageNameList.get(position)));
        ImageAsyLoad.loadBitmap(imageNameList.get(position),iv);*/
        if(selectItem==position){
            iv.setBackgroundColor(Color.BLACK);
        }
        else{//未选中
            iv.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }

    /**
     * 获取图集列表信息
     * @param dataObjs
     */
    public void getDataObjsBean(List<DataObjsBean> dataObjs) {
        list = dataObjs;
    }
}
