package com.loyo.oa.dropdownmenu.model;

/**
 * Created by EthanGong on 2016/10/31.
 */

public interface MenuModel extends MenuChildren {
    String getKey();
    String getValue();
    boolean getSelected();
    void setSelected(boolean selected);
}
