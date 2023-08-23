package com.one.browser.entity;

import android.content.Intent;

public class Notice {

    private int notifyId;
    private String title;
    private String button;
    private int totalProgress;
    private int currentProgress;

    private Intent intent;

    public Notice(){

    }

    public Notice(int notifyId, String title, String button, int totalProgress, int currentProgress, Intent intent) {
        this.notifyId = notifyId;
        this.title = title;
        this.button = button;
        this.totalProgress = totalProgress;
        this.currentProgress = currentProgress;
        this.intent = intent;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public int getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(int totalProgress) {
        this.totalProgress = totalProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
