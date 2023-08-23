package com.one.browser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.tapadoo.alerter.Alerter;

import java.io.UnsupportedEncodingException;

/**
 * @author 18517
 */
public class SysBase64Activity extends AppCompatActivity {


    private MaterialCardView card1;
    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;
    private AutoCompleteTextView textView;
    private MaterialButton jia;
    private MaterialButton jie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base64);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Base64加解密");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        init();
    }
    private void init(){
        textInputEditText = findViewById(R.id.textInputEditText);
        textInputLayout = findViewById(R.id.textInputLayout);
        textView = findViewById(R.id.textview);
        card1 = findViewById(R.id.card1);
        jia = findViewById(R.id.jia);
        jie = findViewById(R.id.jie);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        jia.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError("请输入内容");
                textInputLayout.setErrorEnabled(true);
            }else {
                try {
                    textView.setText(base64Encode(textInputEditText.getText().toString(), "UTF-8"));
                } catch (Exception e) {
                }
            }
        });

        jie.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError("请输入内容");
                textInputLayout.setErrorEnabled(true);
            }else {
                try {
                    textView.setText(base64Decode(textInputEditText.getText().toString(), "UTF-8"));
                } catch (Exception e) {
                }
            }
        });

        card1.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Alerter.create((Activity) v.getContext())
                    .setTitle("复制成功")
                    .setText("已将内容复制到剪切板")
                    .setBackgroundColorInt(0xFF4CAF50)
                    .show();
        });

    }

    /**
     * Base64加密字符串
     * @param content -- 代加密字符串
     * @param charsetName -- 字符串编码方式
     * @return
     */
    private String base64Encode(String content, String charsetName) {
        if (TextUtils.isEmpty(charsetName)) {
            charsetName = "UTF-8";
        }
        try {
            byte[] contentByte = content.getBytes(charsetName);
            return Base64.encodeToString(contentByte, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Base64解密字符串
     * @param content -- 待解密字符串
     * @param charsetName -- 字符串编码方式
     * @return
     */
    private String base64Decode(String content, String charsetName) {
        if (TextUtils.isEmpty(charsetName)) {
            charsetName = "UTF-8";
        }
        byte[] contentByte = Base64.decode(content, Base64.DEFAULT);
        try {
            return new String(contentByte, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}