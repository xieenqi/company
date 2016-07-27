package com.loyo.oa.v2.activityui.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;

import java.util.ArrayList;

/**
 * 销售机会 筛选公共adapter
 * Created by yyy on 16/5/19.
 */
public class AdapterSaleTeamScreenComm extends BaseAdapter {

    private Context mContext;
    private ArrayList<SaleTeamScreen> data;
    private SaleTeamScreen saleTeamScreen;
    private int index;

    public AdapterSaleTeamScreenComm(final Context context, ArrayList<SaleTeamScreen> arrayList, int index) {
        this.mContext = context;
        this.data = arrayList;
        this.index = index;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        saleTeamScreen = data.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saleteam_common, null);
            holder.name = (TextView) convertView.findViewById(R.id.item_saleteam_common_name);
            holder.img = (ImageView) convertView.findViewById(R.id.item_saleteam_common_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.img.setVisibility(index == position ? View.VISIBLE : View.GONE);

        holder.name.setText(saleTeamScreen.getName());
        return convertView;
    }

    class ViewHolder {
        TextView name;
        ImageView img;
    }
}
