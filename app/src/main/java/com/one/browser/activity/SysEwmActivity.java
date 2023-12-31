package com.one.browser.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;


import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;

import com.one.browser.R;
import com.one.browser.onClick.itemOnClick;
import com.one.browser.utils.CodeCreator;
import com.one.browser.utils.FileUtil;

import com.one.browser.utils.SaveBitmapUtils;

import com.yalantis.ucrop.UCrop;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;



public class SysEwmActivity extends AppCompatActivity {

    public final int REQ_CD_IMAGE = 101;
    private MaterialCardView ewmedit;
    private TextView ewmnr;
    private String qjcolor = "#FF000000";
    private String bjcolor = "#FFFFFFFF";
    private Chip chip1;
    private Chip chip2;
    private ImageView ewm;
    private DiscreteSeekBar seekbar1;
    private Intent image = new Intent(Intent.ACTION_PICK);
    private String nr;
    private Bitmap logo;
    private Button saveewm;
    private String savedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ewm);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("二维码生成");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        LinearLayout linear1 = findViewById(R.id.linear1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linear1.getLayoutParams();
        params.width = this.getResources().getDisplayMetrics().widthPixels;
        params.height = this.getResources().getDisplayMetrics().widthPixels;

        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        ewm = findViewById(R.id.ewm);
        seekbar1 = findViewById(R.id.seekbar1);
        saveewm = findViewById(R.id.saveewm);

        saveewm.setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) ewm.getDrawable()).getBitmap();
            if (bitmap == null) {
                Toast.makeText(SysEwmActivity.this, "图片为空", Toast.LENGTH_SHORT).show();
            } else {
                runOnUiThread(() -> {
                    SaveBitmapUtils saveBitmapUtils = new SaveBitmapUtils();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        saveBitmapUtils.saveQUp(bitmap, getApplicationContext(), System.currentTimeMillis() + "");
                        Toast.makeText(SysEwmActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        saveBitmapUtils.saveQNext(bitmap, getApplicationContext(), System.currentTimeMillis() + "");
                        Toast.makeText(SysEwmActivity.this, "图片保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        seekbar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if (!TextUtils.isEmpty(nr) && logo != null) {
                    ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, logo));
                    saveewm.setVisibility(View.VISIBLE);
                } else {
                    ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, null));
                    saveewm.setVisibility(View.VISIBLE);
                }
            }
        });

        chip1.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(v.getContext(), R.style.Dialog_Alert_one)
                .setTitle("前景颜色")
                .initialColor(Color.parseColor(qjcolor))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton("确定", (dialog, selectedColor, allColors) -> {
                    qjcolor = "#" + Integer.toHexString(selectedColor);
                    chip1.setChipIconTint(ColorStateList.valueOf(selectedColor));
                    if (!TextUtils.isEmpty(nr) && logo != null) {
                        ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, logo));
                        saveewm.setVisibility(View.VISIBLE);
                    } else {
                        ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, null));
                        saveewm.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .showColorEdit(true)
                .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                .build()
                .show());

        chip2.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(v.getContext(), R.style.Dialog_Alert_one)
                .setTitle("背景颜色")
                .initialColor(Color.parseColor(bjcolor))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(selectedColor -> {
                })
                .setPositiveButton("确定", (dialog, selectedColor, allColors) -> {
                    bjcolor = "#" + Integer.toHexString(selectedColor);
                    chip2.setChipIconTint(ColorStateList.valueOf(selectedColor));
                    if (!TextUtils.isEmpty(nr) && logo != null) {
                        ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, logo));
                        saveewm.setVisibility(View.VISIBLE);
                    } else {
                        ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, null));
                        saveewm.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .showColorEdit(true)
                .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                .build()
                .show());


        ewmedit = findViewById(R.id.ewmedit);
        ewmnr = findViewById(R.id.ewmnr);
        ewmedit.setOnClickListener(v -> {
            final AlertDialog mDialog = new AlertDialog.Builder(v.getContext())
                    .setPositiveButton("确定", null)
                    .setNegativeButton("取消", null)
                    .create();
            mDialog.setTitle("二维码内容");
            final View contentView = getLayoutInflater().inflate(R.layout.dialog_edit, null);
            mDialog.setView(contentView);
            final TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            textInputLayout.setHint("请输入二维码内容");
            final TextInputEditText edit1 = contentView.findViewById(R.id.editText);
            edit1.setInputType(InputType.TYPE_CLASS_TEXT);
            edit1.setSingleLine(false);
            edit1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    textInputLayout.setErrorEnabled(false);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            mDialog.setOnShowListener(dialog -> {
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(v1 -> {
                    if (TextUtils.isEmpty(edit1.getText().toString())) {
                        textInputLayout.setError("请输入二维码内容");
                        textInputLayout.setErrorEnabled(true);
                    } else {
                        mDialog.dismiss();
                        nr = edit1.getText().toString();
                        ewmnr.setText(edit1.getText().toString());
                        if (!TextUtils.isEmpty(nr) && logo != null) {
                            ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, logo));
                            saveewm.setVisibility(View.VISIBLE);
                        } else {
                            ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, null));
                            saveewm.setVisibility(View.VISIBLE);
                        }
                    }
                });
                negativeButton.setOnClickListener(v12 -> mDialog.dismiss());
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = SysEwmActivity.this.getResources().getDisplayMetrics().widthPixels / 5 * 4;
            mDialog.getWindow().setAttributes(layoutParams);
        });

    }


    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {

        super.onActivityResult(_requestCode, _resultCode, _data);

        if (_resultCode == RESULT_OK && _requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(_data);
            logo = FileUtil.decodeSampleBitmapFromPath(resultUri.getPath(), 1024, 1024);
            if (!TextUtils.isEmpty(nr) && logo != null) {
                ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, logo));
                saveewm.setVisibility(View.VISIBLE);
            } else {
                ewm.setImageBitmap(CodeCreator.createQRCode(nr, seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, null));
                saveewm.setVisibility(View.VISIBLE);
            }
        } else if (_resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(_data);
        }

        switch (_requestCode) {
            case REQ_CD_IMAGE:
                itemOnClick.startUCrop(SysEwmActivity.this, FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()), 1, 1);
                break;
            default:
                break;
        }
    }
}