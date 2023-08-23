package com.one.browser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.one.browser.R;
import com.one.browser.sqlite.ScriptDao;

/**
 * @author 18517
 */
public class ScriptDialog extends Dialog {


    public ScriptDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }


    public interface ScriptClickListener {
        /**
         * 点击事件
         *
         * @param view    试图
         * @param title   标题
         * @param content 内容
         */
        void onClick(View view, String title, String content);
    }


    public static class Builder {
        // 设置弹窗
        private ScriptDialog scriptDialog;
        // 弹窗标题
        private TextView title;
        // 弹窗标题输入框
        private EditText edit_title;
        // 弹窗内容输入框
        private EditText edit_context;
        // 按钮取消点击事件
        private View.OnClickListener cancelListener;
        // 设置布局
        private final View view;
        // 取消
        private final TextView dialog_cancel;
        // 确认
        private TextView dialog_affirm;

        public ScriptClickListener scriptClickListener;

        public Builder(Context context) {
            // 弹窗页面
            scriptDialog = new ScriptDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局
            view = inflater.inflate(R.layout.dialog_script, null, false);
            // 添加布局到弹窗中
            scriptDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            title = view.findViewById(R.id.script_title);
            edit_title = view.findViewById(R.id.script_title_edit);
            edit_context = view.findViewById(R.id.script_context_edit);
            dialog_cancel = view.findViewById(R.id.dialog_cancel);
            dialog_affirm = view.findViewById(R.id.dialog_affirm);
        }

        // 设置标题
        public Builder setTitle(String title) {
            this.title.setText(title);
            return this;
        }

        // 设置脚本名称
        public Builder setEditTitle(String editTitle) {
            this.edit_title.setText(editTitle);
            return this;
        }

        // 设置脚本内容
        public Builder setEditContext(String context) {
            this.edit_context.setText(context);
            return this;
        }

        // 设置取消按钮点击事件
        public Builder setCancelButton(View.OnClickListener onClickListener) {
            this.cancelListener = onClickListener;
            return this;
        }

        // 设置确认按钮点击事件
        public Builder setAffirmButton(ScriptClickListener scriptClickListener) {
            this.scriptClickListener = scriptClickListener;
            Log.i("TAG", " ................ ");
            return this;
        }

        public ScriptDialog create() {
            // 取消事件
            dialog_cancel.setOnClickListener(view -> {
                scriptDialog.dismiss();
                cancelListener.onClick(view);
            });
            // 确定时间
            dialog_affirm.setOnClickListener(view -> {
                scriptDialog.dismiss();
                scriptClickListener.onClick(view, edit_title.getText().toString(), edit_context.getText().toString());
            });
            // 放入试图
            scriptDialog.setContentView(view);
            scriptDialog.setCancelable(true);
            scriptDialog.setCanceledOnTouchOutside(false);
            return scriptDialog;
        }
    }
}
