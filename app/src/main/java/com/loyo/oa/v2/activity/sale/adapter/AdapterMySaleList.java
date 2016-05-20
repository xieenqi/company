package com.loyo.oa.v2.activity.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.adapter
 * 描述 :我的销售机会列表适配器
 * 作者 : yyy
 * 时间 : 16/5/17
 */
public class AdapterMySaleList extends BaseAdapter {

    private ArrayList<SaleMyList.Record> mData;
    private Context mContext;

    public AdapterMySaleList(Context context,ArrayList<SaleMyList.Record> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(final int i) {
        return mData.isEmpty() ? null : mData.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {
        HolderView holder;
        SaleMyList.Record record = mData.get(position);
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_my_sale, viewGroup, false);
            holder = new HolderView();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            view.setTag(holder);
        } else {
            holder = (HolderView) view.getTag();
        }
        holder.tv_name.setText(record.getName());
        holder.tv_number.setText(record.getStageNmae()+"("+record.getProb()+"%)");
        holder.tv_price.setText("预估销售金额:"+record.getEstimatedAmount());

        return view;
    }

    class HolderView {
        TextView tv_name;
        TextView tv_number;
        TextView tv_price;
    }
}
