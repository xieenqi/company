package com.loyo.oa.contactpicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.contactpicker.callback.OnDepartmentSelected;
import com.loyo.oa.contactpicker.model.PickDepartmentModel;
import com.loyo.oa.contactpicker.viewholder.PickDepartmentCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class PickDepartmentAdapter extends RecyclerView.Adapter<PickDepartmentCell> implements OnDepartmentSelected<PickDepartmentCell> {

    private Context mContext;
    private List<PickDepartmentModel> data = new ArrayList<>();
    private int selectedDepartmentIndex = 0;
    private OnDepartmentSelected<PickDepartmentCell> callback;

    public PickDepartmentAdapter(@NonNull Context context) {
        mContext = context;
    }

    public void clearData() {
        this.data.clear();
    }

    public void addData(List<PickDepartmentModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public PickDepartmentCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return PickDepartmentCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(PickDepartmentCell holder, int position) {
        holder.setIndex(position);
        holder.setCallback(this);
        holder.setName(data.get(position).getName(), data.get(position).isLevel1());
        holder.setSelected(position == selectedDepartmentIndex);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setCallback(OnDepartmentSelected<PickDepartmentCell> callback) {
        this.callback = callback;
    }

    @Override
    public void onDepartmentSelected(PickDepartmentCell object, int index) {
        if (index == selectedDepartmentIndex) {
            return;
        }

        notifyItemChanged(index);
        notifyItemChanged(selectedDepartmentIndex);
        selectedDepartmentIndex = index;

        if (this.callback != null) {
            this.callback.onDepartmentSelected(object, index);
        }
    }
}
