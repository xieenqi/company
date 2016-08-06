package com.loyo.oa.v2.activityui.order.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddEstimateActivity;
import com.loyo.oa.v2.activityui.order.OrderEstimateListActivity;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;

import java.util.ArrayList;

/**
 * 【回款记录】适配器
 * Created by yyy on 16/8/3.
 */
public class OrderEstimateListAdapter extends BaseAdapter{

    private Activity mActivity;
    private ArrayList<EstimateAdd> mData;
    private Handler mHandler;
    private Bundle  mBundle;
    private Intent  mIntent;
    private Message msg;
    private String  orderId;

    public OrderEstimateListAdapter(Activity activity,ArrayList<EstimateAdd> data,Handler handler,String orderId){
        this.mActivity = activity;
        this.mData    = data;
        this.mHandler = handler;
        this.orderId  = orderId;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_orderestimate,null);
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

            holder.btn_edit   = (LinearLayout) convertView.findViewById(R.id.btn_edit);
            holder.btn_delete = (LinearLayout) convertView.findViewById(R.id.btn_delete);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        int index = position+1;
        holder.tv_titlenum.setText("回款记录"+index);
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

        holder.btn_delete.setOnTouchListener(Global.GetTouch());
        holder.btn_edit.setOnTouchListener(Global.GetTouch());

        //删除
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg     = new Message();
                mBundle = new Bundle();
                mBundle.putInt("posi",position);
                msg.what = ExtraAndResult.MSG_WHAT_GONG;
                msg.setData(mBundle);
                mHandler.sendMessage(msg);
            }
        });

        //编辑
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(mActivity, OrderAddEstimateActivity.class);
                mIntent.putExtra("orderId",orderId);
                mIntent.putExtra("fromPage", OrderEstimateListActivity.PAGE_EDIT);
                mIntent.putExtra(ExtraAndResult.RESULT_DATA,mData.get(position));
                mActivity.startActivityForResult(mIntent,ExtraAndResult.REQUEST_CODE_STAGE);
            }
        });

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

        LinearLayout btn_edit;     //编辑
        LinearLayout btn_delete;   //删除

    }
}
