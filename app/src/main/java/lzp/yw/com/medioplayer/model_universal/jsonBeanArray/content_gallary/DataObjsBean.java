package lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_gallary;

/**
 * Created by user on 2016/10/28.
 * lzp
 */
public class DataObjsBean {
    /**
     * id : 844
     * cretime : 1477560259220
     * updtime : 1477560259220
     * creatorUserId : 11
     * title : 惊天魔盗团45454
     * isShowTitle : 0
     * usedStatus : 2
     * format :
     * checkedStatus : 3
     * url : ftp://ftp:FTPmedia@172.16.0.17:21/epaper/1477560258768.mp4
     * media : video
     * newName : 1477560258768.mp4
     * fileName : 惊天魔盗团.vob
     * createdBy : admin
     * upDate : 1477560259220
     * selectType : 1
     * typeId : 103
     * updtimeStr : 2016-10-27
     */

    private int id;
    private long cretime;
    private long updtime;
    private int creatorUserId;
    private String title;
    private int isShowTitle;
    private int usedStatus;
    private String format;
    private int checkedStatus;
    private String url;
    private String urls;
    private String media;
    private String newName;

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
    }

    private String fileName;
    private String createdBy;
    private long upDate;
    private String selectType;
    private int typeId;
    private String updtimeStr;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCretime() {
        return cretime;
    }

    public void setCretime(long cretime) {
        this.cretime = cretime;
    }

    public long getUpdtime() {
        return updtime;
    }

    public void setUpdtime(long updtime) {
        this.updtime = updtime;
    }

    public int getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(int creatorUserId) {
        this.creatorUserId = creatorUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsShowTitle() {
        return isShowTitle;
    }

    public void setIsShowTitle(int isShowTitle) {
        this.isShowTitle = isShowTitle;
    }

    public int getUsedStatus() {
        return usedStatus;
    }

    public void setUsedStatus(int usedStatus) {
        this.usedStatus = usedStatus;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getCheckedStatus() {
        return checkedStatus;
    }

    public void setCheckedStatus(int checkedStatus) {
        this.checkedStatus = checkedStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getUpDate() {
        return upDate;
    }

    public void setUpDate(long upDate) {
        this.upDate = upDate;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getUpdtimeStr() {
        return updtimeStr;
    }

    public void setUpdtimeStr(String updtimeStr) {
        this.updtimeStr = updtimeStr;
    }
}
