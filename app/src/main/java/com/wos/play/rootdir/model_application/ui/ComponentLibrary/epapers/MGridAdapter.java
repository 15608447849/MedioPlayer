package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.Uitools.BaseHolder;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by user on 2017/1/4.
 * 网格布局适配器
 */
public class MGridAdapter extends BaseAdapter {

    private Context context;

    public MGridAdapter(Context context) {
        this.context = context;
    }

    private ArrayList<File> fileList;

    public void setDataSource(ArrayList<File> list) {
        fileList = list;
        this.notifyDataSetChanged();
    }

    public File getItemFile(int index) {
        if (fileList != null && index < fileList.size() && index >= 0) {
            return fileList.get(index);

        }
        return null;
    }

    public String getItemFileName(int position) {
        if (fileList != null && position < fileList.size() && position >= 0) {
            return fileList.get(position).getName();
        }
        return "维护中";
    }

    public String getItemFileAbsPath(int position) {
        if (fileList != null && position <= fileList.size() && position >= 0) {
            return fileList.get(position).getAbsolutePath();
        }
        return null;
    }

    @Override
    public int getCount() {
        if (fileList != null && fileList.size() > 0) {
            return fileList.size();
        } else {
            return 0;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.mgridview_item_layout, parent, false);
        }
        TextView tv = BaseHolder.get(convertView, R.id.mgrid_grid_item_text);
        ImageView iv = BaseHolder.get(convertView, R.id.epaper_item_image);
        tv.setText(getItemFileName(position));
        iv.setImageBitmap(getBitmapByPosition(position));
        return convertView;
    }

    /**
     * 获取当天日期报纸图片
     */
    private Bitmap getBitmapByPosition(int position) {
        String filePath = UiTools.getDefImagePath();//取默认图片地址
        if (fileList != null && position < fileList.size() && position >= 0) {
            File[] files = fileList.get(position).listFiles();

            if (files != null && files.length > 0) {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                filePath = UiTools.getImagePath(files[0], true);//取第一张图片地址
                if (filePath.endsWith("def_image.png")) {
                    filePath = UiTools.getImagePath(files[0]);//取第一张图片地址
                }
            }
        }
        return ImageUtils.getBitmap(filePath);
    }
}
