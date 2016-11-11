package lzp.yw.com.medioplayer.model_application.ui.UiElements.page;

import android.graphics.Color;
import android.widget.AbsoluteLayout;

import java.util.List;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiFragments.PagesFragments;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.layout.IviewLayout;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
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


    public pagesView(BaseActivity activity, IviewLayout layout, PagesBean page) {
        super(activity,layout);
        initData(page);
    }

    @Override
    public void initData(Object object) {
        try {

            PagesBean page = ((PagesBean) object);
            setId(page.getId());//视图
            this.id = page.getId();
            this.x = (int)page.getCoordX();
            this.y = (int)page.getCoordY();
            this.width =(int)page.getWidth();
            this.height = (int)page.getHeight();
            this.backGroundColor = page.getBackgroundColor();
            this.backGroundImage = page.getBackground();
            this.label = page.getLabel();
            this.isHome = page.isHome();
            creatFragment(page.getComponents());//创建 fragments
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
    private void creatFragment(List<ComponentsBean> list){
        if (mFragment == null){
            mFragment = new PagesFragments(width,height,x,y,true,"",list);
        }
    }
    @Override
    protected void loadFragment() {
        // 用 activity fragment 管理器  替换 view -> fragment
        activity.repleaceViewToFragment(this,mFragment);
    }



}
