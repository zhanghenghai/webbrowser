package com.one.browser.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.one.browser.R;

/**
 * @author 18517
 */

public class SysBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断主题
        if (isNightMode()) {
            // 夜间模式
            setTheme(R.style.AppTheme_Black);
            Log.i("TAG", "父类夜间模式");

        } else {
            // 日间模式
            Log.i("TAG","父类日间模式");
            setTheme(R.style.AppTheme_White);
        }
    }


    private boolean isNightMode() {
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("night_mode", false);
    }

}