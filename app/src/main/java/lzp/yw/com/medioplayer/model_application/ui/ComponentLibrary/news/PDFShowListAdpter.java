package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;
import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather.LedImageView;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;

/**
 * Created by user on 2016/12/2.
 */

public class PDFShowListAdpter extends BaseAdapter{
    private Context context;

    public PDFShowListAdpter(Context context) {
        this.context = context;
    }

    //可使用 数据源
    private List<String> bitmapFilename ;

    //添加数据源
    public void addData(String data,boolean isClear){
        if (isClear){
            if (bitmapFilename!=null){
                bitmapFilename.clear();
                bitmapFilename=null;
            }
        }
        if (bitmapFilename==null){
            bitmapFilename = new ArrayList<>();
        }
        if (!bitmapFilename.contains(data)){
            bitmapFilename.add(data);
        }
        notifyDataSetChanged();
    }
    public void addData(String[] datas){

        if (bitmapFilename!=null){
            bitmapFilename.clear();
            bitmapFilename=null;
        }
       for (String filename: datas){
           addData(filename,false);
       }
    }
    @Override
    public int getCount() {
        if (bitmapFilename ==null || bitmapFilename.size()==0){
            return 0;
        }else {
            return bitmapFilename.size();
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
        LedImageView image = null;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.pdf_item_layout,null);
            image= (LedImageView) convertView.findViewById(R.id.pad_item_img);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            convertView.setTag(image);
        }else{
            image = (LedImageView) convertView.getTag();
        }

        if (FileUtils.isFileExist(bitmapFilename.get(position))){
            image.setImageBitmap(ImageUtils.getBitmap(bitmapFilename.get(position)));
        }else{
            image.setImageBitmap(ImageUtils.getBitmap(UiTools.getDefImagePath()));
        }
        return convertView;
    }



}
