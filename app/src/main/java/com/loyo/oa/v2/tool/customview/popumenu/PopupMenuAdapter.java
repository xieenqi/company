package com.loyo.oa.v2.tool.customview.popumenu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.tool.customview.popumenu
 * 描述 :弹出窗适配器
 * 作者 : ykb
 * 时间 : 15/11/4.
 */
public class PopupMenuAdapter extends BaseAdapter {

    private ArrayList<PopupMenuItem> menuItems;

    public PopupMenuAdapter(ArrayList<PopupMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public PopupMenuItem getItem(int i) {
        return menuItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final PopupMenuItem item=getItem(i);
        convertView=item.getConvertView();

        return convertView;
    }
}
