package com.one.browser.entity;

/**
 * @author 18517
 */
public class Home {
    private String title;
    private String website;
    private int Icon;
    private String time;

    Home() {

    }

    public Home(String title, String website, int Icon, String time) {
        this.title = title;
        this.website = website;
        this.Icon = Icon;
        this.time = time;
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getIcon(){
        return Icon;
    }

    public void setIcon(int Icon){
        this.Icon = Icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
