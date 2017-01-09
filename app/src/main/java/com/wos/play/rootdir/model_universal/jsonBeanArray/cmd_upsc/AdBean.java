package com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc;

import java.util.List;

/**
 * Created by user on 2016/10/27.
 */
public class AdBean extends PublicAttibute{

    /**
     * id : 365
     * coordX : 0.0
     * coordY : 0.0
     * width : 0.0
     * height : 0.0
     * backgroundColor : #FFFFFF
     * components : []
     * adEnabled : false
     * waitTime : 0
     */
    private double coordX;
    private double coordY;
    private double width;
    private double height;
    private String backgroundColor;
    private String background;

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;
    private List<ComponentsBean> components;

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

    public double getHeight() {
        return height>0?height:-1;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width>0?width:-1;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    public List<ComponentsBean> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentsBean> components) {
        this.components = components;
    }


}
