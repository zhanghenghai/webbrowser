package com.one.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
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

import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.dialog.LoadingDialog;
import com.one.browser.http.HumanFaceHttpServlet;
import com.one.browser.http.RepairHttpServlet;
import com.one.browser.limits.JurisdictionUtils;
import com.one.browser.utils.ImageUtil;
import com.one.browser.utils.SaveBitmapUtils;
import com.one.browser.utils.FileUtil;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author 18517
 */
public class SysRepairActivity extends AppCompatActivity {
    private static final String YES = "YES";
    private static final String TAG = "SysRepairActivity";
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private final int PHOTO_PICKER_REQUEST_CODE = 100;
    private LinearLayout select_repair_img;
    private TextView selectImage;
    private ImageView viewImage;
    private CardView saveCard;
    private Handler handler;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_repair);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("老照片修复");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 图片修复功能
        control();
        // 控件事件
        event();


        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String data = (String) msg.obj;
                // 关闭加载框
                loadingDialog.dismiss();
                // 在主线程中加载图像
                byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                viewImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                // 隐藏布局
                select_repair_img.setVisibility(View.GONE);
                // 显示图片
                viewImage.setVisibility(View.VISIBLE);
                // 显示页面
                saveCard.setVisibility(View.VISIBLE);
            }


        };


    }


    private void control() {
        // 隐藏和显示选择图片的布局
        select_repair_img = findViewById(R.id.select_repair_img);
        // 文字选择图片
        selectImage = findViewById(R.id.selectImage);
        // 显示图片
        viewImage = findViewById(R.id.viewImage);
        // 保存图片
        saveCard = findViewById(R.id.SaveCard);
        // 加载框
        loadingDialog = new LoadingDialog(this, R.drawable.ic_home_loader_line);

    }

    private void event() {
        // 选择图片
        selectImage.setOnClickListener(v -> {
            // 选择图片
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.i("TAG", "API版本 >>> 33以上");
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
            } else {
                Log.i("TAG", "API版本 >>> 33以下");
                // 判断是否有权限
                if (JurisdictionUtils.photoPermissions(this)) {
                    // 如果已授予照片权限，创建一个 Intent 用于选择照片
                    image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(image, PHOTO_PICKER_REQUEST_CODE);
                }
            }
        });



        saveCard.setOnClickListener(v -> new Thread(() -> {
            SaveBitmapUtils saveBitmapUtils = new SaveBitmapUtils();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmapUtils.saveQUp(((BitmapDrawable) viewImage.getDrawable()).getBitmap(), getApplicationContext(), System.currentTimeMillis() + "");

            } else {
                saveBitmapUtils.saveQNext(((BitmapDrawable) viewImage.getDrawable()).getBitmap(), getApplicationContext(), System.currentTimeMillis() + "");

            }
            finishShow(3000);
        }).start());
    }

    private void finishShow(int time) {
        new Thread(() -> {
            try {
                Thread.sleep(time);
                loadingDialog.dismiss();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Log.i("TAG", "onActivityResult: 未选择图片 >>>>>");
            return;
        }
        switch (requestCode) {
            case PHOTO_PICKER_REQUEST_CODE:
                getWebRequest(FileUtil.convertUriToFilePath(getApplicationContext(), data.getData()));
                break;
            default:
                break;
        }
    }

    private void getWebRequest(String path) {
        if (path != null) {

            Log.i("TAG", "getWebRequest: >>>>> " + path);
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                Log.i("TAG", "图片地址 >>>" + path);
                // 压缩图片
                initCompressorIO(path, new SysCartoonActivity.GoBack() {
                    @Override
                    public void succeed(File reduceImage) {
                        try {
                            Log.i(TAG, "原图片大小 >>>>> " + file.length());
                            Log.i(TAG, "图片压缩之后大小 >>>>> " + reduceImage.length());
                            if (reduceImage.length() > 1000000 || reduceImage.length() == 0) {
                                Log.i(TAG, "succeed: ");
                                loadingDialog.dismiss();

                            } else {
                                Log.i(TAG, "succeed: 开始网络请求 >>>>>>> ");
                                FileInputStream fileInputStream = new FileInputStream(reduceImage);
                                byte[] bytes = new byte[fileInputStream.available()];
                                fileInputStream.read(bytes);
                                fileInputStream.close();
                                // 将文件内容转换为 Base64 编码
                                String originalBase64 = ImageUtil.getByte(bytes);
                                // 人脸检测
                                HumanFaceHttpServlet.getBase64(originalBase64, result -> {
                                    Log.i(TAG, "onSuccess: >>>>>>> " + result);
                                    if (result == null) {
                                        loadingDialog.dismiss();

                                    } else {
                                        if (YES.equals(result)){
                                            RepairHttpServlet.getBase64(originalBase64, resultData -> {
                                                Log.i(TAG, "onSuccess: >>>>>>> " + resultData);
                                                if (resultData == null) {
                                                    loadingDialog.dismiss();

                                                } else {
                                                    Message message = new Message();
                                                    message.obj = resultData;
                                                    handler.sendMessage(message);
                                                }
                                            });
                                        }else {
                                            loadingDialog.dismiss();

                                            loadingDialog.dismiss();
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





    private void initCompressorIO(String photos, SysCartoonActivity.GoBack goBack) {
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

}