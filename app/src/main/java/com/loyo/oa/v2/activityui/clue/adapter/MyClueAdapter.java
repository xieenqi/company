package com.loyo.oa.v2.activityui.clue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * 【我的线索】适配器
 * Created by yyy on 16/8/19.
 */
public class MyClueAdapter extends BaseAdapter{

    public String[] mData = {"阿里巴巴","极致生活","京东到家","打野科技","Link"};
    private LayoutInflater inflater;
    private Context mContext;

    public MyClueAdapter(Context context){
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
            convertView = inflater.inflate(R.layout.item_myclue, null);
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
