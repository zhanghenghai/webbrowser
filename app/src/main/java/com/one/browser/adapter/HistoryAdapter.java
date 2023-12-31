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
import com.one.browser.entity.GroupedHistoryData;
import com.one.browser.sqlite.History;

import java.util.List;

/**
 * @author 18517
 */
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GroupedHistoryData> groups;

    private ItemClickListener itemClickListener;

    private static final int VIEW_TYPE_GROUP = 0;
    private static final int VIEW_TYPE_ITEM = 1;


    public interface ItemClickListener {
        /**
         * 点击事件
         *
         * @param url 链接
         */
        void onItemClick(String url);

        /**
         * 长按事件
         *
         * @param view           视图
         * @param groupPosition  分组位置
         * @param itemPosition   组内项目位置
         */
        void onItemLongClick(View view,int position, int newPosition,int groupPosition, int itemPosition);

    }

    public HistoryAdapter(List<GroupedHistoryData> groups) {
        Log.i("TAG", "BookAdapter: >>> 构造方法 ");
        this.groups = groups;
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            GroupedHistoryData group = getGroup(position);
            ((GroupViewHolder) holder).dateView.setText(group.getDate());
        } else if (holder instanceof ItemViewHolder) {
            History item = getItem(position);
            if (item.getIcon() != null && !item.getIcon().trim().isEmpty()) {
                ((ItemViewHolder) holder).icon.setImageBitmap(BitmapFactory.decodeFile(item.getIcon()));
            } else {
                Log.i("TAG", "显示本地图片 >>> ");
                ((ItemViewHolder) holder).icon.setImageResource(R.drawable.ic_nv_time_line);
            }
            // 标题显示
            ((ItemViewHolder) holder).title.setText(item.getTitle());
            holder.itemView.setOnClickListener(view -> {
                // 点击事件触发时，删除数据项
                itemClickListener.onItemClick(item.getUrl());
            });
            holder.itemView.setOnLongClickListener(view -> {
                Log.i("TAG", "长按触发 》》》 " + position);
                if (position != RecyclerView.NO_POSITION) {
                    int[] positions = getPositionInGroup(position);
                    int groupPosition = positions[0];
                    int itemPosition = positions[1];
                    Log.i("TAG", "第 >>>  : " + groupPosition + " <<< 组");
                    Log.i("TAG", "itemPosition : " + itemPosition);
                    if (groupPosition != -1 && itemPosition != -1) {
                        int newPosition = position - groupPosition - 1;
                        Log.i("TAG", " groupPosition >>> " + groupPosition + " <<< itemPosition >>> " + itemPosition);
                        itemClickListener.onItemLongClick(view,position,newPosition,groupPosition, itemPosition);
                    }
                    return true;
                }
                return false;
            });
        }
    }


    private int[] getPositionInGroup(int position) {
        int[] positions = new int[2];
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            count++; // 分组标题
            if (position < count) {
                positions[0] = i;
                // 点击的是分组标题
                positions[1] = -1;
                return positions;
            }
            // 项目数量
            count += groups.get(i).getHistoryList().size();
            if (position < count) {
                positions[0] = i;
                positions[1] = position - (count - groups.get(i).getHistoryList().size());
                return positions;
            }
        }
        positions[0] = -1;
        positions[1] = -1;
        return positions;
    }


    @Override
    public int getItemCount() {
        int count = 0;
        for (GroupedHistoryData group : groups) {
            count += group.getHistoryList().size() + 1;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (GroupedHistoryData group : groups) {
            if (position == count) {
                return VIEW_TYPE_GROUP;
            }
            // 分组标题
            count++;
            if (position < count + group.getHistoryList().size()) {
                return VIEW_TYPE_ITEM;
            }
            // 项目数量
            count += group.getHistoryList().size();
        }
        return super.getItemViewType(position);
    }

    private GroupedHistoryData getGroup(int position) {
        int count = 0;
        for (GroupedHistoryData group : groups) {
            if (position == count) {
                return group;
            }
            // 分组标题
            count++;
            // 项目数量
            count += group.getHistoryList().size();
        }
        return null;
    }

    private History getItem(int position) {
        int count = 0;
        for (GroupedHistoryData group : groups) {
            // 分组标题
            count++;
            if (position < count + group.getHistoryList().size()) {
                return group.getHistoryList().get(position - count);
            }
            // 项目数量
            count += group.getHistoryList().size();
        }
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_GROUP) {
            View view = inflater.inflate(R.layout.item_history_date, parent, false);
            return new GroupViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_history, parent, false);
            return new ItemViewHolder(view);
        }
    }


    static class GroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.history_date);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView title;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.history_image);
            title = itemView.findViewById(R.id.history_title);
        }
    }


}