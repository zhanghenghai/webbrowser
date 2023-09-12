package com.one.browser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;


import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.google.android.material.internal.NavigationMenuView;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.one.browser.activity.SysBase64Activity;
import com.one.browser.activity.SysBaseActivity;
import com.one.browser.activity.SysChineseActivity;
import com.one.browser.activity.SysClockActivity;
import com.one.browser.activity.SysEwmActivity;
import com.one.browser.activity.SysFanActivity;
import com.one.browser.activity.SysIdiomActivity;
import com.one.browser.activity.SysSelectActivity;
import com.one.browser.activity.SysTPAsActivity;
import com.one.browser.adapter.DialogPageAdapter;
import com.one.browser.config.AppConfig;
import com.one.browser.databinding.ActivityMainBinding;
import com.one.browser.dialog.MultiDialog;
import com.one.browser.dialog.OfficialDialog;
import com.one.browser.entity.DialogWindow;
import com.one.browser.entity.Notice;
import com.one.browser.entity.WebMessage;

import com.one.browser.more.BookmarkActivity;
import com.one.browser.more.DownloadActivity;
import com.one.browser.more.HistoryActivity;
import com.one.browser.more.ScriptActivity;
import com.one.browser.more.SettingActivity;
import com.one.browser.service.FileDownloaded;
import com.one.browser.sqlite.Bookmark;
import com.one.browser.sqlite.BookmarkDao;
import com.one.browser.sqlite.DownloadDao;
import com.one.browser.sqlite.History;
import com.one.browser.sqlite.HistoryDao;
import com.one.browser.sqlite.Script;
import com.one.browser.sqlite.ScriptDao;
import com.one.browser.sqlite.Website;
import com.one.browser.sqlite.WebsiteDao;
import com.one.browser.utils.BitMapUtil;
import com.one.browser.utils.KeyBoardUtil;
import com.one.browser.utils.SHA256Util;
import com.one.browser.utils.WebMessageUtil;
import com.one.browser.web.NewWeb;


import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author 18517
 */
public class MainActivity extends SysBaseActivity {
    /**
     * 状态
     */
    private static int state = 0;
    /**
     * 通知渠道的唯一标识符，由应用程序指定。
     */
    private static final String MESSAGES_CHANNEL = "messages";

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    /**
     * 地址栏
     */
    private String topContent;
    /**
     * 书签
     */
    public final static int BOOKMARK = 2;
    /**
     * 历史
     */
    public final static int HISTORY = 3;
    /**
     * 主页
     */
    private final String HOME_URL = "file:///android_asset/Home/index.html";
    /**
     * 退出
     */
    private boolean isExit;
    /**
     * 日志
     */
    private final String TAG = "TAG";
    /**
     * 控件绑定
     */
    private ActivityMainBinding binding;
    /**
     * 存储选择页面
     */
    private ArrayList<DialogWindow> dialogWindowsPages;
    /**
     * WebView页面
     */
    private List<NewWeb> webList;
    /**
     * 弹窗
     */
    private MultiDialog multiDialog;
    /**
     * web标签
     */
    private int pageId;
    /**
     * 当前选择页面
     */
    private int currentPage;
    /**
     * 搜索结果截图
     */
    private final String HOMESTRETCH = "Results";
    /**
     * 搜索列表
     */
    private ArrayList<String> arrayList;
    /**
     * 搜索列表适配器
     */
    private ArrayAdapter<String> adapter;
    /**
     * 是否需要粘贴
     */
    private final boolean paste = false;
    /**
     * 是否允许打开应用
     */
    private boolean open_app;
    /**
     * 是否正在加载
     */
    private boolean isLoading;
    /**
     * 更多内容
     */
    private MoreDialog moreDialog;
    /**
     * 书签
     */
    private BookmarkDao bookmarkDao;
    /**
     * 右上角更多
     */
    private PopupMenu mPopupMenu;
    /**
     * 当前页面
     */
    private String PRESENT_URL;
    /**
     * 历史记录
     */
    private HistoryDao historyDao;
    /**
     * 正在下载实时数据传输Message标志
     */
    private static final int PROCESSING = 1;
    /**
     * 下载失败时的Message标志
     */
    private static final int FAILURE = -1;
    /**
     * 线程下载UI
     */
    private Handler handler = new UIHander();
    /**
     * 下载最大值
     */
    private float scheduleMax = 0;
    /**
     * 通知栏按钮点击
     */
    private static Handler BarHandler;

    /**
     * 下载文件
     */
    private String filePath;
    /**
     * 线程存储
     */
    private Map<Integer, DownloadTask> map = new LinkedHashMap<>();
    /**
     * 下载文件
     */
    private DownloadDao downloadDao;

    /**
     * 夜间模式
     */
    private SharedPreferences prefs;

    /**
     * 异步线程
     */
    private ExecutorService mExecutor;

    /**
     * 读取配置文件
     */
    private SharedPreferences config;
    /**
     * 搜索引擎
     */
    private String search;
    /**
     * 开启全屏
     */
    private View mCustomView;
    /**
     * 全屏相关
     */
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    /**
     * 搜索引擎
     */
    private WebsiteDao websiteDao;

    /**
     * 书签图标
     */
    private String iconBookPath = null;
    /**
     * 菜单
     */
    private PopupWindow popupWindow;

    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //设置padding
        binding.homeOne.setPadding(0, getStatusBarHeight(this), 0, 0);
        webList = new ArrayList<>();
        // 添加页面
        addNewPage(HOME_URL);
        // 创建对象
        setListeners();
        // 页面刷新
        pageRefreshClick();
        // 页面分页事件
        popUpClick();
        // 地址栏事件
        locationClick();
        // 侧边栏事件
        sideBarClick();
        // 侧栏
        drawerClick();
        // 侧栏内容显示
        navigation();
        // 初始化搜索列表
        listSearch();
        // 导航栏事件
        navigationOnClick();
        // 更多内容
        moreContext();
        // 变量初始化
        variable();
        // 更多选项
        moreClick();
        // 检查存储权限
        authority();
        // 通知栏点击异步处理
        bar();
        // 对象初始化
        ObjectInit();
        // 配置文件
        AppConfig();
    }

    private void AppConfig() {
        // 获取顶部显示
        getTopContent();
        // 获取搜索引擎
        getEngine();

    }

    private String getTopContent() {
        config = getSharedPreferences("Config", MODE_PRIVATE);
        topContent = config.getString("TopContent", "title");
        Log.i(TAG, "顶部配置文件 >>> " + topContent);
        return topContent;
    }

    private String getEngine() {
        config = getSharedPreferences("Config", MODE_PRIVATE);
        search = config.getString("Engine", "https://m.baidu.com/s?word=");
        Log.i(TAG, "搜索引擎配置文件 >>> " + search);
        return search;
    }

    private void navigation() {
        binding.navigationView2.setItemIconTintList(null);
        // 去掉navigationView的滚动条
        NavigationMenuView navigationMenuView = (NavigationMenuView) binding.navigationView2.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        binding.navigationView2.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                // 成语
                case R.id.sys_idiom:{
                    Intent intent = new Intent(MainActivity.this, SysIdiomActivity.class);
                    startActivity(intent);
                    break;
                }
                // 证件
                case R.id.sys_portrait: {
                    Intent intent = new Intent(MainActivity.this, SysSelectActivity.class);
                    intent.putExtra("title", "常用证件");
                    startActivity(intent);
                    break;
                }
                // 翻译
                case R.id.sys_translate: {
                    Intent intent = new Intent(MainActivity.this, SysFanActivity.class);
                    startActivity(intent);
                    break;
                }
                // 时间屏幕
                case R.id.sys_timeScreen: {
                    Intent intent = new Intent(MainActivity.this, SysClockActivity.class);
                    startActivity(intent);
                    break;
                }
                // 图片取色
                case R.id.sys_colorPicker: {
                    Intent intent = new Intent(MainActivity.this, SysTPAsActivity.class);
                    startActivity(intent);
                }
                // Base64
                case R.id.sys_baseImage: {
                    Intent intent = new Intent(MainActivity.this, SysBase64Activity.class);
                    startActivity(intent);
                }
                // 中文词典
                case R.id.sys_chinese: {
                    Intent intent = new Intent(MainActivity.this, SysChineseActivity.class);
                    startActivity(intent);
                }
                // 二维码生成
                case R.id.sys_qr:{
                    Intent intent = new Intent(MainActivity.this, SysEwmActivity.class);
                    startActivity(intent);
                }
            }
            return true;
        });

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (state == 1) {
            Log.i(TAG, "不做任何处理");
        } else {
            if ((newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                Log.i("TAG", "当前为夜间模式");
                // 赋值为真
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("night_mode", true);
                editor.apply();
                white();
            } else {
                Log.i(TAG, "当前为日间模式");
                // 赋值为假
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("night_mode", false);
                editor.apply();
                black();
            }
        }
    }


    private void black() {
        // 搜索栏
        binding.homeOne.setBackgroundColor(Color.WHITE);
        // 侧栏
        binding.homePaging.setColorFilter(Color.BLACK);
        // 输入栏
        binding.homeOneEdit.setBackgroundColor(Color.WHITE);
        // 输入栏文字颜色
        binding.homeOneEdit.setTextColor(Color.BLACK);
        // 输入栏提示文字颜色
        binding.homeOneEdit.setHintTextColor(Color.BLACK);
        // 刷新按钮
        binding.homeRefresh.setColorFilter(Color.BLACK);
        // 更多按钮
        binding.homeRightMore.setColorFilter(Color.BLACK);
        // 底栏
        binding.homeThere.setBackgroundColor(Color.WHITE);
        // 前进
        binding.homeUpPage.setColorFilter(Color.BLACK);
        // 后退
        binding.homeNextPage.setColorFilter(Color.BLACK);
        // 页数
        binding.homeThereSelect.setTextColor(Color.BLACK);
        // 主页
        binding.homeIndex.setColorFilter(Color.BLACK);
        // 更多
        binding.homeMore.setColorFilter(Color.BLACK);
        // 导航栏颜色
        getWindow().setNavigationBarColor(Color.WHITE);
        // 状态栏文字颜色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void white() {
        // 搜索栏
        binding.homeOne.setBackgroundColor(Color.BLACK);
        // 侧栏
        binding.homePaging.setColorFilter(Color.WHITE);
        // 输入栏
        binding.homeOneEdit.setBackgroundColor(Color.BLACK);
        // 输入栏文字颜色
        binding.homeOneEdit.setTextColor(Color.WHITE);
        // 输入栏提示文字颜色
        binding.homeOneEdit.setHintTextColor(Color.WHITE);
        // 刷新按钮
        binding.homeRefresh.setColorFilter(Color.WHITE);
        // 更多按钮
        binding.homeRightMore.setColorFilter(Color.WHITE);
        // 底栏
        binding.homeThere.setBackgroundColor(Color.BLACK);
        // 前进
        binding.homeUpPage.setColorFilter(Color.WHITE);
        // 后退
        binding.homeNextPage.setColorFilter(Color.WHITE);
        // 页数
        binding.homeThereSelect.setTextColor(Color.WHITE);
        // 主页
        binding.homeIndex.setColorFilter(Color.WHITE);
        // 更多
        binding.homeMore.setColorFilter(Color.WHITE);
        // 导航栏颜色
        getWindow().setNavigationBarColor(Color.BLACK);
        // 状态栏文字颜色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * 通知栏权限申请
     */
    private void createNotificationChannel() {
        CharSequence name = getString(R.string.app_name);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(MESSAGES_CHANNEL, name, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * 对象初始化
     */
    private void ObjectInit() {
        // 文件下载
        downloadDao = new DownloadDao(MainActivity.this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // 创建线程池，包含 4 个核心线程和 8 个最大线程
        mExecutor = Executors.newFixedThreadPool(2);
    }

    private void authority() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }


    public void notifyNow(Notice model) {
        //管理和控制通知的类
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Log.i(TAG, "通知id >>>: " + model.getNotifyId());
        notificationManager.notify(model.getNotifyId(), scheduleInform(model).build());
    }

    private NotificationManager getManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 通知栏点击异步处理
     */
    private void bar() {
        BarHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                int id = bundle.getInt("id");
                switch (msg.what) {
                    case 0:
                        Log.i(TAG, "关闭下载");
                        // 关闭通知
                        Log.i(TAG, "获取ID >>>: " + id);
                        // 关闭通知栏
                        NotificationManager manager_one = getManager();
                        manager_one.cancel(id);
                        if (map.get(id) != null) {
                            exit(map.get(id));
                        }
                        break;
                    case 1:
                        Log.i(TAG, "点击安装");
                        String fileName = bundle.getString("fileName");
                        String mime = bundle.getString("mime");
                        Log.i(TAG, "获取ID >>>: " + id);
                        Log.i(TAG, "安装路径: " + fileName);
                        Log.i(TAG, "获取类型: " + mime);
                        install(fileName, mime);
                        NotificationManager manager_two = getManager();
                        manager_two.cancel(id);
                        break;
                }
            }
        };
    }


    private void install(String fileName, String mime) {
        Log.i(TAG, "文件类型 >>> " + mime);
        Log.i(TAG, "文件名称 >>> " + fileName);
        File file = new File(AppConfig.PACKAGE, fileName);
        Log.i(TAG, "安装全路径: >>> " + file);
        Uri fileUri;
        fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.one.browser", file);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(fileUri, mime);
        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(installIntent);
    }


    /**
     * 进度条
     */
    private NotificationCompat.Builder scheduleInform(Notice notice) {
        // 构造
        createNotificationChannel();
        // 创建通知的类
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MESSAGES_CHANNEL).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle(notice.getTitle()).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // 点击
                .addAction(R.drawable.home_index, notice.getButton(), PendingIntent.getBroadcast(this, notice.getNotifyId(), notice.getIntent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                // 进度条
                .setProgress(notice.getTotalProgress(), notice.getCurrentProgress(), false)
                // 设置为自动取消
                .setAutoCancel(true);
        return builder;
    }


    /**
     * 退出下载
     */
    public void exit(DownloadTask downloadTask) {
        Log.i(TAG, "exit: 判断是否为null");
        Log.i(TAG, "exit: 不为空");
        downloadTask.exit();
    }

    /**
     * 运行在主线程
     *
     * @param path 下载地址
     */
    private void download(String path, String saveDir, String fileName, String mime, int fileSize) {
        Log.i(TAG, "path >>>> " + path);
        Log.i(TAG, "saveDir >>>> " + saveDir);
        Log.i(TAG, "fileName >>>> " + fileName);
        Log.i(TAG, "mime >>>> " + mime);
        Log.i(TAG, "fileSize >>>> " + fileSize);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.init(path, saveDir, new FileDownloaded(getApplicationContext(), path), downloadTask, fileName, mime, fileSize);
        Thread thread = new Thread(downloadTask);
        thread.start();
    }

    /**
     * 获取权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 用户已授予存储权限，可以访问存储空间
                    Toast.makeText(this, "用户已授予权限", Toast.LENGTH_SHORT).show();
                } else {
                    // 用户拒绝了存储权限，不能访问存储空间
                    Toast.makeText(this, "用户拒绝授予权限", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    /**
     * 子页面返回值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "code >>> " + resultCode);
        //==========书签相关=============
        if (requestCode == BOOKMARK && resultCode == BOOKMARK) {
            addNewPage(seniorUrl(data.getStringExtra("Bookmark")));
        }
        //==========书签相关=============

        //==========历史记录相关==========
        if (requestCode == HISTORY && resultCode == HISTORY) {
            addNewPage(seniorUrl(data.getStringExtra("History")));
        }
        //==========历史记录相关==========
    }


    @Override
    public void onBackPressed() {
        if (webList.get(currentPage).canGoBack()) {
            // 如果WebView可以进行后退操作，则执行后退操作
            webList.get(currentPage).goBack();
        } else {
            // 否则，执行默认的返回行为（例如退出Activity）
            super.onBackPressed();
        }
    }

    /**
     * 更多选项
     */
    private void moreClick() {
        //binding.homeRightMore.setOnClickListener(this::showPoPup);
        binding.homeRightMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop();
            }
        });
    }


    /**
     * 弹出菜单
     */
    private void showPop() {
        // 设置布局文件
        popupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.home_right_add, null));
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        popupWindow.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        popupWindow.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        popupWindow.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        popupWindow.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        popupWindow.showAsDropDown(binding.homeRightMore, -100, 0);
        // 书签点击事件
        LinearLayout bookLayout = popupWindow.getContentView().findViewById(R.id.home_bookmark_add);
        bookLayout.setOnClickListener(view -> {
            // 关闭弹窗
            popupWindow.dismiss();
            Bookmark bookmark;
            String title = webList.get(currentPage).getTitle();
            String url = webList.get(currentPage).getUrl();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sDateFormat.format(new Date());
            History history = historyDao.getOne(title);
            // 从数据库查询然后进行书签添加
            if (history != null) {
                Log.i(TAG, "查询到图标 >>> : " + history.getIcon());
                Log.i(TAG, "查询到标题 >>> : " + history.getTitle());
                Log.i(TAG, "查询到俩链接 >>> : " + history.getUrl());
                bookmark = new Bookmark(history.getIcon(), history.getTitle(), history.getUrl(), time, "yes");
            } else {
                bookmark = new Bookmark(null, title, url, time, "yes");
            }
            BookmarkDao bookmarkDao = new BookmarkDao(MainActivity.this);
            bookmarkDao.insertDate(bookmark);

        });
        // 网页源码获取
        LinearLayout htmlCodeLayout = popupWindow.getContentView().findViewById(R.id.home_html_code);
        htmlCodeLayout.setOnClickListener(view -> {
            // 关闭弹窗
            popupWindow.dismiss();
            webList.get(currentPage).loadUrl("view-source:" + PRESENT_URL);
        });


    }

    /**
     * 更多页面函数
     */
    private void showPoPup(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(MainActivity.this, view);
            MenuInflater inflater = mPopupMenu.getMenuInflater();
            inflater.inflate(R.menu.home_more, mPopupMenu.getMenu());
            mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
        }
        mPopupMenu.show();
    }

    private final PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener = item -> {
        // 添加书签
        if (item.getItemId() == R.id.home_collect) {
            Bookmark bookmark;
            String title = webList.get(currentPage).getTitle();
            String url = webList.get(currentPage).getUrl();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sDateFormat.format(new Date());
            History history = historyDao.getOne(title);
            // 从数据库查询然后进行书签添加
            if (history != null) {
                Log.i(TAG, "查询到图标 >>> : " + history.getIcon());
                Log.i(TAG, "查询到标题 >>> : " + history.getTitle());
                Log.i(TAG, "查询到俩链接 >>> : " + history.getUrl());
                bookmark = new Bookmark(history.getIcon(), history.getTitle(), history.getUrl(), time, "yes");
            } else {
                bookmark = new Bookmark(null, title, url, time, "yes");
            }
            BookmarkDao bookmarkDao = new BookmarkDao(MainActivity.this);
            bookmarkDao.insertDate(bookmark);
//            new BookDialog(MainActivity.this, R.style.dialogTheme, title, url,
//                    (title1, url1, yn) -> {
//                        if (title1.isEmpty() || url1.isEmpty()) {
//                            Toast.makeText(getApplicationContext(), "标题或链接为空", Toast.LENGTH_SHORT).show();
//                        } else {
//                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//                            String time = sDateFormat.format(new Date());
//                            Bookmark bookmark = new Bookmark(title1, url1, time, "yes");
//                            BookmarkDao bookmarkDao = new BookmarkDao(MainActivity.this);
//                            long id = bookmarkDao.insertDate(bookmark);
//                            Log.i(TAG, "插入成功之后 ID" + id);
//                        }
//                    }, "添加书签").
//                    show();
        }
        // 获取网页源码
        if (item.getItemId() == R.id.home_code) {
            Log.i(TAG, "查看源码的网站 >>> " + PRESENT_URL);
            webList.get(currentPage).loadUrl("view-source:" + PRESENT_URL);
        }
        return super.onOptionsItemSelected(item);
    };

    /**
     * 变量初始化
     */
    private void variable() {
        bookmarkDao = new BookmarkDao(this);
    }

    /**
     * 更多内容
     */
    private void moreContext() {
        binding.homeMore.setOnClickListener(view -> {
            moreDialog = new MoreDialog(MainActivity.this, R.style.dialogTheme);
            moreDialog.show();
        });
    }

    /**
     * 导航栏事件
     */
    private void navigationOnClick() {
        // 前进
        binding.homeUpPage.setOnClickListener(view -> upPage());
        // 后退
        binding.homeNextPage.setOnClickListener(v -> nextPage());
    }

    /**
     * 初始化搜索列表
     */
    private void listSearch() {
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(MainActivity.this, R.layout.search_list_item, arrayList);
        binding.homeSearchList.setAdapter(adapter);
        binding.homeSearchList.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "这是什么 >>> " + arrayList.get(position));
            binding.homeOneEdit.setText(arrayList.get(position));
            binding.homeOneEdit.setSelection(binding.homeOneEdit.getText().toString().length());
            // 进行搜索
            String text = arrayList.get(position);
            webList.get(currentPage).loadUrl(seniorUrl(text));
            // 隐藏搜索栏
            hideSearch();
        });
    }

    /**
     * drawerClick
     */
    private void drawerClick() {
        binding.drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // 滑动的过程中执行 slideOffset：从0到1
                //主页内容
                View content = binding.drawer.getChildAt(0);
                //侧边栏
                View menu = drawerView;
                float scale = 1 - slideOffset;//1~0
                float leftScale = (float) (1 - 0.3 * scale);
                float rightScale = (float) (0.7f + 0.3 * scale);//0.7~1
                menu.setScaleY((leftScale));//1~0.7
                content.setScaleX((float) ((rightScale)));
                content.setScaleY((float) (rightScale));
                content.setTranslationX(menu.getMeasuredWidth() * slideOffset);//0~width
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Log.i(TAG, "打开页面");
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                Log.i(TAG, "关闭页面");
                //binding.homeConstrain.setBackgroundResource(R.drawable.home_index);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.i(TAG, "不知道是什么事件");
            }
        });
        //获取对应的shape实例
        GradientDrawable background = (GradientDrawable) binding.navigationView.getBackground();
        //设置颜色
        background.setColor(Color.TRANSPARENT);
        binding.drawer.setScrimColor(Color.TRANSPARENT);
    }

    /**
     * 侧边栏事件
     */
    private void sideBarClick() {
        binding.homePaging.setOnClickListener(v -> {
            Log.i(TAG, "侧栏触发事件");
            if (binding.drawer.isDrawerOpen(binding.navigationView)) {
                binding.drawer.closeDrawer(binding.navigationView);
                Log.i(TAG, "关闭侧栏");
            } else {
                binding.drawer.openDrawer(binding.navigationView);
                Log.i(TAG, "打开侧栏");
                // 可以添加夜间判断
                //binding.homeConstrain.setBackgroundResource(R.drawable.home_shape_home);
            }
        });
    }

    /**
     * 创建对象
     */
    private void setListeners() {
        websiteDao = new WebsiteDao(this);
        historyDao = new HistoryDao(this);
        popupWindow = new PopupWindow(this);
    }

    /**
     * 页面刷新
     */
    private void pageRefreshClick() {
        // 刷新页面
        binding.homeRefresh.setOnClickListener(v -> webList.get(currentPage).reload());
    }

    /**
     * 地址栏事件
     */
    private void locationClick() {
        // 响应
        binding.homeOneEdit.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                String text = binding.homeOneEdit.getText().toString();
                if (text.length() > 0) {
                    webList.get(currentPage).loadUrl(seniorUrl(text));
                    hideSearch();

                } else {
                    openSearch();
                }

                // 关闭键盘
                KeyBoardUtil.closeKeyboard(MainActivity.this, binding.homeOneEdit);
                binding.homeOneEdit.setText("");
                binding.homeOneEdit.clearFocus();
                return true;
            }
            return false;
        });
        binding.homeOneEdit.setSelectAllOnFocus(true);
        // 主页显示
        binding.homeOneEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.homeOneEdit.setText(webList.get(currentPage).getUrl());
                binding.homeOneEdit.selectAll();
                if (HOME_URL.equals(binding.homeOneEdit.getText().toString())) {
                    binding.homeOneEdit.setText("");
                    binding.homeOneEdit.setHint("搜你所想");
                }
                // 获取焦点
                binding.homeOneEdit.requestFocus();
                // 打开搜索页面
                openSearch();
            } else {
                binding.homeOneEdit.setText("");
                // 关闭键盘
                KeyBoardUtil.closeKeyboard(MainActivity.this, binding.homeOneEdit);

            }
        });
        /**
         * 输入数据
         */
        binding.homeOneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = binding.homeOneEdit.getText().toString();
                if (HOME_URL.equals(text)) {
                    text = "";
                    Log.i(TAG, "重置");
                    arrayList.clear();
                }
                Log.i(TAG, "搜索值为 >>>" + text);
                if (!text.isEmpty()) {
                    searchList(text);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 查询列表
     */
    private void searchList(final String text) {
        Log.i(TAG, "传过来的值 " + text);
        if ("".equals(text)) {
            Log.i(TAG, "查询数据为空");
        } else {
            Log.i(TAG, "查询数据不为空");
            //get请求简洁版实现
            //获取关键字提示列表
            new RxVolley.Builder().url("http://sg1.api.bing.com/qsonhs.aspx?type=cb&cb=callback&q=" + text).shouldCache(false).httpMethod(RxVolley.Method.GET).callback(new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    try {
                        // 清除list数组中的缓存
                        arrayList.clear();
                        String string = t.substring(43);
                        Log.i(TAG, "返回子字符串: " + string);
                        if (string.contains(HOMESTRETCH)) {
                            Log.i(TAG, "处理字符串");
                            String str = string.substring(0, string.indexOf("/* pageview_candidate */);"));
                            // 截取
                            Log.i(TAG, "截取完毕字段 >>>: " + str);
                            // 字符串转换为 JSON
                            JSONObject jsonObject = JSONObject.parseObject(str);
                            // 获取根字段
                            JSONObject AS = jsonObject.getJSONObject("AS");
                            Log.i(TAG, "AS根字段 >>>" + AS);
                            if (AS.toString().contains("FullResults")) {
                                // 进行判断
                                if (AS.toString().contains("Results")) {
                                    // 获取 Results
                                    JSONArray result = AS.getJSONArray("Results");
                                    Log.i(TAG, "数组 >>>: " + result.toString());
                                    // 获取第一个数组
                                    JSONObject json = result.getJSONObject(0);
                                    Log.i(TAG, "Results: " + json);
                                    // 获取 Suggests
                                    JSONArray array = json.getJSONArray("Suggests");
                                    // 循环
                                    for (Object s : array) {
                                        Log.i(TAG, "onSuccess: " + s.toString());
                                        JSONObject object = JSONObject.parseObject(s.toString());
                                        Log.i(TAG, "onSuccess: " + object.getString("Txt"));
                                        // 更新列表数据
                                        arrayList.add(object.getString("Txt"));

                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
            }).encoding("UTF-8").doTask();
        }
    }

    /**
     * 页面分页事件
     */
    private void popUpClick() {
        // 页面选择点击事件
        binding.homeThereSelect.setOnClickListener(v -> {
            // 页面信息
            dialogWindowsPages = new ArrayList<>();
            // 遍历页面数组
            for (NewWeb newWeb : webList) {
                Log.i(TAG, "下标 >>> " + webList.indexOf(newWeb));
                // 标签ID
                pageId = webList.indexOf(newWeb);
                Log.i("TAG", "标签ID >>>" + pageId);
                // 标签名称
                String pageTitle = newWeb.getTitle();
                Log.i("TAG", "标签名称 >>>" + pageTitle);
                // 标签链接
                String pageUrl = newWeb.getUrl();
                Log.i("TAG", "标签链接 >>>" + pageUrl);
                dialogWindowsPages.add(new DialogWindow(pageId, pageTitle, pageUrl, v1 -> {
                    Log.i(TAG, "要删除的ID >>> " + pageId);
                    multiDialog.dismiss();
                }));
            }
            Log.i(TAG, "分页存储页面有 >>> " + dialogWindowsPages.size());
            multiDialog = new MultiDialog(MainActivity.this, R.style.dialogTheme, dialogWindowsPages);
            multiDialog.show();
            // 切屏
            multiDialog.setOnItemClickListener(new DialogPageAdapter.ItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // 切屏
                    Log.i(TAG, "切屏");
                    Log.i(TAG, "选择的页面数组 >>> " + position);
                    Log.i(TAG, "选择的页面 >>> " + dialogWindowsPages.get(position).getTitle());
                    // 关闭弹窗
                    multiDialog.dismiss();
                    // 隐藏当前的页面
                    Log.i(TAG, "隐藏当前的页面 >>> " + currentPage);
                    webList.get(currentPage).setVisibility(View.GONE);
                    // 将选择要打开的页面下标赋值给变量 currentPage
                    currentPage = position;
                    // 显示要打开的页面
                    Log.i(TAG, "显示要打开的页面" + currentPage);
                    webList.get(currentPage).setVisibility(View.VISIBLE);
                }

                @Override
                public void onClick(int position) {
                    // 删除
                    Log.i(TAG, "删除 >>> " + position);
                    if (webList.size() == 1) {
                        destroyWebView(webList.get(position));
                        webList.clear();
                        addNewPage(HOME_URL);
                    } else {
                        Log.i("TAG", "要删除的页面" + position);
                        destroyWebView(webList.get(position));
                        // 删除页面数据
                        dialogWindowsPages.remove(position);
                        // 删除web
                        webList.remove(webList.get(position));
                        Log.i(TAG, "删除之后当前页面 >>>" + position);
                        Log.i(TAG, "onClick: " + currentPage);
                        if (currentPage >= 1) {
                            webList.get(--currentPage).setVisibility(View.VISIBLE);
                            binding.homeThereSelect.setText(String.valueOf(webList.size()));
                        } else {
                            webList.get(currentPage).setVisibility(View.VISIBLE);
                            binding.homeThereSelect.setText(String.valueOf(webList.size()));
                        }
                    }
                    // 关闭弹窗
                    multiDialog.dismiss();
                }
            });
            // 删除所有页面
            multiDialog.setEmptyClickListener(v1 -> {
                // 关闭所有页面
                multiDialog.dismiss();
                Log.i(TAG, "需要销毁的页面数量 >>> " + webList.size());
                for (NewWeb newWeb : webList) {
                    // 销毁页面
                    destroyWebView(newWeb);
                }
                webList.clear();
                addNewPage(HOME_URL);
            });
            // 新增页面
            multiDialog.setAddPageClickListener(v2 -> {
                multiDialog.dismiss();
                addNewPage(HOME_URL);
            });
        });
    }


    /**
     * 获得状态栏高度
     */
    protected int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }


    /**
     * 添加页面
     */
    private void addNewPage(String url) {
        Log.i(TAG, "当前页面的下标 >>>" + webList.size());
        // 页面添加判断
        if (webList.size() != 0) {
            //隐藏之前显示页面
            Log.i(TAG, "页面隐藏 " + currentPage);
            webList.get(currentPage).setVisibility(View.GONE);
        }
        // 记录当前页面
        // 将此时页面的下标赋值给---变量 currentPage
        currentPage = webList.size();
        // 创建对象
        NewWeb newWeb = new NewWeb(MainActivity.this);
        // 初始化
        initWebView(newWeb, url);
        // 设置头部内容

        // 添加视图
        binding.homeTwoWeb.addView(newWeb, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 添加页面
        webList.add(newWeb);
        Log.i(TAG, "总页面大小" + webList.size());
        // 更新选择器
        binding.homeThereSelect.setText(String.valueOf(webList.size()));
    }

    private void setTopHint(String topContent) {
        Log.i(TAG, "获取头部标题内容 >>>>: " + topContent);
        try {
            switch (topContent) {
                case "title":
                    binding.homeOneEdit.setHint(webList.get(currentPage).getTitle());
                    break;
                case "website":
                    binding.homeOneEdit.setHint(webList.get(currentPage).getUrl());
                    break;
                default:
                    binding.homeOneEdit.setHint(webList.get(currentPage).getOriginalUrl().split("/")[2]);
            }
            if (HOME_URL.equals(webList.get(currentPage).getUrl())) {
                binding.homeOneEdit.setHint("主页");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化
     */
    private void initWebView(NewWeb newWeb, String url) {
        newWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i(TAG, "加载的资源 >>>>>>: " + url);
            }

            /**网页开始加载 onPageStarted至多会被执行一次*/
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG, "开始加载网址 >>> " + url);
            }

            // 重定向
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 获取路径
                String url = request.getUrl().toString();
                if (!url.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);
                    if (open_app) {
                        // 判断应用是否打开
                        if (list.size() > 0) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            System.out.println("该应用存在");
                            MessageDialogBuilder(view, url);
                        } else {
                            // 说明系统不存在该activity
                            System.out.println("该应用不存在");
                        }
                    }
                    return true;
                }
                // 当前页面赋值给变量
                PRESENT_URL = url;
                return super.shouldOverrideUrlLoading(view, request);
            }

            /**
             * 网页加载完成
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


        newWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // 进度条
                if (newProgress == 100) {
                    binding.homeProgressBar.setVisibility(View.GONE);
                } else {
                    binding.homeProgressBar.setVisibility(View.VISIBLE);
                    // 进度条显示
                    updateProgressBar(newProgress);
                    // 顶栏显示
                    setTopHint(getTopContent());
                }
                // 运行脚本
                mExecutor.submit(() -> {
                    // 处理网页脚本
                    ScriptDao scriptDao = new ScriptDao(getApplicationContext());
                    // 判断是否存在数据
                    if (scriptDao.isDataExist()) {
                        for (Script item : scriptDao.getAllDate()) {
                            Log.i(TAG, "onProgressChanged: >>> 运行脚本 ");
                            runOnUiThread(() -> newWeb.evaluateJavascript(item.getScript(), value -> {
                            }));
                        }
                    }
                });
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.i(TAG, "获取标题 >>>> " + title);
                // 保存历史记录
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sDateFormat.format(new Date());
                String ti = "2023-09-10 12:12:12";
                History history = new History(null, view.getTitle().trim(), view.getUrl(), time);
                historyDao.insertDate(history);
            }

            // 如果网址有ico图标则进行图标保存
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                Log.i(TAG, "被调用: >>>  " + view.getTitle());
                Log.i(TAG, "被调用: >>>  " + view.getUrl());
                super.onReceivedIcon(view, icon);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String name = SHA256Util.getSHA256Hash(view.getUrl());
                File storageDir = getApplicationContext().getExternalFilesDir(null);
                String path = (icon != null) ? (storageDir.getAbsolutePath() + File.separator + "Favicons") : null;
                Log.i(TAG, "历史文件存储图标地址  >>>>: " + path);
                if (path != null) {
                    Log.i(TAG, "网址图标不为 null >>> ");
                    // 当前网址是搜索引擎网址
                    if (view.getUrl().contains(getEngine())) {
                        Log.i(TAG, "当前网址是搜索引擎 >>> ");
                        // 判断数据库中是否有数据
                        Website website = websiteDao.getOne(getEngine());
                        if (website != null) {
                            Log.i(TAG, "数据不为 null");
                            Log.i(TAG, "获取网址 >>> " + website.getUrl());
                            Log.i(TAG, "获取图标 >>> " + website.getPath());
                            String iconPath = website.getPath();
                            // 更新图标
                            iconBookPath = iconPath;
                            historyDao.Update(iconPath, view.getTitle());
                        } else {
                            Log.i(TAG, "数据为 null");
                            Log.i(TAG, "保存返回全路径 >>> " + BitMapUtil.saveBitmap(icon, path, name));
                            String iconPath = BitMapUtil.saveBitmap(icon, path, name);
                            if (iconPath != null) {
                                // 插入数据
                                WebsiteDao websiteDao = new WebsiteDao(getApplicationContext());
                                websiteDao.installData(new Website(getEngine(), path + File.separator + name));
                                // 更新图标
                                iconBookPath = iconPath;
                                historyDao.Update(iconPath, view.getTitle());
                            }
                        }
                    } else {
                        Log.i(TAG, "保存返回全路径 >>> " + BitMapUtil.saveBitmap(icon, path, name));
                        String iconPath = BitMapUtil.saveBitmap(icon, path, name);
                        if (iconPath != null) {
                            // 历史数据存储
                            iconBookPath = iconPath;
                            historyDao.Update(iconPath, view.getTitle());
                        }
                    }
                } else {
                    Log.i(TAG, "网址图标为 null >>> ");
                }
            }

            // 开启全屏
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                mCustomViewCallback = callback;
                // 设置 Activity 的方向为横屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // 在当前的 Activity 中全屏展示视频
                ViewGroup decor = (ViewGroup) getWindow().getDecorView();
                decor.addView(mCustomView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                // 隐藏状态栏
                hideStatusBar();
                // 隐藏导航栏
                hideSystemBar();
                state = 1;
                super.onShowCustomView(view, callback);
            }

            // 关闭全屏
            @Override
            public void onHideCustomView() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                if (mCustomView == null) {
                    return;
                }
                // 从当前的 Activity 中移除全屏展示的视频
                ViewGroup decor = (ViewGroup) getWindow().getDecorView();
                decor.removeView(mCustomView);
                mCustomView = null;
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
                // 显示状态栏
                showStatusBar();
                super.onHideCustomView();
            }
        });

        // 浏览器监听下载
        newWeb.setDownloadListener((s, s1, s2, s3, l) ->
        {
            Log.i(TAG, "下载链接: " + s);
            Log.i(TAG, "userAgent: " + s1);
            Log.i(TAG, "文件类型: " + s3);
            Log.i(TAG, "文件大小: " + l);
            Log.i(TAG, "initWebView: ");


            // 此处进行文件解析 获取下载的文件名称和文件大小
            WebMessageUtil webMessageUtil = new WebMessageUtil();
            new Thread(() -> {
                // 获取用户信息
                WebMessage webMessage = webMessageUtil.gitFileName(s);
                Log.i(TAG, "文件名称 >>> " + webMessage.getFileName());
                Log.i(TAG, "文件类型 >>> " + webMessage.getMime());
                Log.i(TAG, "文件大小 >>> " + webMessage.getFileSize());

                // 更新 UI
                runOnUiThread(() -> {
                    // 计算文件大小
                    long bytes = webMessage.getFileSize();
                    double megabytes = (double) bytes / (1024 * 1024);
                    Log.i(TAG, "文件大小 >>>> " + megabytes);
                    Locale locale = Locale.US;
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
                    DecimalFormat decimalFormat = new DecimalFormat("#.##", symbols);
                    String formattedNumber = decimalFormat.format(megabytes);
                    String path = Environment.DIRECTORY_DOWNLOADS + "/" + AppConfig.PACK;

                    // 取消
                    View.OnClickListener onClickCancel = v -> {
                        Log.i("TAG", "取消下载 >>> ");
                    };
                    // 确认
                    View.OnClickListener onClickLConfirm = v -> {
                        Log.i("TAG", "确认下载 >>> ");
                        // 下载地址和下载目录
                        download(s, path, webMessage.getFileName(), webMessage.getMime(), webMessage.getFileSize());
                    };
                    // 弹窗显示
                    showFocusDialog(webMessage.getFileName(), formattedNumber, onClickCancel, onClickLConfirm);
                });

            }).start();

        });
        newWeb.loadUrl(url);

    }

    private void updateProgressBar(int progress) {
        int currentProgress = binding.homeProgressBar.getProgress();
        if (progress > currentProgress) {
            // 使用 ValueAnimator 来动态更新 ProgressBar 的进度
            ValueAnimator animator = ValueAnimator.ofInt(currentProgress, progress);
            animator.setDuration(300); // 设置你想要的动画持续时间（毫秒）

            // 在动画执行过程中更新 ProgressBar 的进度值
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    binding.homeProgressBar.setProgress(value);
                }
            });
            // 开始动画
            animator.start();
        } else {
            // 如果新进度小于等于当前进度，则直接设置新进度值，不进行动画效果
            binding.homeProgressBar.setProgress(progress);
        }
    }


    protected void showFocusDialog(String filePath, String size, View.OnClickListener cancelOnClick, View.OnClickListener confirmOnClick) {
        OfficialDialog.Builder builder = new OfficialDialog.Builder(MainActivity.this);
        builder.setTitle("文件下载");
        builder.setMessage(filePath);
        builder.setSize(size);
        builder.setButtonCancel(cancelOnClick);
        builder.setButtonConfirm(confirmOnClick);
        OfficialDialog customDialog = builder.create();
        customDialog.show();
    }


    public void hideStatusBar() {
        // API Level 16及以上
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    // 隐藏导航栏
    private void hideSystemBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }


    public void showStatusBar() {
        // 根据主题 重新显示状态栏文字
        SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean("night_mode", false)) {
            Log.i(TAG, "showStatusBar: 夜间");
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
            Log.i(TAG, "showStatusBar: 日间");
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    /**
     * 打开对话框
     */
    private void MessageDialogBuilder(WebView view, String url) {
        Log.i(TAG, "要打开的链接" + url);
    }

    /**
     * 销毁WebView页面
     */
    private void destroyWebView(NewWeb webViewD) {
        if (webViewD != null) {
            // 需要 webView 加载null内容
            webViewD.stopLoading();
            webViewD.clearHistory();
            webViewD.clearCache(true);
            webViewD.loadUrl(null);
            webViewD.pauseTimers();
            // 页面删除
            binding.homeTwoWeb.removeView(webViewD);
            webViewD.destroy();
        }
    }

    /**
     * 退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 返回
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 当前是否是搜索页面
            if (binding.homeSearchList.getVisibility() == View.VISIBLE) {
                Log.i(TAG, "搜索列表显示");
                hideSearch();
                return true;
            } else {
                Log.i(TAG, "返回键触发当前页为 >>>" + currentPage);
                // 判断当前页面是否是显示状态
                if (webList.get(currentPage).getVisibility() == View.VISIBLE) {
                    // 判断当前网页是否是首页
                    if (HOME_URL.equals(webList.get(currentPage).getUrl())) {
                        Log.i(TAG, "当前页面显示的网页是首页");
                        Log.i(TAG, "当前页面为第 >>> " + currentPage + " <<< 页");
                        Log.i(TAG, "总页面大小 >>> " + webList.size());
                        // 判断是否只为第一个页面
                        if (currentPage >= 1) {
                            Log.i(TAG, "页面大于 1 ");
                            // 销毁当前页面
                            destroyWebView(webList.get(currentPage));
                            // 删除web
                            webList.remove(webList.get(currentPage));
                            // 显示页面
                            webList.get(--currentPage).setVisibility(View.VISIBLE);
                            // 更新视图
                            binding.homeThereSelect.setText(String.valueOf(webList.size()));
                        } else {
                            Log.i(TAG, "当前页面为最后一页");
                            if (webList.size() > 1) {
                                Log.i(TAG, "此页面可以干掉");
                                // 销毁当前页面
                                destroyWebView(webList.get(currentPage));
                                // 删除web
                                webList.remove(webList.get(currentPage));
                                // 显示页面
                                webList.get(currentPage).setVisibility(View.VISIBLE);
                                // 更新视图
                                binding.homeThereSelect.setText(String.valueOf(webList.size()));
                            } else {
                                // 软件此时可以退出了
                                Log.i(TAG, "软件调用退出接口");
                                if (isExit) {
                                    // 退出事件
                                    Log.i(TAG, "点击了退出");
                                } else {
                                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                                    isExit = true;
                                    new Handler().postDelayed(() -> isExit = false, 2000);
                                    return true;
                                }
                                return super.onKeyDown(keyCode, event);
                            }
                        }
                    } else {
                        // 判断此页面是否可以返回上级
                        if (webList.get(currentPage).canGoBack()) {
                            Log.i(TAG, "可以返回上一个网页");
                            // 进行返回上一级
                            goBack();
                        } else {
                            Log.i(TAG, "不可以返回上一个网页");
                            // 销毁当前页面
                            destroyWebView(webList.get(currentPage));
                            // 删除web
                            webList.remove(webList.get(currentPage));
                            // 显示页面
                            webList.get(--currentPage).setVisibility(View.VISIBLE);
                            // 更新视图
                            binding.homeThereSelect.setText(String.valueOf(webList.size()));
                        }
                    }
                }
            }
            return true;
        } else {
            Log.i(TAG, "未开发");
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 页面前进
     */
    private void upPage() {
        if (isLoading) {
            webList.get(currentPage).stopLoading();
        } else if (webList.get(currentPage).canGoBack()) {
            webList.get(currentPage).goBack();
        }
    }

    /**
     *
     */
    private void nextPage() {
        if (webList.get(currentPage).canGoForward()) {
            webList.get(currentPage).goForward();

        }
    }

    /**
     * 关闭搜索页面
     */
    private void hideSearch() {
        // 隐藏搜索页面
        binding.homeSearchList.setVisibility(View.GONE);
        // 打开浏览页面
        binding.homeTwoWeb.setVisibility(View.VISIBLE);
        // 清除搜索
        binding.homeOneEdit.setText("");
        binding.homeOneEdit.clearFocus();
        setLocal();
    }

    /**
     * 打开搜索页面
     */
    private void openSearch() {
        // 隐藏浏览页面
        binding.homeTwoWeb.setVisibility(View.GONE);
        // 打开搜索页面
        binding.homeSearchList.setVisibility(View.VISIBLE);
    }

    /**
     * 地址内容转换
     */
    private String seniorUrl(String string) {
        try {
            if (string.length() > 0) {
                if (HOME_URL.equals(string)) {
                    return string;
                }
                try {
                    URL url = new URL(string);
                    return url.toString();
                } catch (Exception ew) {
                    try {
                        if (Patterns.WEB_URL.matcher("http://" + string).matches()) {
                            return "http://" + string;
                        } else {
                            return getEngine() + string;
                        }
                    } catch (Exception eh) {
                        return getEngine() + string;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    /**
     * 地址栏显示
     */
    private void setLocal() {
        if (HOME_URL.equals(webList.get(currentPage).getUrl())) {
            binding.homeOneEdit.setHint("主页");
        }

    }

    /**
     * 网页返回上一级
     */
    public void goBack() {
        webList.get(currentPage).goBack();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭线程池
        mExecutor.shutdown();
    }

    /**
     * 使用内部类可以无条件使用外围类所有元素
     */
    class MoreDialog extends Dialog {
        private Context context;

        private PagerAdapter pagerAdapter;

        private ViewPager viewPager;

        public MoreDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 设置动画级位置
            Window window = getWindow();
            window.setGravity(Gravity.BOTTOM);
            // 设置导航栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setContentView(R.layout.dialog_more);
            // 主题
            LinearLayout night = findViewById(R.id.night);
            // 初始化控件
            ImageView imageView = findViewById(R.id.night_img);
            TextView textView = findViewById(R.id.night_title);
            // 判断
            SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (prefs.getBoolean("night_mode", false)) {
                Log.i(TAG, "夜间");
                // 是否是夜间
                imageView.setColorFilter(Color.BLUE);
                textView.setTextColor(Color.BLUE);
            } else {
                Log.i(TAG, "白天");
                imageView.setColorFilter(Color.BLACK);
                textView.setTextColor(Color.BLACK);
            }
            night.setOnClickListener(v -> {
                // 写入
                boolean isTheme1 = prefs.getBoolean("night_mode", false);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("night_mode", !isTheme1);
                editor.apply();
                if (prefs.getBoolean("night_mode", false)) {
                    Log.i(TAG, "点击夜间");
                    white();
                } else {
                    Log.i(TAG, "点击白天");
                    black();
                }
                dismiss();
            });
            // 书签
            LinearLayout bookmark = findViewById(R.id.bookmark);
            bookmark.setOnClickListener(v -> {
                Log.i("TAG", "收藏按钮点击了");
                Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                MainActivity.this.startActivityForResult(intent, BOOKMARK);
                //overridePendingTransition(0, 0);
                // 关闭页面
                dismiss();
            });
            // 历史
            LinearLayout history = findViewById(R.id.history);
            history.setOnClickListener(v -> {
                Log.i("TAG", "历史按钮点击了");
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                MainActivity.this.startActivityForResult(intent, HISTORY);
                //overridePendingTransition(0, 0);
                // 关闭页面
                dismiss();
            });
            // 下载
            LinearLayout download = findViewById(R.id.download);
            download.setOnClickListener(v -> {
                Log.i(TAG, "下载按钮点击了");
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
                //overridePendingTransition(0, 0);
                // 关闭页面
                dismiss();
            });
            // 脚本
            LinearLayout script = findViewById(R.id.script);
            script.setOnClickListener(v -> {
                Log.i(TAG, "下载按钮点击了");
                Intent intent = new Intent(MainActivity.this, ScriptActivity.class);
                startActivity(intent);
                // 关闭页面
                dismiss();
            });
            // 设置
            LinearLayout set = findViewById(R.id.set);
            set.setOnClickListener(v -> {
                Log.i(TAG, "设置按钮点击了");
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                // 关闭页面
                dismiss();
            });
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.width = displayMetrics.widthPixels; // 设置dialog宽度
            getWindow().setAttributes(layoutParams);
            setCanceledOnTouchOutside(true);// 点击Dialog外部消失
            setCancelable(true);
        }
    }

    private class UIHander extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //下载时
                case PROCESSING:
                    Bundle bundle = msg.getData();
                    // 线程ID
                    int id = bundle.getInt("id");
                    Log.i(TAG, "通知栏ID:>>> " + id);
                    // 从消息中获取已经下载的数据长度
                    int size = bundle.getInt("size");
                    Log.i(TAG, "已经下载的数据长度: >>> " + size);
                    // 从消息中获取文件总大小
                    int sum = bundle.getInt("sum");
                    Log.i(TAG, "文件总大小: >>> " + sum);
                    // 计算已经下载的百分比,此处需要转换为浮点数计算
                    float num = (float) size / (float) sum;
                    Log.i(TAG, "转换为浮点 >>> " + num);
                    // 把获取的浮点数计算结果转换为整数
                    int result = (int) (num * 100);
                    Log.i(TAG, "下载 >>> " + result + "%");
                    String path = bundle.getString("path");
                    Log.i(TAG, "保存路径 >>> " + path);
                    String fileName = bundle.getString("filename");
                    Log.i(TAG, "文件名称 >>>  " + fileName);
                    Notice notice = new Notice();
                    notice.setNotifyId(id);
                    notice.setTotalProgress(sum);
                    notice.setCurrentProgress(size);
                    // 判断是否下载完成
                    if (result == 100) {
                        notice.setTitle("点击安装");
                        notice.setButton("安装");
                        Intent broadcast = new Intent(MainActivity.this, CustomButtonReceiver.class);
                        broadcast.setAction("yes");
                        // 传递状态
                        broadcast.putExtra("schedule", "100");
                        // 传递文件路径
                        broadcast.putExtra("fileName", fileName);
                        // 传递线程ID
                        broadcast.putExtra("id", id);
                        Log.i(TAG, "handleMessage: " + broadcast.getStringExtra("schedule"));
                        Log.i(TAG, "handleMessage: " + broadcast.getStringExtra("fileName"));
                        notice.setNotifyId(id++);
                        notice.setIntent(broadcast);
                    } else {
                        Intent broadcast = new Intent(MainActivity.this, CustomButtonReceiver.class);
                        broadcast.setAction("no");
                        broadcast.putExtra("location", path);
                        broadcast.putExtra("id", id);
                        Log.i(TAG, "取消下载id: " + id);
                        Log.i(TAG, "handleMessage: " + broadcast.getIntExtra("id", 0));
                        notice.setIntent(broadcast);
                        Log.i(TAG, "正在下载 点击取消");
                        notice.setNotifyId(id++);
                        notice.setTitle(fileName);
                        notice.setButton("暂停");
                    }
                    // 循环通知
                    notifyNow(notice);
                    break;
                case FAILURE:
                    //下载失败时提示
                    Log.i(TAG, "文件下载失败");
                    break;
                default:
                    Log.i(TAG, "handleMessage: ");
            }
        }
    }


    private class DownloadTask implements Runnable {
        /**
         * 下载地址
         */
        private String path;
        /**
         * 保存目录
         */
        private String saveDir;
        /**
         * 下载
         */
        private FileDownloaded loader;
        /**
         * 下载线程
         */
        private DownloadTask downloadTask;
        /**
         * 文件名称
         */
        private String fileName;
        /**
         * 文件类型
         */
        private String mime;
        /**
         * 文件大小
         */
        private int fileSize;


        /**
         * @param saveDir      安装目录
         * @param loader       loader
         * @param downloadTask downloadTask
         */
        private void init(String path, String saveDir, FileDownloaded loader, DownloadTask downloadTask, String fileName, String mime, int fileSize) {
            this.path = path;
            this.saveDir = saveDir;
            this.loader = loader;
            this.downloadTask = downloadTask;
            this.fileName = fileName;
            this.mime = mime;
            this.fileSize = fileSize;

            Log.i(TAG, "初始化线程ID: " + Thread.currentThread().getId());
        }

        /**
         * 退出下载
         */
        private void exit() {
            Log.i(TAG, "exit: 线程 >>> " + Thread.currentThread().getName());
            Log.i(TAG, "exit: 线程 ID >>> " + Thread.currentThread().getId());
            Log.i(TAG, "exit: 调用退出下载 >>>");
            if (loader != null) {
                Log.i(TAG, "exit: 已调用退出");
                loader.exit();
            }
        }

        public void run() {
            try {
                // 下载线程接收到的线程
                Log.i(TAG, "线程 >>> : " + Thread.currentThread().getName());
                Log.i(TAG, "线程 ID >>> : " + Thread.currentThread().getId());
                loader.init(saveDir, fileName, mime, fileSize);
                Log.i(TAG, "返回文件路径全称: " + filePath);
                scheduleMax = loader.getFileSize();
                Log.i(TAG, "返回文件总大小 run: " + scheduleMax);
                // 设置
                map.put((int) Thread.currentThread().getId(), downloadTask);
                loader.download(downloaded -> {
                    Log.i(TAG, "打印返回 >>>> " + Thread.currentThread().getName() + " >>>" + downloaded.getDownloadedSize());
                    Log.i(TAG, "获取线程Id >>>> " + Thread.currentThread().getId());
                    Log.i(TAG, "文件已下载 >>> " + downloaded.getDownloadedSize());
                    Log.i(TAG, "文件总大小 >>> " + downloaded.getFileSize());
                    Log.i(TAG, "文件安装路径 >>> " + downloaded.getSaveFile());
                    Log.i(TAG, "文件名称 >>> " + downloaded.getFileName());
                    Log.i(TAG, "下载状态 >>> " + downloaded.isExited());
                    if (downloaded.isExited()) {
                        Log.i(TAG, "结束下载");
                        return;
                    }
                    Log.i(TAG, "获取最终的下载地址 >>>");
                    Bundle bundle = new Bundle();
                    // 线程ID
                    bundle.putInt("id", (int) Thread.currentThread().getId());
                    // 已下载文件大小
                    bundle.putInt("size", downloaded.getDownloadedSize());
                    // 文件总大小
                    bundle.putInt("sum", downloaded.getFileSize());
                    // 文件路径
                    bundle.putString("path", downloaded.getSaveFile());
                    // 文件名称
                    bundle.putString("filename", downloaded.getFileName());
                    // 文件消息
                    Message msg = new Message();
                    msg.what = 1;
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                });
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(handler.obtainMessage(-1));
            }
        }
    }

    public static class CustomButtonReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", "广播通知");
            String state = intent.getAction();
            Log.i("TAG", "下载状态 >>> " + state);
            Message message = Message.obtain();
            if ("yes".equals(state)) {
                int id = intent.getIntExtra("id", 0);
                String fileName = intent.getStringExtra("fileName");
                String mime = intent.getStringExtra("mime");
                Log.i("TAG", "获取 >>> " + id);
                Log.i("TAG", "获取文件路径 >>> " + fileName);
                Log.i("TAG", "获取文件类型 >>> " + mime);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putString("fileName", fileName);
                bundle.putString("mime", mime);
                message.setData(bundle);
                Log.i("TAG", "下载完毕");
                // 进行安装
                message.what = 1;
                BarHandler.sendMessage(message);
            } else if ("no".equals(state)) {
                int schedule = intent.getIntExtra("schedule", 0);
                int id = intent.getIntExtra("id", 0);
                Log.i("TAG", "获取通知ID >>> " + id);
                Log.i("TAG", "获取进度 >>> " + schedule);
                Log.i("TAG", "暂停下载");
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                // 传递数据
                message.setData(bundle);
                // 进行取消
                message.what = 0;
                BarHandler.sendMessage(message);
            }
        }
    }
}