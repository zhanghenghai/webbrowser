package com.one.browser.activity;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.one.browser.dialog.LoadingDialog;
import com.one.browser.http.CartoonHttpServlet;
import com.one.browser.http.HumanFaceHttpServlet;
import com.one.browser.limits.JurisdictionUtils;
import com.one.browser.utils.FileUtil;
import com.one.browser.utils.ImageUtil;
import com.one.browser.utils.ScaleBitmap;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author 18517
 */
public class SysCartoonActivity extends AppCompatActivity {

    private static final String YES = "YES";
    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private LinearLayout select_img;
    private TextView selectImage;
    private ImageView viewImage;
    private LinearLayout select_style;
    private TextView line1;
    private TextView line2;
    private TextView line3;
    private TextView line7;
    private String path;
    private final String TAG = "TAG";
    private Handler handler;
    private String saveImage;
    private CardView SaveCardView;
    /**
     * 自定义弹窗
     */
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_cartoon);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("图片动漫化");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 控件初始化
        controlInit();
        // 控件事件
        controlEvent();


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String data = (String) msg.obj;
                saveImage = data;
                // 关闭加载框
                loadingDialog.dismiss();
                // 在主线程中加载图像
                Glide.with(getApplicationContext())
                        .load(data)
                        .into(viewImage);

                // 隐藏布局
                select_img.setVisibility(View.GONE);
                // 显示图片
                viewImage.setVisibility(View.VISIBLE);
                // 显示选择风格按钮
                select_style.setVisibility(View.VISIBLE);
                // 显示保存按钮
                SaveCardView.setVisibility(View.VISIBLE);
            }
        };

    }

    private void controlEvent() {
        selectImage.setOnClickListener(this::onClick);
        // 选择类型
        line1.setOnClickListener(v -> {
            line1.setTextColor(Color.parseColor("#8f89b7"));
            line2.setTextColor(Color.parseColor("#000000"));
            line3.setTextColor(Color.parseColor("#000000"));

            line7.setTextColor(Color.parseColor("#000000"));
            getWebRequest("anime");
        });
        line2.setOnClickListener(v -> {
            line1.setTextColor(Color.parseColor("#000000"));
            line2.setTextColor(Color.parseColor("#8f89b7"));
            line3.setTextColor(Color.parseColor("#000000"));
            line7.setTextColor(Color.parseColor("#000000"));
            getWebRequest("3d");
        });
        line3.setOnClickListener(v -> {
            line1.setTextColor(Color.parseColor("#000000"));
            line2.setTextColor(Color.parseColor("#000000"));
            line3.setTextColor(Color.parseColor("#8f89b7"));
            line7.setTextColor(Color.parseColor("#000000"));
            getWebRequest("handdrawn");
        });
        line7.setOnClickListener(v -> {
            line1.setTextColor(Color.parseColor("#000000"));
            line2.setTextColor(Color.parseColor("#000000"));
            line3.setTextColor(Color.parseColor("#000000"));
            line7.setTextColor(Color.parseColor("#8f89b7"));
            getWebRequest("hongkong");
        });

        SaveCardView.setOnClickListener(v -> new Thread(() -> {
            Log.i("TAG", "获取图片地址: >>>> " + saveImage);
            Uri uri;
            OutputStream output;
            try {
                URL url = new URL(saveImage); // 更改为图片的URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();

                // 判断Android版本
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg"); // 更改文件名
                    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg"); // 更改MIME类型
                    values.put(MediaStore.Images.ImageColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // 更改保存路径
                    ContentResolver contentResolver = getContentResolver();
                    uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Log.i("TAG", "图片保存位置: >>>> " + uri);
                    output = contentResolver.openOutputStream(uri);


                } else {
                    File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File outputFile = new File(externalDir, System.currentTimeMillis() + ".jpg"); // 更改文件名
                    uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", outputFile);
                    output = getContentResolver().openOutputStream(uri);

                }

                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "其他异常: " + e.getMessage());
            }
        }).start());
    }


    private void controlInit() {
        select_img = findViewById(R.id.select_img);
        selectImage = findViewById(R.id.selectImage);
        viewImage = findViewById(R.id.viewImage);
        select_style = findViewById(R.id.select_style);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);
        line7 = findViewById(R.id.line7);
        SaveCardView = findViewById(R.id.SaveCard);
        loadingDialog = new LoadingDialog(this, R.drawable.ic_home_loader_line);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Log.i(TAG, "onActivityResult: 未选择图片 >>>>>");
            return;
        }
        switch (requestCode) {
            case REQ_CD_IMAGE:
                path = FileUtil.convertUriToFilePath(getApplicationContext(), data.getData());
                Log.i("TAG", "图片路径 >>> " + path);
                int customColor = Color.parseColor("#8f89b7");
                line1.setTextColor(customColor);
                getWebRequest("anime");
                // 使用RxJava创建一个Observable
                break;
            default:
                break;
        }
    }

    private void getWebRequest(String type) {
        if (path != null) {
            setMassage("正在加载图片...", R.drawable.ic_home_loader_line);
            Log.i("TAG", "getWebRequest: >>>>> " + path);
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                Log.i(TAG, "图片地址 >>>" + path);
                // 压缩图片
                initCompressorIO(path, new GoBack() {
                    @Override
                    public void succeed(File reduceImage) {
                        try {
                            Log.i(TAG, "原图片大小 >>>>> " + file.length());
                            Log.i(TAG, "图片压缩之后大小 >>>>> " + reduceImage.length());
                            if (reduceImage.length() > 1000000 || reduceImage.length() == 0) {
                                Log.i(TAG, "succeed: ");

                            } else {
                                Log.i(TAG, "succeed: 开始网络请求 >>>>>>> ");
                                Bitmap bitmap = BitmapFactory.decodeFile(reduceImage.getAbsolutePath());
                                bitmap = ScaleBitmap.centerCrop(bitmap, 295, 413);


                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();

                                // 将文件内容转换为 Base64 编码
                                String originalBase64 = ImageUtil.getByte(byteArray);
                                // 检测人脸
                                HumanFaceHttpServlet.getBase64(originalBase64, result -> {
                                    Log.i(TAG, "onSuccess: >>>>>>> " + result);
                                    if (result == null || "NO".equals(result)) {
                                        Log.i(TAG, "succeed: 人脸检测失败 >>>>>>> ");
                                        loadingDialog.dismiss();
                                        handler.post(() -> {
                                            Toast.makeText(SysCartoonActivity.this, "人脸检测失败", Toast.LENGTH_SHORT).show();
                                        });
                                    } else {
                                        if (result.equals(YES)) {
                                            CartoonHttpServlet.getBase64(originalBase64, type, resultData -> {
                                                Log.i(TAG, "onSuccess: >>>>>>> " + resultData);
                                                if (resultData == null) {
                                                    loadingDialog.dismiss();
                                                    handler.post(() -> {
                                                        Toast.makeText(SysCartoonActivity.this, "人脸检测失败", Toast.LENGTH_SHORT).show();
                                                    });
                                                } else {
                                                    Message message = new Message();
                                                    message.obj = resultData;
                                                    handler.sendMessage(message);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void lose() {

                    }
                });
            }
        } else {
            Log.i("TAG", "getWebRequest: >>>>> " + null);
            Toast.makeText(this, "图片路径为空", Toast.LENGTH_SHORT).show();
        }
    }


    private void initCompressorIO(String photos, GoBack goBack) {
        try {
            Luban.with(this)
                    .load(photos)
                    .ignoreBy(1024)
                    .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            try {
                                goBack.succeed(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                            goBack.lose();
                        }
                    }).launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置提示文字和图片
     */
    private void setMassage(String message, int imageId) {
        loadingDialog.setMessage(message, imageId);
        loadingDialog.show();

    }

    /**
     * 成功或者失败
     */
    private void setSandF(int time) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
                loadingDialog.dismiss();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void onClick(View v) {
        // 选择图片
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.i("TAG", "API版本 >>> 33以上");
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            startActivityForResult(intent, REQ_CD_IMAGE);
        } else {
            Log.i("TAG", "API版本 >>> 33以下");
            // 判断是否有权限
            if (JurisdictionUtils.photoPermissions(SysCartoonActivity.this)) {
                // 如果已授予照片权限，创建一个 Intent 用于选择照片
                image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(image, REQ_CD_IMAGE);
            }
        }
    }

    public interface GoBack {
        void succeed(File file) throws IOException;

        void lose();
    }
}