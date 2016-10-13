package com.loyo.oa.contactpicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.loyo.oa.contactpicker.model.PickUserModel;
import com.loyo.oa.contactpicker.model.UserModel;
import com.loyo.oa.contactpicker.viewholder.PickUserCell;
import com.loyo.oa.contactpicker.viewholder.PickUserSectionHeader;
import com.loyo.oa.indexablelist.adapter.BaseAdapter;
import com.loyo.oa.indexablelist.adapter.expand.StickyRecyclerHeadersAdapter;
import com.loyo.oa.indexablelist.widget.IndexAdapter;

import java.util.List;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class PickUserAdapter extends BaseAdapter<PickUserModel,PickUserCell>
        implements StickyRecyclerHeadersAdapter<PickUserSectionHeader>,IndexAdapter
{

    private Context mContext;
    private List<UserModel> list;
    List<PickUserModel> data;

    public PickUserAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void loadData(List<PickUserModel> data) {
        this.data = data;
        this.clear();
        this.addAll(data);
    }

    @Override
    public PickUserCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return PickUserCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(PickUserCell holder, int position) {
        holder.setModel(getItem(position));
    }


    /**
     *
     */
    @Override
    public long getHeaderId(int position) {
        return getItem(position).user.getSortLetter().charAt(0);
    }

    @Override
    public PickUserSectionHeader onCreateHeaderViewHolder(ViewGroup parent) {
        return PickUserSectionHeader.instance(parent);
    }

    @Override
    public void onBindHeaderViewHolder(PickUserSectionHeader holder, int position) {
        holder.setText(getItem(position).getIndex());
    }
}
