package com.loyo.oa.v2.activityui.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.adapter
 * 描述 :我的销售机会列表适配器
 * 作者 : yyy
 * 时间 : 16/5/17
 */
public class AdapterMySaleList extends BaseAdapter {

    private ArrayList<SaleRecord> mData = new ArrayList<>();
    private Context mContext;

    public AdapterMySaleList(Context context) {
        mContext = context;
    }

    public ArrayList<SaleRecord> getData() {
        return mData;
    }

    public void setData(ArrayList<SaleRecord> newData) {
        this.mData = newData;
        notifyDataSetChanged();
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
        SaleRecord record = mData.get(position);
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
        holder.tv_number.setText(record.getStageNmae() + "(" + record.getProb() + "%)");
        holder.tv_price.setText("预估销售金额:" + Utils.setValueDouble(record.getEstimatedAmount()));

        return view;
    }

    class HolderView {
        TextView tv_name;
        TextView tv_number;
        TextView tv_price;
    }
}
