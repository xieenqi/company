package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class WorksheetTemplateMenuModel implements MenuModel {

    public WorksheetTemplate mTemplate;
    private boolean isSelected;

    public static FilterModel getFilterModel(List<WorksheetTemplate> templates) {
        List<MenuModel> tempModel = new ArrayList<>();
        tempModel.add(new WorksheetTemplateMenuModel(WorksheetTemplate.Null));// 全部
        for (WorksheetTemplate temp:templates) {
            tempModel.add(new WorksheetTemplateMenuModel(temp));
        }
        return new FilterModel(tempModel, "全部类型", MenuListType.SINGLE_LIST_SINGLE_SEL);
    }

    public WorksheetTemplateMenuModel(WorksheetTemplate template) {
        mTemplate = template;
    }
    @Override
    public String getKey() {
        return mTemplate.id;
    }

    @Override
    public String getValue() {
        return mTemplate.name;
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
