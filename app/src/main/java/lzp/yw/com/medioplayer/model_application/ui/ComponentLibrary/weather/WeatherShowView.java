package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import lzp.yw.com.medioplayer.R;

/**
 * Created by user on 2016/11/23.
 */

public class WeatherShowView {

    public WeatherShowView(Context context, ViewGroup vp) {
        init(context,vp);
    }

    private ImageView image;
    private Bitmap bitmap;
    private TextView city;
    private TextView type;
    private TextView temperature;
    private TextView wind;

    private void init(Context context, ViewGroup vp) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_show_layout, null);
        image = (ImageView)view.findViewById(R.id.show_icon);
        city = (TextView) view.findViewById(R.id.city);
        type = (TextView) view.findViewById(R.id.type);
        temperature = (TextView) view.findViewById(R.id.temperature);;
        wind = (TextView) view.findViewById(R.id.wind);
        vp.addView(view);
    }

    /**
     *
     * @param val 城市,类型,温度,风力
     */
    public void setValue(String[] val, Bitmap bp){
        stop();
        city.setText(val[0]);
        type.setText(val[1]);
        temperature.setText(val[2]);
        wind.setText(val[3]);
        bitmap = bp;
        image.setImageBitmap(bitmap);
    }

    public void stop(){
        if (bitmap!=null){
            bitmap.recycle();
            bitmap=null;
        }
    }

}
