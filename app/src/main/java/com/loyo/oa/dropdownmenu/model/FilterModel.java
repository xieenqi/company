package com.loyo.oa.dropdownmenu.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/31.
 */

public class FilterModel implements MenuChildren{

    protected String defaultTitle;
    protected MenuListType type;
    private List<MenuModel> chilren = new ArrayList<>();
    protected int defaultSelectedIndex;
    protected List<Integer> selectedIndexes = new ArrayList<>();

    public FilterModel(List<MenuModel> children, @NonNull String defaultTitle, MenuListType type) {
        this.defaultTitle = defaultTitle;
        this.type = type;
        this.chilren.addAll(children);
        if (children.size() >0) {
            defaultSelectedIndex = 0;
            children.get(0).setSelected(true);
        }
        else  {
            defaultSelectedIndex = -1;
        }
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public MenuListType getType() {
        return type;
    }

    public void setSelectedIndexes(List<Integer> indexes) {
        selectedIndexes.clear();
        selectedIndexes.addAll(indexes);
    }

    public List<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    public List<MenuModel> getSelectedModels() {
        List<MenuModel> result = new ArrayList<>();
        for (Integer idx:selectedIndexes) {
            result.add(chilren.get(idx));
        }
        return result;
    }

    @Override
    public List<MenuModel> getChildren() {
        return chilren;
    }

    @Override
    public MenuModel getChildrenAtIndex(int index) {
        if (index < 0 || index >= chilren.size()) {
            return null;
        }

        return chilren.get(index);
    }

    @Override
    public int getChildrenCount() {
        return chilren.size();
    }
}
