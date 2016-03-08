package com.loyo.oa.v2.tool.commonadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    LayoutInflater mInflater;
    int mLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int layoutId) {
        mContext = context;
        this.mDatas = mDatas;
        mLayoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }
    public void setmDatas(List<T> datas){
        if(null!=datas){
            this.mDatas=datas;
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertview, parent, mLayoutId, position);

        convert(holder, getItem(position));

        return holder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T t);

}
