package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class Download {

    private String title;
    private String path;
    private String mime;
    private String time;

    Download(){
    }
    public Download(String title, String path, String mime,String time) {
        this.title = title;
        this.path = path;
        this.mime = mime;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
