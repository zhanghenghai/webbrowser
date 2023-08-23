package com.one.browser.entity;

import java.io.Serializable;

/**
 * @author 18517
 */
public class Resource implements Serializable {

    private String title;
    private int w;
    private int h;
    private int wash_w;
    private int wash_y;

    public Resource(String title, int w, int h, int wash_w, int wash_y) {
        this.title = title;
        this.w = w;
        this.h = h;
        this.wash_w = wash_w;
        this.wash_y = wash_y;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getWash_w() {
        return wash_w;
    }

    public void setWash_w(int wash_w) {
        this.wash_w = wash_w;
    }

    public int getWash_y() {
        return wash_y;
    }

    public void setWash_y(int wash_y) {
        this.wash_y = wash_y;
    }
}
