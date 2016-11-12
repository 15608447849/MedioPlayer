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
    private AbsoluteLayout layout;
    public ArrayList<Iview> componetViewArr = null; //组件元素
    //添加组件的 key
    public void addConpone(Iview iview){
        if (componetViewArr==null){
            componetViewArr = new ArrayList<>();
        }
        componetViewArr.add(iview);
    }
    //组件内容
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
        Log.i("","PagesFragments()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null){
            //创建绝对布局
            layout  = new AbsoluteLayout(getActivity());
            layout.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h,x,y));
            if (isBgColor){
                try {
                    layout.setBackgroundColor(Color.parseColor(bg));
                } catch (Exception e) {
                    layout.setBackgroundColor(Color.RED);
//                    e.printStackTrace();
                }
            }

        }
        createConponent();
        Log.i(""," - -onCreateView() ");
        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        exeComponents(); //执行组件
    }

    @Override
    public void onPause() {
        super.onPause();
        unexeComponents(); // 结束执行组件
    }

    //创建组件
    private void createConponent() {
        if (componetsArr!=null){
            Iview iv = null;
            for (ComponentsBean component:componetsArr){
                //反射创建匹配组件
                iv = CreateComponent.create(component,layout,getActivity());
                if (iv != null){
                    addConpone(iv);
                }
            }
        }

    }

    /**
     * 执行组件
     */
    protected void exeComponents() {
        if (componetViewArr!=null && componetViewArr.size()>0){
            for (Iview iv : componetViewArr){
                iv.startWork();
            }
        }
    }

    /**
     * 取消执行组件
     */
    protected void unexeComponents() {
        if (componetViewArr!=null && componetViewArr.size()>0){
            for (Iview iv : componetViewArr){
                iv.stopWork();
            }
        }
    }


}
