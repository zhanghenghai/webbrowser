package com.one.browser.adapter;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.one.browser.R;
import com.one.browser.sqlite.Bookmark;
import com.one.browser.sqlite.Script;
import com.one.browser.sqlite.ScriptDao;

import java.util.List;

/**
 * @author 18517
 */
public class ScriptAdapter extends RecyclerView.Adapter<ScriptAdapter.ViewHolder> {

    private final List<Script> scriptList;

    private ItemClickListener itemClickListener;

    private final String FORBIDDEN = "0";


    public ScriptAdapter(List<Script> scriptList) {
        Log.i("TAG", "BookAdapter: >>> 构造方法 ");
        this.scriptList = scriptList;
    }

    public interface ItemClickListener {
        /**
         * 点击事件
         *
         * @param name    脚本名称
         * @param content 脚本内容
         */
        void onItemClick(String name, String content);
        /**
         * 长按
         *
         * @param view     视图
         * @param position 下标
         */
        void onItemLongClick(View view, int position);
        /**
         * 状态
         * @param position  下标
         * @param b 真假
         * */
        void onChecked(int position,boolean b);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_script, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Script script = scriptList.get(position);
        holder.title.setText(script.getTitle());
        if (FORBIDDEN.equals(script.getState())) {
            // 禁用
            holder.switchMaterial.setChecked(false);
        } else {
            // 启动
            holder.switchMaterial.setChecked(true);
        }
        // 点击事件
        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onItemClick(script.getTitle(), script.getScript());
        });
        // 长按事件
        holder.itemView.setOnLongClickListener(view -> {
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemLongClick(view, position);
                return true;
            }
            return false;
        });
        // 状态修改
        holder.switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
            holder.switchMaterial.setChecked(b);
            itemClickListener.onChecked(position,b);
        });
    }

    @Override
    public int getItemCount() {
        return scriptList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        private SwitchMaterial switchMaterial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.script_title);
            switchMaterial = itemView.findViewById(R.id.script_switch);
        }
    }
}
