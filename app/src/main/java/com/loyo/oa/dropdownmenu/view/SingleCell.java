package com.loyo.oa.dropdownmenu.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class SingleCell extends RecyclerView.ViewHolder {

    public TextView valueView;
    public View line;
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

    private SingleCell(View itemView) {
        super(itemView);
        valueView = (TextView) itemView.findViewById(R.id.value_view);
        line = itemView.findViewById(R.id.separate_line);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onMenuItemClick(index);
                }
            }
        });
    }

    public static SingleCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_single_cell, parent, false);
        return new SingleCell(itemView);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            line.setBackgroundColor(0xFF008DF2);
            valueView.setTextColor(0xFF008DF2);
        }
        else {
            line.setBackgroundColor(0xffe6e6e6);
            valueView.setTextColor(0xff333333);
        }
    }
}
