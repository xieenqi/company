package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 【跟进和拜访】菜单时间model
 * Created by yyy on 2016/10/31.
 */

public class DashboardFilterTimeModel {

    public static FilterModel getFilterModel(DynamicFilterByTime defaultValue) {

        List<MenuModel> list = new ArrayList<>();
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.TODAY));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.YESTERDAY));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.TOWEEK));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.LASTWEEK));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.TOMONTH));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.LASTMONTH));

        FilterModel model = new FilterModel(list, defaultValue.value, MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    public static FilterModel getDashboardOrderMOneyFilterModel(DynamicFilterByTime defaultValue) {

        List<MenuModel> list = new ArrayList<>();
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.TOMONTH));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.LASTMONTH));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.TOQUARTER));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.LASQUARTER));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.TOYEAR));
        list.add(new MenuFilterByTimeModel(DynamicFilterByTime.LASYEAR));

        FilterModel model = new FilterModel(list,defaultValue.value, MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }

    static private class MenuFilterByTimeModel implements MenuModel {
        private DynamicFilterByTime time;
        private boolean isSelected;

        public MenuFilterByTimeModel(DynamicFilterByTime time) {
            this.time = time;
        }

        @Override
        public String getKey() {
            return time.getKey();
        }

        @Override
        public String getValue() {
            return time.getValue();
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
