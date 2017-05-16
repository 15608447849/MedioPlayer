package com.wos.play.rootdir.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.Uitools.ImageUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user on 2016/12/1.
 */

public class ListViewAdapter extends BaseAdapter{

    private LayoutInflater inflater;

    public ListViewAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    //可使用 数据源
    private List<NewsDataBeans> useDataBeans ;
    //不可使用 数据源
    private List<NewsDataBeans> notDataBeans ;

    //添加数据源
    public void addUDataBean(NewsDataBeans data){
        if (useDataBeans==null){
            useDataBeans = new ArrayList<>();
        }
        for (NewsDataBeans bean : useDataBeans){
            if (bean.getFilePath().equals(data.getFilePath())){
                return;
            }
        }
        useDataBeans.add(data);
        notifyDataSetChanged();
    }
    //添加无效数据源
    public void addNDataBean(NewsDataBeans data){
        if (notDataBeans==null){
            notDataBeans = new ArrayList<>();
        }
        notDataBeans.add(data);
    }

    //index - 获取 数据源
    public NewsDataBeans getUData(int position){
        if (useDataBeans==null){
            return null;
        }
        return useDataBeans.get(position);
    }
    //index - 获取 无效数据源
    public NewsDataBeans getNta(String key){
        if (notDataBeans==null)return null;
        Iterator<NewsDataBeans> it = notDataBeans.iterator();
        NewsDataBeans beans = null;
        while (it.hasNext()){
            beans = it.next();
            if (beans.getFilePath().equals(key)){
                it.remove();
               break;
            }
            beans = null;
        }
        return beans;
    }

    @Override
    public int getCount() {
        if (useDataBeans ==null || useDataBeans.size()==0){
            return 0;
        }else {
            return useDataBeans.size();
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
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.news_item,null);
            holder.setImageview((ImageView) convertView.findViewById(R.id.item_image));
            holder.setTitle((TextView) convertView.findViewById(R.id.item_title));
            holder.setDate((TextView) convertView.findViewById(R.id.item_date));
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        //设置数据
        NewsDataBeans dataBeans = useDataBeans.get(position);
        //如果后台有给有缩略图
        if (dataBeans.getThumbPath() != null && !"".equals(dataBeans.getThumbPath()))
            holder.getImageview().setImageBitmap(ImageUtils.getBitmap(dataBeans.getThumbPath()));
        else
            holder.getImageview().setImageBitmap(ImageUtils.getBitmap(dataBeans.getFilePath()));
        holder.getTitle().setText(dataBeans.getTitle());
        holder.getDate().setText(dataBeans.getDateStr());
        return convertView;
    }





    private class ViewHolder {
        private ImageView imageview;
        private TextView title;
        private TextView date;

        public ImageView getImageview() {
            return imageview;
        }

        public void setImageview(ImageView imageview) {
            this.imageview = imageview;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }
    }

}
