package com.loyo.oa.v2.activityui.work.common;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class WorkReportCategoryMenuModel implements MenuModel {

    public WorkReportCategory type;
    private boolean isSelected;

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new WorkReportCategoryMenuModel(WorkReportCategory.ALL));
        list.add(new WorkReportCategoryMenuModel(WorkReportCategory.DAY_REPORT));
        list.add(new WorkReportCategoryMenuModel(WorkReportCategory.WEEK_REPORT));
        list.add(new WorkReportCategoryMenuModel(WorkReportCategory.MONTH_REPORT));

        FilterModel model =
                new FilterModel(list, WorkReportCategory.ALL.getValue(), MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public WorkReportCategoryMenuModel(WorkReportCategory type) {
        this.type = type;
    }

    @Override
    public String getKey() {
        return type.getKey();
    }

    @Override
    public String getValue() {
        return type.getValue();
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
