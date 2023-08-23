package com.one.browser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.one.browser.R;

/**
 * @author 18517
 */
public class CustomEngineDialog extends Dialog {

    private EditText editText;
    private TextView confirm;
    private TextView cancel;

    public CustomClickListener customClickListener;

    public interface CustomClickListener {
        /**
         * 单击事件
         *
         * @param url 下载链接
         */
        void onClick(String url);
    }

    public void setCustomClickListener(CustomClickListener customClickListener) {
        this.customClickListener = customClickListener;
    }

    public CustomEngineDialog(@NonNull Context context, int theme, String engine) {
        super(context, theme);
        setContentView(R.layout.dialog_custom);
        init(engine);
        // 取消
        cancel.setOnClickListener(v -> dismiss());
        // 确定
        confirm.setOnClickListener(v -> {
            // 获取数据
            String url = editText.getText().toString();
            Log.i("TAG", "获取自定义链接 >>>> " + url);
            customClickListener.onClick(url);
            dismiss();
        });
    }

    private void init(String engine) {
        editText = findViewById(R.id.custom_edit);
        cancel = findViewById(R.id.custom_cancel);
        confirm = findViewById(R.id.custom_confirm);
        if (engine != null && engine.length() > 0) {
            editText.setText(engine);
        }
    }
}
