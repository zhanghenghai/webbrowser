package com.one.browser.entity;

import android.view.View;

/**
 * @author 18517
 */
public class DialogWindow {
    private int id;
    private String title;
    private String url;
    private View.OnClickListener onClickListener;

    public DialogWindow(int id,String title,String url,View.OnClickListener onClickListener){
        this.id = id;
        this.title = title;
        this.url = url;
        this.onClickListener = onClickListener;
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

}
