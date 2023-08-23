package com.one.browser.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.one.browser.R;
import com.one.browser.adapter.DialogPageAdapter;
import com.one.browser.entity.DialogWindow;

import java.util.ArrayList;

/**
 * @author 18517
 */
public class MultiDialog extends Dialog {
    private final String TAG = "TAG";
    private Context context;

    private RecyclerView recyclerView;

    private ArrayList<DialogWindow> pages;

    private DialogPageAdapter dialogPageAdapter;


    public MultiDialog(@NonNull Context context, int themeResId, ArrayList<DialogWindow> pages) {
        super(context, themeResId);
        this.context = context;
        this.pages = pages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //==========设置工具箱Dialog位置及动画==========
        Window window = getWindow();
        assert window != null;
        // 设置dialog显示位置
        window.setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_multi);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        // 设置dialog宽度
        layoutParams.width = displayMetrics.widthPixels;
        getWindow().setAttributes(layoutParams);
        setCanceledOnTouchOutside(true);
        //==========设置工具箱Dialog位置及动画==========

        recyclerView = findViewById(R.id.listViewPage);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        dialogPageAdapter = new DialogPageAdapter(pages);
        recyclerView.setAdapter(dialogPageAdapter);


        TextView buttonDismiss = findViewById(R.id.DismissMulti);
        buttonDismiss.setOnClickListener(v -> dismiss());
    }


    /**
     * 添加页面
     */
    public void setAddPageClickListener(View.OnClickListener onClickListener) {
        ImageView addPage = findViewById(R.id.addPage);
        addPage.setOnClickListener(onClickListener);
    }

    /**
     * 无痕模式
     */
    void setIncognitoClickListener(View.OnClickListener onClickListener) {
        ImageView incognito = findViewById(R.id.buttonIncognito);
        incognito.setOnClickListener(onClickListener);
    }

    /**
     * 页面切屏
     */
    public void setOnItemClickListener(DialogPageAdapter.ItemClickListener itemClickListener) {
        dialogPageAdapter.setOnItemClickListener(itemClickListener);
    }


    /**
     * 删除全部页面
     */
    public void setEmptyClickListener(View.OnClickListener onClickListener) {
        ImageView emptyPage = findViewById(R.id.emptyPage);
        emptyPage.setOnClickListener(onClickListener);
    }


    //==========设置各个按钮监听事件==========

}