package com.library.module.widget.nestgridview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.nestgridview.IViewGroupAdapter;

import java.util.List;

/**
 * 介绍：datas->View 的 Base Adapter
 * 整个设计的第二层，这里引入datas，实现IViewGroupAdapter的方法
 * <p>
 */

public abstract class BaseAdapter<T> implements IViewGroupAdapter {
    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseAdapter(Context context, List<T> datas) {
        mDatas = datas;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * ViewGroup调用获取ItemView,create bind一起做
     *
     * @param parent
     * @param pos
     * @return
     */
    @Override
    public View getView(ViewGroup parent, int pos) {
        return getView(parent, pos, mDatas.get(pos));
    }

    /**
     * 实际的createItemView的地方
     *
     * @param parent
     * @param pos
     * @param data
     * @return
     */
    public abstract View getView(ViewGroup parent, int pos, T data);

    /**
     * ViewGroup调用，得到ItemCount
     *
     * @return
     */
    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public BaseAdapter setDatas(List<T> datas) {
        mDatas = datas;
        return this;
    }
}
