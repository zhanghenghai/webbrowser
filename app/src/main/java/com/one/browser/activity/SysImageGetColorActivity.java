package com.one.browser.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;
import com.one.browser.utils.FileUtil;


import java.util.Objects;

/**
 * @author 18517
 */
public class SysImageGetColorActivity extends AppCompatActivity {
    /**
     * Intent
     */
    private Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    /**
     * 选择图片
     */
    private TextView sysRecognitionOnClick;
    /**
     * 图片
     */
    private ImageView sysRecognitionImageView;
    /**
     * 结果
     */
    private TextView sysRecognitionResultTextView;
    /**
     * 颜色显示控件
     */
    private MaterialCardView sysRecognitionColorCardView;
    /**
     * 光标
     */
    private ImageView sysRecognitionCursorImageView;


    /**
     * API33以上
     */
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.i("PhotoPicker", "Selected URI: " + uri);
            String path = FileUtil.convertUriToFilePath(getApplicationContext(), uri);
            Log.i("TAG", "真实图片地址 >>>> ");
            sysRecognitionImageView.setImageURI(uri);
            setColor();
            setCursor();
        }
    });

    /**
     * API33以下
     */
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        // 处理获取到的图片URI
        if (uri != null) {
            // 使用图片URI
            Log.i("TAG", "获取到的图片URI: " + uri);
            String path = FileUtil.convertUriToFilePath(getApplicationContext(), uri);
            Log.i("TAG", "真实地址为 >>>> " + path);
            sysRecognitionImageView.setImageURI(uri);
            setColor();
            setCursor();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_image_get_color);


        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(R.color.appbarColor).navigationBarColor(R.color.backgroundColor).autoDarkModeEnable(true).navigationBarDarkIcon(true).init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("颜色识别");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // 初始化控件
        initialize();
        // 控件设置监听
        setListener();

    }

    private void setListener() {
        // 选择图片
        sysRecognitionOnClick.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(new PickVisualMediaRequest.Builder().build());

            } else {
                mGetContent.launch("image/*");

            }
        });

        /**
         *进行图片触摸检测
         */
        sysRecognitionImageView.setOnTouchListener((v, event) -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            ImageView imageView = (ImageView) v;
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            // ImageView尺寸
            int imageViewWidth = imageView.getWidth();
            int imageViewHeight = imageView.getHeight();

            // Bitmap尺寸
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();


            // 计算缩放比例和偏移量
            float scale;
            int xOffset = 0, yOffset = 0;
            if (imageViewWidth * bitmapHeight > imageViewHeight * bitmapWidth) {
                // 垂直方向有空白
                scale = (float) imageViewHeight / (float) bitmapHeight;
                xOffset = (imageViewWidth - (int) (bitmapWidth * scale)) / 2;
            } else {
                // 水平方向有空白
                scale = (float) imageViewWidth / (float) bitmapWidth;
                yOffset = (imageViewHeight - (int) (bitmapHeight * scale)) / 2;
            }

            // 调整坐标
            x = (int) ((x - xOffset) / scale);
            y = (int) ((y - yOffset) / scale);

            // 检查坐标越界问题
            if (x < 0 || y < 0 || x >= bitmapWidth || y >= bitmapHeight) {
                // 越界处理，例如可以不做任何操作或者给出提示
                return true;
            }


            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Log.i("TAG", "setListener: 已触发 >>>> ");

                    // 更新光标位置
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) sysRecognitionCursorImageView.getLayoutParams();
                    layoutParams.leftMargin = (int) event.getX() - sysRecognitionCursorImageView.getWidth() / 2; // 根据触摸点更新，减去光标宽度的一半
                    layoutParams.topMargin = (int) event.getY() - sysRecognitionCursorImageView.getHeight() / 2; // 根据触摸点更新，减去光标高度的一半
                    sysRecognitionCursorImageView.setLayoutParams(layoutParams);
                    sysRecognitionCursorImageView.setVisibility(View.VISIBLE);
                    // 获取像素颜色值
                    int pixel = bitmap.getPixel(x, y);
                    int redValue = Color.red(pixel);
                    int greenValue = Color.green(pixel);
                    int blueValue = Color.blue(pixel);

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        sysRecognitionResultTextView.setText("Red: " + redValue + " Green: " + greenValue + " Blue: " + blueValue);
                    } else {
                        sysRecognitionColorCardView.setCardBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                        sysRecognitionResultTextView.setText("Red: " + redValue + " Green: " + greenValue + " Blue: " + blueValue);
                    }
                    return true;
            }
            return true;
        });
    }

    /**
     * 设置光标位置
     */
    private void setCursor() {
        sysRecognitionImageView.post(() -> {
            Drawable drawable = sysRecognitionImageView.getDrawable();
            if (drawable == null) {
                return;
            }

            RectF drawableRect = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            sysRecognitionImageView.getImageMatrix().mapRect(drawableRect); // 将drawableRect映射到ImageView中的实际显示矩形

            // 使用drawableRect来定位光标
            float centerX = drawableRect.left + drawableRect.width() / 2;
            float centerY = drawableRect.top + drawableRect.height() / 2;

            // 调整光标位置，考虑光标本身尺寸
            float adjustedCenterX = centerX - sysRecognitionCursorImageView.getWidth() / 2;
            float adjustedCenterY = centerY - sysRecognitionCursorImageView.getHeight() / 2;

            // 更新光标位置
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) sysRecognitionCursorImageView.getLayoutParams();
            params.leftMargin = (int) adjustedCenterX;
            params.topMargin = (int) adjustedCenterY;
            sysRecognitionCursorImageView.setLayoutParams(params);
            sysRecognitionCursorImageView.setVisibility(View.VISIBLE);
        });
    }


    /**
     * 设置默认颜色
     */
    private void setColor() {
        sysRecognitionImageView.post(() -> {
            Drawable drawable = sysRecognitionImageView.getDrawable();
            if (!(drawable instanceof BitmapDrawable)) {
                return;
            }
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            // 获取ImageView的Drawable的显示矩形
            RectF drawableRect = new RectF(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            Matrix imageMatrix = sysRecognitionImageView.getImageMatrix();
            imageMatrix.mapRect(drawableRect);

            // 计算中心点坐标
            int x = (int) (drawableRect.left + drawableRect.width() / 2);
            int y = (int) (drawableRect.top + drawableRect.height() / 2);

            // 将中心点坐标转换为bitmap坐标
            float[] bitmapPoints = new float[]{x, y};
            Matrix inverse = new Matrix();
            imageMatrix.invert(inverse);
            inverse.mapPoints(bitmapPoints);
            int bitmapX = (int) bitmapPoints[0];
            int bitmapY = (int) bitmapPoints[1];

            if (bitmapX >= 0 && bitmapY >= 0 && bitmapX < bitmap.getWidth() && bitmapY < bitmap.getHeight()) {
                // 获取像素颜色值
                int pixel = bitmap.getPixel(bitmapX, bitmapY);
                int redValue = Color.red(pixel);
                int greenValue = Color.green(pixel);
                int blueValue = Color.blue(pixel);
                sysRecognitionColorCardView.setCardBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                sysRecognitionResultTextView.setText("Red: " + redValue + " Green: " + greenValue + " Blue: " + blueValue);
                Log.i("TAG", "setColor: Red: " + redValue + " Green: " + greenValue + " Blue: " + blueValue);
            }
        });
    }


    private void initialize() {
        sysRecognitionOnClick = findViewById(R.id.sys_image_get_color_onClick);
        sysRecognitionImageView = findViewById(R.id.sys_image_get_color_imageView);
        sysRecognitionColorCardView = findViewById(R.id.sys_image_get_color_cardView);
        sysRecognitionResultTextView = findViewById(R.id.sys_image_get_color_textView);
        sysRecognitionCursorImageView = findViewById(R.id.sys_image_get_color_cursor);
    }

}