package com.loyo.oa.dropdownmenu.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.dropdownmenu.view.SingleCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class SingleSelectionAdapter extends RecyclerView.Adapter<SingleCell> implements OnMenuItemClick{

    private List<MenuModel> data = new ArrayList<>();
    private OnMenuItemClick callback;

    public void loadData(List<MenuModel> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
    }

    public OnMenuItemClick getCallback() {
        return callback;
    }

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
        holder.setSelected(model.getSelected());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onMenuItemClick(int index) {

        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelected(i == index);
        }
        notifyDataSetChanged();

        if (this.callback != null) {
            this.callback.onMenuItemClick(index);
        }
    }
}
