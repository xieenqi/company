package com.loyo.oa.v2.activity.sale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 我的团队 销售机会 adapter
 * Created by yyy on 16/5/17.
 */
public class SaleTeamListAdapter extends BaseAdapter {

    public Context mContext;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder = null;
        if(convertView == null){
            holder = new viewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saleteamlist,null);
            convertView.setTag(holder);
        }else{
            holder = (viewHolder)convertView.getTag();
        }

        holder.creator = (TextView) convertView.findViewById(R.id.sale_teamlist_creator);
        holder.state = (TextView) convertView.findViewById(R.id.sale_teamlist_state);
        holder.guess = (TextView) convertView.findViewById(R.id.sale_teamlist_guess);
        holder.money = (TextView) convertView.findViewById(R.id.sale_teamlist_money);
        holder.title = (TextView) convertView.findViewById(R.id.sale_teamlist_title);

        return null;
    }

    class viewHolder{
        TextView title;
        TextView state;
        TextView guess;
        TextView money;
        TextView creator;
    }
}
