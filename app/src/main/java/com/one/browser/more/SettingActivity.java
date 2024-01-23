package com.one.browser.more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.one.browser.R;
import com.one.browser.dialog.CustomEngineDialog;
import com.one.browser.dialog.ListDialog;
import com.one.browser.dialog.MultipleListDialog;

import java.util.Objects;

/**
 * @author 18517
 */
public class SettingActivity extends AppCompatActivity {

    private LinearLayout settingSearch;

    private LinearLayout settingTopAil;
    private LinearLayout settingClear;
    private SharedPreferences configure;
    public SharedPreferences.Editor editor;
    /**
     * 搜索引擎
     */
    private String engine = "";
    private String topContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("应用设置");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // 初始化变量
        init();
        // 点击事件
        onClick();

    }

    private void init() {
        settingSearch = findViewById(R.id.setting_search);
        settingTopAil = findViewById(R.id.setting_top_rail);
        settingClear = findViewById(R.id.setting_data_clear);
        configure = this.getSharedPreferences("Config", MODE_PRIVATE);
        // 写入
        editor = configure.edit();
        // 搜索引擎
        engine = configure.getString("Engine", "https://www.baidu.com/s?wd=");
        // 顶栏
        topContent = configure.getString("TopContent", "title");
        Log.i("TAG", "获取到数据  >>>: " + engine);
    }

    private void onClick() {
        // 设置弹窗
        settingSearch.setOnClickListener(view -> {
            ListDialog.Builder builder = new ListDialog.Builder(this, new String[]{"百度", "必应", "搜狗", "自定义"});
            builder.setSelectedPosition(getEngine(engine));
            builder.setOnClickListener((parent, view1, position, id) -> {
                // 写入数据
                switch (position) {
                    case 0:
                        engine = "https://www.baidu.com/s?wd=";
                        editor.putString("Engine", engine);
                        editor.apply();
                        break;
                    case 1:
                        engine = "https://cn.bing.com/search?q=";
                        editor.putString("Engine", engine);
                        editor.apply();
                        break;
                    case 2:
                        engine = "https://www.sogou.com/web?query=";
                        editor.putString("Engine", engine);
                        editor.apply();
                        break;
                    default:
                        CustomEngineDialog.CustomClickListener customClickListener = url -> {
                            Log.i("TAG", "获取到的链接 >>>: " + url);
                            if (url != null && url.length() > 0) {
                                Log.i("TAG", "自定义链接 >>>: " + url);
                                engine = url;
                                Log.i("TAG", "准备写入 >>> " + engine);
                                editor.putString("Engine", engine);
                                editor.apply();
                            }
                        };
                        CustomEngineDialog clickListener = new CustomEngineDialog(this, R.style.custom_dialog, engine);
                        clickListener.setCustomClickListener(customClickListener);
                        clickListener.show();
                        break;
                }
            });
            ListDialog listDialog = builder.create();
            listDialog.show();
        });
        // 顶栏弹窗
        settingTopAil.setOnClickListener(view -> {
            ListDialog.Builder builder = new ListDialog.Builder(this, new String[]{"标题", "网址", "域名"});
            builder.setSelectedPosition(getTopContent(topContent));
            builder.setOnClickListener((parent, view12, position, id) -> {
                switch (position) {
                    case 0:
                        topContent = "title";
                        editor.putString("TopContent", topContent);
                        editor.apply();
                        break;
                    case 1:
                        topContent = "website";
                        editor.putString("TopContent", topContent);
                        editor.apply();
                        break;
                    case 2:
                        topContent = "domain";
                        editor.putString("TopContent", topContent);
                        editor.apply();
                        break;
                    default:
                        break;
                }

            });
            builder.create().show();
        });
        // 清楚数据
        settingClear.setOnClickListener(v -> {
            String[] items = {"浏览历史", "Cookies", "表单数据", "网页存储"};
            MultipleListDialog.Builder builder = new MultipleListDialog.Builder(this, items);
            builder.setOnClickListener(checkedPositions -> {
                for (int i = 0; i < checkedPositions.size(); i++) {
                    // 如果选择的位置为true 则就是选择了该元素
                    if (checkedPositions.get(i)) {
                        Log.i("TAG", "onClick: " + items[i]);
                        if (i == 0) {
                            // 清除历史记录
                            editor.putString("History", "");
                            editor.apply();
                        } else if (i == 1) {
                            // 清除缓存
                            editor.putString("Cache", "");
                            editor.apply();
                        } else if (i == 2) {
                            // 清除Cookie
                            editor.putString("Cookie", "");
                            editor.apply();
                        } else {
                            // 清除所有数据
                            editor.putString("History", "");
                            editor.putString("Cache", "");
                            editor.putString("Cookie", "");
                            editor.apply();
                        }
                    }
                }
            });

            builder.create().show();

        });
    }


    private int getEngine(String engine) {
        int selectedPosition;
        switch (engine) {
            case "https://www.baidu.com/s?wd=":
                selectedPosition = 0;
                break;
            case "https://cn.bing.com/search?q=":
                selectedPosition = 1;
                break;
            case "https://www.sogou.com/web?query=":
                selectedPosition = 2;
                break;
            default:
                selectedPosition = 3;
                break;
        }
        return selectedPosition;
    }

    public int getTopContent(String topContent) {
        int checkedIndex;
        switch (topContent) {
            case "title":
                checkedIndex = 0;
                break;
            case "website":
                checkedIndex = 1;
                break;
            default:
                checkedIndex = 2;
        }
        return checkedIndex;
    }

}