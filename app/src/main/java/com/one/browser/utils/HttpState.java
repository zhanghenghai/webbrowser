package com.one.browser.utils;

/**
 * @author 18517
 */

public enum HttpState {
    SUCCESS(200);

    private int i;

    HttpState(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }
}
