package com.one.browser.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.one.browser.R;
import com.one.browser.entity.GroupedDownload;
import com.one.browser.sqlite.Download;

import java.util.List;

/**
 * @author 18517
 */
public class DownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ItemClickListener itemClickListener;

    private final List<Download> downloadList;

    private static final int DATE = 0;
    private static final int PHOTO = 1;


    public DownloadAdapter(List<Download> downloadList) {
        Log.i("TAG", "DownloadAdapter: >>> 构造方法 ");
        this.downloadList = downloadList;
    }

    public interface ItemClickListener {
        /**
         * 点击事件
         *
         * @param name 文件名称
         * @param path 文件路径
         * @param mime 文件类型
         */
        void onClick(String name, String path, String mime);

        /**
         * 长按事件
         *
         * @param view     视图
         * @param position 下标
         */
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Download download = downloadList.get(position);
        Log.i("TAG", "history: 获得的数据 >>>" + download.getTitle());
        ((ViewHolder) holder).icon.setImageResource(R.drawable.download_line);
        ((ViewHolder) holder).title.setText(download.getTitle());
        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onClick(download.getTitle(), download.getPath(), download.getMime());
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemLongClick(view, position);
                return true;
            }
            return false;
        });
    }

    /**
     * 返回所有分组中的数据项总数
     */
    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.download_image);
            title = itemView.findViewById(R.id.download_title);
        }
    }
}
