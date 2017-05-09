package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.image.MeImageView;
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

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.epaper_list_item_layout, null);
            holder.image = (MeImageView) convertView.findViewById(R.id.thumb_image);
            holder.thumb_text_view = (TextView) convertView.findViewById(R.id.thumb_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String thumbImagePath = UiTools.getImagePath(getSource(position),true);
//      ImageAsyLoad.loadBitmap(thumbImagePath, holder.image);
        holder.image.setImageBitmap(ImageUtils.getBitmap(thumbImagePath));
        String path = getSource(position).getAbsolutePath();
        holder.thumb_text_view.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
        return convertView;
    }

    private class ViewHolder {
        public MeImageView image;
        TextView thumb_text_view;
    }

    public List<File> getList() {
        return list;
    }

}
