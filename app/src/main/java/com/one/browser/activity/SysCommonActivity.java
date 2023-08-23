package com.one.browser.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.one.browser.R;
import com.one.browser.fragment.WebFragment;


/**
 * @author 18517
 */
public class SysCommonActivity extends AppCompatActivity {

    private WebFragment webFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        webFragment = new WebFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, webFragment).commitAllowingStateLoss();
    }
}