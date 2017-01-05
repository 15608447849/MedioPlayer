package com.wos.play.rootdir.model_application.ui.ComponentLibrary.epapers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.wos.play.rootdir.R;

/**
 * Created by user on 2017/1/4.
 */

public class MGridView {

    private  View v;
    private TextView title;
    private GridView grid;
    private Button refresh;
    private boolean isInit = false;

    public MGridView(Context context) {
        initView(context);
    }

    //创建视图
    private void initView(Context context) {
        if (!isInit){
            v = LayoutInflater.from(context).inflate(R.layout.mgridview_layout,null);//根视图
            title = (TextView) v.findViewById(R.id.mgrid_title);
            grid = (GridView) v.findViewById(R.id.mgrid_grid);
            refresh = (Button)v.findViewById(R.id.mgrid_refsh);
            isInit = true;
        }
    }

    private boolean islayout = false;
    private ViewGroup vp;
    public void setLayout(ViewGroup vp){
        if (v!=null && !islayout){

            vp.addView(v);
            islayout = true;
        }
    }
    public void unLayout(){
        if (islayout && vp!=null && v!=null){
            vp.removeView(v);
            islayout = false;
        }
    }

    //设置grid数据源
    public void setAdapete(BaseAdapter adpte){
        if (grid!=null){
            grid.setAdapter(adpte);
        }
    }
    //设置grid点击事件
    public void setItemOnclick(AdapterView.OnItemClickListener itemClickEvent){
        if (grid!=null){
            grid.setOnItemClickListener(itemClickEvent);
        }
    }

    //设置监听事件
    public void setButtonOnclick(View.OnClickListener clickEvent){
        if (refresh!=null){
            refresh.setOnClickListener(clickEvent);
        }
    }

    //设置标题
    public void setTtile(String titleVal){
        if (title!=null){
            title.setText((titleVal==null || titleVal.equals("")?"点击刷新尝试加载":titleVal));
        }
    }
}
