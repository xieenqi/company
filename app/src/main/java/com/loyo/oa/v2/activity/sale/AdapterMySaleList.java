package com.loyo.oa.v2.activity.sale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.adapter
 * 描述 :我的销售机会列表适配器
 * 作者 : yyy
 * 时间 : 16/5/17
 */
public class AdapterMySaleList extends BaseAdapter {

    private ArrayList<String> data = new ArrayList<>();
    private Context mContext;

    public AdapterMySaleList(Context context) {
        mContext = context;
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(final int i) {
        return data.isEmpty() ? null : data.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {
        HolderView holder;
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
        holder.tv_name.setText(data.get(position));

        return view;
    }

    class HolderView {
        TextView tv_name;
        TextView tv_number;
        TextView tv_price;
    }
}
