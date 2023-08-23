package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class History {
    private String icon;
    private String title;

    private String url;
    private String time;

    public History(){

    }

    public History(String icon, String title, String url, String time) {
        this.icon = icon;
        this.title = title;
        this.url = url;
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
