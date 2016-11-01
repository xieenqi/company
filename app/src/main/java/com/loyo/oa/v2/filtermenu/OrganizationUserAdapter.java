package com.loyo.oa.v2.filtermenu;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.dropdownmenu.view.SingleCell;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class OrganizationUserAdapter extends RecyclerView.Adapter<SingleCell> implements OnMenuItemClick{

    private MenuModel menuModel;

    private OnMenuItemClick callback;

    public int selectedIndex = -1;

    @Override
    public SingleCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return SingleCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(SingleCell holder, int position) {
        MenuModel model = menuModel.getChildrenAtIndex(position);
        holder.setIndex(position);
        holder.setCallback(this);
        holder.valueView.setText(model.getValue());
        holder.setSelected(selectedIndex == position);
    }
    @Override
    public int getItemCount() {
        if (menuModel== null) {
            return 0;
        }

        return menuModel.getChildrenCount();
    }

    public void loadModel(MenuModel menuModel) {
        this.menuModel = menuModel;
        notifyDataSetChanged();
    }

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }

    @Override
    public void onMenuItemClick(int index) {
        if (selectedIndex != index) {
            selectedIndex = index;
            notifyDataSetChanged();
        }

        if (this.callback != null) {
            this.callback.onMenuItemClick(index);
        }
    }
}
