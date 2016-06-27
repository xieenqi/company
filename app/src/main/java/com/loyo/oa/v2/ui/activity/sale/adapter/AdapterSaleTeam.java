package com.loyo.oa.v2.ui.activity.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.sale.bean.SaleRecord;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

/**
 * 我的团队 销售机会 adapter
 * Created by yyy on 16/5/17.
 */
public class AdapterSaleTeam extends BaseAdapter {

    public Context mContext;
    private ArrayList<SaleRecord> mData;

    public AdapterSaleTeam(Context context) {
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
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        SaleRecord record = mData.get(position);
        if (convertView == null) {
            holder = new viewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saleteamlist, null);
            holder.creator = (TextView) convertView.findViewById(R.id.sale_teamlist_creator);
            holder.state = (TextView) convertView.findViewById(R.id.sale_teamlist_state);
            holder.guess = (TextView) convertView.findViewById(R.id.sale_teamlist_guess);
            holder.money = (TextView) convertView.findViewById(R.id.sale_teamlist_money);
            holder.title = (TextView) convertView.findViewById(R.id.sale_teamlist_title);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.title.setText(record.getName());
        holder.money.setText(Utils.setValueDouble(record.getEstimatedAmount()) + "");
        holder.creator.setText(record.getCreateName());
        String stageName = "初步接洽";
        if (!record.getStageNmae().isEmpty()) {
            stageName = record.getStageNmae();
        }
        holder.state.setText(stageName + "(" + record.getProb() + "%)");

        return convertView;
    }

    class viewHolder {
        TextView title;
        TextView state;
        TextView guess;
        TextView money;
        TextView creator;
    }
}
