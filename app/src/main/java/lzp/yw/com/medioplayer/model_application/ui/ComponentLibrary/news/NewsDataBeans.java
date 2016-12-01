package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.news;

import android.graphics.Bitmap;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.ImageUtils;

/**
 * Created by user on 2016/12/1.
 * 咨询 适配数据 持有者
 */
public class NewsDataBeans {
    private String title;
    private String editer;
    private String dateStr;
    private String filePath;
    private Bitmap bitmap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEditer() {
        return editer;
    }

    public void setEditer(String editer) {
        this.editer = editer;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    //生成一个 数据源
    public static NewsDataBeans generteDataSource(String title,String editer,String createDate,String filePath,boolean isExitsBitmap){
        NewsDataBeans newsDataBeans = new NewsDataBeans();
        newsDataBeans.setTitle(title);
        newsDataBeans.setEditer(editer);
        newsDataBeans.setDateStr(createDate);
        newsDataBeans.setFilePath(filePath);
        newsDataBeans.setBitmap(isExitsBitmap? ImageUtils.getBitmap(filePath):null);
        return newsDataBeans;
    }
}
