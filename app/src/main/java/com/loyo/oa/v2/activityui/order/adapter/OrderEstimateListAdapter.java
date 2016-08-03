package com.loyo.oa.v2.activityui.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.tool.DateTool;
import java.util.ArrayList;

/**
 * 【回款记录】适配器
 * Created by yyy on 16/8/3.
 */
public class OrderEstimateListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<EstimateAdd> mData;

    public OrderEstimateListAdapter(Context context,ArrayList<EstimateAdd> data){
        this.mContext = context;
        this.mData    = data;
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_orderestimate,null);
        ViewHolder holder = null;
        EstimateAdd mEstimateAdd = mData.get(position);
        if(null == holder){
            holder = new ViewHolder();
            holder.tv_esttime = (TextView) convertView.findViewById(R.id.tv_esttime);
            holder.tv_esttime_price = (TextView) convertView.findViewById(R.id.tv_esttime_price);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_payee = (TextView) convertView.findViewById(R.id.tv_payee);
            holder.tv_payment = (TextView) convertView.findViewById(R.id.tv_payment);
            holder.tv_Remarks = (TextView) convertView.findViewById(R.id.tv_Remarks);
            holder.tv_titlenum = (TextView) convertView.findViewById(R.id.tv_titlenum);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_titlenum.setText("回款记录"+position+1);
        holder.tv_esttime.setText(DateTool.timet(mEstimateAdd.receivedAt+"","yyyy-MM-dd"));
        holder.tv_esttime_price.setText("￥"+mEstimateAdd.receivedMoney);
        holder.tv_price.setText("￥"+mEstimateAdd.billingMoney);
        holder.tv_payee.setText(mEstimateAdd.payeeUser.name);
        holder.tv_Remarks.setText(mEstimateAdd.remark);

        switch (mEstimateAdd.payeeMethod){
            case 1:
                holder.tv_payment.setText("现金");
                break;

            case 2:
                holder.tv_payment.setText("支票");
                break;

            case 3:
                holder.tv_payment.setText("银行转账");
                break;

            case 4:
                holder.tv_payment.setText("其他");
                break;

        }

        return convertView;
    }


    public class ViewHolder{

        TextView tv_esttime;       //回款日期
        TextView tv_esttime_price; //回款金额
        TextView tv_price;         //开票
        TextView tv_payee;         //收款人
        TextView tv_payment;       //收款方式
        TextView tv_Remarks;       //备注
        TextView tv_titlenum;

    }
}
