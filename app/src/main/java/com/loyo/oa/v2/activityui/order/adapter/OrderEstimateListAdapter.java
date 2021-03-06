package com.loyo.oa.v2.activityui.order.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddEstimateActivity;
import com.loyo.oa.v2.activityui.order.OrderAttachmentActivity;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

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
    private int fromPage, orderStatus;
    private int requestPage;
    private boolean isAdd;

    public OrderEstimateListAdapter(Activity activity, ArrayList<EstimateAdd> data, Handler handler, String orderId, int fromPage, boolean isAdd) {
        this.mActivity = activity;
        this.mData = data;
        this.mHandler = handler;
        this.orderId = orderId;
        this.fromPage = fromPage;
        this.isAdd = isAdd;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
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
        holder.tv_esttime.setText(DateTool.getDateFriendly(mEstimateAdd.receivedAt ));
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
        OrderCommon.getEstimateStatus(holder.tv_status, mEstimateAdd.status);

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
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("edit_index", position);
                mBundle.putSerializable(OrderAddEstimateActivity.KEY_USER_INFO, map);
                mBundle.putSerializable(ExtraAndResult.RESULT_DATA, mData.get(position));
                MainApp.getMainApp().startActivityForResult(mActivity,
                        OrderAddEstimateActivity.class, MainApp.ENTER_TYPE_RIGHT, requestPage, mBundle);
            }
        });
        LogUtil.dee("status:"+mEstimateAdd.status);
        holder.ll_action.setVisibility(isAdd?View.VISIBLE:View.GONE);
        if (!isAdd) {
            holder.ll_action.setVisibility(View.GONE);
        } else {
            //当订单状态为待审批 审批中 已通过 已完成时，不能编辑和删除
            if (mEstimateAdd.status == 1
                    || mEstimateAdd.status == 2
                    || mEstimateAdd.status == 4
                    || mEstimateAdd.status == 5
                    || mEstimateAdd.status == 7) {
                holder.ll_action.setVisibility(View.GONE);
            } else if(mEstimateAdd.status == 3){
                holder.ll_action.setVisibility(View.VISIBLE);
            }
        }

        //附件监听
        holder.ll_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBundle = new Bundle();
                mBundle.putInt("bizType", 26);
                mBundle.putBoolean("isOver", true);
                mBundle.putString("uuid", mEstimateAdd.attachmentUUId);
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, false);
                MainApp.getMainApp().startActivityForResult(mActivity, OrderAttachmentActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
            }
        });


        holder.tv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEstimateAdd.wfId)) {//没有生成审批和不需要审批不可点击进入审批详情  mEstimateAdd.status != 6 && mEstimateAdd.status != 0
                    Intent intentWf = new Intent();
                    intentWf.putExtra(ExtraAndResult.EXTRA_ID, mEstimateAdd.wfId);
                    intentWf.setClass(mActivity, WfinstanceInfoActivity_.class);
                    mActivity.startActivityForResult(intentWf, ExtraAndResult.REQUEST_CODE);
                    mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                }
            }
        });
        holder.tv_status.setVisibility(orderStatus == 1 ? View.GONE : View.VISIBLE);//订单待审核不显示回款记录状态
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
