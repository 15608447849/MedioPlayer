package com.wos.play.rootdir.model_application.ui.ComponentLibrary.news;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by user on 2016/12/1.
 */

public class CListView extends ListView{

    private BaseAdapter adpter;
    private boolean isInit = false;
    public CListView(Context context) {
        super(context);
    }
    //初始化
    public void init(ViewGroup layout,BaseAdapter adpter,AdapterView.OnItemClickListener itemClickEvent) {
        if (!isInit){
            this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout.addView(this);
            this.adpter = adpter;
            this.setAdapter(adpter);
            this.setOnItemClickListener(itemClickEvent);
            isInit = true;
        }
    }
}
