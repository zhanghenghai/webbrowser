package com.one.browser.more;

import static com.one.browser.utils.PopPlaceUtil.calculatePopWindowPos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.one.browser.R;
import com.one.browser.adapter.BookAdapter;
import com.one.browser.sqlite.Bookmark;
import com.one.browser.sqlite.BookmarkDao;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author 18517
 */
public class BookmarkActivity extends AppCompatActivity {
    /**
     * 弹窗
     */
    private PopupWindow popupWindow;

    private List<Bookmark> bookmarkList;

    private BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        init();
        // 标题
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("书签收藏");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        bookmarkList = new LinkedList<>();
        // 判断是否有数据
        BookmarkDao bookmarkDao = new BookmarkDao(this);
        if (bookmarkDao.isDataExist()) {
            Log.i("TAG", "书签中存数据 >>> ");
            bookmarkList = bookmarkDao.getAllDate();
        }
        // Recycler
        RecyclerView recyclerView = findViewById(R.id.bookmarkList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BookAdapter(bookmarkList);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new BookAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String url) {
                Log.d("TAG", "获取网址 >>>" + url);
                Intent intent = new Intent();
                intent.putExtra("Bookmark", url);
                setResult(2, intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showPopDialog(view, position);
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
        int layoutId = R.layout.pop_bookmark_item;
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

        LinearLayout delete = popupWindow.getContentView().findViewById(R.id.bookmark_delete);
        LinearLayout cope = popupWindow.getContentView().findViewById(R.id.bookmark_cope);
        LinearLayout share = popupWindow.getContentView().findViewById(R.id.bookmark_share);

        delete.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            Bookmark bookmark = bookmarkList.get(position);
            BookmarkDao bookmarkDao = new BookmarkDao(this);
            bookmarkDao.deleteBookmark(bookmark);
            bookmarkList.remove(position);
            // 更新视图
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
        });

        cope.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            String url = bookmarkList.get(position).getUrl();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", url);
            clipboard.setPrimaryClip(clip);
        });

        share.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            String url = bookmarkList.get(position).getUrl();
            Intent intent12 = new Intent(Intent.ACTION_SEND);
            intent12.setType("text/plain");
            intent12.putExtra(Intent.EXTRA_TEXT, url);
            this.startActivity(intent12);
        });
    }

}