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
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;

import com.one.browser.R;
import com.one.browser.onClick.itemOnClick;
import com.one.browser.utils.FileUtil;
import com.permissionx.guolindev.PermissionX;
import com.tapadoo.alerter.Alerter;
import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkText;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SysTpsyActivity extends AppCompatActivity {

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private MaterialButton xztp;
    private MaterialButton bctp;
    private Chip chip1;
    private Chip chip2;
    private ImageView tp;
    private TextView synr;
    private DiscreteSeekBar seekbar1;
    private DiscreteSeekBar seekbar2;
    private DiscreteSeekBar seekbar3;
    private int syys = 0xFFFFFFFF;
    private WatermarkText watermarkText;
    private MaterialCardView syedit;
    private Bitmap bitmap;
    private ListPopupWindow listPopupWindow;
    private String string;
    private String savedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpsy);

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
        toolbar.setTitle("图片水印");
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

        LinearLayout linear1 = findViewById(R.id.linear1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linear1.getLayoutParams();
        params.width = this.getResources().getDisplayMetrics().widthPixels;
        params.height = this.getResources().getDisplayMetrics().widthPixels/5*3;

        xztp = findViewById(R.id.xztp);
        bctp = findViewById(R.id.bctp);
        tp = findViewById(R.id.tp);
        synr = findViewById(R.id.synr);
        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        seekbar1 = findViewById(R.id.seekbar1);
        seekbar2 = findViewById(R.id.seekbar2);
        seekbar3 = findViewById(R.id.seekbar3);
        syedit = findViewById(R.id.syedit);

        bctp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                itemOnClick.LoadingDialog(SysTpsyActivity.this);

                if (Build.VERSION.SDK_INT >= 30) {
                    PermissionX.init(SysTpsyActivity.this)
                            .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                            .request((allGranted, grantedList, deniedList) -> {
                                if (allGranted) {
                                    Toast.makeText(SysTpsyActivity.this, "已获取文件访问权限", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SysTpsyActivity.this, "未获得文件访问权限", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, 3);
                                }
                            });
                }



                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        savedFile = itemOnClick.SaveImage(SysTpsyActivity.this, ((BitmapDrawable) tp.getDrawable()).getBitmap(), "/HH浏览器/图片水印/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                        if (savedFile != null){
                            android.media.MediaScannerConnection.scanFile((Activity) SysTpsyActivity.this, new String[]{savedFile}, null, new android.media.MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String str, Uri uri) {
                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    intent.setData(uri);
                                    ((Activity) SysTpsyActivity.this).sendBroadcast(intent);
                                    itemOnClick.loadDialog.dismiss();
                                    Alerter.create((Activity) SysTpsyActivity.this)
                                            .setTitle("保存成功")
                                            .setText("已保存到："+savedFile)
                                            .setBackgroundColorInt(Color.parseColor("#4CAF50"))
                                            .show();
                                }
                            });
                        }else{
                            itemOnClick.loadDialog.dismiss();
                        }
                    }
                }).start();
            }
        });

        listPopupWindow = new ListPopupWindow(SysTpsyActivity.this);
        final String[] style = {"无","中等","大"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SysTpsyActivity.this, R.layout.item_chinese, style);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(chip2);
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                if(style[position].equals("无")){
                    chip2.setText("间距：无");
                    string = synr.getText().toString();
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar2.getProgress())
                            .setRotation(seekbar3.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(tp);
                }
                if(style[position].equals("中等")){
                    chip2.setText("间距：中等");
                    string = synr.getText().toString() + "\n";
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar2.getProgress())
                            .setRotation(seekbar3.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(tp);
                }
                if(style[position].equals("大")){
                    chip2.setText("间距：大");
                    string = synr.getText().toString() + "\n\n";
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar2.getProgress())
                            .setRotation(seekbar3.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(tp);
                }
            }
        });

        chip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(v.getContext(),R.style.Dialog_Alert_one)
                        .setTitle("水印颜色")
                        .initialColor(syys)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("确定", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                syys = selectedColor;
                                chip1.setChipIconTint(ColorStateList.valueOf(selectedColor));
                                if(chip2.getText().equals("间距：无")){
                                    string = synr.getText().toString();
                                }else if(chip2.getText().equals("间距：中等")){
                                    string = synr.getText().toString() + "\n";
                                }else if(chip2.getText().equals("间距：大")){
                                    string = synr.getText().toString() + "\n\n";
                                }
                                watermarkText = new WatermarkText(string)
                                        .setTextColor(syys)
                                        .setTextAlpha(seekbar2.getProgress())
                                        .setRotation(seekbar3.getProgress())
                                        .setTextSize(seekbar1.getProgress());
                                WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                                        .setTileMode(true)
                                        .loadWatermarkText(watermarkText)
                                        .getWatermark()
                                        .setToImageView(tp);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .showColorEdit(true)
                        .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                        .build()
                        .show();
            }
        });
        chip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPopupWindow.show();
            }
        });
        xztp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(image, REQ_CD_IMAGE);
            }
        });
        syedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog mDialog = new AlertDialog.Builder(v.getContext())
                        .setPositiveButton("确定",null)
                        .setNegativeButton("取消",null)
                        .create();
                mDialog.setTitle("水印内容");
                final View contentView = getLayoutInflater().inflate(R.layout.dialog_edit,null);
                mDialog.setView(contentView);
                final TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
                textInputLayout.setHint("请输入水印内容");
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
                mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(edit1.getText().toString())){
                                    textInputLayout.setError("请输入水印内容");
                                    textInputLayout.setErrorEnabled(true);
                                }else {
                                    mDialog.dismiss();
                                    synr.setText(edit1.getText().toString());
                                    if(chip2.getText().equals("间距：无")){
                                        string = synr.getText().toString();
                                    }else if(chip2.getText().equals("间距：中等")){
                                        string = synr.getText().toString() + "\n";
                                    }else if(chip2.getText().equals("间距：大")){
                                        string = synr.getText().toString() + "\n\n";
                                    }
                                    watermarkText = new WatermarkText(string)
                                            .setTextColor(syys)
                                            .setTextAlpha(seekbar2.getProgress())
                                            .setRotation(seekbar3.getProgress())
                                            .setTextSize(seekbar1.getProgress());
                                    WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                                            .setTileMode(true)
                                            .loadWatermarkText(watermarkText)
                                            .getWatermark()
                                            .setToImageView(tp);
                                }
                            }
                        });
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog.dismiss();
                            }
                        });
                    }
                });
                mDialog.show();
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = SysTpsyActivity.this.getResources().getDisplayMetrics().widthPixels/5*4;
                mDialog.getWindow().setAttributes(layoutParams);
            }
        });
        seekbar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if(chip2.getText().equals("间距：无")){
                    string = synr.getText().toString();
                }else if(chip2.getText().equals("间距：中等")){
                    string = synr.getText().toString() + "\n";
                }else if(chip2.getText().equals("间距：大")){
                    string = synr.getText().toString() + "\n\n";
                }
                watermarkText = new WatermarkText(string)
                        .setTextColor(syys)
                        .setTextAlpha(seekbar2.getProgress())
                        .setRotation(seekbar3.getProgress())
                        .setTextSize(seekbar1.getProgress());
                WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                        .setTileMode(true)
                        .loadWatermarkText(watermarkText)
                        .getWatermark()
                        .setToImageView(tp);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        seekbar2.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if(chip2.getText().equals("间距：无")){
                    string = synr.getText().toString();
                }else if(chip2.getText().equals("间距：中等")){
                    string = synr.getText().toString() + "\n";
                }else if(chip2.getText().equals("间距：大")){
                    string = synr.getText().toString() + "\n\n";
                }
                watermarkText = new WatermarkText(string)
                        .setTextColor(syys)
                        .setTextAlpha(seekbar2.getProgress())
                        .setRotation(seekbar3.getProgress())
                        .setTextSize(seekbar1.getProgress());
                WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                        .setTileMode(true)
                        .loadWatermarkText(watermarkText)
                        .getWatermark()
                        .setToImageView(tp);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        seekbar3.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if(chip2.getText().equals("间距：无")){
                    string = synr.getText().toString();
                }else if(chip2.getText().equals("间距：中等")){
                    string = synr.getText().toString() + "\n";
                }else if(chip2.getText().equals("间距：大")){
                    string = synr.getText().toString() + "\n\n";
                }
                watermarkText = new WatermarkText(string)
                        .setTextColor(syys)
                        .setTextAlpha(seekbar2.getProgress())
                        .setRotation(seekbar3.getProgress())
                        .setTextSize(seekbar1.getProgress());
                WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                        .setTileMode(true)
                        .loadWatermarkText(watermarkText)
                        .getWatermark()
                        .setToImageView(tp);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

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
                    bctp.setVisibility(View.VISIBLE);
                    bitmap = FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024);
                    if(chip2.getText().equals("间距：无")){
                        string = synr.getText().toString();
                    }else if(chip2.getText().equals("间距：中等")){
                        string = synr.getText().toString() + "\n";
                    }else if(chip2.getText().equals("间距：大")){
                        string = synr.getText().toString() + "\n\n";
                    }
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar2.getProgress())
                            .setRotation(seekbar3.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(SysTpsyActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(tp);
                }
                break;
            default:
                break;
        }
    }
}