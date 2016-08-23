package com.loyo.oa.v2.activityui.clue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;

import java.util.ArrayList;

/**
 * 【我的线索】适配器
 * Created by yyy on 16/8/19.
 */
public class MyClueAdapter extends BaseAdapter{

    private ArrayList<ClueListItem> mData;
    private LayoutInflater inflater;
    private Context mContext;

    public MyClueAdapter(Context context,ArrayList<ClueListItem> mData){
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.mData = mData;
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
        Holder holder = null;
        ClueListItem clueListItem = mData.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_myclue, null);
            holder = new Holder();
            holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.tv_name.setText(clueListItem.name);
        holder.tv_company_name.setText(clueListItem.companyName);
        holder.tv_name.setText(clueListItem.name);

        return convertView;
    }

    class Holder{

        TextView tv_company_name; //公司名
        TextView tv_time;         //跟进时间
        TextView tv_name;         //客户名

    }

}
