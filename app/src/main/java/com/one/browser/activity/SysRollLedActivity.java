package com.one.browser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.one.browser.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * @author 18517
 */
public class SysRollLedActivity extends AppCompatActivity {


    private MaterialCardView card1;
    private MaterialCardView card2;
    private String bjcolor = "#FF000000";
    private String wzcolor = "#FFFFFFFF";
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    private DiscreteSeekBar seekbar1;
    private DiscreteSeekBar seekbar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_roll_led);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .keyboardEnable(true)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("LED滚动字幕");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        textInputEditText = findViewById(R.id.textInputEditText);
        textInputLayout = findViewById(R.id.textInputLayout);
        seekbar1 = findViewById(R.id.seekbar1);
        seekbar2 = findViewById(R.id.seekbar2);
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);

        textInputEditText.addTextChangedListener(new TextWatcher() {
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    textInputLayout.setError("请输入内容");
                    textInputLayout.setErrorEnabled(true);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), SysLedActivity.class);
                    intent.putExtra("nr", textInputEditText.getText().toString());
                    intent.putExtra("bjys", bjcolor);
                    intent.putExtra("wzys", wzcolor);
                    intent.putExtra("sd", seekbar2.getProgress());
                    intent.putExtra("dx", seekbar1.getProgress());
                    startActivity(intent);
                }
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(v.getContext(), R.style.Dialog_Alert_one)
                        .setTitle("背景颜色")
                        .initialColor(Color.parseColor(bjcolor))
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
                                bjcolor = "#" + Integer.toHexString(selectedColor);
                                card1.setCardBackgroundColor(selectedColor);
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

        card2.setOnClickListener(v -> ColorPickerDialogBuilder
                .with(v.getContext(), R.style.Dialog_Alert_one)
                .setTitle("文字颜色")
                .initialColor(Color.parseColor(wzcolor))
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
                        wzcolor = "#" + Integer.toHexString(selectedColor);
                        card2.setCardBackgroundColor(selectedColor);
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
                .show());
    }
}