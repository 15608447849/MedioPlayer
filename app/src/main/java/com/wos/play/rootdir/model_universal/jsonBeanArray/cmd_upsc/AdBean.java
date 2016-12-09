package com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc;

import java.util.List;

/**
 * Created by user on 2016/10/27.
 */
public class AdBean {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
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

    public boolean isAdEnabled() {
        return adEnabled;
    }

    public void setAdEnabled(boolean adEnabled) {
        this.adEnabled = adEnabled;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public List<ComponentsBean> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentsBean> components) {
        this.components = components;
    }

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
     private int id;
    private double coordX;
    private double coordY;
    private double width;
    private double height;
    private String backgroundColor;
    private boolean adEnabled;
    private int waitTime;
    private List<ComponentsBean> components;

}
