package com.loyo.oa.v2.activityui.project.common;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class ProjectStatusMenuModel implements MenuModel {

    public ProjectStatus status;
    private boolean isSelected;

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new ProjectStatusMenuModel(ProjectStatus.ALL));
        list.add(new ProjectStatusMenuModel(ProjectStatus.IN_PROCESS));
        list.add(new ProjectStatusMenuModel(ProjectStatus.TERMINATED));

        FilterModel model = new FilterModel(list, "不限状态", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public ProjectStatusMenuModel(ProjectStatus status) {
        this.status = status;
    }

    @Override
    public String getKey() {
        return status.getKey();
    }

    @Override
    public String getValue() {
        return status.getValue();
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
