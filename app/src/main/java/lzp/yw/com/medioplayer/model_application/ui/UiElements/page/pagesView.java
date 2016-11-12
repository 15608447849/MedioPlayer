package lzp.yw.com.medioplayer.model_application.ui.UiElements.page;

import android.graphics.Color;
import android.util.Log;
import android.widget.AbsoluteLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiFragments.PagesFragments;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.PagesBean;

/**
 * Created by user on 2016/11/10.
 */

public class pagesView extends IviewPage{



    private String backGroundImage ;//背景图
    private String backGroundColor;
    private String label ;
    private int width;
    private int height;
    private int x;
    private int y;
    private PagesFragments mFragment;
    PagesBean page;

    public pagesView(BaseActivity activity, PagesBean page) {
        super(activity);
        initData(page);
    }

    @Override
    public void initData(Object object) {
        try {

            page = ((PagesBean) object);
            setId(page.getId());//视图
            this.id = page.getId();
            this.x = (int)page.getCoordX();
            this.y = (int)page.getCoordY();
            this.width =(int)page.getWidth();
            this.height = (int)page.getHeight();
            this.backGroundColor = page.getBackgroundColor();
            this.backGroundImage = page.getBackground();//请截取 uri 暂时未做 确定bg颜色还是图片
            this.label = page.getLabel();
            this.isHome = page.isHome();
            isInit = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setAttrbute() {
        if (isAttr) {
            return;
        }
        //先设置大小
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
        this.setBackgroundColor(Color.GREEN);
        isAttr = true;
    }

    /**
     * 创建 fragment
     */
    private void creatFragment(boolean isBgColor){
        if (mFragment == null){
            mFragment = new PagesFragments(width,height,x,y,
                    isBgColor,isBgColor?backGroundColor:backGroundImage,  //是否是背景颜色
                    page.getComponents());
        }
        Log.i("","creatFragment()");
    }
    @Override
    protected void loadFragment() {
        if (page.getComponents()!=null && page.getComponents().size()>0){
            creatFragment(true);//创建 fragments
        }
        // 用 activity fragment 管理器  替换 view -> fragment
        Log.i("","loadFragment - "+mFragment);
        activity.repleaceViewToFragment(this,mFragment);
    }

    @Override
    protected void removeFragment() {
        activity.deleteFragments(mFragment);
     //   mFragment = null;
    }
}
