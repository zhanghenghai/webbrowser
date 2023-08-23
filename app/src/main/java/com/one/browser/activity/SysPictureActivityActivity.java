package com.one.browser.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.one.browser.adapter.ColorAdapter;
import com.one.browser.entity.Colors;
import com.one.browser.http.PictureHttpServlet;

import com.one.browser.R;
import com.one.browser.utils.AlphaUtil;
import com.one.browser.utils.ImageUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author 18517
 */
public class SysPictureActivityActivity extends AppCompatActivity {

    /**
     * 图片显示控件
     */
    private ImageView imageView;
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片宽度
     */
    private int width;
    /**
     * 图片高度
     */
    private int height;
    /**
     * 多线程更新
     */
    private Handler handler;
    /**
     * alpha图
     */
    private Bitmap alpha;
    /**
     * 图片地址
     */
    private String path;
    /**
     * 数据
     */
    private List<Colors> colorsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("证件制作");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Bundle bundle = this.getIntent().getExtras();
        colorsList = new LinkedList<>();
        colorsList.add(new Colors("黑色", "#FF000000"));
        colorsList.add(new Colors("白色", "#FFFFFFFF"));
        colorsList.add(new Colors("蓝色", "#FF3700B3"));
        // 获取图片地址
        path = bundle.getString("path");
        name = bundle.getString("name");
        width = bundle.getInt("width", 295);
        height = bundle.getInt("height", 413);
        // 获取原图Base64
        String originalBase64 = ImageUtil.getImageStr(path);
        Log.i("TAG", "获取Base64数据");
        // 绑定ID
        imageView = findViewById(R.id.image);
        // 适配器
        RecyclerView recyclerView = findViewById(R.id.colourList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        ColorAdapter colorAdapter = new ColorAdapter(colorsList);
        recyclerView.setAdapter(colorAdapter);
        // 点击事件
        colorAdapter.setOnItemClickListener(position -> {
            colorAdapter.setPosition(position);
            colorAdapter.notifyDataSetChanged();
            Log.i("TAG", "获取标题 >>>" + colorsList.get(position).getTitle());
            Log.i("TAG", "获取颜色 >>>" + colorsList.get(position).getColor());
            compound(colorsList.get(position).getColor());
        });


        // 创建对象
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String data = (String) msg.obj;
                byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                alpha = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // 合成
                compound("#438edb");
            }
        };

        // 网络请求获取alpha
        PictureHttpServlet.getBase64(getApplicationContext(), originalBase64, result -> {
            Message msg = handler.obtainMessage();
            msg.what = RESULT_OK;
            msg.obj = result;
            handler.sendMessage(msg);
        });

    }

    private void compound(String color) {
        // 原图
        Bitmap original;
        // 背景
        Bitmap background;
        original = BitmapFactory.decodeFile(path);
        background = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        background.eraseColor(Color.parseColor(color));
        Bitmap bgImg = Bitmap.createScaledBitmap(background, original.getWidth(), original.getHeight(), true);
        Bitmap image = AlphaUtil.alphaBitmap(original, bgImg, alpha);
        imageView.setImageBitmap(image);
    }
}