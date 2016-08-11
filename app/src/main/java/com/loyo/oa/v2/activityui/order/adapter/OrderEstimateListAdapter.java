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
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 【回款记录】适配器
 * Created by yyy on 16/8/3.
 */
public class OrderEstimateListAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<EstimateAdd> mData;
    private Handler mHandler;
    private Bundle mBundle;
    private Intent mIntent;
    private Message msg;
    private String orderId;
    private int fromPage;
    private int requestPage;

    public OrderEstimateListAdapter(Activity activity, ArrayList<EstimateAdd> data, Handler handler, String orderId, int fromPage) {
        this.mActivity = activity;
        this.mData = data;
        this.mHandler = handler;
        this.orderId = orderId;
        this.fromPage = fromPage;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_orderestimate, null);
        ViewHolder holder = null;
        final EstimateAdd mEstimateAdd = mData.get(position);
        if (null == holder) {
            holder = new ViewHolder();
            holder.tv_esttime = (TextView) convertView.findViewById(R.id.tv_esttime);
            holder.tv_esttime_price = (TextView) convertView.findViewById(R.id.tv_esttime_price);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_payee = (TextView) convertView.findViewById(R.id.tv_payee);
            holder.tv_payment = (TextView) convertView.findViewById(R.id.tv_payment);
            holder.tv_Remarks = (TextView) convertView.findViewById(R.id.tv_Remarks);
            holder.tv_titlenum = (TextView) convertView.findViewById(R.id.tv_titlenum);
            holder.btn_edit = (LinearLayout) convertView.findViewById(R.id.btn_edit);
            holder.btn_delete = (LinearLayout) convertView.findViewById(R.id.btn_delete);
            holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            holder.ll_action = (LinearLayout) convertView.findViewById(R.id.ll_action);
            holder.tv_attachment = (TextView) convertView.findViewById(R.id.tv_attachment);
            holder.ll_attachment = (LinearLayout) convertView.findViewById(R.id.ll_attachment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int index = position + 1;
        holder.tv_titlenum.setText("回款记录" + index);
        holder.tv_esttime.setText(DateTool.timet(mEstimateAdd.receivedAt + "", "yyyy-MM-dd"));
        holder.tv_esttime_price.setText("￥" + mEstimateAdd.receivedMoney);
        holder.tv_price.setText("￥" + mEstimateAdd.billingMoney);
        holder.tv_payee.setText(mEstimateAdd.payeeUser.name);
        holder.tv_Remarks.setText(mEstimateAdd.remark);
        holder.tv_payment.setText(OrderCommon.getPaymentMode(mEstimateAdd.payeeMethod));
        if (mEstimateAdd.attachmentCount != 0) {
            holder.tv_attachment.setText("附件(" + mEstimateAdd.attachmentCount + ")");
        }
        holder.btn_delete.setOnTouchListener(Global.GetTouch());
        holder.btn_edit.setOnTouchListener(Global.GetTouch());
        OrderCommon.getOrderStatus(holder.tv_status, mEstimateAdd.status);
        //删除
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = new Message();
                mBundle = new Bundle();
                mBundle.putInt("posi", position);
                msg.what = ExtraAndResult.MSG_WHAT_GONG;
                msg.setData(mBundle);
                mHandler.sendMessage(msg);
            }
        });

        //编辑
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBundle = new Bundle();
                mBundle.putInt("size", mEstimateAdd.attachmentCount);
                mBundle.putString("orderId", orderId);
                if (fromPage == OrderEstimateListActivity.ORDER_ADD) {
                    requestPage = OrderEstimateListActivity.OADD_EST_EDIT;
                } else if (fromPage == OrderEstimateListActivity.ORDER_DETAILS) {
                    requestPage = OrderEstimateListActivity.ODET_EST_EDIT;
                }
                mBundle.putInt("fromPage", requestPage);
                mBundle.putSerializable(ExtraAndResult.RESULT_DATA, mData.get(position));
                MainApp.getMainApp().startActivityForResult(mActivity, OrderAddEstimateActivity.class, MainApp.ENTER_TYPE_RIGHT, requestPage, mBundle);
            }
        });
        if (mEstimateAdd.status == 1 || mEstimateAdd.status == 3) {
            holder.ll_action.setVisibility(View.GONE);
        } else {
            holder.ll_action.setVisibility(View.VISIBLE);
        }

        //附件监听
        holder.ll_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }


    public class ViewHolder {

        TextView tv_esttime;       //回款日期
        TextView tv_esttime_price; //回款金额
        TextView tv_price;         //开票
        TextView tv_payee;         //收款人
        TextView tv_payment;       //收款方式
        TextView tv_Remarks;       //备注
        TextView tv_titlenum, tv_status;
        TextView tv_attachment;    //附件

        LinearLayout ll_attachment;
        LinearLayout btn_edit;     //编辑
        LinearLayout btn_delete, ll_action;   //删除

    }
}
