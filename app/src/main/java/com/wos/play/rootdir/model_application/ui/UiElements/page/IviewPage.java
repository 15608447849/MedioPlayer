package com.wos.play.rootdir.model_application.ui.UiElements.page;

import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import com.wos.play.rootdir.model_application.baselayer.BaseActivity;
import com.wos.play.rootdir.model_application.ui.UiFragments.PagesFragments;
import com.wos.play.rootdir.model_application.ui.UiInterfaces.Iview;
import com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import com.wos.play.rootdir.model_universal.tool.AppsTools;
import com.wos.play.rootdir.model_universal.tool.Logs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/11/10.
 */

public class IviewPage extends FrameLayout implements Iview {
    protected static final String TAG = "IviewPage";
    protected BaseActivity activity;
    protected AbsoluteLayout layout;//父布局 - activity上面的

    protected boolean isHome = false;
    public boolean isHome() {
        return isHome;
    }
    public void setHome(boolean home) {
        isHome = home;
    }

    protected boolean isInit = false;//是否初始化
    protected  boolean isLayout = false;//是否布局

    private PagesBean page;
    private int x,y,height,width;
    private AbsoluteLayout.LayoutParams layoutParams;
    protected int id; //自身id

    private String backGroundImage ;//背景图片
    private String backGroundColor;//背景颜色

    public IviewPage(BaseActivity activity,PagesBean page) {
        super(activity);
        this.activity = activity;
        this.layout = (AbsoluteLayout) activity.getActivityLayout();
        initData(page);
    }

    @Override
    public void initData(Object object) {
        try {
            page = ((PagesBean) object);
            this.id = page.getId();
            this.x = (int)page.getCoordX();
            this.y = (int)page.getCoordY();
            this.width =(int)page.getWidth();
            this.height = (int)page.getHeight();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            this.backGroundColor = page.getBackgroundColor();
            this.backGroundImage = page.getBackground();//请截取 uri 暂时未做 确定bg颜色还是图片
            this.isHome = page.isHome();
            //设置视图id
            this.setId(id+ AppsTools.randomNum(10,500));//view_id
            isInit = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setAttrbute() {
        //设置大小
        this.setLayoutParams(layoutParams);
    }
    @Override
    public void layouted() {
        if (!isLayout){
            try {
                layout.addView(this);
                isLayout = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void unLayouted() {
        if (isLayout){
            try {
                removeFragment();
                layout.removeView(this);
                isLayout =false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void startWork() {
        try{
            if (!isInit){
                return;
            }
            setAttrbute();//设置属性
            layouted();//设置布局
            if (isLayout){
                loadFragment();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void stopWork() {
        try {
            unLayouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        //--------------------------------------------------------------------------------//

    private PagesFragments mFragment;
    /**
     * 创建 fragment
     */
    private void creatFragment(boolean isBgColor){

        if (mFragment == null){
            Logs.i(TAG,"creatFragment()");
            mFragment = new PagesFragments(width,height,x,y,
                    isBgColor,isBgColor?backGroundColor:backGroundImage,  //是否是背景颜色
                    page.getComponents());
        }
    }

    protected void loadFragment() {
        if (page.getComponents()!=null && page.getComponents().size()>0){
            Logs.i(TAG,"loadFragment()");
            //创建 fragments
            creatFragment(true);
            // 用 activity fragment 管理器  替换 view -> fragment
            activity.repleaceViewToFragment(this,mFragment);
        }
    }

    protected void removeFragment() {
        if (mFragment!=null){
            Logs.i(TAG,"removeFragment() - "+mFragment);
            activity.deleteFragments(mFragment);
            //        mFragment = null;
        }

    }
    // 用于 - 切换页面 做判定
    public Map<String, Integer> getPageSize() {
        Map<String, Integer> map = new HashMap<>();
        map.put("width",width);
        map.put("height",height);
        map.put("x",x);
        map.put("y",y);
        return map;
    }
}
