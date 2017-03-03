package com.library.module.widget.nestgridview;

import android.view.ViewGroup;

/**
 * 引入 ViewCache概念 缓存view避免重复渲染view 造成内存消耗过大
 * ViewCache的接口
 * 1 回收
 */

public interface ICacheViewAdapter {
    void recycleView(ViewGroup parent, ViewHolder holder);

    void recycleViews(ViewGroup parent);
}
