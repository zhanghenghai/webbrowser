package com.one.browser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.one.browser.R;

/**
 * @author 18517
 */
public class OfficialDialog extends Dialog {
    /* Constructor */

    private OfficialDialog(Context context) {
        super(context);
    }

    private OfficialDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private OfficialDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * Builder
     */
    public static class Builder {
        private TextView title, size;
        private final EditText message;
        private TextView dialog_cancel, dialog_confirm;
        private View mLayout;
        private View.OnClickListener mButtonCancelClickListener;
        private View.OnClickListener mButtonConfirmClickListener;
        private OfficialDialog mDialog;

        public Builder(Context context) {
            mDialog = new OfficialDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局文件
            mLayout = inflater.inflate(R.layout.dialog_official, null, false);
            // 添加布局文件到 Dialog
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            title = mLayout.findViewById(R.id.title);
            size = mLayout.findViewById(R.id.size);
            message = mLayout.findViewById(R.id.message);
            dialog_cancel = mLayout.findViewById(R.id.dialog_cancel);
            dialog_confirm = mLayout.findViewById(R.id.dialog_confirm);
        }


        /**
         * 设置标题
         */
        public Builder setTitle(String title) {
            this.title.setText(title);
            return this;
        }

        /**
         * 设置内容
         */
        public Builder setMessage(String message) {
            this.message.setText(message);
            return this;
        }

        /**
         * 设置文件大小
         */
        public Builder setSize(String size) {
            Log.i("TAG", "打印文件大小 >>>: " + size);
            String num = "文件大小：" + size + "MB";
            this.size.setText(num);
            return this;
        }


        /**
         * 设置取消按钮文字和监听
         */
        public Builder setButtonCancel(View.OnClickListener listener) {
            mDialog.dismiss();
            mButtonCancelClickListener = listener;
            return this;
        }

        /**
         * 设置确认按钮文字和监听
         */
        public void setButtonConfirm(View.OnClickListener listener) {
            mDialog.dismiss();
            mButtonConfirmClickListener = listener;
        }

        public OfficialDialog create() {
            dialog_cancel.setOnClickListener(view -> {
                mDialog.dismiss();
                mButtonCancelClickListener.onClick(view);
            });
            dialog_confirm.setOnClickListener(view -> {
                mDialog.dismiss();
                mButtonConfirmClickListener.onClick(view);
            });
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }
    }
}
