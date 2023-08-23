package com.one.browser.entity;

import com.one.browser.sqlite.Download;

import java.util.List;

/**
 * @author 18517
 */
public class GroupedDownload {

    private String date;
    private List<Download> downloadList;

    public GroupedDownload(String date, List<Download> downloadList) {
        this.date = date;
        this.downloadList = downloadList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Download> getDownloadList() {
        return downloadList;
    }

    public void setDownloadList(List<Download> downloadList) {
        this.downloadList = downloadList;
    }
}
