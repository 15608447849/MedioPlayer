package lzp.yw.com.medioplayer.model_application.ui.UiFragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import java.util.ArrayList;
import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.centerManager.CreateComponent;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;

/**
 * Created by user on 2016/11/11.
 * 页面 fragment
 */

public class PagesFragments extends Fragment{
    private static final String TAG = "PagesFragments";
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
        Logs.i(TAG,"- -PagesFragments()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.i(TAG," - -onCreateView() ");
        if (layout == null){
            //创建绝对布局
            layout  = new AbsoluteLayout(getActivity());
            layout.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h,x,y));
            if (isBgColor){
                try {
                    layout.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(bg)));
                } catch (Exception e) {
//                    layout.setBackgroundColor(1+(int)(Math.random()*2)==1?Color.RED:Color.WHITE);
                    e.printStackTrace();
                }
            }

        }
        createConponent();
        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        Logs.i(TAG,"- -onResume()");
        exeComponents(); //执行组件
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.i(TAG,"- -onPause()");
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
        Logs.i(TAG,"- -exeComponents()");
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
        Logs.i(TAG,"- -unexeComponents()");
        if (componetViewArr!=null && componetViewArr.size()>0){
            for (Iview iv : componetViewArr){
                iv.stopWork();
            }
        }
    }

/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG,"- -onDestroy()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logs.i(TAG,"- -onDestroyView()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logs.i(TAG,"- -onDetach()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logs.i(TAG,"- -onStop()");
    }*/

}
