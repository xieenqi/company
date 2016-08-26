package com.loyo.oa.v2.activityui.clue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.application.MainApp;

import java.util.ArrayList;
import java.util.Date;

/**
 * 【团队线索】适配器
 * Created by yyy on 16/8/19.
 */
public class TeamClueAdapter extends BaseAdapter {

    private ArrayList<ClueListItem> mData;
    private LayoutInflater inflater;
    private Context mContext;

    public TeamClueAdapter(Context context, ArrayList<ClueListItem> mData) {
        this.mData = mData;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<ClueListItem> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
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
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_teamclue, null);
            holder = new Holder();
            holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
            holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ClueListItem clueListItem = mData.get(position);
        holder.tv_name.setText(clueListItem.name);
        holder.tv_company_name.setText(clueListItem.companyName);
        holder.tv_customer.setText(clueListItem.responsorName);
        if (clueListItem.lastActAt != 0) {
            holder.tv_time.setText(MainApp.getMainApp().df3.format(new Date(Long.valueOf(clueListItem.lastActAt + "") * 1000)));
//            app.df3.format(new Date(Long.valueOf(sales.updateAt + "") * 1000))
        }

        return convertView;
    }

    class Holder {
        TextView tv_company_name; /* 公司名称 */
        TextView tv_customer;     /* 负责人 */
        TextView tv_time;         /* 跟进时间 */
        TextView tv_name;         /* 客户名称 */
    }
}
