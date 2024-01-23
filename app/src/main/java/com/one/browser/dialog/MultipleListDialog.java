package com.one.browser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.one.browser.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author 18517
 */
public class MultipleListDialog extends Dialog {

    public MultipleListDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    public interface OnClickListener {
        /**
         * 点击事件
         */
        void onClick(ArrayList<Boolean> checkedPositions);

    }

    public static class Builder {
        // 设置弹窗
        private final MultipleListDialog scriptDialog;
        // 视图
        private final View view;

        private OnClickListener onClickListener;

        private ArrayList<Boolean> checkedPositions;


        public Builder(Context context, String[] data) {
            checkedPositions = new ArrayList<>(Collections.nCopies(data.length, false));
            // 弹窗页面
            scriptDialog = new MultipleListDialog(context, R.style.custom_dialog);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 加载布局
            view = inflater.inflate(R.layout.dialog_multiple_list, null, false);
            // 添加布局到弹窗中
            scriptDialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 选择列表
            ListView listView = view.findViewById(R.id.list_dialog);
            // 确定
            TextView confirm = view.findViewById(R.id.custom_confirm);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item_multiple_layout, R.id.itemTextView, data) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    CheckBox checkBox = view.findViewById(R.id.itemCheckBox);
                    checkBox.setChecked(checkedPositions.get(position));
                    // 设置CheckBox事件
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        checkedPositions.set(position, isChecked);

                    });

                    return view;
                }
            };
            listView.setAdapter(adapter);
            confirm.setOnClickListener(v -> {
                onClickListener.onClick(checkedPositions);
                scriptDialog.dismiss();
            });
            // 设置默认选中项
//            listView.setItemChecked(selectedPosition, true);
            // 点击事件
//            listView.setOnItemClickListener((parent, view, position, id) -> {
//                // 更新选中项位置
//                selectedPosition = position;
//                // 通知适配器数据发生变化，以便更新列表项的显示状态
//                adapter.notifyDataSetChanged();
//                // 在这里处理点击事件
//                String selectedItem = data[position];
//                Toast.makeText(context, "你点击了：" + selectedItem, Toast.LENGTH_SHORT).show();
//                onClickListener.onClick(parent, view, position, id);
//
//                scriptDialog.dismiss();
//            });
            // 设置ListView的选择模式为单选
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

//        public void setSelectedPosition(int selectedPosition) {
//            this.selectedPosition = selectedPosition;
//        }


        public MultipleListDialog create() {
            // 放入试图
            scriptDialog.setContentView(view);
            scriptDialog.setCancelable(true);
            scriptDialog.setCanceledOnTouchOutside(true);
            return scriptDialog;
        }
    }
}
