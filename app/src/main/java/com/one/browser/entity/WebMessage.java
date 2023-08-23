package com.one.browser.entity;

/**
 * @author 18517
 */
public class WebMessage {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String mime;

    /**
     * 文件大小
     */
    private int fileSize;


    public WebMessage(String fileName, String mime, int fileSize) {
        this.fileName = fileName;
        this.mime = mime;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
