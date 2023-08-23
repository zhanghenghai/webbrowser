package com.one.browser.entity;

import android.graphics.Color;

/**
 * @author 18517
 */
public class Colors {


    private String title;

    private String color;

    public Colors(String title, String color) {
        this.title = title;
        this.color = color;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
