package com.loyo.oa.v2.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class TimeFilterModel {

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new MenuFilterByTimeModel(FilterByTime.FOLLOW, SortOrder.DESC));
        list.add(new MenuFilterByTimeModel(FilterByTime.FOLLOW, SortOrder.ASC));
        list.add(new MenuFilterByTimeModel(FilterByTime.CREATE, SortOrder.DESC));
        list.add(new MenuFilterByTimeModel(FilterByTime.CREATE, SortOrder.ASC));

        FilterModel model = new FilterModel(list, "时间", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }


    static private class MenuFilterByTimeModel implements MenuModel {
        private FilterByTime time;
        private SortOrder order;
        private boolean isSelected;

        public MenuFilterByTimeModel(FilterByTime time, SortOrder order) {
            this.time = time;
            this.order = order;
        }

        @Override
        public String getKey() {
            return time.getKey() + " " + order.getKey();
        }

        @Override
        public String getValue() {
            return time.getValue() + " " + order.getValue();
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
}
