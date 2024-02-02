package com.one.browser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.one.browser.R;

/**
 * @author 18517
 */
public class LoadingDialog extends Dialog {

    private static final String TAG = "LoadingDialog";

    private Context mContext;
    private String mMessage;
    private int mImageId;
    private boolean mCancelable;
    private RotateAnimation mRotateAnimation;

    public LoadingDialog(@NonNull Context context, String message, int imageId) {
        this(context, R.style.LoadingDialog, message, imageId, false);
    }

    public LoadingDialog(@NonNull Context context, int themeResId, String message, int imageId, boolean cancelable) {
        super(context, themeResId);
        this.mContext = context;
        this.mMessage = message;
        this.mImageId = imageId;
        this.mCancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_loading);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        // 设置窗口背景透明度
        attributes.alpha = 0.3f;
        // 设置窗口宽高为屏幕的三分之一（为了更好地适配，请别直接写死）
        attributes.width = screenWidth / 3;
        attributes.height = attributes.width;
        getWindow().setAttributes(attributes);
        setCancelable(mCancelable);

        TextView tv_loading = findViewById(R.id.tv_loading);
        ImageView iv_loading = findViewById(R.id.iv_loading);
        tv_loading.setText(mMessage);
        iv_loading.setImageResource(mImageId);
        // 先对imageView进行测量，以便拿到它的宽高（否则getMeasuredWidth为0）
        iv_loading.measure(0, 0);
        // 设置选择动画
        mRotateAnimation = new RotateAnimation(0, 360, iv_loading.getMeasuredWidth() / 2, iv_loading.getMeasuredHeight() / 2);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(-1);
        iv_loading.startAnimation(mRotateAnimation);
    }

    /**
     * 重写取消事件
     */
    @Override
    public void dismiss() {
        // 取消动画
        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
        }
        super.dismiss();

    }
}

