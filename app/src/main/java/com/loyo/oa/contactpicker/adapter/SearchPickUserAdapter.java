package com.loyo.oa.contactpicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.loyo.oa.contactpicker.callback.OnPickUserEvent;
import com.loyo.oa.contactpicker.callback.OnUserItemClicked;
import com.loyo.oa.contactpicker.model.PickDepartmentModel;
import com.loyo.oa.contactpicker.model.PickUserModel;
import com.loyo.oa.contactpicker.viewholder.PickUserCell;
import com.loyo.oa.contactpicker.viewholder.PickUserSectionHeader;
import com.loyo.oa.indexablelist.adapter.BaseAdapter;
import com.loyo.oa.indexablelist.adapter.expand.StickyRecyclerHeadersAdapter;
import com.loyo.oa.indexablelist.widget.IndexAdapter;

import java.util.List;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class SearchPickUserAdapter extends BaseAdapter<PickUserModel,PickUserCell>
        implements StickyRecyclerHeadersAdapter<PickUserSectionHeader>,IndexAdapter, OnUserItemClicked<PickUserCell>
{

    private Context mContext;
    private List<PickUserModel> data;
    private PickDepartmentModel department;
    private boolean isDepartmentSelected;
    private OnPickUserEvent callback;

    public SearchPickUserAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void loadData(List<PickUserModel> data) {
        this.data = data;
        this.clear();
        this.addAll(data);
    }

    public void setDepartment(PickDepartmentModel department) {
        this.department = department;
    }

    public void setDepartmentAllSelected(boolean isDepartmentSelected) {
        this.isDepartmentSelected = isDepartmentSelected;
    }

    public boolean getDepartmentAllSelected() {
        return this.isDepartmentSelected;
    }

    public void setCallback(OnPickUserEvent callback) {
        this.callback = callback;
    }

    @Override
    public PickUserCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return PickUserCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(PickUserCell holder, int position) {
        holder.setModel(getItem(position));
        holder.setIndex(position);
        holder.setCallback(this);
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

    @Override
    public void onUserItemClicked(PickUserCell object, int index) {
        PickUserModel model = getItem(index);
        model.setSelected(! model.isSelected());
        if (callback == null) {
            return;
        }
        if (model.isSelected()) {
            callback.onAddUser(getItem(index));
        }
        else {
            callback.onDeleteUser(getItem(index));
        }
    }
}
