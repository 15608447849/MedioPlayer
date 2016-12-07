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
    private String contentSource;//image 资源 news资源uri 图集资源uri 跑马灯的文本内容
    private String materialType;//如果是video(多媒体)是 需要判断的字段

    private String sourceUp;//button 资源 默认时
    private String sourceDown;// button 资源 按下时

    private int timeLength;//播放时长
    private int playTimes;//次数
    private int playIndex;//内容下标
    private boolean transparent;//是否透明
    private int updateFreq;//高级控件更新频率

    //天气 城市
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getUpdateFreq() {
        return updateFreq;
    }

    public void setUpdateFreq(int updateFreq) {
        this.updateFreq = updateFreq;
    }

    //文本控件相关
    private String contentName;//标题 //跑马灯标题
    private String contents;//内容

//    跑马灯次数 速度
   private int rollingTimes;
   private int rollingSpeed;
   private String backgroundColor;//跑马灯背景颜色
    private int backgroundAlpha;//跑马灯背景透明图
    private String fontColor;//跑马灯前景颜色-字体颜色


    //电子报 相关
    private String renewalPaperTime;//每天更新电子报的时间
    private int daysKeep;//保持天数
    private int saveDays;//保存天数

    public String getRenewalPaperTime() {
        return renewalPaperTime;
    }

    public void setRenewalPaperTime(String renewalPaperTime) {
        this.renewalPaperTime = renewalPaperTime;
    }

    public int getDaysKeep() {
        return daysKeep;
    }

    public void setDaysKeep(int daysKeep) {
        this.daysKeep = daysKeep;
    }

    public int getSaveDays() {
        return saveDays;
    }

    public void setSaveDays(int saveDays) {
        this.saveDays = saveDays;
    }

    public int getRollingTimes() {
        return rollingTimes;
    }

    public void setRollingTimes(int rollingTimes) {
        this.rollingTimes = rollingTimes;
    }

    public int getRollingSpeed() {
        return rollingSpeed;
    }

    public void setRollingSpeed(int rollingSpeed) {
        this.rollingSpeed = rollingSpeed;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

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
