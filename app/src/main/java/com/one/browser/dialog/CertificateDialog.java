package com.one.browser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.one.browser.R;


/**
 * @author 18517
 */
public class CertificateDialog extends Dialog {


    protected CertificateDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    /**
     * Builder
     */
    public static class Builder {
        private final TextView certificateTitle;
        private final TextView certificateWidth;
        private final TextView certificateHeight;
        private final TextView btnCancel;
        private final TextView btnConfirm;
        private final SureListener sureListener;
        private final View mLayout;
        private View.OnClickListener mButtonCancelClickListener;
        private View.OnClickListener mButtonConfirmClickListener;
        private final CertificateDialog mDialog;

        public Builder(Context context, SureListener sureListener) {
            mDialog = new CertificateDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局文件
            mLayout = inflater.inflate(R.layout.dialog_certificate, null, false);
            // 添加布局文件到 Dialog
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            certificateTitle = mLayout.findViewById(R.id.certificate_title);
            certificateWidth = mLayout.findViewById(R.id.certificate_width);
            certificateHeight = mLayout.findViewById(R.id.certificate_height);
            btnCancel = mLayout.findViewById(R.id.btn_cancel);
            btnConfirm = mLayout.findViewById(R.id.btn_confirm);
            this.sureListener = sureListener;
        }


        /**
         * 设置取消按钮文字和监听
         */
        public void setButtonCancel(View.OnClickListener listener) {
            mButtonCancelClickListener = listener;
        }

        /**
         * 设置确认按钮文字和监听
         */
        public void setButtonConfirm(View.OnClickListener listener) {
            mDialog.dismiss();
            mButtonConfirmClickListener = listener;
        }

        public CertificateDialog create() {
            btnCancel.setOnClickListener(view -> {
                mDialog.dismiss();
                mButtonCancelClickListener.onClick(view);
            });

            btnConfirm.setOnClickListener(view -> {
                mDialog.dismiss();
                mButtonConfirmClickListener.onClick(view);
                Log.i("TAG", "setButtonConfirm: 宽度" + certificateWidth.getText());
                sureListener.getContent(certificateTitle.getText().toString(), certificateWidth.getText().toString(), certificateHeight.getText().toString());
            });


            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            return mDialog;
        }


    }

    public interface SureListener {

        /**
         * 内容
         * @param title 标题
         * @param certificateWidth 宽度
         * @param certificateHeight 高度
         * */
        void getContent(String title, String certificateWidth, String certificateHeight);

    }
}
