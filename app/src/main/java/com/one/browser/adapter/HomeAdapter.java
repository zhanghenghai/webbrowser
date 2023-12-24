package com.one.browser.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.one.browser.entity.Home;
import com.one.browser.R;

import java.util.List;

/**
 * @author 18517
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final List<Home> homeList;

    private Context context;

    private int press = 333;

    public HomeAdapter(List<Home> homeList, Context context) {
        this.homeList = homeList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {

        Home home = homeList.get(position);
        holder.homeImg.setImageResource(home.getIcon());
        // 判断 如果过去到的值为则进行文字输出
        if (homeList.get(position).getIcon() == 0  ){
            // 获取标题
            System.out.println("获取标题 >>> "+ homeList.get(position).getTitle());
            // 获取第一个值并赋值
            holder.homeTitle.setText(homeList.get(position).getTitle().substring(0,1));
            // 显示数据
            holder.homeTitle.setVisibility(View.VISIBLE);
            // 渲染颜色
            holder.homeImg.setImageResource(R.color.white);
        }else {
            holder.homeTitle.setVisibility(View.GONE);
        }
        holder.homeText.setText(home.getTitle());
        if (press == 333) {
            holder.homeClose.setVisibility(View.GONE);
        }else {
            holder.homeClose.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView homeImg;
        TextView homeTitle;
        TextView homeText;
        ImageView homeClose;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeImg = itemView.findViewById(R.id.home_img);
            homeTitle = itemView.findViewById(R.id.home_title);
            homeText = itemView.findViewById(R.id.home_text);
            homeClose = itemView.findViewById(R.id.home_cancel);
        }
    }

}
