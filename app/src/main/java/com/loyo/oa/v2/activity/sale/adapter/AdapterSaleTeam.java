package com.loyo.oa.v2.activity.sale.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamList;
import com.loyo.oa.v2.beans.Position;
import java.util.ArrayList;

/**
 * 我的团队 销售机会 adapter
 * Created by yyy on 16/5/17.
 */
public class AdapterSaleTeam extends BaseAdapter {

    public Context mContext;
    public ArrayList<SaleTeamList.Record> mData;

    public AdapterSaleTeam(Context context,ArrayList<SaleTeamList.Record> mData){
        this.mContext = context;
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
        viewHolder holder = null;
        SaleTeamList.Record record = mData.get(position);
        if(convertView == null){
            holder = new viewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_saleteamlist,null);
            holder.creator = (TextView) convertView.findViewById(R.id.sale_teamlist_creator);
            holder.state = (TextView) convertView.findViewById(R.id.sale_teamlist_state);
            holder.guess = (TextView) convertView.findViewById(R.id.sale_teamlist_guess);
            holder.money = (TextView) convertView.findViewById(R.id.sale_teamlist_money);
            holder.title = (TextView) convertView.findViewById(R.id.sale_teamlist_title);
            convertView.setTag(holder);
        }else{
            holder = (viewHolder)convertView.getTag();
        }

        holder.title.setText(record.getName());
        holder.money.setText(record.getEstimatedAmount());
        holder.creator.setText(record.getCreateName());
        String stageName = "初步接洽";
        if(!record.getStageNmae().isEmpty()){
            stageName = record.getStageNmae();
        }
        holder.state.setText(stageName+"("+record.getProb()+"%)");

        return convertView;
    }

    class viewHolder{
        TextView title;
        TextView state;
        TextView guess;
        TextView money;
        TextView creator;
    }
}
