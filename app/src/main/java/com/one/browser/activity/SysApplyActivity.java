package com.one.browser.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.one.browser.R;
import com.one.browser.adapter.AppSizeAdapter;
import com.one.browser.entity.Certificate;
import com.one.browser.entity.Resource;
import com.one.browser.http.HumanFaceHttpServlet;
import com.one.browser.http.PictureHttpServlet;
import com.one.browser.sqlite.CommonDao;
import com.one.browser.sqlite.ExaminationDao;
import com.one.browser.sqlite.StudentDao;
import com.one.browser.sqlite.VisaDao;
import com.one.browser.utils.AlphaUtil;
import com.one.browser.utils.BitmapUtils;
import com.one.browser.utils.ImageUtil;
import com.one.browser.utils.ScaleBitmap;
import com.one.browser.view.NoScrollViewPager;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * @author 18517
 */
public class SysApplyActivity extends AppCompatActivity {

    private final static String YES = "YES";
    private final String[] titles = new String[]{"背景", "尺寸"};
    private static final String TAG = SysApplyActivity.class.getSimpleName();
    private PhotoEditor mPhotoEditor;
    private ArrayList<View> pageView;
    public String image_url;
    /**
     * 保存
     */
    private Handler handler = null;
    /**
     * 输入图像面板
     */
    protected PhotoEditorView photoEditorView;
    /**
     * 变量及控件
     */
    private String recolor = "#438edb";
    private CardView chip1;
    private String nr;
    private Bitmap logo;
    int default_title = 0;
    /**
     * 计算Bitmap
     */
    private Bitmap calculate;
    /**
     * 保存按钮
     */
    private TextView save;
    /**
     * 颜色卡片1
     */
    private CardView cardView1;
    private CardView cardView2;
    private CardView cardView3;
    private CardView cardView4;
    private CardView cardView5;
    private CardView cardView6;
    private ImageView chip1_image;
    private ImageView card_image1;
    private ImageView card_image2;
    private ImageView card_image3;
    private ImageView card_image4;
    private ImageView card_image5;
    private ImageView card_image6;

    private LinearLayout views;
    private TabLayout tabLayout;
    private NoScrollViewPager viewPager;
    private DiscreteSeekBar discreteSeekBar;
    private LinearLayout control;
    private int quality = 100;
    private LinearLayout panel;
    /**
     * 图片尺寸名称
     */
    private String name;
    /**
     * 图片宽度
     */
    private int width;
    /**
     * 图片高度
     */
    private int height;
    /**
     * 选择下标
     */
    private int start;
    /**
     * alpha图
     */
    private Bitmap alpha;
    /**
     * 原图
     */
    private Bitmap rawBitmap;

    private ArrayList<Resource> resources;

    private ArrayList<Certificate> certificates;

    private TextView imageSize;

    private LinearLayout ll_image_size;

    public interface Call {
        /**
         * succeed 成功
         *
         * @param file 返回文件
         */
        void succeed(File file);

        /**
         * lose 失败
         */
        void lose();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.activity_app_bar1, null);
        View view4 = inflater.inflate(R.layout.activity_app_bar4, null);
        control = findViewById(R.id.control);
        //实现状态栏文字颜色为暗色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("图片编辑");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Bundle bundle = this.getIntent().getExtras();
        String url = bundle.getString("bitmap");
        name = bundle.getString("name");
        width = bundle.getInt("width", 295);
        height = bundle.getInt("height", 413);
        int coordinate = bundle.getInt("coordinate", 111);
        Log.i(TAG, "获取坐标: " + coordinate);
        if (url == null) {
            Log.i(TAG, "onCreate: >>>>>>> 为空");
        } else {
            Log.i(TAG, "onCreate: >>>>>>> 不为空 >>>" + url);
            Log.i(TAG, "onCreate: 宽度 >>>" + width);
            Log.i(TAG, "onCreate: 高度 >>>" + height);
            image_url = url;
        }
        // 主页选择
        selection(coordinate);
        // 控件赋值
        initialize();
        // tab内容
        pageView = new ArrayList<>();
        pageView.add(view1);
        pageView.add(view4);
        // 数据库
        CommonDao commonDao = new CommonDao(getApplicationContext());
        ExaminationDao examinationDao = new ExaminationDao(getApplicationContext());
        StudentDao studentDao = new StudentDao(getApplicationContext());
        VisaDao visaDao = new VisaDao(getApplicationContext());
        resources = new ArrayList<>();
        certificates = new ArrayList<>();


        if (commonDao.getAll() != null) {
            resources.addAll(commonDao.getAll());
        }

        if (studentDao.getAll() != null) {
            resources.addAll(studentDao.getAll());
        }

        if (examinationDao.getAll() != null) {
            resources.addAll(examinationDao.getAll());
        }

        if (visaDao.getAll() != null) {
            resources.addAll(visaDao.getAll());
        }

        // 进行判断
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getTitle().equals(name)) {
                Log.i(TAG, "获取到下标 " + i);
                start = i;
                break;
            }
        }

        PagerAdapter pagerAdapter = new PagerAdapter() {
            public int getItemPosition(@NonNull Object object) {
                return POSITION_UNCHANGED;
            }

            @Override
            public int getCount() {
                return pageView.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                // 将当前位置的View移除
                container.addView(pageView.get(position));
                switch (position) {
                    // 选择颜色
                    case 0: {
                        cardView1 = viewPager.findViewById(R.id.card1);
                        cardView2 = viewPager.findViewById(R.id.card2);
                        cardView3 = viewPager.findViewById(R.id.card3);
                        cardView4 = viewPager.findViewById(R.id.card4);
                        cardView5 = viewPager.findViewById(R.id.card5);
                        cardView6 = viewPager.findViewById(R.id.card6);
                        chip1_image = viewPager.findViewById(R.id.chip1_image);
                        card_image1 = viewPager.findViewById(R.id.card_image1);
                        card_image2 = viewPager.findViewById(R.id.card_image2);
                        card_image3 = viewPager.findViewById(R.id.card_image3);
                        card_image4 = viewPager.findViewById(R.id.card_image4);
                        card_image5 = viewPager.findViewById(R.id.card_image5);
                        card_image6 = viewPager.findViewById(R.id.card_image6);
                        chip1 = viewPager.findViewById(R.id.chip1);

                        cardView1.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.GONE);
                            card_image1.setVisibility(View.VISIBLE);
                            card_image2.setVisibility(View.GONE);
                            card_image3.setVisibility(View.GONE);
                            card_image4.setVisibility(View.GONE);
                            card_image5.setVisibility(View.GONE);
                            card_image6.setVisibility(View.GONE);
                            recolor = "#438edb";
                            Log.i(TAG, "输出颜色 >>>" + recolor);
                            compound(recolor);
                        });
                        cardView2.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.GONE);
                            card_image1.setVisibility(View.GONE);
                            card_image2.setVisibility(View.VISIBLE);
                            card_image3.setVisibility(View.GONE);
                            card_image4.setVisibility(View.GONE);
                            card_image5.setVisibility(View.GONE);
                            card_image6.setVisibility(View.GONE);
                            recolor = "#FFFFFF";
                            Log.i(TAG, "输出颜色 >>>" + recolor);
                            compound(recolor);
                        });
                        cardView3.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.GONE);
                            card_image1.setVisibility(View.GONE);
                            card_image2.setVisibility(View.GONE);
                            card_image3.setVisibility(View.VISIBLE);
                            card_image4.setVisibility(View.GONE);
                            card_image5.setVisibility(View.GONE);
                            card_image6.setVisibility(View.GONE);
                            recolor = "#FFF20000";
                            Log.i(TAG, "输出颜色 >>>" + recolor);
                            compound(recolor);
                        });
                        cardView4.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.GONE);
                            card_image1.setVisibility(View.GONE);
                            card_image2.setVisibility(View.GONE);
                            card_image3.setVisibility(View.GONE);
                            card_image4.setVisibility(View.VISIBLE);
                            card_image5.setVisibility(View.GONE);
                            card_image6.setVisibility(View.GONE);
                            recolor = "#FF70A1DB";
                            Log.i(TAG, "输出颜色 >>>" + recolor);
                            compound(recolor);
                        });
                        cardView5.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.GONE);
                            card_image1.setVisibility(View.GONE);
                            card_image2.setVisibility(View.GONE);
                            card_image3.setVisibility(View.GONE);
                            card_image4.setVisibility(View.GONE);
                            card_image5.setVisibility(View.VISIBLE);
                            card_image6.setVisibility(View.GONE);
                            recolor = "#FFAEAFAD";
                            Log.i(TAG, "输出颜色 >>>" + recolor);
                            compound(recolor);
                        });
                        cardView6.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.GONE);
                            card_image1.setVisibility(View.GONE);
                            card_image2.setVisibility(View.GONE);
                            card_image3.setVisibility(View.GONE);
                            card_image4.setVisibility(View.GONE);
                            card_image5.setVisibility(View.GONE);
                            card_image6.setVisibility(View.VISIBLE);
                            recolor = "#FFF24C4C";
                            Log.i(TAG, "输出颜色 >>>" + recolor);
                            compound(recolor);
                        });
                        chip1.setOnClickListener(view -> {
                            chip1_image.setVisibility(View.VISIBLE);
                            card_image1.setVisibility(View.GONE);
                            card_image2.setVisibility(View.GONE);
                            card_image3.setVisibility(View.GONE);
                            card_image4.setVisibility(View.GONE);
                            card_image5.setVisibility(View.GONE);
                            card_image6.setVisibility(View.GONE);
                            ColorPickerDialogBuilder
                                    .with(view.getContext(), R.style.Dialog_Alert_one)
                                    .setTitle("前景颜色")
                                    .initialColor(Color.parseColor(recolor))
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener(selectedColor -> {
                                    })
                                    .setPositiveButton("确定", (dialog, selectedColor, allColors) -> {
                                        recolor = "#" + Integer.toHexString(selectedColor);

                                        if (!TextUtils.isEmpty(nr) && logo != null) {
                                            if (alpha != null) {
                                                Log.i(TAG, "onCreate: 流不为空");
                                            } else {
                                                Log.i(TAG, "onCreate: 流为空");
                                            }
                                        } else {
                                            if (alpha != null) {
                                                Log.i(TAG, "onCreate: 流不为空");
                                            } else {
                                                Log.i(TAG, "onCreate: 流为空");
                                            }

                                            Log.i(TAG, "输出颜色 >>>" + recolor);
                                            chip1.setCardBackgroundColor(Color.parseColor(recolor));
                                            compound(recolor);
                                        }
                                    })
                                    .setNegativeButton("取消", (dialog, which) -> {
                                    })
                                    .showColorEdit(true)
                                    .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                                    .build()
                                    .show();
                        });


                        break;
                    }


                    // 质量修改
                    case 2: {
                        discreteSeekBar = viewPager.findViewById(R.id.seekbar1);
                        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                            @Override
                            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                            }

                            @Override
                            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                                Log.i(TAG, "获取滑动 >>>" + discreteSeekBar.getProgress());
                                quality = discreteSeekBar.getProgress();
                                ByteArrayOutputStream bas;
                                try {
                                    if (calculate != null || rawBitmap != null) {
                                        bas = new ByteArrayOutputStream();
                                        calculate.compress(Bitmap.CompressFormat.JPEG, quality, bas);
                                        Log.i(TAG, "文件大小 >>>" + bas.toByteArray().length);
                                        Log.i(TAG, "文件大小 >>>" + bas.size() / 1024);

                                        runOnUiThread(() -> BitmapUtils.getKb(getApplicationContext(), width, height, mPhotoEditor, quality, bitmapSize -> {
                                            Log.i(TAG, "图片大小: >>>> " + bitmapSize);
                                            imageSize.setText(bitmapSize + "KB");
                                        }));

                                        bas.close();
                                    } else {
                                        Toast.makeText(SysApplyActivity.this, "禁止操作", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
                    default:
                        break;
                }
                return pageView.get(position);
            }


            @Override
            public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
                // TODO Auto-generated method stub
                // 将当前位置的View移除
                container.removeView(pageView.get(position));
            }

        };
        // 绑定适配器
        viewPager.setAdapter(pagerAdapter);
        // 设置初始界面
        viewPager.setCurrentItem(default_title);
        Objects.requireNonNull(tabLayout.getTabAt(default_title)).select();
        // 禁用滑动
        viewPager.setScanScroll(false);
        // 切换页面
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        // 保存图片
        save.setOnClickListener(v -> {
            if (alpha != null || rawBitmap != null) {
                Toast.makeText(this, "图片保存成功", Toast.LENGTH_SHORT).show();
                // 保存图片
                mPhotoEditor.clearHelperBox();
                BitmapUtils.saveBitmap(SysApplyActivity.this, width, height, quality, mPhotoEditor);
            } else {
                // 警告
                Toast.makeText(this, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        });


        // 逻辑判断
        if (coordinate == 0 || coordinate == 111) {
            Log.i(TAG, "onCreate: 可以执行 >>> ");
            // 网络请求
            webServer();
        } else {
            LocalityServer();
        }


    }

    // 不是替换背景都走这里
    private void LocalityServer() {
        try {
            File file = new File(image_url);
            Log.i(TAG, "图片地址 >>>" + image_url);
            // 如果文件存在
            if (file.exists() && file.isFile()) {
                // 压缩图片
                initCompressorIO(image_url, new Call() {
                    @Override
                    public void succeed(File reduceImage) {
                        Log.i(TAG, "原图片大小 >>>>> " + file.length());
                        Log.i(TAG, "图片压缩之后大小 >>>>> " + reduceImage.length());
                        if (reduceImage.length() > 1000000 && reduceImage.length() == 0) {
                            Toast.makeText(SysApplyActivity.this, "图片无效", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            rawBitmap = BitmapFactory.decodeFile(reduceImage.getAbsolutePath());
                        }
                        if (rawBitmap != null) {
                            Log.i(TAG, "rawBitmap 不为null: ");
                        } else {
                            Log.i(TAG, "rawBitmap 为null ");
                            Toast.makeText(SysApplyActivity.this, "该图片无效", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Log.i(TAG, "图片的宽度 >>>>" + width);
                        Log.i(TAG, "图片的高度 >>>" + height);
                        rawBitmap = ScaleBitmap.centerCrop(rawBitmap, width, height);
                        Log.i(TAG, "图片转换之后的宽度 : " + rawBitmap.getWidth());
                        Log.i(TAG, "图片转换之后的高度 : " + rawBitmap.getHeight());

                        // 创建二进制对象
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        rawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        // 将BASE64转为数据流
                        String originalBase64 = ImageUtil.getByte(byteArray);
                        // 网络请求检测人脸
                        Toast.makeText(SysApplyActivity.this, "检测人脸中", Toast.LENGTH_SHORT).show();
                        HumanFaceHttpServlet.getBase64(originalBase64, result -> {
                            Log.i(TAG, "succeed: >>>>>>> " + result);
                            if (result == null) {
                                Log.i(TAG, "handleMessage: 网络请求失败");
                                Toast.makeText(SysApplyActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                            } else {
                                if (result.equals(YES)) {
                                    runOnUiThread(() -> {
                                        Log.i(TAG, "succeed: >>>>>>> " + result);
                                        // photoEditorView
                                        photoEditorView.getSource().setAdjustViewBounds(true);
                                        // 显示最终剪切图片
                                        photoEditorView.getSource().setImageBitmap(rawBitmap);
                                        // 图片质量
                                        calculate = rawBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                        // 显示保存
                                        save.setVisibility(View.VISIBLE);
                                        // 显示控制台
                                        panel.setVisibility(View.VISIBLE);
                                        // 显示尺寸
                                        runOnUiThread(() -> BitmapUtils.getKb(getApplicationContext(), width, height, mPhotoEditor, quality, bitmapSize -> {
                                            Log.i(TAG, "图片大小: >>>> " + bitmapSize);
                                            ll_image_size.setVisibility(View.VISIBLE);
                                            imageSize.setText(bitmapSize + "KB");
                                        }));
                                    });

                                } else {
                                    Toast.makeText(SysApplyActivity.this, "未检测到人脸", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void lose() {
                        Toast.makeText(SysApplyActivity.this, "图片修改失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void webServer() {
        try {
            File file = new File(image_url);
            Log.i(TAG, "图片地址 >>>" + image_url);
            // 如果文件存在
            if (file.exists() && file.isFile()) {
                handler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                        String data = (String) msg.obj;
                        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                        alpha = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        // 背景大小
                        compound(recolor);
                        // 软件大小显示
                        runOnUiThread(() -> BitmapUtils.getKb(getApplicationContext(), width, height, mPhotoEditor, quality, bitmapSize -> {
                            Log.i(TAG, "图片大小: >>>> " + bitmapSize);
                            ll_image_size.setVisibility(View.VISIBLE);
                            imageSize.setText(bitmapSize + "KB");
                        }));
                    }
                };
                // 压缩图片
                initCompressorIO(image_url, new Call() {
                    @Override
                    public void succeed(File reduceImage) {
                        Log.i(TAG, "原图片大小 >>>>> " + file.length());
                        Log.i(TAG, "图片压缩之后大小 >>>>> " + reduceImage.length());
                        if (reduceImage.length() > 1000000 && reduceImage.length() == 0) {
                            Toast.makeText(SysApplyActivity.this, "图片无效", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            rawBitmap = BitmapFactory.decodeFile(reduceImage.getAbsolutePath());
                        }
                        if (rawBitmap != null) {
                            Log.i(TAG, "rawBitmap 不为null: ");
                        } else {
                            Log.i(TAG, "rawBitmap 为null ");
                            Toast.makeText(SysApplyActivity.this, "该图片无效", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 加载等待页面
                        Toast.makeText(SysApplyActivity.this, "背景替换中", Toast.LENGTH_SHORT).show();
                        // 创建视图
                        rawBitmap = ScaleBitmap.centerCrop(rawBitmap, width, height);
                        // 创建二进制对象
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        rawBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        // 将BASE64转为数据流
                        String originalBase64 = ImageUtil.getByte(byteArray);
                        // 网络请求检测人脸
                        HumanFaceHttpServlet.getBase64(originalBase64, result -> {
                            if (result == null) {
                                Log.i(TAG, "handleMessage: 网络请求失败");
                                Toast.makeText(SysApplyActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                            } else {
                                if (result.equals(YES)) {
                                    runOnUiThread(() -> {
                                        Log.i(TAG, "succeed: >>>>>>> " + result);
                                        // photoEditorView
                                        photoEditorView.getSource().setAdjustViewBounds(true);
                                        // 显示最终剪切图片
                                        photoEditorView.getSource().setImageBitmap(rawBitmap);
                                        // 图片质量
                                        calculate = rawBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                        // 显示保存
                                        save.setVisibility(View.VISIBLE);
                                        // 显示控制台
                                        panel.setVisibility(View.VISIBLE);
                                        // 显示尺寸
                                        runOnUiThread(() -> BitmapUtils.getKb(getApplicationContext(), width, height, mPhotoEditor, quality, bitmapSize -> {
                                            Log.i(TAG, "图片大小: >>>> " + bitmapSize);
                                            ll_image_size.setVisibility(View.VISIBLE);
                                            imageSize.setText(bitmapSize + "KB");
                                        }));
                                    });

                                } else {
                                    Toast.makeText(SysApplyActivity.this, "未检测到人脸", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void lose() {
                        Toast.makeText(SysApplyActivity.this, "图片修改失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCompressorIO(String photos, Call call) {
        try {
            Luban.with(this)
                    .load(photos)
                    .ignoreBy(1024)
                    .filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")))
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            call.succeed(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                            call.lose();
                        }
                    }).launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        // 属性赋值
        ll_image_size = findViewById(R.id.ll_image_size);
        imageSize = findViewById(R.id.ImageSize);
        photoEditorView = findViewById(R.id.iv_input_image);
        tabLayout = findViewById(R.id.tab_picture);
        views = findViewById(R.id.views_picture);
        viewPager = findViewById(R.id.viewpager_picture);
        save = findViewById(R.id.save);
        panel = findViewById(R.id.panel);
        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView).setPinchTextScalable(true).build();
        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        for (int i = 0; i < titles.length; i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setText(titles[i]);
        }
    }

    private void selection(int coordinate) {

        if (coordinate != 111) {
            if (coordinate == 0) {
                // 进行隐藏控件
                control.setVisibility(View.GONE);
                default_title = 0;
            } else if (coordinate == 1) {
                control.setVisibility(View.GONE);
                default_title = 1;
            } else if (coordinate == 2) {
                control.setVisibility(View.GONE);
                default_title = 2;
            } else if (coordinate == 3) {
                control.setVisibility(View.GONE);
                default_title = 3;
            }
        }

    }







    /**
     * @param color 背景颜色
     */
    private void compound(String color) {
        if (alpha != null) {
            // 显示保存
            save.setVisibility(View.VISIBLE);
            // 显示控制台
            panel.setVisibility(View.VISIBLE);
            // 总背景信息
            Bitmap background;
            // 图片宽高
            background = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888);
            // 图片背景
            background.eraseColor(Color.parseColor(color));
            // 此处为合成图片 原图与替换图片合成大小必须一致
            Bitmap bgImg = Bitmap.createScaledBitmap(background, rawBitmap.getWidth(), rawBitmap.getHeight(), true);
            // 最终生成图片
            Bitmap image = AlphaUtil.alphaBitmap(rawBitmap, bgImg, alpha);
            // photoEditorView
            photoEditorView.getSource().setAdjustViewBounds(true);
            // 显示最终剪切图片
            photoEditorView.getSource().setImageBitmap(image);
            // 质量大小修改
            calculate = image.copy(Bitmap.Config.ARGB_8888, true);
            Log.i(TAG, "计算 >>>: " + calculate.getByteCount());

        } else {
            // 隐藏保存
            save.setVisibility(View.GONE);
            // 隐藏控制台
            panel.setVisibility(View.GONE);
            Toast.makeText(this, "禁止操作", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing() && rawBitmap != null) {
            // 释放最终渲染
            if (rawBitmap != null && !rawBitmap.isRecycled()) {
                rawBitmap.recycle();
            }
        }
        if (isFinishing() && alpha != null) {
            // 释放位图
            if (alpha != null) {
                alpha.isRecycled();
            }
        }
        if (isFinishing() && alpha != null) {
            // 释放最终大小质量
            if (calculate != null) {
                calculate.isRecycled();
            }
        }
    }
}
