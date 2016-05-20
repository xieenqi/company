package com.loyo.oa.v2.activity.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamScreen;

import java.util.List;

/**
 * 团队机会 公司双列表筛选Adapter
 * Created by yyy on 16/5/18.
 */
public class AdapterSaleTeamScreen extends BaseAdapter {

    private Context mContext;
    private List<SaleTeamScreen> data;
    private int selectPosition;
    private int page;

    public AdapterSaleTeamScreen(Context context,final List<SaleTeamScreen> data,final int page){
        this.mContext = context;
        this.data = data;
        this.page = page;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void selectPosition(final int position){
        selectPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_list_item,null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_menu_item);
            holder.index = (ImageView) convertView.findViewById(R.id.iv_menu_select);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(data.get(position).getName());

        if(page == 1){
            if(selectPosition == position){
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.ececec));
            }else{
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            }
        }

        return convertView;
    }

    class ViewHolder{
        TextView name;
        ImageView index;
    }
}
