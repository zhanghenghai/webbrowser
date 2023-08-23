package com.one.browser.more;

import static com.one.browser.utils.PopPlaceUtil.calculatePopWindowPos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.one.browser.R;
import com.one.browser.adapter.DownloadAdapter;
import com.one.browser.config.AppConfig;
import com.one.browser.entity.GroupedDownload;
import com.one.browser.sqlite.Bookmark;
import com.one.browser.sqlite.BookmarkDao;
import com.one.browser.sqlite.Download;
import com.one.browser.sqlite.DownloadDao;
import com.one.browser.utils.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 18517
 */
public class DownloadActivity extends AppCompatActivity {


    private PopupWindow popupWindow;
    private List<Download> downloadList;
    private DownloadAdapter downloadAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloa);
        init();
        // 标题
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("下载记录");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        downloadList = new LinkedList<>();
        // 判断是否有数据
        DownloadDao downloadDao = new DownloadDao(this);
        if (downloadDao.isDataExist()) {
            Log.i("TAG", "下载历史中存在数据 >>> ");
            downloadList = downloadDao.getAllDate();
        }
        // Recycler
        RecyclerView recyclerView = findViewById(R.id.downloadList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DownloadActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        downloadAdapter = new DownloadAdapter(downloadList);
        recyclerView.setAdapter(downloadAdapter);
        downloadAdapter.setOnItemClickListener(new DownloadAdapter.ItemClickListener() {
            @Override
            public void onClick(String name, String path, String mime) {
                Log.i("TAG", "获取文件名称  >>>> " + name);
                Log.i("TAG", "获取文件路径  >>>> " + path);
                Log.i("TAG", "获取文件类型  >>>> " + mime);
                File file = new File(AppConfig.PACKAGE, name);
                Log.i("TAG", "安装全路径: >>> " + file);
                Uri fileUri;
                fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.one.browser", file);
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(fileUri, mime);
                installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(installIntent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showPopDialog(view,position);
            }
        });
    }

    private void init() {
        popupWindow = new PopupWindow(this);
    }
    private void showPopDialog(View view, int position) {
        // 获取长按事件的位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        // 设置布局文件
        int layoutId = R.layout.pop_download_item;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
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
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置背景
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        int[] windowPos = calculatePopWindowPos(view, contentView);
        // 可以自己调整偏移
        int xOff = 20;
        windowPos[0] -= xOff;
        // windowContentViewRoot是根布局View
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);

        LinearLayout delete = popupWindow.getContentView().findViewById(R.id.download_delete);
        LinearLayout share = popupWindow.getContentView().findViewById(R.id.download_share);

        delete.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            String title = downloadList.get(position).getTitle();
            DownloadDao downloadDao = new DownloadDao(this);
            downloadDao.deleteData(title);
            downloadList.remove(position);
            downloadAdapter.notifyItemRemoved(position);
            downloadAdapter.notifyItemRangeChanged(position, downloadAdapter.getItemCount());
        });
        share.setOnClickListener(view1 -> {
            File file = new File(AppConfig.PACKAGE, downloadList.get(position).getTitle());
            Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "com.one.browser", file);
            String type= downloadList.get(position).getMime();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(type);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            // 授予 URI 临时读取权限
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "文件分享"));
        });


    }

}