package com.one.browser.sqlite;

/**
 * @author 18517
 */
public class Charge {
    /**
     * 记账图标
     */
    private String icon;
    /**
     * 记账标题
     */
    private String title;
    /**
     * 记账金额
     */
    private String money;
    /**
     * 记账时间
     */
    private String time;
    /**
     * 记账备注
     */
    private String remark;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
