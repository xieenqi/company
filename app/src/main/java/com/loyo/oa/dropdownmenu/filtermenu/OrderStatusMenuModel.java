package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.order.common.OrderStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class OrderStatusMenuModel implements MenuModel {

    public OrderStatus status;
    private boolean isSelected;

    public static FilterModel getFilterModel() {
        List<MenuModel> statusModel = new ArrayList<>();
        statusModel.add(new OrderStatusMenuModel(OrderStatus.ALL));
        statusModel.add(new OrderStatusMenuModel(OrderStatus.WAIT_APPROVE));
        statusModel.add(new OrderStatusMenuModel(OrderStatus.NOT_APPROVED));
        statusModel.add(new OrderStatusMenuModel(OrderStatus.PROCESSING));
        statusModel.add(new OrderStatusMenuModel(OrderStatus.FINISHED));
        statusModel.add(new OrderStatusMenuModel(OrderStatus.TERMINATED));
        return new FilterModel(statusModel, "全部状态", MenuListType.SINGLE_LIST_SINGLE_SEL);
    }

    public OrderStatusMenuModel(OrderStatus status) {
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
