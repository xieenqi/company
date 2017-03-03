package com.library.module.widget.nestgridview;

import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.nestgridview.listener.OnItemClickListener;
import com.library.module.widget.nestgridview.listener.OnItemLongClickListener;

/**
 * 绑定创建 view
 */
public class VGUtil {
    ViewGroup mParent;
    IViewGroupAdapter mAdapter;

    boolean remainExistViews;

    OnItemClickListener mOnItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;


    public VGUtil(ViewGroup parent, IViewGroupAdapter adapter) {
        mParent = parent;
        mAdapter = adapter;
    }

    public VGUtil(ViewGroup parent, IViewGroupAdapter adapter, boolean remainExistViews) {
        mParent = parent;
        mAdapter = adapter;
        this.remainExistViews = remainExistViews;
    }

    public VGUtil(ViewGroup parent, IViewGroupAdapter adapter, OnItemClickListener onItemClickListener) {
        mParent = parent;
        mAdapter = adapter;
        mOnItemClickListener = onItemClickListener;
    }

    public VGUtil(ViewGroup parent, IViewGroupAdapter adapter, OnItemLongClickListener onItemLongClickListener) {
        mParent = parent;
        mAdapter = adapter;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public VGUtil(ViewGroup parent, IViewGroupAdapter adapter, boolean remainExistViews, OnItemClickListener onItemClickListener) {
        mParent = parent;
        mAdapter = adapter;
        this.remainExistViews = remainExistViews;
        mOnItemClickListener = onItemClickListener;
    }

    public VGUtil(ViewGroup parent, IViewGroupAdapter adapter, boolean remainExistViews, OnItemClickListener onItemClickListener, OnItemLongClickListener onItemLongClickListener) {
        mParent = parent;
        mAdapter = adapter;
        this.remainExistViews = remainExistViews;
        mOnItemClickListener = onItemClickListener;
        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**
     * Begin bind views for {@link #mParent}
     */
    public VGUtil bind() {
        if (mParent == null || mAdapter == null) {
            return this;
        }
        //Step 1
        //If need clear all existed views
        if (!remainExistViews) {
            mAdapter.recycleViews(mParent);
        }
        //Step 2, begin add views
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            //Get itemView by adapter
            View itemView = mAdapter.getView(mParent, i);
            mParent.addView(itemView);

            //Step 3 (Optional),
            //If item has not set click listener before, add click listener for each item.
            if (null != mOnItemClickListener && !itemView.isClickable()) {
                final int finalI = i;
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(mParent, view, finalI);
                    }
                });
            }
            //If item has not set long click listener before, add long click listener for each item.
            if (null != mOnItemLongClickListener && !itemView.isLongClickable()) {
                final int finalI = i;
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return mOnItemLongClickListener.onItemLongClick(mParent, view, finalI);
                    }
                });
            }
        }
        return this;

    }
}
