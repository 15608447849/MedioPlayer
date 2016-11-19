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

    private String contentType;//类型
    private String contentSource;//image 资源 news资源uri 图集资源uri
    private String materialType;//如果是video(多媒体)是 需要判断的字段

    private String sourceUp;//button 资源 默认时
    private String sourceDown;// button 资源 按下时

    private int timeLength;//播放时长
    private int playTimes;//次数
    private int playIndex;//内容下标
    private boolean transparent;//是否透明
    private int updateFreq;//高级控件更新频率

    public int getUpdateFreq() {
        return updateFreq;
    }

    public void setUpdateFreq(int updateFreq) {
        this.updateFreq = updateFreq;
    }

    //文本控件相关
    private String contentName;//标题
    private String contents;//内容

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getSourceDown() {
        return sourceDown;
    }
    public void setSourceDown(String sourceDown) {
        this.sourceDown = sourceDown;
    }
    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
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

    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getContentSource() {
        return contentSource;
    }
    public void setContentSource(String contentSource) {
        this.contentSource = contentSource;
    }

    public String getSourceUp() {
        return sourceUp;
    }

    public void setSourceUp(String sourceUp) {
        this.sourceUp = sourceUp;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }
}
