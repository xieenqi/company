package com.loyo.oa.v2.activityui.followup.common;

import com.loyo.oa.dropdownmenu.filtermenu.TagItemMenuModel;
import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.customer.model.TagItem;
import com.loyo.oa.v2.activityui.followup.common.FollowFilter;
import com.loyo.oa.v2.activityui.followup.common.FollowFilterItem;
import com.loyo.oa.v2.activityui.other.model.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class FollowFilterMenuModel implements MenuModel {

    public static FilterModel getFilterModel(List<FollowFilter> filters) {
        List<MenuModel> filterModel = new ArrayList<>();
        for (FollowFilter filter:filters) {
            filterModel.add(new FollowFilterMenuModel(filter));
        }
        return new FilterModel(filterModel, "筛选", MenuListType.DOUBLE_LIST_MULTI_SEL);
    }

    private FollowFilter filter;
    private boolean isSelected;

    private List<MenuModel> children = new ArrayList<>();

    public FollowFilterMenuModel(FollowFilter filter) {
        this.filter = filter;
        List<FollowFilterItem> items = filter.items;
        for (FollowFilterItem item:items) {
            FollowFilterItemMenuModel model = new FollowFilterItemMenuModel(item);
            children.add(model);
        }
    }


    @Override
    public String getKey() {
        return filter.name;
    }

    @Override
    public String getValue() {
        return filter.name;
    }

    @Override
    public boolean getSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public List<MenuModel> getChildren() {
        return children;
    }

    @Override
    public MenuModel getChildrenAtIndex(int index) {
        return children.get(index);
    }

    @Override
    public int getChildrenCount() {
        return children.size();
    }
}


