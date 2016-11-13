package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.button;

import android.content.Context;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;

import lzp.yw.com.medioplayer.R;
import lzp.yw.com.medioplayer.model_application.baselayer.BaseActivity;
import lzp.yw.com.medioplayer.model_application.ui.UiFactory.UiManager;
import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.Iview;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;

/**
 * Created by user on 2016/11/12.
 * 按钮 点击
 */
public class CButton extends ImageButton implements View.OnClickListener,Iview{
    private static final String TAG = "_CButton";
    private Context context;
    private AbsoluteLayout layout;
    private int componentId;
    private int x,y,width,height;
    private int linkId;
    private boolean isInitData;
    private boolean isLayout;

    public CButton(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }


    //初始化
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
            this.componentId = cb.getId();
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            this.linkId = cb.getLinkId();
            this.isInitData = true;
            this.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //设置属性
    @Override
    public void setAttrbute() {
        this.setLayoutParams(new AbsoluteLayout.LayoutParams(width,height,x,y));
        this.setImageResource(R.mipmap.click_image);//如果是图片 使用图片
    }
    //布局
    @Override
    public void layouted() {
        if (isLayout){
            return;
        }
        layout.addView(this);
        isLayout = true;
    }
    //未布局
    @Override
    public void unLayouted() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    //开始工作
    @Override
    public void startWork() {
//        Logs.i(TAG,"button - startWork()");
        try {
            if (!isInitData){
                return;
            }
            setAttrbute();
            layouted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //停止
    @Override
    public void stopWork() {
//        Logs.i(TAG,"button - startWork()");
        try {
            unLayouted(); //移除布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }









    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        try {
            ((BaseActivity)context).showToast("按钮 -"+componentId +" linkId - "+linkId);
            UiManager.getInstans().exeTask(linkId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
