package lzp.yw.com.medioplayer.model_application.ui.UiFragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_application.ui.componentLibrary.CreateComponent;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;

/**
 * Created by user on 2016/11/11.
 * 页面 fragment
 */

public class PagesFragments extends Fragment{

    private int x,y,w,h;
    private boolean isBgColor = true;
    private String bg;
    private AbsoluteLayout layout ;

    public ArrayList<Iview> componetArr = null;
    //添加组件的 key
    public void addConpone(Iview iview){
        if (componetArr==null){
            componetArr = new ArrayList<>();
        }
        componetArr.add(iview);
    }

    private List<ComponentsBean> componetsArr = null;

    public PagesFragments( int w, int h,int x, int y, boolean isBgColor, String bg,List<ComponentsBean> list) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isBgColor = isBgColor;
        this.bg = bg;
        if (list!=null){
            this.componetsArr = list;
        }
        Log.e("","PagesFragments()");
    }

        //返回 视图
       public AbsoluteLayout getLayoutView(){
           return layout;
       }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null){
            System.err.println("---"+w+"*"+h+"-"+x+"-"+y);
            //创建绝对布局
            layout  = new AbsoluteLayout(getActivity());
            layout.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h,x,y));
            layout.setBackgroundColor(Color.RED);
        }
       System.err.println("layout - " + layout.toString());
        createConponent();
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        startSource();
    }

    //创建组件
    private void createConponent() {
        if (componetsArr!=null){
            Iview iv = null;
            for (ComponentsBean component:componetsArr){
//            反射创建任意组件
                iv = CreateComponent.create(component,layout,getActivity());
                if (iv != null){
                    addConpone(iv);
                }
            }
        }

    }


    protected void startSource() {
        if (componetArr!=null && componetArr.size()>0){
            Log.i("","loadSource()");
            for (Iview iv : componetArr){
                iv.startWork();
            }
        }
    }





}
