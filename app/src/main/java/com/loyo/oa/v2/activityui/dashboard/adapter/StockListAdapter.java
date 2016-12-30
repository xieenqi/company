package com.loyo.oa.v2.activityui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.model.StockStatistic;
import java.util.ArrayList;

/**
 * Created by yyy on 16/12/13.
 */

public class StockListAdapter extends BaseAdapter{

    private Context mContext;
    public ArrayList<StockStatistic> models;

    public StockListAdapter(Context mContext,ArrayList<StockStatistic> stockListModel){
        this.mContext = mContext;
        models = stockListModel;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        StockStatistic model = models.get(position);
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

        holder.tv_title.setText(model.tagItemName);
        holder.tv_zsize.setText(model.getIncement());
        holder.tv_csize.setText(model.getStock());
        return convertView;
    }

    class ViewHolder{

        TextView tv_zsize;
        TextView tv_csize;
        TextView tv_title;

    }
}
