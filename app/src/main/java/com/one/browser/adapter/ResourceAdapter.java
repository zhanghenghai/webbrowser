package com.one.browser.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.one.browser.R;
import com.one.browser.entity.Resource;

import java.util.List;


/**
 * @author 18517
 */
public class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {

    private List<Resource> list;

    private ItemClickListener mItemClickListener;

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener ;
    }

    public void addList(){
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView itemPixelW;
        TextView itemPixelH;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            itemPixelW = itemView.findViewById(R.id.item_pixel_w);
            itemPixelH = itemView.findViewById(R.id.item_pixel_h);
        }
    }

    public ResourceAdapter(List<Resource> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resource,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = list.get(position);
        holder.title.setText(resource.getTitle());
        holder.itemPixelW.setText(resource.getW()+"");
        holder.itemPixelH.setText(resource.getH()+"");

        holder.itemView.setOnClickListener(v -> {
            System.out.println(" ... "+list.get(position).getTitle());
            mItemClickListener.onItemClick(position);
        });

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

}



