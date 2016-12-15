package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xeq on 16/12/12.
 */

public class DashboardSortTypeMenuModel implements MenuModel {
    public DashboardSortType type;
    private boolean isSelected;

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.AUGMENTER_BID_SMALL));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.AUGMENTER_SMALL_BIG));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.STOCK_BID_SMALL));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.STOCK_SMALL_BIG));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public DashboardSortTypeMenuModel(DashboardSortType type) {
        this.type = type;
    }

    @Override
    public String getKey() {
        return type.key;
    }

    @Override
    public String getValue() {
        return type.value;
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
