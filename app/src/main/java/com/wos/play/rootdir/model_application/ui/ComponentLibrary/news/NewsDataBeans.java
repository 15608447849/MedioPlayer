package com.wos.play.rootdir.model_application.ui.ComponentLibrary.news;

import com.wos.play.rootdir.model_application.ui.Uitools.UiTools;

/**
 * Created by user on 2016/12/1.
 * 咨询 适配数据 持有者
 */
public class NewsDataBeans {
    interface FileType{
        String PDF = "pdf";
        String IMAGE = "image";
        String VIDEO = "video";
        String WORD = "word";
    }
    private String title;
    private String editor;
    private String dateStr;
    private String filePath;
    private String thumbPath;//新增缩略图
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

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
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

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    //生成一个 数据源
    public static NewsDataBeans generateDataSource(String fileType,String title,String editor,String createDate,String filePath, String thumbPath, String [] moreFileList){

        NewsDataBeans newsDataBeans = new NewsDataBeans();
        newsDataBeans.setFileType(fileType);
        newsDataBeans.setTitle(title);
        newsDataBeans.setEditor(editor);
        newsDataBeans.setDateStr(createDate);
        newsDataBeans.setFilePath(filePath);
        //新增缩略图
        newsDataBeans.setThumbPath(thumbPath);
        if (newsDataBeans.getFileType().equals(FileType.PDF) || newsDataBeans.getFileType().equals(FileType.WORD)){
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
