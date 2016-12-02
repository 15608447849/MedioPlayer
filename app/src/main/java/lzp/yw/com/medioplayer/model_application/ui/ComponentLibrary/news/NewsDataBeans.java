package lzp.yw.com.medioplayer.model_application.ui.ComponentLibrary.news;

import lzp.yw.com.medioplayer.model_application.ui.Uitools.UiTools;

/**
 * Created by user on 2016/12/1.
 * 咨询 适配数据 持有者
 */
public class NewsDataBeans {
    interface FileType{
        String PDF = "pdf";
        String IMAGE = "image";
        String VIDEO = "video";
    }
    private String title;
    private String editer;
    private String dateStr;
    private String filePath;

    private String fileType;
    private String [] moreFileList;

    public String[] getMoreFileList() {
        return moreFileList;
    }

    public void setMoreFileList(String[] moreFileList) {
        this.moreFileList = moreFileList;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


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



    //生成一个 数据源
    public static NewsDataBeans generteDataSource(String fileType,String title,String editer,String createDate,String filePath,String [] moreFileList){

        NewsDataBeans newsDataBeans = new NewsDataBeans();
        newsDataBeans.setFileType(fileType);
        newsDataBeans.setTitle(title);
        newsDataBeans.setEditer(editer);
        newsDataBeans.setDateStr(createDate);
        newsDataBeans.setFilePath(filePath);
        if (newsDataBeans.getFileType().equals(FileType.PDF)){
            if (moreFileList!=null){
                    for (int i=0;i<moreFileList.length;i++){
                        moreFileList[i] = UiTools.getUrlTanslationFilename(moreFileList[i]);
                    }
            }
            newsDataBeans.setMoreFileList(moreFileList);
        }
        return newsDataBeans;
    }
}
