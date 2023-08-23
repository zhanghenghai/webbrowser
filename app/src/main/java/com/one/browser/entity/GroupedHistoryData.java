package com.one.browser.entity;

import com.one.browser.sqlite.History;

import java.util.List;

/**
 * @author 18517
 */
public class GroupedHistoryData {

    private String date;
    private List<History> historyList;

    public GroupedHistoryData(String date, List<History> historyList) {
        this.date = date;
        this.historyList = historyList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<History> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<History> historyList) {
        this.historyList = historyList;
    }


}
