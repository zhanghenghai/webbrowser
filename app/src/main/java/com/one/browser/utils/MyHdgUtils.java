package com.one.browser.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MyHdgUtils {

    //==========软键盘==========
    // 弹出软键盘
    public static void openKeyboard(Context context, View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(view, 0);
    }

    // 关闭软键盘
    public static void closeKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //开关软键盘
    public static void toggleKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
    }
    //==========软键盘==========

}
