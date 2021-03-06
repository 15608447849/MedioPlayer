package com.wos.play.rootdir.model_application.ui.ComponentLibrary.text;

import android.support.v4.view.ViewPager;

/**
 * Created by user on 2016/11/15.
 */

public class TextViewPagerAction implements ViewPager.OnPageChangeListener {

    private TextViewPager mViewPager;

    public TextViewPagerAction(TextViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }

    /**
     * 滑动中 一直被调用
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 滑动完成
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        mViewPager.setCurrIndex(position);
    }
    /**
     * 滑动状态监听
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
