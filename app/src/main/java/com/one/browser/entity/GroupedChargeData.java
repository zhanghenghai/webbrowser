package com.one.browser.entity;

import com.one.browser.sqlite.Charge;


import java.util.List;

/**
 * @author 18517
 */
public class GroupedChargeData {

    private String date;
    private List<Charge> chargeList;

    public GroupedChargeData(String date, List<Charge> chargeList) {
        this.date = date;
        this.chargeList = chargeList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Charge> getChargeList() {
        return chargeList;
    }

    public void setChargeList(List<Charge> chargeList) {
        this.chargeList = chargeList;
    }



}
