package com.loyo.oa.v2.activityui.project.common;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class ProjectTypeMenuModel implements MenuModel {

    public ProjectType type;
    private boolean isSelected;

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new ProjectTypeMenuModel(ProjectType.ALL));
        list.add(new ProjectTypeMenuModel(ProjectType.IN_CHARGE));
        list.add(new ProjectTypeMenuModel(ProjectType.CREATE_BY_ME));
        list.add(new ProjectTypeMenuModel(ProjectType.PARTICIPATE_IN));

        FilterModel model = new FilterModel(list, "全部类型", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public ProjectTypeMenuModel(ProjectType type) {
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
