package com.loyo.oa.dropdownmenu.model;

import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public interface MenuChildren {
    List<MenuModel> getChildren();
    MenuModel getChildrenAtIndex(int index);
    int getChildrenCount();
}
