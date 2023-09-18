package com.one.browser.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.utils.FileUtil;
import com.one.browser.utils.HttpUtil;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 18517
 */
public class SysBase64Activity extends AppCompatActivity {


    private MaterialCardView card1;
    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;
    private AutoCompleteTextView textView;
    private MaterialButton select;
    public final int REQ_CD_IMAGE = 101;

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

    private void init() {
        textInputEditText = findViewById(R.id.textInputEditText);
        textInputLayout = findViewById(R.id.textInputLayout);
        textView = findViewById(R.id.textview);
        card1 = findViewById(R.id.card1);
        select = findViewById(R.id.select);


        select.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, REQ_CD_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CD_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String path = FileUtil.convertUriToFilePath(getApplicationContext(), data.getData());
                Log.i("TAG", "onActivityResult: 回调图片地址 >>>>>>>" + path);
                try {
                    FileInputStream fileInputStream = new FileInputStream(path);
                    byte[] bytes = new byte[fileInputStream.available()];
                    fileInputStream.read(bytes);
                    fileInputStream.close();
                    getMessage(bytes, new Message() {
                        @Override
                        public void message(String base64) {
                            Log.i("TAG", "获取到的数据 : >>>" + base64);
                            JSONObject jsonObject = JSON.parseObject(base64);
                            String data = jsonObject.getString("data");
                            Log.i("TAG", "message: >>>" + data);
                            textView.setText(data);


                        }
                    });
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }
    }


    private void getMessage(byte[] bytes, Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("imageFile", "", RequestBody.create(MediaType.parse("image/*"), bytes)).build();
                Request request = new Request.Builder().url(HttpUtil.BASE64Image).post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String base64 = response.body().string();
                        Log.i("TAG", "返回的base64: >>> " + base64);
                        message.message(base64);


                    }
                });
            }
        });
    }

    private interface Message {
        /**
         * 成功返回
         *
         * @param base64 返回字符串
         */
        void message(String base64);
    }

}