package com.one.browser.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.utils.FileUtil;
import com.one.browser.utils.HttpUtil;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 18517
 */
public class SysImageColourActivity extends AppCompatActivity {

    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private MaterialButton selectImage;
    private MaterialButton saveImage;
    private ImageView picture;
    public final int REQ_CD_IMAGE = 101;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_image_colour);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("图像上色");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // 初始化
        init();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CD_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    String path = FileUtil.convertUriToFilePath(getApplicationContext(), data.getData());
                    Log.i("TAG", "onActivityResult: 回调图片地址 >>>>>>>" + path);
                    // 网络请求
                    try {
                        FileInputStream fileInputStream = new FileInputStream(path);
                        byte[] imageByte = new byte[fileInputStream.available()];
                        fileInputStream.read(imageByte);
                        fileInputStream.close();
                        getRestoration(imageByte, new Message() {
                            @Override
                            public void success(String url) {
                                Log.i("TAG", "success: >>>" + url);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Picasso.get().load(url).into(picture);
                                    }
                                });


                            }
                        });
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
                break;
            default:
                break;
        }
    }

    private void getRestoration(byte[] bytes, Message message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
                // 请求体
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "", RequestBody.create(MediaType.parse("image/*"), bytes)).build();
                // 构建异步请求
                Request request = new Request.Builder()
                        .url(HttpUtil.RESTORATION)
                        .post(requestBody)
                        .build();

                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i("TAG", "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String data = response.body().string();
                        Log.i("TAG", "onResponse: " + data);
                        JSONObject jsonObject = JSON.parseObject(data);

                        // Extract the image URL from the "data" field
                        String dataString = jsonObject.getString("data");
                        JSONObject dataObject = JSON.parseObject(dataString);
                        String imageUrl = dataObject.getJSONObject("data").getString("imageURL");
                        message.success(imageUrl);
                        System.out.println("Image URL: " + imageUrl);
                    }
                });


            }
        }).start();
    }


    interface Message {
        void success(String url);
    }

    private void init() {
        selectImage = findViewById(R.id.selectImage);
        saveImage = findViewById(R.id.saveImage);
        picture = findViewById(R.id.picture);

        selectImage.setOnClickListener(v -> {
            image = new Intent(Intent.ACTION_PICK);
            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(image, REQ_CD_IMAGE);
        });
    }

}
