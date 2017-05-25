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
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.news.CNews;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.video.CMedia;
import com.wos.play.rootdir.model_application.ui.ComponentLibrary.video.CVideoView;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.IView;
import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 2016/11/11.
 * 页面 fragment
 */
@SuppressLint("ValidFragment")
public class PagesFragments extends Fragment{
    private static final String TAG = "PagesFragments";

    private int x,y,w,h;//原点,坐标
    private boolean isBgColor = true; //是否是背景颜色
    private String background;//背景(图片或者颜色)
    private AbsoluteLayout layout;
    private boolean isShowTopLayer = false;      // 是否设置顶层显示
    private List<ComponentsBean> componentsDataArr = null;//组件内容数据列表
    private LinkedHashSet<IView> componentViewArr = null; //组件元素


    //添加组件的 key
    public void addComponent(IView iView){
        if (componentViewArr==null){
            componentViewArr = new LinkedHashSet<>();
        }else if(componentViewArr.contains(iView)){
            componentViewArr.remove(iView);
        }
        componentViewArr.add(iView);
    }
    public PagesFragments(){}

    public PagesFragments( int w, int h,int x, int y, boolean isBgColor
            , String background,List<ComponentsBean> list) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.isBgColor = isBgColor;
        this.background = background;
        if (list!=null){ this.componentsDataArr = list; }
    }
    public void showTopLayer(){
        isShowTopLayer = true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layout == null){
            //创建绝对布局
            layout  = new AbsoluteLayout(getActivity());
            layout.setLayoutParams(new AbsoluteLayout.LayoutParams(w, h,x,y));
            if (isBgColor){
                try {
                    layout.setBackgroundColor(Color.parseColor(UiTools.TanslateColor(background)));
                } catch (Exception e) {
                    Logs.e(TAG,"设置背景参数错误 - [background:"+background+"]" +e.getMessage());
                }
            }
        }
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (layout!=null) createComponent();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Logs.i(TAG,"碎片 - -onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
//        Logs.i(TAG,"碎片 - -onStart");
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
        unExeComponents(); // 结束执行组件
    }

    //创建组件
    private void createComponent() {
        Logs.i(TAG,"组件 创建中");
        if (componentsDataArr!=null && componentsDataArr.size()>0){
            IView iv;
            for (ComponentsBean component:componentsDataArr){ //反射创建匹配组件
                iv = CreateComponent.create(component,layout,getActivity());
                if (iv != null)addComponent(iv);

            }
        }

    }

    /**
     * 执行组件
     */
    protected void exeComponents() {

        if (componentViewArr!=null && componentViewArr.size()>0){
            Logs.i(TAG,"组件 开始工作中");
            for (IView iv : componentViewArr){
                if(isShowTopLayer && iv instanceof CMedia){
                    Logs.i(TAG,"组件 无人值守视频组件置顶");
                    ((CMedia)iv).setShowTopLayer(true);
                }
                iv.startWork();
            }
        }
    }

    /**
     * 取消执行组件
     */
    protected void unExeComponents() {

        if (componentViewArr!=null && componentViewArr.size()>0){
            Logs.i(TAG,"组件 结束工作中");
            for (IView iv : componentViewArr){
                iv.stopWork();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        Logs.i(TAG,"碎片 - -onStop()");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Logs.i(TAG,"碎片 - -onDestroyView()");
    }
    @Override
    public void onDetach() {
        super.onDetach();
//        Logs.i(TAG,"碎片 - -onDetach()");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Logs.i(TAG,"碎片 - -onDestroy()");
    }







}
