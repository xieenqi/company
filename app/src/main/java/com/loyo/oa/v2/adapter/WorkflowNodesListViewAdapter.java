package com.loyo.oa.v2.adapter;


import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WfNodes;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.StringUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2014/12/11 0011.
 */
public class WorkflowNodesListViewAdapter extends BaseAdapter {
    MainApp app;
    LayoutInflater mInflater;
    ArrayList<WfNodes> lstData;
    int wfInstanceStatus;

    public WorkflowNodesListViewAdapter(int _wfInstanceStatus, ArrayList<WfNodes> lstData, LayoutInflater _mInflater) {
        mInflater = _mInflater;
        wfInstanceStatus = _wfInstanceStatus;
        app = MainApp.getMainApp();
        this.lstData = lstData;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        Item_info item_info;
        //convertView为null的时候初始化convertView。
        if (convertView == null) {
            item_info = new Item_info();
            convertView = mInflater.inflate(R.layout.item_workflownodes, null);
            item_info.img_left = (ImageView) convertView.findViewById(R.id.img_left);
            item_info.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            item_info.tv_deal_time = (TextView) convertView.findViewById(R.id.tv_deal_time);
            item_info.tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
            item_info.tv_creator_title = (TextView) convertView.findViewById(R.id.tv_creator_title);
            item_info.tv_result = (TextView) convertView.findViewById(R.id.tv_result);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        WfNodes wfNodes = lstData.get(position);
        if (wfNodes != null) {
            if (wfNodes.getExecutorUser() != null) {
                item_info.tv_name.setText(wfNodes.getExecutorUser().getRealname());

                try {
                    item_info.tv_creator_title.setText(wfNodes.getExecutorUser().getShortPosition().getName());
                } catch (Exception ex) {
                    item_info.tv_creator_title.setText("");
                    Global.ProcException(ex);
                }
            }

            item_info.tv_result.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.pxTosp(app.diptoPx(20)));

            //审批通过
            if (wfNodes.isActive() && wfNodes.isApproveFlag()) {
                item_info.tv_deal_time.setText(app.df3.format(new Date(wfNodes.getUpdateAt()*1000)));
                if (!StringUtil.isEmpty(wfNodes.getComment())) {
                    item_info.tv_comment.setVisibility(View.VISIBLE);
                    item_info.tv_comment.setText(wfNodes.getComment());
                } else {
                    item_info.tv_comment.setVisibility(View.GONE);
                }

                //已否决
                if (wfInstanceStatus == WfInstance.STATUS_ABORT) {
                    for (int i = lstData.size() - 1; i >= 0; i--) {
                        if (lstData.get(i).isActive() &&
                                lstData.get(i).isNeedApprove() &&
                                lstData.get(i).isApproveFlag()) {

                            if (TextUtils.equals(lstData.get(i).getId() , wfNodes.getId())) {
                                item_info.img_left.setImageResource(R.drawable.img_wfinstance_notagree);
                                item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notagree));
                                item_info.tv_result.setText("驳回");
                            } else {
                                item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree);
                                item_info.tv_result.setText("同意");
                            }
                            break;
                        }
                    }
                } else if (wfInstanceStatus == WfInstance.STATUS_FINISHED
                        && TextUtils.equals(lstData.get(lstData.size() - 1).getId() , wfNodes.getId())) {
                    item_info.img_left.setImageResource(R.drawable.img_wfinstance_complete);
                    item_info.tv_result.setText("完成");
                } else {
                    item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree);
                    item_info.tv_result.setText("同意");
                }
            } else {
                item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait);
                item_info.tv_deal_time.setText("");
                item_info.tv_result.setText("待处理");
                item_info.tv_comment.setVisibility(View.GONE);
                item_info.tv_result.setTextColor(convertView.getResources().getColor(R.color.wfinstance_notprocess));
            }
        }

        return convertView;
    }

    public class Item_info {
        public ImageView img_left;
        public TextView tv_deal_time;
        public TextView tv_creator_title;
        public TextView tv_name;
        public TextView tv_comment;
        public TextView tv_result;
    }
}
