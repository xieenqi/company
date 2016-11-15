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
 * 【我的线索】适配器
 * Created by yyy on 16/8/19.
 */
public class MyClueAdapter extends BaseAdapter {

    private ArrayList<ClueListItem> mData;
    private LayoutInflater inflater;
    private Context mContext;

    public MyClueAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<ClueListItem> records) {
        this.mData = records;
        notifyDataSetChanged();
    }

    public ClueListItem getItemData(int postion) {
        return mData.get(postion);
    }

    @Override
    public int getCount() {
        return null == mData ? 0 : mData.size();
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
        ClueListItem clueListItem = getItemData(position);
        holder.setContentView(clueListItem);
        return convertView;
    }

    class Holder {

        TextView tv_company_name; //公司名
        TextView tv_time;         //跟进时间
        TextView tv_name;         //客户名

        public void setContentView(ClueListItem clueListItem) {
            tv_name.setText(clueListItem.name);
            tv_company_name.setText(clueListItem.companyName);
            if (clueListItem.lastActAt != 0) {
                tv_time.setText(MainApp.getMainApp().df3.format(new Date(Long.valueOf(clueListItem.lastActAt + "") * 1000)));
            } else {
                tv_time.setText("--");
            }
        }
    }

}
