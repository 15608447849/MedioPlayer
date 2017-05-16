package com.wos.play.rootdir.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import cn.trinea.android.common.util.FileUtils;
import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.video.MyVideoView;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.tool.AppsTools;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by user on 2016/12/1.
 * 使用 布局文件 加载布局
 */
public class CShowLayout {
    public static final String TAG = CShowLayout.class.getSimpleName();
    private Context context;
    private ViewGroup layout;
    private FrameLayout news_show;
    private ImageButton back;
    private TextView title;
    private TextView subtitle;
    private FrameLayout video_show;
    private MyVideoView video;
    private ListView imageList;
    private PDFShowListAdapter adapter;

    public CShowLayout(Context context, ViewGroup layout, View.OnClickListener buttonEvent) {
        this.context = context;
        this.layout = layout;
        initView(buttonEvent);
    }

    //初始化 视图 层
    private void initView(View.OnClickListener buttonEvent) {
        news_show = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.news_show_layout, null);
        back = (ImageButton) news_show.findViewById(R.id.news_show_back_btn);
        back.setOnClickListener(buttonEvent);
        title = (TextView) news_show.findViewById(R.id.news_show_title);
        subtitle = (TextView) news_show.findViewById(R.id.news_show_subtitle);
        video_show = (FrameLayout) news_show.findViewById(R.id.news_show_video);
        video = new MyVideoView(context);
        imageList = (ListView) news_show.findViewById(R.id.news_show_image);
        adapter = new PDFShowListAdapter(context);
        imageList.setAdapter(adapter);
        layout.addView(news_show);
    }


    //销毁数据
    public void destoryData() {

    }

    /**
     * 视图层 赋值
     */
    public void setData(NewsDataBeans data) {
        if (data == null) {
            return;
        }
        //设置标题
        title.setText(data.getTitle());
        subtitle.setText("作者 : " + data.getEditor() + "  日期 : " + data.getDateStr());
        if (AppsTools.isMp4Suffix(data.getFilePath())) {
            //shi mp4
            imageList.setVisibility(GONE);
            video_show.setVisibility(VISIBLE);
            if (FileUtils.isFileExist(data.getFilePath())) {
                video.start(video_show, data.getFilePath(), true);
            } else {
                video.start(video_show, UiTools.getDefVideoPath(), true);
            }
        } else {
            imageList.setVisibility(View.VISIBLE);
            video_show.setVisibility(View.GONE);
           if (data.getFileType().equals(NewsDataBeans.FileType.IMAGE)){
               adapter.addData(data.getFilePath(),true);
           }else{
               adapter.addData(data.getMoreFileList());
           }
        }

    }

    public FrameLayout getRootView() {
        return news_show;
    }

}
