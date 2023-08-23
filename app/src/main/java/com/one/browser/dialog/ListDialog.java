package com.one.browser.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.one.browser.R;

/**
 * @author 18517
 */
public class ListDialog extends Dialog {

    public ListDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    public interface OnClickListener {
        /**
         * 点击事件
         *
         * @param parent   数据
         * @param view     视图
         * @param position 下标
         * @param id       不知道是啥
         */
        void onClick(AdapterView<?> parent, View view, int position, long id);

    }

    public static class Builder {
        // 初始未选中任何项
        private int selectedPosition = 0;
        // 设置弹窗
        private final ListDialog scriptDialog;
        // 视图
        private final View view;

        private OnClickListener onClickListener;


        public Builder(Context context, String[] data) {
            // 弹窗页面
            scriptDialog = new ListDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局
            view = inflater.inflate(R.layout.dialog_list, null, false);
            // 添加布局到弹窗中
            scriptDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 选择列表
            ListView listView = view.findViewById(R.id.list_dialog);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item_layout, R.id.itemTextView, data) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ImageView imageView = view.findViewById(R.id.itemImage);
                    if (position == selectedPosition) {
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                    return view;
                }
            };
            listView.setAdapter(adapter);
            // 设置默认选中项
            listView.setItemChecked(selectedPosition, true);
            // 点击事件
            listView.setOnItemClickListener((parent, view, position, id) -> {
                // 更新选中项位置
                selectedPosition = position;
                // 通知适配器数据发生变化，以便更新列表项的显示状态
                adapter.notifyDataSetChanged();
                // 在这里处理点击事件
                String selectedItem = data[position];
                Toast.makeText(context, "你点击了：" + selectedItem, Toast.LENGTH_SHORT).show();
                onClickListener.onClick(parent, view, position, id);

                scriptDialog.dismiss();
            });
            // 设置ListView的选择模式为单选
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void setSelectedPosition(int selectedPosition) {
            this.selectedPosition = selectedPosition;
        }


        public ListDialog create() {
            // 放入试图
            scriptDialog.setContentView(view);
            scriptDialog.setCancelable(true);
            scriptDialog.setCanceledOnTouchOutside(true);
            return scriptDialog;
        }
    }
}
