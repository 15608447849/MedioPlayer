package com.wos.play.rootdir.model_application.ui.ComponentLibrary.textshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wos.play.rootdir.R;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IContentView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.MediaInterface;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;

/**
 * Created by user on 2016/11/15.
 * 上下滚动 文本框
 */
public class TextScrollView extends ScrollView implements IContentView{

    private Context context;
    private int length;
    public TextScrollView(Context context,ContentsBean content) {// ViewGroup vp
        super(context);
        this.context = context;
        initData(content);
    }
    @Override
    public int getLength() {
        return length==0?30:length;
    }

    @Override
    public void setMediaInterface(MediaInterface bridge) {

    }

    @Override
    public void initData(Object object) {
        try {
            ContentsBean contents = (ContentsBean)object;
            this.length = contents.getTimeLength();
            View root = LayoutInflater.from(context).inflate(R.layout.text_content_layout,null);
            TextView title = (TextView) root.findViewById(R.id.text_title);
            TextView content = (TextView) root.findViewById(R.id.text_content);
            title.setText(contents.getContentName());
            content.setText(contents.getContents());
            this.addView(root);
            setAttribute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ViewGroup.LayoutParams layoutParams;
    @Override
    public void setAttribute() {
        if (layoutParams==null){
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        this.setLayoutParams(layoutParams);//匹配父容器
    }

    @Override
    public void onLayouts() {

    }

    @Override
    public void unLayouts() {
    }

    @Override
    public void startWork() {
    }

    @Override
    public void stopWork() {
    }
}
