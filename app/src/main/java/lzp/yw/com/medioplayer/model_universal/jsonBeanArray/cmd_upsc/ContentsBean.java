package lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc;

/**
 * Created by user on 2016/10/27.
 * 内容
 * lzp
 *
 */
public class ContentsBean {
    /**
     * id : 0
     * componentId : 756
     * contentType : news
     * checkState : 2
     * checked : true
     * contentSource : http://172.16.0.17:9000/epaper/dyannews/page?stairId=103&categoryId=-1&sortBy=allSorts+asc,upDate+desc&filter=Base64
     * categoryId : 103
     * subcategoryId :
     * updateFreq : 600
     *
     *  "id":883,
     "contentName":"小兔子.jpg",
     "contentType":"image",
     "materialType":"image",
     "contentSource":"ftp://ftp:FTPmedia@172.16.0.17:21/content/1478163190677.jpg",
     "timeLength":10,
     "playTimes":1
     */
    private int id;
    private int componentId;
    private String contentName;
    private String contentType;
    private String materialType;
    private String checkState;
    private boolean checked;
    private String contentSource;
    private String categoryId;
    private String subcategoryId;
    private String updateFreq;
    private int timeLength;//时长
    private int playTimes;//次数
    private int playIndex;//内容下标

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public int getPlayTimes() {
        return playTimes;
    }

    public void setPlayTimes(int playTimes) {
        this.playTimes = playTimes;
    }

    public int getPlayIndex() {
        return playIndex;
    }

    public void setPlayIndex(int playIndex) {
        this.playIndex = playIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getContentSource() {
        return contentSource;
    }

    public void setContentSource(String contentSource) {
        this.contentSource = contentSource;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getUpdateFreq() {
        return updateFreq;
    }

    public void setUpdateFreq(String updateFreq) {
        this.updateFreq = updateFreq;
    }
}
