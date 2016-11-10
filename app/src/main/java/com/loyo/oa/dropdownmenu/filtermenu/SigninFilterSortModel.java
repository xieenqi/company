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

public class SigninFilterSortModel {

    public static FilterModel getFilterModel() {

        List<MenuModel> list = new ArrayList<>();
        list.add(new MenuFilterByTimeModel(SigninFilterBySort.SORT1));
        list.add(new MenuFilterByTimeModel(SigninFilterBySort.SORT2));
        list.add(new MenuFilterByTimeModel(SigninFilterBySort.SORT3));

        FilterModel model = new FilterModel(list, "排序", MenuListType.SINGLE_LIST_SINGLE_SEL);
        return model;
    }


    static private class MenuFilterByTimeModel implements MenuModel {
        private SigninFilterBySort time;
        private boolean isSelected;

        public MenuFilterByTimeModel(SigninFilterBySort time) {
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
