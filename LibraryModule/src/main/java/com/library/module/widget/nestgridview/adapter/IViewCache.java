package com.library.module.widget.nestgridview.adapter;

import com.library.module.widget.nestgridview.ViewHolder;

/**
 * 引入 ViewCache概念
 */

public interface IViewCache {
    void put(ViewHolder view);

    ViewHolder get(int itemType);

    void setCacheSize(int itemType, int size);

    int getCacheSize(int itemType);
}
