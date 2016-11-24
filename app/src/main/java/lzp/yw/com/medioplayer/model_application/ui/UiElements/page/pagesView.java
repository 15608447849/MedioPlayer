package lzp.yw.com.medioplayer.model_application.ui.UiElements.page;

import android.widget.AbsoluteLayout;

import java.util.HashMap;
import java.util.Map;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiFragments.PagesFragments;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.PagesBean;
import lzp.yw.com.medioplayer.model_universal.tool.Logs;

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
            this.setId(page.getId());//视图
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
        Logs.i(TAG,"setAttrbute() - ");
        //先设置大小
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
    }

    /**
     * 创建 fragment
     */
    private void creatFragment(boolean isBgColor){
        Logs.i(TAG,"creatFragment()");
        if (mFragment == null){
            mFragment = new PagesFragments(width,height,x,y,
                    isBgColor,isBgColor?backGroundColor:backGroundImage,  //是否是背景颜色
                    page.getComponents());
        }
    }
    @Override
    protected void loadFragment() {
        Logs.i(TAG,"loadFragment()");
        if (page.getComponents()!=null && page.getComponents().size()>0){
            //创建 fragments
            creatFragment(true);
            // 用 activity fragment 管理器  替换 view -> fragment
            activity.repleaceViewToFragment(this,mFragment);
        }
    }

    @Override
    protected void removeFragment() {
        Logs.i(TAG,"removeFragment() - "+isLayout);
        if (mFragment!=null){
            activity.deleteFragments(mFragment);
        }
//        mFragment = null;
    }

    @Override
    public Map<String, Integer> getPageSize() {
        Map<String, Integer> map = new HashMap<>();
        map.put("width",width);
        map.put("height",height);
        map.put("x",x);
        map.put("y",y);
        return map;
    }
}
