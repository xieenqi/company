package com.loyo.oa.v2.filtermenu;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.filtermenu.view.DepartmentMenuCell;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class OrganizationDepartmentAdpater extends RecyclerView.Adapter<DepartmentMenuCell> implements OnMenuItemClick {

    private OrganizationFilterModel filterModel;
    public int selectedIndex = 0;

    private OnMenuItemClick callback;

    @Override
    public DepartmentMenuCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return DepartmentMenuCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(DepartmentMenuCell holder, int position) {
        MenuModel model = filterModel.getChildrenAtIndex(position);
        holder.setIndex(position);
        holder.setCallback(this);
        holder.valueView.setText(model.getValue());
        holder.setSelected(selectedIndex == position);
    }

    @Override
    public int getItemCount() {
        if (filterModel == null) {
            return 0;
        }

        return filterModel.getChildrenCount();
    }

    @Override
    public void onMenuItemClick(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            notifyDataSetChanged();
            if (this.callback != null) {
                this.callback.onMenuItemClick(index);
            }
        }
    }

    public void setFilterModel(OrganizationFilterModel model) {
        this.filterModel = model;
        notifyDataSetChanged();
    }

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }
}
