package com.one.browser.activity;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.dialog.LoadingDialog;
import com.one.browser.limits.JurisdictionUtils;
import com.one.browser.utils.FileUtil;
import com.one.browser.utils.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 18517
 */
public class SysRecognitionActivity extends AppCompatActivity {
    private TextView sysRecognitionOnClick;
    private ImageView sysRecognitionImageView;
    private TextView sysRecognitionResultTextView;
    private MaterialCardView materialCardView;
    private LinearLayoutCompat sysRecognitionResultLinearLayoutCompat;
    private Disposable disposable;
    private Intent image = new Intent(Intent.ACTION_PICK);
    private LoadingDialog loadingDialog;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.i("PhotoPicker", "Selected URI: " + uri);
                    // 将图片转为真是地址
                    String path = FileUtil.convertUriToFilePath(getApplicationContext(), uri);
                    Log.i("PhotoPicker", "Selected path: " + path);
                    // 显示图片
                    sysRecognitionImageView.setImageURI(uri);
                    // 进行网络请求
                    try {
                        networkRequest(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.i("PhotoPicker", "No media selected");
                }
            });

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // 处理获取到的图片URI
                if (uri != null) {
                    // 使用图片URI
                    Log.i("TAG", "获取到的图片URI: " + uri);
                    String path = FileUtil.convertUriToFilePath(getApplicationContext(), uri);
                    Log.i("TAG", "获取到的图片路径: " + path);
                    // 显示图片
                    sysRecognitionImageView.setImageURI(uri);
                    // 进行网络请求
                    try {
                        networkRequest(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("文字识别");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //初始化控件
        initialize();
        //设置监听
        setListener();
    }

    private void setListener() {
        sysRecognitionOnClick.setOnClickListener(v -> {
            // 判断版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            } else {
                if (JurisdictionUtils.photoPermissions(SysRecognitionActivity.this)) {
                    // 如果已授予照片权限，创建一个 Intent 用于选择照片
                    image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    mGetContent.launch("image/*");
                }
            }
        });
        materialCardView.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", sysRecognitionResultTextView.getText().toString()));
            Log.i("TAG", "复制完成");
        });
    }

    private void initialize() {
        sysRecognitionOnClick = findViewById(R.id.sys_recognition_onClick);
        sysRecognitionImageView = findViewById(R.id.sys_recognition_imageView);
        sysRecognitionResultTextView = findViewById(R.id.sys_recognition_textView);
        sysRecognitionResultLinearLayoutCompat = findViewById(R.id.sys_recognition_layout);
        loadingDialog = new LoadingDialog(this, "正在识别", R.drawable.ic_home_loader_line);
        materialCardView = findViewById(R.id.sys_recognition_cardView);
    }


    /**
     * 网络请求
     */
    private void networkRequest(String path) throws IOException {

        // 启动等待弹窗
        loadingDialog.show();
        disposable = Observable.fromCallable(() -> {
                    OkHttpClient client = new OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS).build();
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody requestBody = RequestBody.create("{\"image\":\"" + imageToBase64(path) + "\"}", mediaType);
                    Request request = new Request.Builder().url(HttpUtil.RECOGNITION).post(requestBody).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.i("TAG", "onResponse: " + s);
                    StringBuffer parsedResult = new StringBuffer();
                    // 处理数据
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    String data = jsonObject.getString("data");
                    JSONObject words_result = JSONObject.parseObject(data);
                    String data1 = words_result.getString("words_result");
                    JSONArray jsonArray = JSONArray.parseArray(data1);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String words = jsonObject1.getString("words");
                        Log.i("TAG", "onResponse: " + words);
                        parsedResult.append(words).append("\n");
                    }
                    // 解析数据将数据显示到页面上
                    sysRecognitionResultTextView.setText(parsedResult.toString());
                    // 隐藏
                    sysRecognitionOnClick.setVisibility(View.GONE);
                    // 显示
                    sysRecognitionResultLinearLayoutCompat.setVisibility(View.VISIBLE);
                    // 关闭弹窗
                    loadingDialog.dismiss();
                }, Throwable::printStackTrace);
    }


    public static String imageToBase64(String imagePath) throws IOException {
        File file = new File(imagePath);
        FileInputStream imageInFile = new FileInputStream(file);

        byte[] imageData = new byte[(int) file.length()];
        imageInFile.read(imageData);
        imageInFile.close();

        return URLEncoder.encode(Base64.getEncoder().encodeToString(imageData), "UTF-8");
    }

}