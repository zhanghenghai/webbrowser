package com.one.browser.adapter;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.one.browser.R;
import com.one.browser.sqlite.Bookmark;

import java.util.List;

/**
 * @author 18517
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private final List<Bookmark> bookmarkList;

    private ItemClickListener itemClickListener;

    public BookAdapter(List<Bookmark> bookmarkList) {
        Log.i("TAG", "BookAdapter: >>> 构造方法 ");
        this.bookmarkList = bookmarkList;
    }

    public interface ItemClickListener{
        /**
         * 点击事件
         * @param url 打开链接
         * */
        void onItemClick(String url);
        /**
         * 长按事件
         * @param view 视图
         * @param position 下标
         * */
        void onItemLongClick(View view, int position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        Log.i("TAG", "书签图标: " + bookmark.getIcon());
        if (bookmark.getIcon() != null && !bookmark.getIcon().trim().isEmpty()) {
            holder.icon.setImageBitmap(BitmapFactory.decodeFile(bookmark.getIcon()));
        }else {
            holder.icon.setImageResource(R.drawable.bookmark_2_line);
        }

        holder.title.setText(bookmark.getTitle());
        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onItemClick(bookmark.getUrl());
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemLongClick(view, position);
                return true;
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView icon;
        private final TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.bookmark_image);
            title = itemView.findViewById(R.id.bookmark_title);
        }
    }
}
