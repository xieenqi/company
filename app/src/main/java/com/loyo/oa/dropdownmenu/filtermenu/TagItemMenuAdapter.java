package com.loyo.oa.dropdownmenu.filtermenu;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.dropdownmenu.view.MultiCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class TagItemMenuAdapter extends RecyclerView.Adapter<MultiCell> implements OnMenuItemClick {
    private List<MenuModel> data = new ArrayList<>();
    public int selectedIndex = 0;

    private OnMenuItemClick callback;

    @Override
    public MultiCell onCreateViewHolder(ViewGroup parent, int viewType) {
        return MultiCell.instance(parent);
    }

    @Override
    public void onBindViewHolder(MultiCell holder, int position) {
        MenuModel model = data.get(position);
        holder.setIndex(position);
        holder.setCallback(this);
        holder.valueView.setText(model.getValue());
        holder.setSelected(model.getSelected());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onMenuItemClick(int index) {
//        if (selectedIndex != index) {
//            selectedIndex = index;
//            notifyDataSetChanged();
////            if (this.callback != null) {
////                this.callback.onMenuItemClick(index);
////            }
//        }
        MenuModel model = data.get(index);
        model.setSelected(! model.getSelected());

        if (isAllSelected()) {
            selectAll();
        }
        else {
            deSelectAll();
        }

        notifyDataSetChanged();
    }

    public void loadData(List<MenuModel> list) {
        data.clear();
        data.addAll(list);
        if (isAllSelected()) {
            selectAll();
        }
        else {
            deSelectAll();
        }
        notifyDataSetChanged();
    }

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }

    private boolean isAllSelected() {
        if (data.size()<=1) {
            return true;
        }
        boolean result = data.get(1).getSelected();
        for (int i = 2; i < data.size(); i++) {
            if (result != data.get(i).getSelected()) {
                return false;
            }
        }
        return true;
    }

    private void selectAll() {
        if (data.size() > 0) {
            data.get(0).setSelected(true);
        }
        for (int i = 1; i < data.size(); i++) {
            data.get(i).setSelected(false);
        }
    }

    private void deSelectAll() {
        if (data.size() > 0) {
            data.get(0).setSelected(false);
        }
    }
}
