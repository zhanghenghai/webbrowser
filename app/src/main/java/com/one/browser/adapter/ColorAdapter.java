package com.one.browser.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.one.browser.R;
import com.one.browser.entity.Colors;

import java.util.List;

/**
 * @author 18517
 */
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private final List<Colors> colorsList;

    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        /**
         * 点击事件
         *
         * @param position 下标
         */
        void onClick(int position);
    }


    public ColorAdapter(List<Colors> colorsList) {
        this.colorsList = colorsList;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setPosition(int position) {
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // 颜色标题
        TextView title;
        // 颜色卡片
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            cardView = itemView.findViewById(R.id.item_color);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_colors, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Colors colors = colorsList.get(position);
        holder.title.setText(colors.getTitle());
        Log.i("TAG", "获取的颜色 >>> " + colors.getColor());
        holder.cardView.setCardBackgroundColor(Color.parseColor(colors.getColor()));

        holder.itemView.setOnClickListener(view -> {
            Log.i("TAG", "被点击 >>>>>");
            itemClickListener.onClick(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return colorsList.size();
    }
}
