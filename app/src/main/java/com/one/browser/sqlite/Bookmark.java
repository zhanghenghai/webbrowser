package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class Bookmark {

    private String icon;
    private String title;
    private String url;
    private String time;
    private String state;

   public Bookmark(){

    }

    public Bookmark(String icon, String title, String url, String time, String state) {
        this.icon = icon;
        this.title = title;
        this.url = url;
        this.time = time;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
