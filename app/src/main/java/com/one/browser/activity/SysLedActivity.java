package com.one.browser.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.widget.MarqueeView;

/**
 * @author 18517
 */
public class SysLedActivity extends AppCompatActivity {

    private MarqueeView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_led);


        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        mv = findViewById(R.id.mv);
        mv.setContent(getIntent().getStringExtra("nr"));
        mv.setTextColor(Color.parseColor(getIntent().getStringExtra("wzys")));
        mv.setTextSize(getIntent().getIntExtra("dx",120));
        mv.setTextSpeed(getIntent().getIntExtra("sd",12));
        mv.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("bjys")));

    }
}