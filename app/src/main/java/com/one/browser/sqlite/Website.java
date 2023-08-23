package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class Website {

    private String url;
    private String path;


    public Website(){

    }
    public Website(String title, String path) {
        this.url = title;
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
