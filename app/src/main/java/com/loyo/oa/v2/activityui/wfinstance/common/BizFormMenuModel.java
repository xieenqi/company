package com.loyo.oa.v2.activityui.wfinstance.common;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class BizFormMenuModel implements MenuModel {

    public BizForm bizForm;
    private boolean isSelected;

    public static FilterModel getFilterModel(List<BizForm> bizForms) {
        List<MenuModel> list = new ArrayList<>();
        BizForm all = new BizForm();
        all.setId("");
        all.setName("全部类别");
        list.add(new BizFormMenuModel(all));
        for (BizForm bizForm:bizForms) {
            list.add(new BizFormMenuModel(bizForm));
        }
        FilterModel model = new FilterModel(list, "全部类别", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public BizFormMenuModel(BizForm bizForm) {
        this.bizForm = bizForm;
    }

    @Override
    public String getKey() {
        return bizForm.getId();
    }

    @Override
    public String getValue() {
        return bizForm.getName();
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
