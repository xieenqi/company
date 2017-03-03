package com.library.module.widget.nestgridview;

import android.view.ViewGroup;

/**
 * 介绍：V1.5.0 引入 ViewCache概念
 * ViewCache的接口
 * 1 回收
 */

public interface ICacheViewAdapter {
    void recycleView(ViewGroup parent, ViewHolder holder);

    void recycleViews(ViewGroup parent);
}
