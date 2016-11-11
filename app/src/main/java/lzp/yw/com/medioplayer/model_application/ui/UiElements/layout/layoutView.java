package lzp.yw.com.medioplayer.model_application.ui.UiElements.layout;

import android.graphics.Color;
import android.widget.AbsoluteLayout;

import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiElements.layout.IviewLayout;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ProgramBean;

/**
 * Created by user on 2016/11/10.
 */
public class layoutView extends IviewLayout {
    private String title;
    private String resolution;
    private int width;
    private int height;
    public layoutView(BaseActivity activity, ProgramBean pgb) {
        super(activity);
        initData(pgb);
    }
    @Override
    public void initData(Object object) {
        try {
            ProgramBean pgb = (ProgramBean)object;
            this.id = pgb.getLayoutId();
            this.title = pgb.getTitle();
            this.resolution = pgb.getResolution();
            this.width = pgb.getWidth();
            this.height = pgb.getHeight();
            isInit = true;//成功初始化
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setAttrbute() {
        if (isAttr) {
            return;
        }
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,0,0));
        this.setBackgroundColor(Color.GRAY);
        isAttr = true;
    }


}
