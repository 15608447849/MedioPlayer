package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wos.play.rootdir.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 2017/1/4.
 * 网格布局适配器
 */
public class MgridAdptes extends BaseAdapter {


    private Context context ;

    public MgridAdptes(Context context) {
        this.context = context;
    }

    private ArrayList<File> fileList ;

    public void setDataSource(ArrayList<File> list){
        fileList = list;
        this.notifyDataSetChanged();
    }
    public File getItemFile(int index){
        if (fileList!=null && index<fileList.size() && index>=0){
            return fileList.get(index);

        }
        return null;
    }
    public String getItemFileName(int position){
        if (fileList!=null && position<fileList.size() && position>=0){
            return fileList.get(position).getName();
        }
        return "维护中";
}
    public String getItemFileAbsPath(int position){
        if (fileList!=null && position<=fileList.size() && position>=0){
            return fileList.get(position).getAbsolutePath();
        }
        return null;
    }

    @Override
    public int getCount() {
        if (fileList!=null && fileList.size() > 0){
            return fileList.size();
        }else{
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
        ViewHolder holder = null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.mgridview_item_layout,null);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.mgrid_grid_item_text);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(getItemFileName(position));
        return convertView;
    }






    //视图持有者
    private class ViewHolder{
        TextView content; //内容 - 文件的文件名
    }



}
