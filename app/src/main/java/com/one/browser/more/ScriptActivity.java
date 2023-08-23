package com.one.browser.more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.one.browser.R;
import com.one.browser.adapter.ScriptAdapter;
import com.one.browser.dialog.ScriptDialog;
import com.one.browser.sqlite.Script;
import com.one.browser.sqlite.ScriptDao;
import com.one.browser.utils.PopPlaceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 18517
 */
public class ScriptActivity extends AppCompatActivity {

    /**
     * 更多内容
     */
    private PopupWindow popupWindow;
    private PopupWindow dialogPopup;
    private ImageView imageView;
    private List<Script> scriptList;

    private ScriptAdapter adapter;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script);
        init();
        // 标题
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("脚本管理");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // 更多内容
        imageView = findViewById(R.id.script_more);
        imageView.setOnClickListener(view -> {
            showPop();
        });
        scriptList = new ArrayList<>();
        ScriptDao scriptDao = new ScriptDao(this);
        if (scriptDao.isDataExist()) {
            scriptList.addAll(scriptDao.getAllDate());
            Log.i("TAG", "脚本数据中存数据大小为 >>> " + scriptList.size());
        }
        // Recycler
        recyclerView = findViewById(R.id.scriptList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ScriptAdapter(scriptList);
        recyclerView.setAdapter(adapter);
        // 列表点击事件
        adapter.setItemClickListener(new ScriptAdapter.ItemClickListener() {
            // 单选
            @Override
            public void onItemClick(String name, String content) {
                Log.i("TAG", "获取脚本标题 >>> " + name);
                Log.i("TAG", "获取脚本内容 >>> " + content);
            }

            // 长按
            @Override
            public void onItemLongClick(View view, int position) {
                Log.i("TAG", "onItemLongClick: 点击了 >>> " + position);
                showPopDialog(view, position);
            }

            // 切换
            @Override
            public void onChecked(int position, boolean b) {
                Log.i("TAG", "脚本状态 >>>>> " + b);
                Log.i("TAG", "要禁用的脚本是 >>> " + scriptList.get(position).getTitle());
                Script oldScript = scriptList.get(position);
                Script newScript;
                if (b) {
                    newScript = new Script(oldScript.getTitle(), oldScript.getScript(), "1", null);
                    Log.i("TAG", "启用 >>>: ");

                } else {
                    newScript = new Script(oldScript.getTitle(), oldScript.getScript(), "0", null);
                    Log.i("TAG", "禁用 >>>: ");

                }
                ScriptDao scriptDao = new ScriptDao(getApplicationContext());
                scriptDao.updateState(oldScript, newScript);
            }
        });

    }


    private void init() {
        popupWindow = new PopupWindow(this);
        dialogPopup = new PopupWindow(this);
    }


    private void showPop() {
        // 设置布局文件
        popupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.pop_script_add, null));
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
        popupWindow.showAsDropDown(imageView, -100, 0);
        // 取消事件
        View.OnClickListener cancel = view -> {
            Log.i("TAG", "取消按钮: ");
        };
        // 确定事件
        ScriptDialog.ScriptClickListener scriptClickListener = (view, title, content) -> {
            Log.i("TAG", "标题 >>>> " + title);
            Log.i("TAG", "内容 >>>> " + content);
            ScriptDao scriptDao = new ScriptDao(this);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = simpleDateFormat.format(new Date());
            scriptDao.insertDate(new Script(title, content, "0", time));
            recyclerView.scrollToPosition(0);
            scriptList.add(0, new Script(title, content, "0", time));
            adapter.notifyItemInserted(0);
            adapter.notifyItemRangeChanged(0, scriptList.size());
        };
        // 设置点击事件
        LinearLayout linearLayoutAdd = popupWindow.getContentView().findViewById(R.id.script_add);
        linearLayoutAdd.setOnClickListener(view -> {
            popupWindow.dismiss();
            setShowDialog("脚本添加", null, null, cancel, scriptClickListener);
        });
    }


    private void showPopDialog(View view, int position) {
        // 获取长按事件的位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        // 设置布局文件
        int layoutId = R.layout.pop_script_item;
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
        int[] windowPos = PopPlaceUtil.calculatePopWindowPos(view, contentView);
        // 可以自己调整偏移
        int xOff = 20;
        windowPos[0] -= xOff;
        // windowContentViewRoot是根布局View
        dialogPopup.showAtLocation(view, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        // 点击删除操作
        LinearLayout scriptDelete = dialogPopup.getContentView().findViewById(R.id.script_delete);
        scriptDelete.setOnClickListener(view1 -> {
            dialogPopup.dismiss();
            Script script = scriptList.get(position);
            Log.i("TAG", "要删除脚本是 >>> " + script.getTitle());
            ScriptDao scriptDao = new ScriptDao(this);
            scriptDao.deleteScript(script);
            scriptList.remove(position);
            // 更新视图
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
        });
        // 点击修改按钮
        LinearLayout scriptUpdate = dialogPopup.getContentView().findViewById(R.id.script_update);
        scriptUpdate.setOnClickListener(view12 -> {
            dialogPopup.dismiss();
            // 取消
            View.OnClickListener cancelClick = view13 -> {
                Log.i("TAG", "弹窗取按钮: ");
            };
            // 确定
            ScriptDialog.ScriptClickListener affirmClick = (view14, title, context) -> {
                Script script = new Script(title, context, "1", null);
                ScriptDao scriptDao = new ScriptDao(this);
                scriptDao.updateScript(scriptList.get(position), script);
                scriptList.set(position, script);
                // 更新视图
                adapter.notifyItemChanged(position);
            };
            setShowDialog("修改脚本", scriptList.get(position).getTitle(), scriptList.get(position).getScript(), cancelClick, affirmClick);
        });
    }

    // 修改
    private void setShowDialog(String title, String editName, String editContext, View.OnClickListener cancel, ScriptDialog.ScriptClickListener affirm) {
        ScriptDialog.Builder builder = new ScriptDialog.Builder(this);
        builder.setTitle(title);
        builder.setEditTitle(editName);
        builder.setEditContext(editContext);
        builder.setCancelButton(cancel);
        builder.setAffirmButton(affirm);
        ScriptDialog scriptDialog = builder.create();
        scriptDialog.show();
    }



}