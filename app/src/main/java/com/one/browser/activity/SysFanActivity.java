package com.one.browser.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.http.TranslateUtil;
import com.one.browser.onClick.itemOnClick;
import com.one.browser.utils.HttpState;
import com.one.browser.utils.HttpUtil;

import java.util.Objects;


/**
 * @author 18517
 */
public class SysFanActivity extends AppCompatActivity {

    private MaterialButton button;
    private MaterialButton button1;
    private String name;
    private String left = "auto";
    private String right = "en";
    private ListPopupWindow listPopupWindow;
    private ListPopupWindow listPopupWindow1;
    private EditText editText;
    private TextView textView;
    private MaterialCardView card1;
    private MaterialCardView card2;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan);


        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Google翻译");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        button1 = findViewById(R.id.button1);
        textView = findViewById(R.id.textView);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        tts = new TextToSpeech(getApplicationContext(), null);

        listPopupWindow = new ListPopupWindow(SysFanActivity.this);
        final String[] style = {"自动检测", "简体中文", "英语", "日语", "韩语", "法语", "俄语", "繁体中文"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SysFanActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, style);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(button);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {

            listPopupWindow.dismiss();
            name = style[position];

            if (name.equals("自动检测")) {
                left = "auto";
                button.setText("自动检测");
            }
            if (name.equals("简体中文")) {
                left = "zh";
                button.setText("简体中文");
            }
            if (name.equals("英语")) {
                left = "en";
                button.setText("英语");
            }
            if (name.equals("日语")) {
                left = "ja";
                button.setText("日语");
            }
            if (name.equals("韩语")) {
                left = "ko";
                button.setText("韩语");
            }
            if (name.equals("法语")) {
                left = "fr";
                button.setText("法语");
            }
            if (name.equals("俄语")) {
                left = "ru";
                button.setText("俄语");
            }
            if (name.equals("繁体中文")) {
                left = "zh-tw";
                button.setText("繁体中文");
            }

        });

        button.setOnClickListener(v -> listPopupWindow.show());

        listPopupWindow1 = new ListPopupWindow(SysFanActivity.this);
        final String[] style1 = {"简体中文", "英语", "日语", "韩语", "法语", "俄语", "繁体中文"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(SysFanActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, style1);
        listPopupWindow1.setAdapter(adapter1);
        listPopupWindow1.setAnchorView(button1);
        listPopupWindow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                listPopupWindow1.dismiss();
                name = style1[position];

                if (name.equals("简体中文")) {
                    right = "zh";
                    button1.setText("简体中文");
                }
                if (name.equals("英语")) {
                    right = "en";
                    button1.setText("英语");
                }
                if (name.equals("日语")) {
                    right = "ja";
                    button1.setText("日语");
                }
                if (name.equals("韩语")) {
                    right = "ko";
                    button1.setText("韩语");
                }
                if (name.equals("法语")) {
                    right = "fr";
                    button1.setText("法语");
                }
                if (name.equals("俄语")) {
                    right = "ru";
                    button1.setText("俄语");
                }
                if (name.equals("繁体中文")) {
                    right = "zh-tw";
                    button1.setText("繁体中文");
                }

            }
        });

        button1.setOnClickListener(v -> listPopupWindow1.show());

        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                Log.i("TAG", "日志输出 >>>>");
            } else {
                Log.i("TAG", "left >>> " + left);
                Log.i("TAG", "right >>>" + right);
                Log.i("TAG", "输入内容 >>>> " + editText.getText().toString());
                itemOnClick.LoadingDialog(view.getContext());
                new TranslateUtil().getMessage(editText.getText().toString(), left, right, result -> {
                    Log.i("TAG", "onSuccess: >>> " + result);
                    JSONObject jsonObject = JSON.parseObject(result);
                    int code = jsonObject.getInteger("code");
                    String targetText;
                    if (code == HttpState.SUCCESS.getI()) {

                        String tarData = jsonObject.getString("data");
                        JSONObject dataObject = JSON.parseObject(tarData);
                        targetText = dataObject.getString("TargetText");
                        Log.i("TAG", "onSuccess: >>> " + targetText);
                    } else {
                        targetText = "翻译失败";
                        Log.i("TAG", "onCreate: 翻译失败");
                    }
                    textView.setText(targetText);
                    itemOnClick.loadDialog.dismiss();
                });
            }
        });

        card2.setOnClickListener(v -> tts.speak(textView.getText().toString(), TextToSpeech.QUEUE_ADD, null));

        card1.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Log.i("TAG", "日志输出 >>>");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}