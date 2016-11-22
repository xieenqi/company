package com.loyo.oa.dropdownmenu.filtermenu;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.filtermenu.view.CommonMenuCell;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.dropdownmenu.view.SingleCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class SingleSelectionAdapter extends RecyclerView.Adapter<SingleCell> implements OnMenuItemClick {

    private List<MenuModel> data = new ArrayList<>();
    public int selectedIndex = 0;

    private OnMenuItemClick callback;

    @Override
    public SingleCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return SingleCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(SingleCell holder, int position) {
        MenuModel model = data.get(position);
        holder.setIndex(position);
        holder.setCallback(this);
        holder.valueView.setText(model.getValue());
        holder.setSelected(selectedIndex == position);
    }

    @Override
    public int getItemCount() {
        return data.size();
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

    public void loadData(List<MenuModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }


    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }
}
