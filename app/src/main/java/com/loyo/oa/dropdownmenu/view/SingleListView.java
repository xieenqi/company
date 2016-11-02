package com.loyo.oa.dropdownmenu.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.loyo.oa.dropdownmenu.adapter.SingleSelectionAdapter;
import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class SingleListView extends RecyclerView {

    private SingleSelectionAdapter adapter;

    private List<MenuModel> data = new ArrayList<>();

    private OnMenuItemClick callback;

    public void setCallback(OnMenuItemClick callback) {
        this.callback = callback;
        this.adapter.setCallback(callback);
    }

    public SingleListView(Context context) {
        super(context);
        init(context);
    }

    public SingleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SingleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        setLayoutManager(new LinearLayoutManager(context));
        adapter = new SingleSelectionAdapter();
        setAdapter(adapter);
    }

    public void loadData(List<MenuModel> list) {
        data.clear();
        data.addAll(list);
        adapter.loadData(data);
        adapter.notifyDataSetChanged();
    }
}
