package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.DemandsAddActivity;
import com.loyo.oa.v2.activity.DemandsManageActivity;
import com.loyo.oa.v2.activity.WfinstanceInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CommonTag;
import com.loyo.oa.v2.beans.Demand;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class DemandsRadioListViewAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<Demand> lstData;
    private Context mContext;
    private boolean isMyUser;

    public DemandsRadioListViewAdapter(Context context, ArrayList<Demand> lstData,boolean isMyUser) {
        mInflater = LayoutInflater.from(context);
        this.lstData = lstData;
        mContext = context;
        this.isMyUser = isMyUser;
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(int position) {
        return lstData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_listview_demands, parent, false);
        }

        ImageView img_edit_damand = ViewHolder.get(convertView, R.id.img_edit_damand);
        TextView tv_customer_name = ViewHolder.get(convertView, R.id.tv_customer_name);
        TextView tv_phase = ViewHolder.get(convertView, R.id.tv_phase);
        TextView tv_content_plan = ViewHolder.get(convertView, R.id.tv_damand_list_estimated);
        TextView tv_content_act = ViewHolder.get(convertView, R.id.tv_damand_list_act);
        TextView tv_lose_reason = ViewHolder.get(convertView, R.id.tv_demand_lose_reason);
        TextView tv_memo = ViewHolder.get(convertView, R.id.tv_memo);

        final Demand demand = lstData.get(position);

        tv_customer_name.setText(demand.getProduct().getName());
        tv_phase.setText("阶段：" + demand.getSaleStage().getName());
        tv_content_plan.setText("预估：\t数量\t\t" + demand.getEstimatedNum() + "\t\t\t\t单价\t\t" + demand.getEstimatedPrice());
        tv_content_act.setText("成交：\t数量\t\t" + demand.getActualNum() + "\t\t\t\t单价\t\t" + demand.getActualPrice());
        if (demand.getSaleStage().getProb() == 0) {
            tv_lose_reason.setVisibility(View.VISIBLE);
            StringBuilder reason = new StringBuilder("输单原因：");
            if (null != demand.getLoseReason() && !demand.getLoseReason().isEmpty()) {
                for (int i = 0; i < demand.getLoseReason().size(); i++) {
                    CommonTag tag = demand.getLoseReason().get(i);
                    reason.append(tag.getName());
                    if (i != demand.getLoseReason().size() - 1) {
                        reason.append(",");
                    }
                }
            }
            tv_lose_reason.setText(reason);
        } else {
            tv_lose_reason.setVisibility(View.GONE);
        }

        tv_memo.setText("备注：" + demand.getMemo());

        if(!isMyUser){
            img_edit_damand.setVisibility(View.GONE);
        }

        img_edit_damand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDemand(demand);
            }
        });
        if (demand.getWfState() == WfInstance.STATUS_PROCESSING) {
            tv_phase.setOnTouchListener(Global.GetTouch());
            tv_phase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goWfInstance(demand.getWfId());
                }
            });
        } return convertView;
    }

    /**
     * 审批
     * @param wFInstanceId
     */
    private void goWfInstance(String wFInstanceId) {
        Bundle b=new Bundle();
        b.putString("id",wFInstanceId);
       MainApp.getMainApp().startActivityForResult((DemandsManageActivity)mContext, WfinstanceInfoActivity_.class,0, BaseMainListFragment.REQUEST_REVIEW,b);
    }

    /**
     * 编辑购买意向
     *
     * @param data
     */
    private void editDemand(Demand data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Demand.class.getName(), data);
        MainApp.getMainApp().startActivityForResult((Activity) mContext, DemandsAddActivity.class, MainApp.ENTER_TYPE_BUTTOM, DemandsManageActivity.VIEW_DEMANDS, bundle);
    }
}
