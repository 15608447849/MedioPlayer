package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import lzp.yw.com.medioplayer.R;

/**
 * Created by user on 2016/12/1.
 * 使用 布局文件 加载布局
 */
public class CshowLayout{

    private Context context;
    private ViewGroup layout;
    private FrameLayout news_show;
    private Button back;
    private TextView title;
    private TextView subtitle;
    private FrameLayout video_show;
    private ImageView imageShow;


    public CshowLayout(Context context, ViewGroup layout, View.OnClickListener buttonEvent) {
        this.context = context;
        this.layout = layout;
        initView(buttonEvent);
    }
    //初始化 视图 层
    private void initView(View.OnClickListener buttonEvent) {
        news_show = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.news_show_layout,null);
        back = (Button) news_show.findViewById(R.id.news_show_backbtn);
        back.setOnClickListener(buttonEvent);
        title = (TextView) news_show.findViewById(R.id.news_show_title);
        subtitle = (TextView) news_show.findViewById(R.id.news_show_subtitle);
        video_show = (FrameLayout) news_show.findViewById(R.id.news_show_video);
        imageShow = (ImageView) news_show.findViewById(R.id.news_show_image);
        layout.addView(news_show);
    }


    //销毁数据
    public void destoryData() {

    }

    /**
     * 视图层 赋值
     */
    public void setData(){

    }

    public FrameLayout getRootView(){
        return news_show;
    }

}
