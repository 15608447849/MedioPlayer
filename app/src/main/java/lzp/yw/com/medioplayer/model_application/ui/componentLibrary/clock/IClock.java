package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.clock;

import android.content.Context;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponent;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;

/**
 * Created by user on 2016/11/22.
 *
 */
public class IClock extends FrameLayout implements IComponent {
    private static final java.lang.String TAG = "IClock";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;
    private AbsoluteLayout.LayoutParams layoutParams;
    private boolean isInitData;
    private boolean isLayout;
    public IClock(Context context, AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }
    @Override
    public void initData(Object object) {
        try {
            ComponentsBean cb = ((ComponentsBean)object);
            this.componentId = cb.getId();
            this.width = (int)cb.getWidth();
            this.height = (int)cb.getHeight();
            this.x = (int)cb.getCoordX();
            this.y = (int)cb.getCoordY();
            layoutParams = new AbsoluteLayout.LayoutParams(width,height,x,y);
            createContent(null);
            this.isInitData = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setAttrbute() {
        this.setLayoutParams(layoutParams);
    }
    @Override
    public void layouted() {
        if (!isLayout){
            layout.addView(this);
            isLayout = true;
        }
    }
    @Override
    public void unLayouted() {
        if (isLayout){
            layout.removeView(this);
            isLayout = false;
        }
    }
    @Override
    public void startWork() {
        try {
            if (!isInitData){
                return;
            }
            setAttrbute();
            layouted();
            loadContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void stopWork() {
        try {
            unLoadContent();
            unLayouted(); //移除布局

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ClockView clockView = null;
    @Override
    public void createContent(Object object) {

        clockView = new ClockView(context);
        clockView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(clockView);
//        clockView.setOnTimeChangeListener(new ClockView.OnTimeChangeListener() {
//            @Override
//            public void onTimeChange(View view, int hour, int minute, int second) {
//                Logs.i(TAG,String.format("%s-%s-%s", hour, minute, second));
//            }
//        });

    }

    @Override
    public void loadContent() {
    }

    @Override
    public void unLoadContent() {
    }

















}
