package com.one.browser.entity;

import java.io.File;

/**
 * @author 18517
 */
public class DownloadMsg {

    private int downloadedSize;
    private int fileSize;
    private String saveFile;
    private String fileName;
    private boolean exited;

    public DownloadMsg(int downloadedSize, int fileSize, String saveFile, String fileName, boolean exited) {
        this.downloadedSize = downloadedSize;
        this.fileSize = fileSize;
        this.saveFile = saveFile;
        this.fileName = fileName;
        this.exited = exited;
    }

    public int getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(int downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isExited() {
        return exited;
    }

    public void setExited(boolean exited) {
        this.exited = exited;
    }
}
