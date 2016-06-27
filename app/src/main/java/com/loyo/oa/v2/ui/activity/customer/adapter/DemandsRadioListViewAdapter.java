package com.loyo.oa.v2.ui.activity.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.customer.activity.DemandsAddActivity;
import com.loyo.oa.v2.ui.activity.customer.activity.DemandsManageActivity;
import com.loyo.oa.v2.ui.activity.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CommonTag;
import com.loyo.oa.v2.beans.Demand;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.ViewHolder;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 【购买意向】适配器
 * Created by Administrator on 2014/12/11 0011.
 */
public class DemandsRadioListViewAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    ArrayList<Demand> lstData;
    private Context mContext;
    private boolean isMyUser;
    private String customerId, customerName;

    public DemandsRadioListViewAdapter(final Context context, final ArrayList<Demand> lstData, final boolean isMyUser, final String customerId, final String customerName) {
        mInflater = LayoutInflater.from(context);
        this.lstData = lstData;
        mContext = context;
        this.isMyUser = isMyUser;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    private String setValueFloat(Object obj) {
        if (null == obj) {
            return "没有内容";
        }
            BigDecimal bigDecimal = new BigDecimal(obj + "");
            return bigDecimal.toPlainString() + "";
    }

    @Override
    public int getCount() {
        return lstData.size();
    }

    @Override
    public Object getItem(final int position) {
        return lstData.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
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
        if (demand.getProduct() != null) {
            tv_customer_name.setText(TextUtils.isEmpty(demand.getProduct().name) ? "无" : demand.getProduct().name);
        }

        tv_phase.setText("阶段：" + demand.getSaleStage().getName() + wfStatusText(demand.getWfState()));
        tv_content_plan.setText("预估：\t数量\t\t" + setValueFloat(demand.getEstimatedNum()) + "\t\t\t\t单价\t\t" + setValueFloat(demand.getEstimatedPrice()) + " "
                + (null == demand.getProduct() ? "" : demand.getProduct().unit));
        tv_content_act.setText("成交：\t数量\t\t" + setValueFloat(demand.getActualNum()) + "\t\t\t\t单价\t\t" + setValueFloat(demand.getActualPrice()) + " "
                + (null == demand.getProduct() ? "" : demand.getProduct().unit));

/*        tv_phase.setText("阶段：" + demand.getSaleStage().getName() + wfStatusText(demand.getWfState()));
        tv_content_plan.setText("预估：\t数量\t\t" + demand.getEstimatedNum() + "\t\t\t\t单价\t\t" + demand.getEstimatedPrice() + " "
                + (null == demand.getProduct() ? "" : demand.getProduct().unit));
        tv_content_act.setText("成交：\t数量\t\t" + demand.getActualNum() + "\t\t\t\t单价\t\t" + demand.getActualPrice() + " "
                + (null == demand.getProduct() ? "" : demand.getProduct().unit));*/

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

        if (!isMyUser || 1 == demand.getWfState() || 2 == demand.getWfState() || 4 == demand.getWfState() || 5 == demand.getWfState()) {
            img_edit_damand.setVisibility(View.GONE);
        } else if (3 == demand.getWfState() || isMyUser) {
            img_edit_damand.setVisibility(View.VISIBLE);
        }

        img_edit_damand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                editDemand(demand);
            }
        });
        /**
         * 赢单状态可以看审批详情
         */
        if (0 != demand.getWfState()) {
            convertView.setOnTouchListener(Global.GetTouch());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    goWfInstance(demand.getWfId());
                }
            });
        }
        return convertView;
    }

    /**
     * 审批
     *
     * @param wFInstanceId
     */
    private void goWfInstance(final String wFInstanceId) {
        Bundle b = new Bundle();
        b.putString(ExtraAndResult.EXTRA_ID, wFInstanceId);
        MainApp.getMainApp().startActivityForResult((DemandsManageActivity) mContext, WfinstanceInfoActivity_.class, 0, BaseMainListFragment.REQUEST_REVIEW, b);
    }

    /**
     * 编辑购买意向
     *
     * @param data
     */
    private void editDemand(final Demand data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Demand.class.getName(), data);
        bundle.putString(ExtraAndResult.EXTRA_NAME, customerName);
        bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
        MainApp.getMainApp().startActivityForResult((Activity) mContext, DemandsAddActivity.class,
                MainApp.ENTER_TYPE_BUTTOM, DemandsManageActivity.VIEW_DEMANDS, bundle);
    }

    private String wfStatusText(final int index) {
        switch (index) {
            case WfInstance.STATUS_NEW:
                return "(待审核)";
            case WfInstance.STATUS_PROCESSING:
                return "(审核中)";
            case WfInstance.STATUS_ABORT:
                return "(未通过)";
            case WfInstance.STATUS_APPROVED:
                return "(已通过)";
            case WfInstance.STATUS_FINISHED:
                return "(已完结)";
            default:

                break;
        }
        return "";
    }
}
