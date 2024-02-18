package com.one.browser.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.limits.JurisdictionUtils;
import com.one.browser.utils.BitmapUtils;
import com.one.browser.utils.FileUtil;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

/**
 * @author 18517
 */
public class SysVolumeActivity extends AppCompatActivity {
    private Bitmap ImageBitmap;
    private static int QUALITY = 100;
    private TextView volume_text;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView volume_image;
    private LinearLayout volume_size;
    private TextView volume_ImageSize;
    private DiscreteSeekBar discreteSeekBar;


    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.i("PhotoPicker", "Selected URI: " + uri);
                    String path = FileUtil.convertUriToFilePath(this, uri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    BitmapFactory.decodeFile(path, options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    ImageBitmap = decodeSampledBitmapFromFile(path, imageWidth, imageHeight);
                    setImage(ImageBitmap);
                    volume_text.setVisibility(View.GONE);
                }
            });


    ActivityResultLauncher<String> register = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                // 处理获取到的图片URI
                if (uri != null) {
                    // 使用图片URI
                    Log.i("TAG", "获取到的图片URI: " + uri);
                    String path = FileUtil.convertUriToFilePath(this, uri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    BitmapFactory.decodeFile(path, options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    ImageBitmap = decodeSampledBitmapFromFile(path, imageWidth, imageHeight);
                    setImage(ImageBitmap);
                    volume_text.setVisibility(View.GONE);
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_volume);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("图片体积修改");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 初始化
        initView();
        // 设置监听
        setListener();
    }

    private void setListener() {
        volume_text.setOnClickListener(v -> {
            // 选择图片
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            } else {
                if (JurisdictionUtils.photoPermissions(SysVolumeActivity.this)) {
                    // 如果已授予照片权限，创建一个 Intent 用于选择照片
                    register.launch("image/*");
                }
            }

        });

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                Log.i("TAG", "获取滑动 >>>" + discreteSeekBar.getProgress());
                QUALITY = discreteSeekBar.getProgress();

                if (ImageBitmap != null) {
                    runOnUiThread(() -> BitmapUtils.getKb(getApplicationContext(), ImageBitmap.getWidth(), ImageBitmap.getHeight(), mPhotoEditor, QUALITY, bitmapSize -> {
                        Log.i("TAG", "图片大小: >>>> " + bitmapSize);
                        volume_ImageSize.setText(bitmapSize + "KB");
                    }));
                } else {
                    Toast.makeText(SysVolumeActivity.this, "禁止操作", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }

    private void initView() {
        volume_text = findViewById(R.id.volume_text);
        volume_image = findViewById(R.id.volume_image);
        volume_size = findViewById(R.id.volume_size);
        volume_ImageSize = findViewById(R.id.volume_ImageSize);
        discreteSeekBar = findViewById(R.id.volume_seekbar);
        mPhotoEditor = new PhotoEditor.Builder(this, volume_image).setPinchTextScalable(true).build();
    }


    private void setImage(Bitmap bitmap) {

        volume_image.getSource().setImageBitmap(bitmap);
        volume_size.setVisibility(View.VISIBLE);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 计算最大的inSampleSize值，该值是2的幂，并且保持高度和宽度大于请求的高度和宽度。
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String imagePath, int reqWidth, int reqHeight) {
        // 第一次解码获取图片尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // 计算inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 使用计算出的inSampleSize再次解码
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

}