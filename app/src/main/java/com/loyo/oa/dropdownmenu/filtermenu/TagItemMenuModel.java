package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.customer.model.TagItem;

import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class TagItemMenuModel implements MenuModel {

    private TagItem tagItem;
    private boolean isSelected;

    public TagItemMenuModel(TagItem tagItem) {
        this.tagItem = tagItem;
    }

    public TagItemMenuModel(String key, String value) {
        TagItem item = new TagItem();
        item.setId(key);
        item.setName(value);
        this.tagItem = item;
    }

    @Override
    public String getKey() {
        return tagItem.getId();
    }

    @Override
    public String getValue() {
        return tagItem.getName();
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
