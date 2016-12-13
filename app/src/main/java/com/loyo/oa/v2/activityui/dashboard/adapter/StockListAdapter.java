package com.loyo.oa.v2.activityui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;

/**
 * Created by yyy on 16/12/13.
 */

public class StockListAdapter extends BaseAdapter{

    private Context mContext;

    public StockListAdapter(Context mContext){
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dashstock, null);
            holder.tv_zsize = (TextView) convertView.findViewById(R.id.tv_zsize);
            holder.tv_csize = (TextView) convertView.findViewById(R.id.tv_csize);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder{

        TextView tv_zsize;
        TextView tv_csize;
        TextView tv_title;

    }
}
