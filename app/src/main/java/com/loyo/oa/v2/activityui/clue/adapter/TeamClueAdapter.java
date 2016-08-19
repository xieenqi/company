package com.loyo.oa.v2.activityui.clue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 【团队线索】适配器
 * Created by yyy on 16/8/19.
 */
public class TeamClueAdapter extends BaseAdapter{
    public String[] mData = {"123123","4343","5454","656","8787","5765","87989"};
    private LayoutInflater inflater;
    private Context mContext;

    public TeamClueAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public Object getItem(int position) {
        return mData[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;
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

        holder.tv_name.setText(mData[position]);

        return convertView;
    }

    class Holder{

        TextView tv_company_name,tv_customer,tv_time,tv_name;

    }
}