package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageAsyLoad;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;

import java.io.File;
import java.util.List;

/**
 * Created by user on 2017/1/4.
 * 电子报的activity的适配器
 */

public class EActivityGrallyAdpter extends BaseAdapter {

    private Context context;
    private List<File> list;

    public EActivityGrallyAdpter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    public File getSource(int pos) {
        if (list != null && pos < list.size() && pos >= 0) {
            return list.get(pos);
        }
        return null;
    }

    @Override
    public int getCount() {
        if (list == null || list.size() == 0) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //选择项
    private int selectItem;

    //设置选择项
    public void setSelectItem(int selectItem) {

        if (this.selectItem != selectItem) {
            this.selectItem = selectItem;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MeImageView iv = null;
        if (convertView == null) {
            iv = ImageUtils.createImageView(context,1);
            iv.setAdjustViewBounds(true);
            Gallery.LayoutParams params = new Gallery.LayoutParams(150, 150);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setPadding(2, 2, 2, 2);

            convertView = iv;
        } else {
            iv = (MeImageView) convertView;
        }

        final String thumbImagePath = getImagePath(getSource(position));
        ImageAsyLoad.loadBitmap(thumbImagePath, iv);
        if (selectItem == position) {
            iv.setBackgroundColor(Color.WHITE);
        } else {//未选中
            iv.setBackgroundColor(Color.BLACK);
        }
        return convertView;
    }


    //封面图
    public String getImagePath(File source) {
        if (source != null) {
            //循环遍历 - 找出 文件名 thumb_开头的文件
            String[] list = source.list();
            if (list != null && list.length > 0) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].contains(".png")) {
                        return source + "/" + list[i];
                    }
                }
            }
        }
        return UiTools.getDefImagePath();
    }

}
