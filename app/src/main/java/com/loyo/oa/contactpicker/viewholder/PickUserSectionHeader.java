package com.loyo.oa.contactpicker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class PickUserSectionHeader extends RecyclerView.ViewHolder {

    private TextView contentView;

    private PickUserSectionHeader(View itemView) {
        super(itemView);
        contentView = (TextView) itemView;
    }

    public static PickUserSectionHeader instance(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pick_user_header, parent, false);

        return new PickUserSectionHeader(itemView);
    }

    public void setText(String text) {
        contentView.setText(text);
    }
}
