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

import java.util.List;

/**
 * 团队机会 公司双列表筛选Adapter
 * Created by yyy on 16/5/18.
 */
public class AdapterSaleTeamScreen2 extends BaseAdapter {

    private Context mContext;
    private List<SaleTeamScreen> data;
    private int selectPosition = -1, index1 = -1, index2 = -1;
    private int page;

    public AdapterSaleTeamScreen2(Context context, final List<SaleTeamScreen> data, final int page) {
        this.mContext = context;
        this.data = data;
        this.page = page;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void refshData(int index1) {
        this.index1 = index1;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void selectPosition(final int position) {
        selectPosition = position;
        index2 = index1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_list_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_menu_item);
            holder.index = (ImageView) convertView.findViewById(R.id.iv_menu_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(data.get(position).getName());

        if (page == 2) {
            if (selectPosition == position && index2 == index1) {
                holder.index.setVisibility(View.VISIBLE);
            } else {
                holder.index.setVisibility(View.INVISIBLE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView name;
        ImageView index;
    }
}
