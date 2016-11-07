package com.loyo.oa.v2.activityui.wfinstance.common;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class SubmitStatusMenuModel implements MenuModel {

    public SubmitStatus status;
    private boolean isSelected;

    public static FilterModel getFilterModel() {
        List<MenuModel> list = new ArrayList<>();
        list.add(new SubmitStatusMenuModel(SubmitStatus.ALL));
        list.add(new SubmitStatusMenuModel(SubmitStatus.WAIT_APPROVE));
        list.add(new SubmitStatusMenuModel(SubmitStatus.APPROVING));
        list.add(new SubmitStatusMenuModel(SubmitStatus.UNAPPROVED));
        list.add(new SubmitStatusMenuModel(SubmitStatus.APPROVED));

        FilterModel model = new FilterModel(list, "不限状态", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public SubmitStatusMenuModel(SubmitStatus status) {
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
