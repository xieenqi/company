package com.loyo.oa.v2.activityui.followup.common;

import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.customer.model.TagItem;

import java.util.List;

/**
 * Created by xeq on 16/11/18.
 */

public class FollowFilterItemMenuModel implements MenuModel {

    private FollowFilterItem item;
    private boolean isSelected;

    public FollowFilterItemMenuModel(FollowFilterItem item) {
        this.item = item;
    }
    @Override
    public String getKey() {
        return item.value;
    }

    @Override
    public String getValue() {
        return item.name;
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
        return null;
    }

    @Override
    public MenuModel getChildrenAtIndex(int index) {
        return null;
    }

    @Override
    public int getChildrenCount() {
        return 0;
    }
}
