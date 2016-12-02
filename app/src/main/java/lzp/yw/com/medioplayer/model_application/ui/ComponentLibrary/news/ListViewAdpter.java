package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;

/**
 * Created by user on 2016/12/1.
 */

public class ListViewAdpter extends BaseAdapter{

    private LayoutInflater inflater;

    public ListViewAdpter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    //可使用 数据源
    private List<NewsDataBeans> useDatas ;
    //不可使用 数据源
    private List<NewsDataBeans> notDatas ;

    //添加数据源
    public void addUdataBean(NewsDataBeans data){
        if (useDatas==null){
            useDatas = new ArrayList<>();
        }
        useDatas.add(data);
    }
    //添加无效无效数据源
    public void addNdataBean(NewsDataBeans data){
        if (notDatas==null){
            notDatas = new ArrayList<>();
        }
       notDatas.add(data);
    }

    //index - 获取 数据源
    public NewsDataBeans getUdata(int position){
        if (useDatas==null){
            return null;
        }
        return useDatas.get(position);
    }
    //index - 获取 无效数据源
    public NewsDataBeans getNta(String key){
            if (notDatas==null){
                return null;
            }
        Iterator<NewsDataBeans> it = notDatas.iterator();
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
        if (useDatas ==null || useDatas.size()==0){
            return 0;
        }else {
            return useDatas.size();
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
        NewsDataBeans dataBeans = useDatas.get(position);
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
