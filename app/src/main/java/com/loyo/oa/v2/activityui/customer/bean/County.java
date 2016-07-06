package com.loyo.oa.v2.activityui.customer.bean;

import com.loyo.oa.v2.beans.BaseMultiMenuItem;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :地区-区域
 * 作者 : ykb
 * 时间 : 15/10/19.
 */
public class County extends BaseMultiMenuItem {
    @Override
    protected boolean hasChildren() {
        return false;
    }

    @Override
    public ArrayList<BaseMultiMenuItem> getChildren() {
        return null;
    }
}
