package com.loyo.oa.dropdownmenu.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.common.view.AnimCheckBox;
import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class MultiCell extends RecyclerView.ViewHolder {

    public TextView valueView;
    public View line;
    public AnimCheckBox checkBox;
    private int index;

    private OnMenuItemClick callback;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }

    public OnMenuItemClick getCallback() {
        return callback;
    }

    private MultiCell(View itemView) {
        super(itemView);
        valueView = (TextView) itemView.findViewById(R.id.value_view);
        line = itemView.findViewById(R.id.separate_line);
        checkBox = (AnimCheckBox)itemView.findViewById(R.id.checkbox);
        checkBox.setClickable(false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onMenuItemClick(index);
                }
            }
        });
    }

    public static MultiCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_multi_cell, parent, false);
        return new MultiCell(itemView);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            line.setBackgroundColor(0xFF008DF2);
            valueView.setTextColor(0xFF008DF2);
            checkBox.setChecked(selected, false);
        }
        else {
            line.setBackgroundColor(0xffe6e6e6);
            valueView.setTextColor(0xff333333);
            checkBox.setChecked(selected, false);
        }
    }

    public void setSelected(boolean selected, boolean anim) {
        if (selected) {
            line.setBackgroundColor(0xFF008DF2);
            valueView.setTextColor(0xFF008DF2);
            checkBox.setChecked(selected, anim);
        }
        else {
            line.setBackgroundColor(0xffe6e6e6);
            valueView.setTextColor(0xff333333);
            checkBox.setChecked(selected, anim);
        }
    }
}
