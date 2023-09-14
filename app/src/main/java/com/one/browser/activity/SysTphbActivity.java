package com.one.browser.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.onClick.itemOnClick;
import com.one.browser.utils.FileUtil;
import com.permissionx.guolindev.PermissionX;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author 18517
 */
public class SysTphbActivity extends AppCompatActivity {

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private MaterialButton xztp;
    private MaterialButton bctp;
    private ImageView tp;
    private Bitmap bitmap;
    private String savedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tphb);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                //.keyboardEnable(true)
                //.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("图片转黑白");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        xztp = findViewById(R.id.xztp);
        bctp = findViewById(R.id.bctp);
        tp = findViewById(R.id.tp);

        xztp.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        bctp.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= 30) {
                PermissionX.init(this)
                        .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                        .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                Toast.makeText(this, "已获取文件访问权限", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "未获得文件访问权限", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 3);
                            }
                        });
            }



            if (bitmap == null) {
                    Alerter.create(SysTphbActivity.this)
                            .setTitle("温馨提示")
                            .setText("请先选择图片")
                            .setBackgroundColorInt(Color.parseColor("#F44336"))
                            .show();
                }
                else {

                itemOnClick.LoadingDialog(SysTphbActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        savedFile = itemOnClick.SaveImage(SysTphbActivity.this, bitmap, "/HH浏览器/图片转黑白/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                        if (savedFile != null) {
                            android.media.MediaScannerConnection.scanFile((Activity) SysTphbActivity.this, new String[]{savedFile}, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String str, Uri uri) {
                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    intent.setData(uri);
//                                    ((Activity) TphbActivity.this).sendBroadcast(intent);
                                    itemOnClick.loadDialog.dismiss();
                                    Alerter.create(SysTphbActivity.this)
                                            .setTitle("保存成功")
                                            .setText("已保存到：" + savedFile)
                                            .setBackgroundColorInt(Color.parseColor("#4CAF50"))
                                            .show();
                                }
                            });
                        } else {
                            itemOnClick.loadDialog.dismiss();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        switch (_requestCode) {
            case REQ_CD_IMAGE:
                if (_resultCode == Activity.RESULT_OK) {
                    ArrayList<String> _filePath = new ArrayList<>();
                    if (_data != null) {
                        if (_data.getClipData() != null) {
                            for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                                ClipData.Item _item = _data.getClipData().getItemAt(_index);
                                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                            }
                        } else {
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                        }
                    }
                    bitmap = convertGreyImg(FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024));
                    tp.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth(); //获取位图的宽
        int height = img.getHeight(); //获取位图的高
        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

}