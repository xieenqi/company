package com.loyo.oa.contactpicker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.contactpicker.callback.OnUserItemClicked;
import com.loyo.oa.contactpicker.model.PickUserModel;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.GlideManager;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class PickUserCell extends RecyclerView.ViewHolder {

    private TextView  nameView;
    private TextView  departmentView;
    private ImageView avatarView;
    private CheckBox  checkBox;
    private View      container;

    private PickUserModel model;

    private OnUserItemClicked<PickUserCell> callback;
    private int index;

    private PickUserCell(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.name_view);
        departmentView = (TextView) itemView.findViewById(R.id.department_view);
        avatarView = (ImageView) itemView.findViewById(R.id.avatar_view);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        container = itemView.findViewById(R.id.container);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    setSelected(!model.isSelected());
                    callback.onUserItemClicked(PickUserCell.this,
                            PickUserCell.this.index);
                }
            }
        });
    }

    public static PickUserCell instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pick_user_cell, parent, false);
        return new PickUserCell(itemView);
    }

    public void setModel(PickUserModel model) {
        this.model = model;
        nameView.setText(model.getName());
        departmentView.setText(model.user.shortDeptNames);
        if (model.getAvatar() != null)
        {
            GlideManager.getInstance().manager().load(model.getAvatar()).into(avatarView);
        }
        setSelected(model.isSelected());
    }

    public void setCallback(OnUserItemClicked<PickUserCell> callback) {
        this.callback = callback;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setSelected(boolean selected) {
        checkBox.setSelected(selected);
    }
}
