package com.one.browser.activity;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.one.browser.R;
import com.one.browser.adapter.ResourceAdapter;
import com.one.browser.adapter.SearchAdapter;
import com.one.browser.dialog.CertificateDialog;
import com.one.browser.entity.Resource;
import com.one.browser.sqlite.CommonDao;
import com.one.browser.sqlite.CustomDao;
import com.one.browser.sqlite.ExaminationDao;
import com.one.browser.sqlite.StudentDao;
import com.one.browser.sqlite.VisaDao;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SysListActivity extends AppCompatActivity {
    private final Intent image = new Intent(Intent.ACTION_PICK);
    private String[] titles = new String[]{"常用尺寸", "学生证件", "考试证件", "签证证件", "自定义"};
    private ArrayList<View> preview;
    /**
     * 图片的名称
     */
    private String name;
    /**
     * 图片宽度设定
     */
    private int width = 295;
    /**
     * 图片高度设定
     */
    private int height = 413;
    /**
     * 冲洗宽度设定
     */
    private int wash_x = 25;
    /**
     * 冲洗高度设定
     */
    private int wash_y = 35;
    // 常用证件
    ArrayList<Resource> common;
    // 学生证件
    ArrayList<Resource> student;
    // 考试证件
    ArrayList<Resource> examination;
    // 签证证件
    ArrayList<Resource> visa;
    // 自定义证件
    ArrayList<Resource> custom;

    int default_title = 0;

    EditText editText;

    ListView listView;

    SearchAdapter adapter = null;

    LinearLayout search;

    LinearLayout tab_id;

    LinearLayoutCompat views;

    FloatingActionButton floatingActionButton;


    private ArrayList<Resource> list = new ArrayList<Resource>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        editText = findViewById(R.id.et_search);
        listView = findViewById(R.id.listView);
        search = findViewById(R.id.search);
        tab_id = findViewById(R.id.tab_id);
        views = findViewById(R.id.views);

        setListeners();

        Intent intents = getIntent();
        String title = intents.getStringExtra("title");

        switch (title) {
            case "首页搜索": {
                // 进行搜索
                // 隐藏
                tab_id.setVisibility(View.GONE);
                // 隐藏
                views.setVisibility(View.GONE);
                // 显示
                listView.setVisibility(View.VISIBLE);
                break;
            }
            case "常用证件": {
                default_title = 0;
                break;
            }
            case "学生证件": {
                default_title = 1;
                break;
            }
            case "考试证件": {
                default_title = 2;
                break;
            }
            case "签证证件": {
                default_title = 3;
                break;
            }
        }



        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("尺寸大全");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.activity_bar1, null);
        View view2 = inflater.inflate(R.layout.activity_bar2, null);
        View view3 = inflater.inflate(R.layout.activity_bar3, null);
        View view4 = inflater.inflate(R.layout.activity_bar4, null);
        View view5 = inflater.inflate(R.layout.activity_bar5, null);

        preview = new ArrayList<>();
        preview.add(view1);
        preview.add(view2);
        preview.add(view3);
        preview.add(view4);
        preview.add(view5);

        // 常用尺寸
        common = new ArrayList<>();
        // 学生证件
        student = new ArrayList<>();
        // 考试证件
        examination = new ArrayList<>();
        // 签证证件
        visa = new ArrayList<>();
        // 自定义证件
        custom = new ArrayList<>();


        CommonDao commonDao = new CommonDao(getApplicationContext());
        ExaminationDao examinationDao = new ExaminationDao(getApplicationContext());
        StudentDao studentDao = new StudentDao(getApplicationContext());
        VisaDao visaDao = new VisaDao(getApplicationContext());
        CustomDao customDao = new CustomDao(getApplicationContext());


        if (commonDao.getAll() != null) {
            common = (ArrayList<Resource>) commonDao.getAll();
        }

        if (studentDao.getAll() != null) {
            student = (ArrayList<Resource>) studentDao.getAll();
        }

        if (examinationDao.getAll() != null) {
            examination = (ArrayList<Resource>) examinationDao.getAll();
        }

        if (visaDao.getAll() != null) {
            visa = (ArrayList<Resource>) visaDao.getAll();
        }

        if (customDao.getAll() != null) {
            custom = (ArrayList<Resource>) customDao.getAll();
        }


        PagerAdapter pagerAdapter = new PagerAdapter() {

            public int getItemPosition(Object object) {
                return POSITION_UNCHANGED;
            }


            @Override
            public int getCount() {
                return preview.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }


            @NonNull
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(preview.get(position));
                //每次滑动的时候把视图添加到viewpager
                switch (position) {
                    // 常用证件
                    case 0: {
                        RecyclerView recyclerView = findViewById(R.id.resource1);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        System.out.println("常用大小 >>> " + common.size());
                        ResourceAdapter adapter = new ResourceAdapter(common);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(positions -> {
                            // 把图片的高度和宽度赋值
                            name = common.get(positions).getTitle();
                            width = common.get(positions).getW();
                            height = common.get(positions).getH();
                            wash_x = common.get(positions).getWash_w();
                            wash_y = common.get(positions).getWash_y();
                            Log.i("TAG", "常用证件 >>>" + wash_y);
                            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivity();
                        });
                        break;
                    }
                    // 学生证件
                    case 1: {
                        RecyclerView recyclerView = findViewById(R.id.resource2);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        System.out.println("学生大小 >>> " + student.size());
                        ResourceAdapter student_adapter = new ResourceAdapter(student);
                        recyclerView.setAdapter(student_adapter);
                        student_adapter.setOnItemClickListener(positions -> {
                            // 把图片的高度和宽度赋值
                            name = student.get(positions).getTitle();
                            width = student.get(positions).getW();
                            height = student.get(positions).getH();
                            wash_x = student.get(positions).getWash_w();
                            wash_y = student.get(positions).getWash_y();
                            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            //startActivity();
                        });

                        break;
                    }
                    // 考试证件
                    case 2: {
                        RecyclerView recyclerView = findViewById(R.id.resource3);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        System.out.println("大小 >>> " + examination.size());
                        ResourceAdapter adapter = new ResourceAdapter(examination);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(positions -> {
                            // 把图片的高度和宽度赋值
                            name = examination.get(positions).getTitle();
                            width = examination.get(positions).getW();
                            height = examination.get(positions).getH();
                            wash_x = examination.get(positions).getWash_w();
                            wash_y = examination.get(positions).getWash_y();
                            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            //startActivity();
                        });
                        break;
                    }
                    // 签证证件
                    case 3: {
                        RecyclerView recyclerView = findViewById(R.id.resource4);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        System.out.println("学生大小 >>> " + visa.size());
                        ResourceAdapter adapter = new ResourceAdapter(visa);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(positions -> {
                            // 把图片的高度和宽度赋值
                            name = visa.get(positions).getTitle();
                            width = visa.get(positions).getW();
                            height = visa.get(positions).getH();
                            wash_x = visa.get(positions).getWash_w();
                            wash_y = visa.get(positions).getWash_y();
                            Log.i("TAG", "数据库查询: " + wash_y);
                            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            //startActivity();
                        });
                        break;
                    }
                    // 自定义
                    case 4: {
                        RecyclerView recyclerView = findViewById(R.id.resource5);
                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        System.out.println("自定义 >>> " + custom.size());
                        ResourceAdapter adapter = new ResourceAdapter(custom);

                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(positions -> {
                            // 把图片的高度和宽度赋值
                            name = custom.get(positions).getTitle();
                            width = custom.get(positions).getW();
                            height = custom.get(positions).getH();
                            wash_x = custom.get(positions).getWash_w();
                            wash_y = custom.get(positions).getWash_y();
                            Log.i("TAG", "数据库查询: " + wash_y);
                            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            //startActivity();
                        });


                        floatingActionButton = findViewById(R.id.add_certificate);
                        floatingActionButton.setOnClickListener(view -> {
                            Log.i("TAG", "进行自定义");
                            CertificateDialog.Builder builder = new CertificateDialog.Builder(SysListActivity.this, (certificate_title, certificate_width, certificate_height) -> {
                                Log.i("TAG", "自定义标题: " + certificate_title);
                                Log.i("TAG", "自定义宽度: " + certificate_width);
                                Log.i("TAG", "自定义高度度: " + certificate_height);
                                // 进行数据插入
                                customDao.inserData(new Resource(certificate_title + "（自定义）", Integer.parseInt(certificate_width), Integer.parseInt(certificate_height), 0, 0));
                                custom.clear();
                                custom.addAll(customDao.getAll());
                            });
                            View.OnClickListener onCancelClickListener = v -> Log.i("TAG", "onClick: >>> 取消");
                            View.OnClickListener onConfirmClickListener = v -> Log.i("TAG", "onClick: >>> 取消");
                            builder.setButtonCancel(onCancelClickListener);
                            builder.setButtonConfirm(onConfirmClickListener);
                            CertificateDialog certificateDialog = builder.create();
                            certificateDialog.show();
                        });

                        break;
                    }
                    default:
                }
                return preview.get(position);
            }


            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // TODO Auto-generated method stub
                // 将当前位置的View移除
                container.removeView(preview.get(position));
            }
        };

        list.addAll(common);
        list.addAll(student);
        list.addAll(examination);
        list.addAll(visa);
        list.addAll(custom);

        List<Resource> resourceList = list.stream().distinct().collect(Collectors.toList());
        for (Resource r : resourceList) {
            Log.i("TAG", "去重之后的改变 >>> : " + r.getTitle());
        }


        //循环判断

        adapter = new SearchAdapter(this, list, this::setItemClick);
        listView.setAdapter(adapter);

        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        for (int i = 0; i < titles.length; i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setText(titles[i]);
        }

        // 绑定适配器
        viewPager.setAdapter(pagerAdapter);
        // 设置初始界面
        viewPager.setCurrentItem(default_title, false);
        Objects.requireNonNull(tabLayout.getTabAt(default_title)).select();
        // 打印
        Log.i("TAG", "打印当前位置 >>>" + tabLayout.getSelectedTabPosition());
        // 切换页面
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }


    private void startActivity() {
        Intent intent = new Intent(getApplicationContext(), SysSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("width", width);
        bundle.putInt("height", height);
        bundle.putInt("WashX", wash_x);
        bundle.putInt("WashY", wash_y);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private void setListeners() {
        setItemClick(list);
        editText.setOnClickListener(view -> {
            System.out.println("隐藏>>>");
            // 隐藏
            tab_id.setVisibility(View.GONE);
            // 隐藏
            views.setVisibility(View.GONE);
            // 显示
            listView.setVisibility(View.VISIBLE);
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    private void setItemClick(List<Resource> list) {
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            width = list.get(i).getW();
            height = list.get(i).getH();
            wash_x = list.get(i).getWash_w();
            wash_y = list.get(i).getWash_y();
            Log.i("TAG", "数据库查询: " + wash_y);
            image.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            //startActivity();
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // 判断是否为显示
            if (listView.getVisibility() == View.VISIBLE) {
                System.out.println("显示");
                // 显示
                tab_id.setVisibility(View.VISIBLE);
                // 显示
                views.setVisibility(View.VISIBLE);
                // 隐藏
                listView.setVisibility(View.GONE);
                return false;
            }
            System.out.println("不显示");
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

}