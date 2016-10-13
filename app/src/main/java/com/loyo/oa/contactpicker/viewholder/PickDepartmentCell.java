package com.loyo.oa.contactpicker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.contactpicker.callback.OnDepartmentSelected;
import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class PickDepartmentCell extends RecyclerView.ViewHolder {

    private TextView contentView;
    private OnDepartmentSelected<PickDepartmentCell> callback;
    private int index;

    private PickDepartmentCell(View itemView) {
        super(itemView);
        contentView = (TextView) itemView.findViewById(R.id.content_view);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    setSelected(true);
                    callback.onDepartmentSelected(PickDepartmentCell.this,
                            PickDepartmentCell.this.index);
                }
            }
        });
    }

    public static PickDepartmentCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pick_department_cell, parent, false);
        return new PickDepartmentCell(itemView);
    }

    public void setName(String text, boolean level1) {
        contentView.setText(text);
        if (level1) {
            contentView.setTextColor(contentView.getContext().getResources().getColor(R.color.text33));
        }
        else {
            contentView.setTextColor(contentView.getContext().getResources().getColor(R.color.text99));
        }
    }

    public void setCallback(OnDepartmentSelected<PickDepartmentCell> callback) {
        this.callback = callback;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSelected(boolean selected) {
        itemView.setSelected(selected);
    }
}
