package com.one.browser.activity;

import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.BarHide;

import com.one.browser.R;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author 18517
 */
public class SysClockActivity extends AppCompatActivity {

    private TickerView tickerView;
    private TimerTask time;
    private Timer _timer = new Timer();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        getWindow().addFlags(128);
        tickerView = findViewById(R.id.tickerView);
        textView = findViewById(R.id.textView);
        tickerView.setAnimationInterpolator(new OvershootInterpolator());
        tickerView.setCharacterLists(TickerUtils.provideNumberList());

        time = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    tickerView.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    textView.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
                });
            }
        };
        _timer.scheduleAtFixedRate(time, 0, 1000);
    }
}