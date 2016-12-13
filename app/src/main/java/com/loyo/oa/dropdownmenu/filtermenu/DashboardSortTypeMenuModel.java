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

    /**
     * 【客户】【跟进】排序
     * @return
     */
    public static FilterModel getCusFolloupFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.CUS_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.CUS_RISE));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    /**
     * 【客户】【电话录音】 排序
     * @return
     */
    public static FilterModel getCusRecordFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.CUS_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.CUS_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.RECORD_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.RECORD_RISE));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }
    /**
     * 【线索】【跟进】排序
     * @return
     */
    public static FilterModel getSaleFolloupFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.SALE_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.SALE_RISE));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }
    /**
     * 【线索】【电话录音】排序
     * @return
     */
    public static FilterModel getSaleRecordFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.NUMBER_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.SALE_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.SALE_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.RECORD_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.RECORD_RISE));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }
    /**
     * 【订单数量】排序
     * @return
     */
    public static FilterModel getOrderNumberFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.ORDER_NUMBER_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.ORDER_NUMBER_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.COMPILE_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.COMPILE_RISE));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }
    /**
     * 【订单金额】排序
     * @return
     */
    public static FilterModel getOrderMoneyFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.ORDER_MONEY_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.ORDER_MONEY_RISE));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.COMPILE_DROP));
        list.add(new DashboardSortTypeMenuModel(DashboardSortType.COMPILE_RISE));

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
