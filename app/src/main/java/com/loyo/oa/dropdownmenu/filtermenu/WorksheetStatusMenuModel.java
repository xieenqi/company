package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class WorksheetStatusMenuModel implements MenuModel {

    public WorksheetStatus status;
    private boolean isSelected;

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new WorksheetStatusMenuModel(WorksheetStatus.Null));
        list.add(new WorksheetStatusMenuModel(WorksheetStatus.WAITASSIGN));
        list.add(new WorksheetStatusMenuModel(WorksheetStatus.INPROGRESS));
        list.add(new WorksheetStatusMenuModel(WorksheetStatus.WAITAPPROVE));
        list.add(new WorksheetStatusMenuModel(WorksheetStatus.FINISHED));
        list.add(new WorksheetStatusMenuModel(WorksheetStatus.TEMINATED));

        FilterModel model = new FilterModel(list, "全部状态", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public WorksheetStatusMenuModel(WorksheetStatus status) {
        this.status = status;
    }

    @Override
    public String getKey() {
        return status.getKey();
    }

    @Override
    public String getValue() {
        return status.getName();
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
