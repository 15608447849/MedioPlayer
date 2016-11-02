package lzp.yw.com.medioplayer.model_universal.jsonBeanArray.cmd_upsc;

import java.util.List;

/**
 * Created by user on 2016/10/27.
 * lzp
 * 组件
 */
public class ComponentsBean {
    /**
     * id : 756
     * label : 新建资讯7
     * coordX : 0.0
     * coordY : 551.0
     * width : 802.0
     * height : 529.0
     * componentTypeId : 14
     * componentTypeCode : news
     * interactEnabled : true
     * order : 3
     * backgroundAlpha : 22
     * backgroundColor :
     * backgroundPic : ftp://ftp:FTPmedia@172.16.0.17:21/content/1476427174433.jpg
     * backgroundPicName : 长颈鹿.jpg
     * contents : [{"id":0,"componentId":756,"contentType":"news","materialType":"","checkState":"2","checked":true,"contentSource":"http://172.16.0.17:9000/epaper/dyannews/page?stairId=103&categoryId=-1&sortBy=allSorts+asc,upDate+desc&filter=Base64","categoryId":"103","subcategoryId":"","updateFreq":"600"}]
     * titleShowType : 1
     * hasContent : true
     */
    private int id;
    private String label;
    private double coordX;
    private double coordY;
    private double width;
    private double height;
    private int componentTypeId;
    private String componentTypeCode;
    private boolean interactEnabled;
    private int order;
    private String backgroundAlpha;
    private String backgroundColor;
    private String backgroundPic;
    private String backgroundPicName;
    private int titleShowType;
    private boolean hasContent;
    private int linkId;

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    private List<ContentsBean> contents;

    public List<ContentsBean> getContents() {
        return contents;
    }

    public void setContents(List<ContentsBean> contents) {
        this.contents = contents;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getComponentTypeId() {
        return componentTypeId;
    }

    public void setComponentTypeId(int componentTypeId) {
        this.componentTypeId = componentTypeId;
    }

    public String getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(String componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public boolean isInteractEnabled() {
        return interactEnabled;
    }

    public void setInteractEnabled(boolean interactEnabled) {
        this.interactEnabled = interactEnabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(String backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundPic() {
        return backgroundPic;
    }

    public void setBackgroundPic(String backgroundPic) {
        this.backgroundPic = backgroundPic;
    }

    public String getBackgroundPicName() {
        return backgroundPicName;
    }

    public void setBackgroundPicName(String backgroundPicName) {
        this.backgroundPicName = backgroundPicName;
    }

    public int getTitleShowType() {
        return titleShowType;
    }

    public void setTitleShowType(int titleShowType) {
        this.titleShowType = titleShowType;
    }

    public boolean isHasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
