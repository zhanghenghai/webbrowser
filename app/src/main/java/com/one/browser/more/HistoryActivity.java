package com.one.browser.more;

import static com.one.browser.utils.PopPlaceUtil.calculatePopWindowPos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.gson.Gson;
import com.one.browser.R;
import com.one.browser.adapter.HistoryAdapter;
import com.one.browser.entity.GroupedHistoryData;
import com.one.browser.sqlite.History;
import com.one.browser.sqlite.HistoryDao;
import com.one.browser.utils.DateUtil;
import com.one.browser.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 18517
 */
public class HistoryActivity extends AppCompatActivity {
    private PopupWindow dialogPopup;

    private List<GroupedHistoryData> dataList;

    private List<History> historyList;

    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dialogPopup = new PopupWindow(this);

        // 标题
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("历史记录");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        historyList = new LinkedList<>();
        // 判断是否有数据
        HistoryDao historyDao = new HistoryDao(this);
        if (historyDao.isDataExist()) {
            historyList = historyDao.getAllDate();
            Log.i("TAG", "历史数据中存数据大小为 >>> " + historyList.size());
        }
        RecyclerView recyclerView = findViewById(R.id.historyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 构造方法传递数据
        dataList = new ArrayList<>();
        Map<String, List<History>> groupedData = groupDataByTime(historyList);
        for (Map.Entry<String, List<History>> entry : groupedData.entrySet()) {
            String groupTitle = entry.getKey();
            List<History> groupData = entry.getValue();
            dataList.add(new GroupedHistoryData(groupTitle, groupData));
        }

        Gson gson = new Gson();
        String json = gson.toJson(dataList);
        Log.d("TAG", "打印数据: " + json);
        historyAdapter = new HistoryAdapter(dataList);
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.setOnItemClickListener(new HistoryAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String url) {
                //点击返回时间
                Log.d("TAG", "获取网址 >>>" + url);
                Intent intent = new Intent();
                intent.putExtra("History", url);
                setResult(3, intent);
                HistoryActivity.this.finish();
            }

            @Override
            public void onItemLongClick(View view, int SgroupPosition, int itemPosition) {
                showPopDialog(view, SgroupPosition, itemPosition);
            }
        });
    }

    private Map<String, List<History>> groupDataByTime(List<History> dataList) {
        Map<String, List<History>> groupedData = new LinkedHashMap<>();
        for (History item : dataList) {
            String time = DateUtil.getTime(item.getTime());
            if (groupedData.containsKey(time)) {
                groupedData.get(time).add(item);
            } else {
                List<History> group = new ArrayList<>();
                group.add(item);
                groupedData.put(time, group);
            }
        }
        return groupedData;
    }

    public void deleteItem(int groupPosition, int itemPosition) {
        GroupedHistoryData group = dataList.get(groupPosition);
        List<History> items = group.getHistoryList();
        if (itemPosition >= 0 && itemPosition < items.size()) {
            String title = items.get(itemPosition).getTitle();
            Log.i("TAG", "要删除的标题是 >>>: " + title);
            HistoryDao historyDao = new HistoryDao(this);
            historyDao.deleteHistory(title);
            // 先更新数据集，再通知适配器进行删除

            items.remove(itemPosition);
            historyAdapter.notifyItemRemoved(itemPosition);
            historyAdapter.notifyItemRangeChanged(itemPosition, historyAdapter.getItemCount());

            // 如果组内没有数据了，则删除整个组
            if (items.isEmpty()) {
                dataList.remove(groupPosition);
                historyAdapter.notifyItemRemoved(groupPosition);
                historyAdapter.notifyItemRangeChanged(groupPosition, historyAdapter.getItemCount());
            }
            dialogPopup.dismiss();
        }
    }

    public String getUrl(int groupPosition, int itemPosition) {
        GroupedHistoryData group = dataList.get(groupPosition);
        List<History> items = group.getHistoryList();
        if (itemPosition >= 0 && itemPosition < items.size()) {
            String url = items.get(itemPosition).getUrl();
            dialogPopup.dismiss();
            return url;
        }
        return null;
    }


    private void showPopDialog(View view, int SgroupPosition, int itemPosition) {
        // 获取长按事件的位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        // 设置布局文件
        int layoutId = R.layout.pop_history_item;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        // 为了避免部分机型不显示，我们需要重新设置一下宽高
        dialogPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置pop透明效果
        dialogPopup.setBackgroundDrawable(new ColorDrawable(0x0000));
        // 设置pop出入动画
        dialogPopup.setAnimationStyle(R.style.pop_add);
        // 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        dialogPopup.setFocusable(true);
        // 设置pop可点击，为false点击事件无效，默认为true
        dialogPopup.setTouchable(true);
        // 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失
        dialogPopup.setOutsideTouchable(true);
        // 相对于 + 号正下面，同时可以设置偏移量
        dialogPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置背景
        dialogPopup.setBackgroundDrawable(new ColorDrawable());
        int[] windowPos = calculatePopWindowPos(view, contentView);
        // 可以自己调整偏移
        int xOff = 20;
        windowPos[0] -= xOff;
        // windowContentViewRoot是根布局View
        dialogPopup.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        // 点击删除操作
        LinearLayout historyDelete = dialogPopup.getContentView().findViewById(R.id.history_delete);
        historyDelete.setOnClickListener(view1 -> {
            deleteItem(SgroupPosition, itemPosition);
        });
        // 点击复制链接按钮
        LinearLayout copeUrl = dialogPopup.getContentView().findViewById(R.id.cope_url);
        copeUrl.setOnClickListener(v -> {
            String url = getUrl(SgroupPosition, itemPosition);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("url", url);
            clipboard.setPrimaryClip(clip);
        });
        // 分享链接
        LinearLayout share = dialogPopup.getContentView().findViewById(R.id.share_url);
        share.setOnClickListener(v -> {
            String url = getUrl(SgroupPosition, itemPosition);
            Intent intent12 = new Intent(Intent.ACTION_SEND);
            intent12.setType("text/plain");
            intent12.putExtra(Intent.EXTRA_TEXT, url);
            this.startActivity(intent12);
        });
    }



}