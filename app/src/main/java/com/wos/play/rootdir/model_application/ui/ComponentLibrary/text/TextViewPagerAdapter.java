package com.wos.play.rootdir.model_application.ui.ComponentLibrary.text;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by user on 2016/11/15.
 */

public class TextViewPagerAdapter extends PagerAdapter {
    private ArrayList<TextScrollView> viewList = null;

    public TextViewPagerAdapter(ArrayList<TextScrollView> viewList) {
        this.viewList = viewList;
    }

    /**
     * 这个方法，是获取当前窗体界面数
     * 返回页卡的数量
     * @return
     */
    @Override
    public int getCount() {

        if (viewList != null && viewList.size() > 0) {
            return viewList.size();
        } else {
            return 0;
        }

    }
    /**
     * 用于判断是否由对象生成界面
     * @param view
     * @param object
     * @return
     *
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        //官方提示这样写
        return view==object;
    }

    /**
     * return一个对象，这个对象表明了PagerAdapter适配器选择哪个对象*放在当前的ViewPager中
     * 用来实例化页
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //2.当前要显示的数据索引为集合长度
        container.addView(viewList.get(position));
        return viewList.get(position);
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    /**
     * 从ViewGroup中移出当前View
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //3.移除的索引为集合的长度
//        int newPosition = position % viewList.size();
        container.removeView(viewList.get(position));
//        container.removeView((View) object);//删除页面
    }
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container,position,object);
    }

}
