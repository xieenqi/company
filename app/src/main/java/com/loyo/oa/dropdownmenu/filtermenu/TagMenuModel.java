package com.loyo.oa.dropdownmenu.filtermenu;

import com.loyo.oa.dropdownmenu.model.FilterModel;
import com.loyo.oa.dropdownmenu.model.MenuListType;
import com.loyo.oa.dropdownmenu.model.MenuModel;
import com.loyo.oa.v2.activityui.customer.model.Tag;
import com.loyo.oa.v2.activityui.customer.model.TagItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/1.
 */

public class TagMenuModel implements MenuModel {



    public static FilterModel getTagFilterModel(List<Tag> tags) {
        List<MenuModel> tagModel = new ArrayList<>();
        for (Tag tag:tags) {
            tagModel.add(new TagMenuModel(tag));
        }
        return new FilterModel(tagModel, "标签", MenuListType.TAG);
    }

    private Tag tag;
    private boolean isSelected;

    private List<MenuModel> children = new ArrayList<>();

    public TagMenuModel(Tag tag) {
        this.tag = tag;
        List<TagItem> items = tag.getItems();
        for (TagItem item:items) {
            TagItemMenuModel model = new TagItemMenuModel(item);
            children.add(model);
        }
        if (children.size() >0) {
            TagItemMenuModel all = new TagItemMenuModel("", "不限");
            children.add(0, all);
        }
    }

    @Override
    public String getKey() {
        return tag.getId();
    }

    @Override
    public String getValue() {
        return tag.getName();
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
        return children;
    }

    @Override
    public MenuModel getChildrenAtIndex(int index) {
        return children.get(index);
    }

    @Override
    public int getChildrenCount() {
        return children.size();
    }
}


