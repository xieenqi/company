package com.loyo.oa.dropdownmenu.filtermenu.view;

import android.graphics.Color;
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

public class CommonMenuCell extends RecyclerView.ViewHolder {
    public TextView valueView;
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

    private CommonMenuCell(View itemView) {
        super(itemView);
        valueView = (TextView) itemView.findViewById(R.id.value_view);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onMenuItemClick(index);
                }
            }
        });
    }

    public static CommonMenuCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_common_cell, parent, false);
        return new CommonMenuCell(itemView);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            valueView.setBackgroundColor(Color.WHITE);
        }
        else {
            valueView.setBackgroundColor(0xfff4f4f4);
        }
    }
}
