package com.one.browser.entity;
import java.io.Serializable;
import java.sql.Timestamp;

public class Size implements Serializable {

    private Integer classifyId;
    private String classifyTitle;
    private Integer classifyPixelX;
    private Integer classifyPixelY;
    private Integer classifyWashX;
    private Integer classifyWashY;
    private Integer classifySort;
    private Timestamp classifyTime;
    private Integer classifyOneId;

    public Integer getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Integer classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyTitle() {
        return classifyTitle;
    }

    public void setClassifyTitle(String classifyTitle) {
        this.classifyTitle = classifyTitle;
    }

    public Integer getClassifyPixelX() {
        return classifyPixelX;
    }

    public void setClassifyPixelX(Integer classifyPixelX) {
        this.classifyPixelX = classifyPixelX;
    }

    public Integer getClassifyPixelY() {
        return classifyPixelY;
    }

    public void setClassifyPixelY(Integer classifyPixelY) {
        this.classifyPixelY = classifyPixelY;
    }

    public Integer getClassifyWashX() {
        return classifyWashX;
    }

    public void setClassifyWashX(Integer classifyWashX) {
        this.classifyWashX = classifyWashX;
    }

    public Integer getClassifyWashY() {
        return classifyWashY;
    }

    public void setClassifyWashY(Integer classifyWashY) {
        this.classifyWashY = classifyWashY;
    }

    public Integer getClassifySort() {
        return classifySort;
    }

    public void setClassifySort(Integer classifySort) {
        this.classifySort = classifySort;
    }

    public Timestamp getClassifyTime() {
        return classifyTime;
    }

    public void setClassifyTime(Timestamp classifyTime) {
        this.classifyTime = classifyTime;
    }

    public Integer getClassifyOneId() {
        return classifyOneId;
    }

    public void setClassifyOneId(Integer classifyOneId) {
        this.classifyOneId = classifyOneId;
    }
}
