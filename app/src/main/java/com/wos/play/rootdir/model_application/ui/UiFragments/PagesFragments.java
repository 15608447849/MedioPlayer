package com.wos.play.rootdir.model_application.ui.UiFragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

import com.wos.play.rootdir.model_application.ui.ComponentLibrary.AcenterManager.CreateComponent;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.Iview;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/11.
 * 页面 fragment
 */
@SuppressLint("ValidFragment")
public class PagesFragments extends Fragment{
    private static final String TAG = "PagesFragments";

    private int x,y,w,h;//原点,坐标
    private boolean isBgColor = true; //是否是背景颜色
    private String backgroud;//背景(图片或者颜色)
    private AbsoluteLayout layout;

    private List<ComponentsBean> componetsDataArr = null;//组件内容数据列表
    public ArrayList<Iview> componetViewArr = null; //组件元素

    //添加组件的 key
    public void addConpone(Iview iview){
        if (componetViewArr==null){
            componetViewArr = new ArrayList<>();
        }
        componetViewArr.add(iview);
    }
    public PagesFragments(){}

    public PagesFragments( int w, int h,int x, int y, boolean isBgColor, String backgroud,List<ComponentsBean> list) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isBgColor = isBgColor;
        this.backgroud = backgroud;
        if (list!=null){
            this.componetsDataArr = list;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logs.i(TAG,"碎片 - -onCreateView() ");
        if (layout == null){
            //创建绝对布局
            layout  = new AbsoluteLayout(getActivity());
            layout.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h,x,y));
            if (isBgColor){
                try {
                    layout.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(backgroud)));
                } catch (Exception e) {
                    Logs.e(TAG,"设置背景参数错误 - [backgroud:"+backgroud+"]" +e.getMessage());
                }
            }
        }
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logs.i(TAG,"碎片 - -onViewCreated()");
        if (layout!=null){
                    createConponent();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logs.i(TAG,"碎片 - -onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logs.i(TAG,"碎片 - -onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logs.i(TAG,"碎片 - -onResume()");
        exeComponents(); //执行组件
    }

    @Override
    public void onPause() {
        super.onPause();
        Logs.i(TAG,"碎片 - -onPause()");
        unexeComponents(); // 结束执行组件
    }

    //创建组件
    private void createConponent() {
        Logs.i(TAG,"组件 创建中");
        if (componetsDataArr!=null && componetsDataArr.size()>0){
            Iview iv = null;
            for (ComponentsBean component:componetsDataArr){
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
            Logs.i(TAG,"组件 开始工作中");
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
            Logs.i(TAG,"组件 结束工作中");
            for (Iview iv : componetViewArr){
                iv.stopWork();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Logs.i(TAG,"碎片 - -onStop()");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logs.i(TAG,"碎片 - -onDestroyView()");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Logs.i(TAG,"碎片 - -onDetach()");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logs.i(TAG,"碎片 - -onDestroy()");
    }







}
