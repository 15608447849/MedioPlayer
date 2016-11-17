package lzp.yw.com.medioplayer.model_application.ui.componentLibrary.grallery;

import android.content.Context;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lzp.yw.com.medioplayer.model_application.ui.UiInterfaces.IComponent;
import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ComponentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc.ContentsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.DataObjsBean;
import lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary.GallaryBean;

/**
 * Created by user on 2016/11/17.
 */

public class CGrallery extends FrameLayout implements IComponent{
    private static final java.lang.String TAG = "CGrallery";
    private int componentId;
    private int width;
    private int height;
    private int x,y;
    private Context context;
    private AbsoluteLayout layout;

    private boolean isInitData;
    private boolean isLayout;
    public CGrallery(Context context,AbsoluteLayout layout, ComponentsBean component) {
        super(context);
        this.context = context;
        this.layout = layout;
        initData(component);
    }


    @Override
    public void initData(Object object) {
        ComponentsBean cb = ((ComponentsBean)object);
        this.componentId = cb.getId();
        this.width = (int)cb.getWidth();
        this.height = (int)cb.getHeight();
        this.x = (int)cb.getCoordX();
        this.y = (int)cb.getCoordY();
        if (cb.getContents()!=null && cb.getContents().size()==1) {
            createContent(cb.getContents());
        }
        this.isInitData = true;
    }

    @Override
    public void setAttrbute() {

    }

    @Override
    public void layouted() {

    }

    @Override
    public void unLayouted() {

    }

    @Override
    public void startWork() {

    }

    @Override
    public void stopWork() {

    }
    @Override
    public void createContent(Object object) {

        try {
            List<ContentsBean> contents = (List<ContentsBean>)object;
            for (ContentsBean content : contents){
                if (content.getContentSource()!=null ){
                    tanslationUrl(content.getContentSource());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void loadContent() {

    }

    @Override
    public void unLoadContent() {

    }













    private ArrayList<String> imageNameArr = null;
    //添加 图片文件名
    public void addImageName(String name){
        if (imageNameArr == null){
            imageNameArr = new ArrayList<>();
        }
        if (!imageNameArr.contains(name)){
            imageNameArr.add(name);
        }
    }
    /*1 获取文件名
    2 获取文件内容
    3 变成图集对象
    4 获取所有文件名*/
    private void tanslationUrl(String contentSource) {
      String jsonContent = UiTools.fileTanslationObject(contentSource);
       if (jsonContent!=null){
           GallaryBean gallaryBean = new GallaryBean();
           try {
               Gson gson=new Gson();
               Type type = new TypeToken<GallaryBean>(){}.getType();
               gallaryBean=gson.fromJson(jsonContent, type);
           }catch (Exception e){
               e.printStackTrace();
           }
           if (gallaryBean!=null && gallaryBean.getDataObjs()!=null && gallaryBean.getDataObjs().size()>0){

               getImageFilename(gallaryBean.getDataObjs());
           }
       }
    }

    //获取 所有文件名
    private void getImageFilename(List<DataObjsBean> listBean) {
            for (DataObjsBean data : listBean){
                addImageName(UiTools.getUrlTanslationFilename(data.getUrl()));
            }
        System.err.println("");
    }


}
