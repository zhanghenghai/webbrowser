package com.one.browser.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.one.browser.R;
import com.one.browser.entity.DialogWindow;
import java.util.List;


/**
 * @author 18517
 */
public class DialogPageAdapter extends RecyclerView.Adapter<DialogPageAdapter.ViewHolder> {
    private final List<DialogWindow> list;

    private ItemClickListener mItemClickListener;


    public interface ItemClickListener {
        /**
         * 被点击对象
         * @param position 对象
         * */
         void onItemClick(int position);

         /**
          * 点击删除
          * @param position 对象
          * */
         void onClick(int position);

    }

    /**
     * 列表点击事件
     * */
    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView pageWebsite;
        ImageView imageButtonDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.pageTitle);
            pageWebsite = itemView.findViewById(R.id.pageWebsite);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
        }
    }

    public DialogPageAdapter(List<DialogWindow> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mutli, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        DialogWindow dialogWindow = list.get(position);
        viewHolder.title.setText(dialogWindow.getTitle());
        viewHolder.pageWebsite.setText(dialogWindow.getUrl());
        viewHolder.imageButtonDelete.setOnClickListener(v -> mItemClickListener.onClick(position));
        viewHolder.itemView.setOnClickListener(v -> mItemClickListener.onItemClick(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}



