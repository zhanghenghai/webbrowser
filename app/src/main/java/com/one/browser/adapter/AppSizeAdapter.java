package com.one.browser.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.one.browser.R;
import com.one.browser.entity.Resource;

import java.util.List;


/**
 * @author 18517
 */
public class AppSizeAdapter extends RecyclerView.Adapter<AppSizeAdapter.ViewHolder> {

    private List<Resource> list;

    private ItemClickListener mItemClickListener;


    public interface ItemClickListener {

        /**/
        void onClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    private int mPosition;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int Position) {
        this.mPosition = Position;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView itemPixelW;
        TextView itemPixelH;
        LinearLayout select_certificate;
        View views;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            itemPixelW = itemView.findViewById(R.id.item_pixel_w);
            itemPixelH = itemView.findViewById(R.id.item_pixel_h);
            select_certificate = itemView.findViewById(R.id.select_certificate);
            views = itemView.findViewById(R.id.vi);
        }
    }

    public AppSizeAdapter(List<Resource> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_size, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = list.get(position);
        holder.title.setText(resource.getTitle());
        holder.itemPixelW.setText(resource.getW() + "");
        holder.itemPixelH.setText(resource.getH() + "");


        holder.itemView.setOnClickListener(v -> {
            System.out.println("AppSize >>> " + list.get(position).getTitle());
            mItemClickListener.onClick(position);
            notifyDataSetChanged();
        });

        if (position == getPosition()) {
            holder.views.setVisibility(View.VISIBLE);
        } else {
            // 否则的话就全白色初始化背景
            holder.views.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}