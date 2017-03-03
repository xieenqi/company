package com.library.module.widget.nestgridview.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.nestgridview.ViewHolder;

import java.util.List;

/**
 * 介绍：一个简化的Adapter
 * 只支持单种Item
 * 用LayoutId 构建View
 * <p>
 * 使用时，一般将数据结构的泛型传入，配合构造函数传入的ItemLayoutId使用.
 */

public abstract class SingleAdapter<T> extends BaseCacheAdapter<T> {

    protected int mItemLayoutId;

    public SingleAdapter(Context context, List<T> datas, int itemLayoutId) {
        super(context, datas);
        mItemLayoutId = itemLayoutId;
    }

    @Override
    public View getView(ViewGroup parent, int pos, T data) {
        //实现getView
        ViewHolder holder = /*onCreateView(parent, pos)*//*mInflater.inflate(mItemLayoutId, parent, false)*/ getViewHolderByType(parent, mItemLayoutId);
        onBindViewHolder(parent, holder, data, pos);
        return holder.itemView;
    }

    /**
     * 暴漏这个 让外部bind数据
     *
     * @param parent
     * @param holder
     * @param data
     * @param pos
     */
    public abstract void onBindViewHolder(ViewGroup parent, ViewHolder holder, T data, int pos);

/*    *//**
     * 通过ItemLayoutId inflate View
     *
     * @param parent
     * @param pos
     * @return
     *//*
    public View onCreateView(ViewGroup parent, int pos) {
        return createItemView(parent, pos);
    }

    public View createItemView(ViewGroup parent, int pos) {
        return mInflater.inflate(mItemLayoutId, parent, false);
    }*/
}
