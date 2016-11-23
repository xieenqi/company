package com.loyo.oa.dropdownmenu.filtermenu.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.loyo.oa.dropdownmenu.callback.OnMenuButtonClick;
import com.loyo.oa.dropdownmenu.callback.OnMenuItemClick;
import com.loyo.oa.dropdownmenu.filtermenu.CommonMenuAdapter;
import com.loyo.oa.dropdownmenu.filtermenu.SingleSelectionAdapter;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class DoubleMenuView extends LinearLayout implements View.OnClickListener{
    private RecyclerView tagView;
    private RecyclerView tagItemView;
    private Button cancelBtn;
    private Button confirmBtn;

    private CommonMenuAdapter parentAdapter;
    private SingleSelectionAdapter itemAdapter;

    private FilterModel filterModel;

    private OnMenuButtonClick callback;

    private Map<String, MenuModel> selected = new HashMap<>();
    private Map<String, Integer> selectedIndex = new HashMap<>();

    public DoubleMenuView(Context context) {
        super(context);
        init(context);
    }

    public DoubleMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        parentAdapter = new CommonMenuAdapter();
        itemAdapter = new SingleSelectionAdapter();
        inflate(context, R.layout.layout_tag_menu_view, this);

        cancelBtn = (Button)findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        confirmBtn = (Button)findViewById(R.id.btn_confirm);
        confirmBtn.setOnClickListener(this);

        tagView = (RecyclerView)findViewById(R.id.tag_view);
        tagView.setBackgroundColor(0xfff4f4f4);
        tagView.setLayoutManager(new LinearLayoutManager(getContext()));
        tagItemView = (RecyclerView)findViewById(R.id.tag_item_view);
        tagItemView.setLayoutManager(new LinearLayoutManager(getContext()));
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        tagItemView.setItemAnimator(animator);

        tagView.setAdapter(parentAdapter);
        tagItemView.setAdapter(itemAdapter);

        parentAdapter.setCallback(new OnMenuItemClick() {
            @Override
            public void onMenuItemClick(int index) {
                itemAdapter.selectedIndex = selectedIndex.get(filterModel.getChildrenAtIndex(index).getKey());
                itemAdapter.loadData(filterModel.getChildrenAtIndex(index).getChildren());
            }
        });

        itemAdapter.setCallback(new OnMenuItemClick() {
            @Override
            public void onMenuItemClick(int index) {
                int parentIndex = parentAdapter.selectedIndex;
                List<MenuModel> parents = filterModel.getChildren();
                MenuModel parent = parents.get(parentIndex);
                selected.put(parent.getKey(), parent.getChildren().get(index));
                selectedIndex.put(parent.getKey(), index);
            }
        });
    }

    public void setFilterModel(FilterModel filterModel) {
        this.filterModel = filterModel;
        parentAdapter.loadData(filterModel.getChildren());
        if (filterModel.getChildrenCount() > 0) {
            itemAdapter.loadData(filterModel.getChildrenAtIndex(0).getChildren());
        }

        for (MenuModel model:filterModel.getChildren()) {
            MenuModel defaultChild = null;
            if (model.getChildren().size() >0) {
                defaultChild = model.getChildren().get(0);
            }
            selected.put(model.getKey(), defaultChild);
            selectedIndex.put(model.getKey(), 0);
        }
    }

    public void setCallback(OnMenuButtonClick callback) {
        this.callback = callback;
    }

    public List<MenuModel> getSelectedMenuModels() {
        List<MenuModel> result = new ArrayList<>();
        List<MenuModel> tags = filterModel.getChildren();
        for (MenuModel tag:tags) {
            List<MenuModel> tagItems = tag.getChildren();
            if (tagItems.size() > 0 && tagItems.get(0).getSelected()) {
                continue;
            }
            for (int i = 1; i < tagItems.size(); i++) {
                if (tagItems.get(i).getSelected()) {
                    result.add(tagItems.get(i));
                }
            }
        }

        return result;
    }

    public Map<String, MenuModel> getSelectedParams() {

        return selected;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:{
                reset();
                if (callback !=null) {
                    callback.onMenuResetClick();
                }
            }
            break;
            case R.id.btn_confirm: {
                if (callback !=null) {
                    callback.onMenuConfirmClick();
                }
            }
            break;
            default:
                break;
        }
    }

    private void reset() {
        for (MenuModel model:filterModel.getChildren()) {
            MenuModel defaultChild = null;
            if (model.getChildren().size() >0) {
                defaultChild = model.getChildren().get(0);
            }
            selected.put(model.getKey(), defaultChild);
            selectedIndex.put(model.getKey(), 0);
        }
        itemAdapter.selectedIndex = 0;
        itemAdapter.notifyDataSetChanged();
    }
}
