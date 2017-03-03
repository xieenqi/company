package com.library.module.widget.nestgridview;

import android.view.View;
import android.view.ViewGroup;

/**
 * 介绍：最顶层的Adapter接口
 * 不涉及数据,
 * 对外暴漏 getView 和getCount方法 ，供ViewGroup调用。
 * <p>
 * 根据迪米特法则（最少知道原则）,
 * 我们应该抽象出一个顶层的接口，对ViewGroup暴露出最少的方法供使用。
 * 我们想一下，对于ViewGroup，它最少只需要哪些就能完成我们的需求。
 * ChildView是什么---> View
 * 有多少ChildView 需要 添加--->count
 */

public interface IViewGroupAdapter extends ICacheViewAdapter {
    /**
     * ViewGroup调用获取ItemView
     *
     * @param parent
     * @param pos
     * @return
     */
    View getView(ViewGroup parent, int pos);

    /**
     * ViewGroup调用，得到ItemCount
     *
     * @return
     */
    int getCount();
}
