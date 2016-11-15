package com.loyo.oa.v2.activityui.signin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;

import java.util.ArrayList;

/**
 * Created by xeq on 16/11/15.
 */

public class SigninSelectCustomerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<SigninSelectCustomer> data;

    public SigninSelectCustomerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<SigninSelectCustomer> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public SigninSelectCustomer getItemData(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.item_signin_select_customer, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.setContent(getItemData(position));
        return convertView;
    }

    class Holder {
        TextView tv_name, tv_distance, tv_location;

        public void setContent(SigninSelectCustomer item) {
            tv_name.setText(item.name);
            tv_location.setText(item.loc.addr);
            tv_distance.setText(item.distance + "米");
        }
    }
}
