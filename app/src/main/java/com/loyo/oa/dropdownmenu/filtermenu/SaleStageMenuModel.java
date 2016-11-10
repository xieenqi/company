package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.other.model.SaleStage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class SaleStageMenuModel implements MenuModel {

    private SaleStage stage;
    private boolean isSelected;

    public static FilterModel getStageFilterModel(List<SaleStage> stages) {
        List<MenuModel> stageModel = new ArrayList<>();
        SaleStage all = new SaleStage();
        all.setName("全部阶段");
        all.setId("");
        stageModel.add(new SaleStageMenuModel(all));
        for (SaleStage stage:stages) {
            stageModel.add(new SaleStageMenuModel(stage));
        }

        return new FilterModel(stageModel, "销售阶段", MenuListType.SINGLE_LIST_SINGLE_SEL);
    }

    public SaleStageMenuModel(SaleStage stage){
        this.stage = stage;
    }

    @Override
    public String getKey() {
        return stage.getId();
    }

    @Override
    public String getValue() {
        return stage.getName();
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
