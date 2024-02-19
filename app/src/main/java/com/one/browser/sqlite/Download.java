package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class Download {

    private String title;
    private String path;
    private String mime;
    private String size;
    private String time;

    public Download() {
    }

    public Download(String fileName, String fileUrl, String fileType, String fileSize, String time) {
        this.title = fileName;
        this.path = fileUrl;
        this.mime = fileType;
        this.size = fileSize;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
