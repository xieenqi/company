package com.loyo.oa.v2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerRepeat;
import com.loyo.oa.v2.beans.CustomerRepeatList;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.tool.LogUtil;

/**
 * Created by yyy on 15/12/9.
 * 查重adapter
 */
public class CustomerRepeatAdapter extends BaseAdapter {


    PaginationX<CustomerRepeatList> listCommon;
    Context mContext;

    public CustomerRepeatAdapter(final PaginationX<CustomerRepeatList> listCommons, final Context context) {
        listCommon = listCommons;
        mContext = context;
    }

    class viewHolder {
        TextView item_cuslist_tv;
    }


    @Override
    public int getCount() {
        return listCommon.getRecords().size();
    }

    @Override
    public Object getItem(final int i) {
        return null;
    }

    @Override
    public long getItemId(final int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        viewHolder holder = null;
        if (convertView == null) {
            holder = new viewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customerrepeat_list, null);
            holder.item_cuslist_tv = (TextView) convertView.findViewById(R.id.item_cuslist_tv);

            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        LogUtil.dll("data:" + listCommon.getRecords().get(position).getName());

        holder.item_cuslist_tv.setText(listCommon.getRecords().get(position).getName());
        return convertView;
    }
}
