package com.one.browser.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.one.browser.utils.JurisdictionUtil;

import java.util.ArrayList;

/**
 * @author 18517
 */
public class SysSelectActivity extends AppCompatActivity {
    private final int CAMERA = 100;
    private final int PHOTO = 200;
    private final Intent image = new Intent(Intent.ACTION_PICK);
    private Button select_photo;
    private Button select_photograph;
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

    //  申请权限

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
            // 相册权限
        } else if (requestCode == CAMERA) {
            Log.i("TAG", "代码走到这里了");
            // 有访问权限
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (jurisdiction == 1) {
                    // Intent intent = new Intent(getApplicationContext(), CustomActivity.class);
                    // startActivity(intent);
                }
            } else {
                Toast.makeText(this, "没有权限；拒绝访问", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void incident() {
        String content = "<p><b><tt>图片信息：</tt></b></p><p>冲洗大小：" + wash_x + "*" + wash_y + "mm</p><p>像素大小：" + width + "*" + height + "px" + "</p><b><tt>拍照注意：</tt></b></p><p><font color= \"#ff0000\">* </font> 拍照时请将人像对准到人像框中</p><p><font color= \"#ff0000\">* </font> 请站在深色背景下拍照效果最佳</p><p><font color= \"#ff0000\">* </font> 衣服穿着请不要与背景颜色一致</p>";
        select_introduce.setText(Html.fromHtml(content));
        select_photo.setOnClickListener(view -> {
            // 图库
            jurisdiction = 0;
            // 判断是否有权限
            if (JurisdictionUtil.photoPermissions(SysSelectActivity.this)) {
                image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intentActivityResultLauncher.launch(image);
            }
        });

        select_photograph.setOnClickListener(view -> {
            // 相机
            jurisdiction = 1;
            if (JurisdictionUtil.cameraPermissions(SysSelectActivity.this)) {
                // Intent intent = new Intent(getApplicationContext(), CustomActivity.class);
                // startActivity(intent);
            }
        });
    }

    private void init() {
        select_introduce = findViewById(R.id.select_introduce);
        select_photo = findViewById(R.id.select_photo);
        select_photograph = findViewById(R.id.select_photograph);
    }

    /**
     * 页面跳转
     */
    ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        //此处是跳转的result回调方法
        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {

            Log.i("TAG", "onCreateView: OK >>>");
            ArrayList<String> _filePath = new ArrayList<>();
            if (result.getData().getClipData() != null) {
                Log.i("TAG", "get : 不为空 ");
                for (int _index = 0; _index < result.getData().getClipData().getItemCount(); _index++) {
                    ClipData.Item _item = result.getData().getClipData().getItemAt(_index);
                    _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                }
            } else {
                Log.i("TAG", "get : 为空 ");
                if (result.getData().getData() == null) {
                    Log.i("TAG", "onCreateView: data 数据为空");
                } else {
                    Log.i("TAG", "onCreateView: data 数据不为空");

                }
                Log.i("TAG", "图库图片宽度 >>> : " + width);
                Log.i("TAG", "图库图片高度 >>> : " + height);
                Log.i("TAG", "onCreateView: >>>>>>>" + FileUtil.convertUriToFilePath(getApplicationContext(), result.getData().getData()));

                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), result.getData().getData()));
            }
            Intent intent = new Intent(getApplicationContext(), SysPictureActivityActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("path", FileUtil.convertUriToFilePath(getApplicationContext(), result.getData().getData()));
            bundle.putString("name", name);
            bundle.putInt("width", width);
            bundle.putInt("height", height);
            intent.putExtras(bundle);
            if (_filePath.get(0) == null) {
                Log.i("TAG", "onCreateView: 获取数据为空");
            } else {
                Log.i("TAG", "获取数据不为空");
            }
            if (FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024) == null) {
                Log.i("Tag", "onCreateView: Home页面的值为空");
            } else {
                Log.i("TAG", "Home页面的值不为空");
            }
            startActivity(intent);

        }
        Log.i("TAG", "onActivityResult: ");
    });

}