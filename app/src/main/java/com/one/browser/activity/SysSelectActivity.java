package com.one.browser.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.one.browser.R;
import com.one.browser.utils.FileUtil;
import com.one.browser.limits.JurisdictionUtils;


/**
 * @author 18517
 */
public class SysSelectActivity extends AppCompatActivity {
    private final int CAMERA = 100;
    private final int PHOTO = 200;
    private final Intent image = new Intent(Intent.ACTION_PICK);
    private Button select_photo;
    private TextView select_introduce;
    private int jurisdiction;
    /**
     * 图片的名称
     */
    private String name;
    /**
     * 图片宽度设定
     */
    private int width = 295;
    /**
     * 图片高度设定
     */
    private int height = 413;
    /**
     * 冲洗宽度设定
     */
    private int wash_x = 25;
    /**
     * 冲洗高度设定
     */
    private int wash_y = 35;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        // 初始化控件
        init();

        Bundle bundle = this.getIntent().getExtras();
        name = bundle.getString("name");
        Log.i("TAG", "图片的名称 >>>" + name);
        width = bundle.getInt("width");
        Log.i("TAG", "照片选择照片宽度 >>>" + width);
        height = bundle.getInt("height");
        Log.i("TAG", "照片选择照片高度 >>> " + height);
        wash_x = bundle.getInt("WashX");
        Log.i("TAG", "照片选择冲洗宽度 >>>" + wash_x);
        wash_y = bundle.getInt("WashY");
        Log.i("TAG", "照片选择冲洗宽度 >>>" + wash_y);
        // 控件点击事件
        incident();
    }

    /**
     * 申请权限成功之后的接口回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("TAG", "onRequestPermissionsResult: " + requestCode);
        // 相机权限
        if (requestCode == PHOTO) {
            // 有访问权限
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (jurisdiction == 0) {
                    image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    intentActivityResultLauncher.launch(image);
                }
            } else {
                Toast.makeText(this, "没有权限；拒绝访问", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void incident() {
        String content = "<p><b><tt>图片信息：</tt></b></p><p>冲洗大小：" + wash_x + "*" + wash_y + "mm</p><p>像素大小：" + width + "*" + height + "px" + "</p><b><tt>拍照注意：</tt></b></p><p><font color= \"#ff0000\">* </font> 拍照时请将人像对准到人像框中</p><p><font color= \"#ff0000\">* </font> 请站在深色背景下拍照效果最佳</p><p><font color= \"#ff0000\">* </font> 衣服穿着请不要与背景颜色一致</p>";
        select_introduce.setText(Html.fromHtml(content));
        // 相册
        select_photo.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Log.i("TAG", "API版本 >>> 33以上");
                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
                startActivityForResult(intent, CAMERA);
            } else {
                Log.i("TAG", "API版本 >>> 33以下");
                jurisdiction = 0;
                // 判断是否有权限
                if (JurisdictionUtils.photoPermissions(SysSelectActivity.this)) {
                    // 如果已授予照片权限，创建一个 Intent 用于选择照片
                    image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    // 使用 ActivityResultLauncher 启动 Intent，以选择照片
                    intentActivityResultLauncher.launch(image);
                }
            }
        });
        // 相机

    }

    private void init() {
        select_introduce = findViewById(R.id.select_introduce);
        select_photo = findViewById(R.id.select_photo);
    }


    /**
     * 页面跳转
     */
    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        // 此处是跳转的result回调方法
        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
            Log.i("TAG", "onCreateView: OK >>>");
            String filePath = null;

            if (result.getData().getClipData() != null) {
                Log.i("TAG", "get : 不为空 ");
                ClipData.Item item = result.getData().getClipData().getItemAt(0);
                filePath = FileUtil.convertUriToFilePath(getApplicationContext(), item.getUri());
            } else if (result.getData().getData() != null) {
                Log.i("TAG", "onCreateView: data 数据不为空");
                Log.i("TAG", "图库图片宽度 >>> : " + width);
                Log.i("TAG", "图库图片高度 >>> : " + height);
                filePath = FileUtil.convertUriToFilePath(getApplicationContext(), result.getData().getData());
            }

            if (filePath == null) {
                Log.i("TAG", "onCreateView: 获取数据为空");
            } else {
                Log.i("TAG", "获取数据不为空");
                Intent intent = new Intent(getApplicationContext(), SysApplyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bitmap", filePath);
                bundle.putString("name", name);
                bundle.putInt("width", width);
                bundle.putInt("height", height);
                intent.putExtras(bundle);
                if (FileUtil.decodeSampleBitmapFromPath(filePath, 1024, 1024) == null) {
                    Log.i("Tag", "onCreateView: Home页面的值为空");
                } else {
                    Log.i("TAG", "Home页面的值不为空");
                }
                startActivity(intent);
            }
        } else {
            Log.i("TAG", "onCreateView: NO >>> 未获取到图片 >>>");
            return;
        }
        Log.i("TAG", "onActivityResult: ");
    });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Log.i("TAG", "onActivityResult: 未选择图片 >>>>>");
            return;
        }

        switch (requestCode) {
            case CAMERA:
                Intent intent = new Intent(getApplicationContext(), SysApplyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bitmap", FileUtil.convertUriToFilePath(getApplicationContext(), data.getData()));
                bundle.putString("name", name);
                bundle.putInt("width", width);
                bundle.putInt("height", height);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }


    }

    private String getRealPathFromURI(Uri uri) {
        String realPath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                realPath = cursor.getString(column_index);
            }
            cursor.close();
        }

        return realPath;
    }


}