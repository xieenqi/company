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
import com.loyo.oa.dropdownmenu.filtermenu.TagItemMenuAdapter;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class TagMenuView extends LinearLayout implements View.OnClickListener{
    private RecyclerView tagView;
    private RecyclerView tagItemView;
    private Button cancelBtn;
    private Button confirmBtn;

    private CommonMenuAdapter tagAdapter;
    private TagItemMenuAdapter tagItemAdapter;

    private FilterModel filterModel;

    private OnMenuButtonClick callback;

    public TagMenuView(Context context) {
        super(context);
        init(context);
    }

    public TagMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        tagAdapter = new CommonMenuAdapter();
        tagItemAdapter = new TagItemMenuAdapter();
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

        tagView.setAdapter(tagAdapter);
        tagItemView.setAdapter(tagItemAdapter);

        tagAdapter.setCallback(new OnMenuItemClick() {
            @Override
            public void onMenuItemClick(int index) {
                tagItemAdapter.loadData(filterModel.getChildrenAtIndex(index).getChildren());
            }
        });

        tagItemAdapter.setCallback(new OnMenuItemClick() {
            @Override
            public void onMenuItemClick(int index) {
            }
        });
    }

    public void setFilterModel(FilterModel filterModel) {
        this.filterModel = filterModel;
        tagAdapter.loadData(filterModel.getChildren());
        if (filterModel.getChildrenCount() > 0) {
            tagItemAdapter.loadData(filterModel.getChildrenAtIndex(0).getChildren());
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
        List<MenuModel> tags = filterModel.getChildren();
        for (MenuModel tag:tags) {
            resetTag(tag);
        }
        tagItemAdapter.notifyDataSetChanged();
    }

    private void resetTag(MenuModel tag) {
        List<MenuModel> tagItems = tag.getChildren();
        if (tagItems.size() > 0) {
            tagItems.get(0).setSelected(true);
        }
        for (int i = 1; i < tagItems.size(); i++) {
            tagItems.get(i).setSelected(false);
        }
    }

    public List<String> getSelectedTagIds() {
        List<String> result = new ArrayList<>();
        List<MenuModel> tags = filterModel.getChildren();
        for (MenuModel tag:tags) {
            List<MenuModel> tagItems = tag.getChildren();
            if (tagItems.size() > 0 && tagItems.get(0).getSelected()) {
                continue;
            }
            for (int i = 1; i < tagItems.size(); i++) {
                if (tagItems.get(i).getSelected()) {
                    result.add(tagItems.get(i).getKey());
                }
            }
        }

        return result;
    }

    public String getSelectedTagParams() {
        StringBuffer buffer = new StringBuffer();
        List<MenuModel> tags = filterModel.getChildren();
        int tagCount = 0;
        for (MenuModel tag:tags) {
            List<MenuModel> tagItems = tag.getChildren();
            if (tagItems.size() > 0 && tagItems.get(0).getSelected()) {
                continue;
            }
            int count = 0;
            for (int i = 1; i < tagItems.size(); i++) {
                if (tagItems.get(i).getSelected()) {
                    if (count == 0) {
                        if (tagCount != 0) {
                            buffer.append("|");
                        }
                        buffer.append(tag.getKey());
                        buffer.append("/");
                        tagCount++;
                    }
                    else {
                        buffer.append(",");
                    }
                    buffer.append(tagItems.get(i).getKey());
                    count++;
                }
            }
        }

        return buffer.toString();
    }
}
