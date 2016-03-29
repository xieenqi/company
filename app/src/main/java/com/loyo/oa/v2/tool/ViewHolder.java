package com.loyo.oa.v2.tool;

import android.util.SparseArray;
import android.view.View;

/**
 * com.loyo.oa.v2.tool
 * 描述 :视图缓存类
 * 作者 : ykb
 * 时间 : 15/7/20.
 */
public class ViewHolder {
    protected ViewHolder() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }

        View child = viewHolder.get(id);
        if (child == null) {
            child = view.findViewById(id);
            viewHolder.put(id, child);
        }
        return (T) child;
    }

}
