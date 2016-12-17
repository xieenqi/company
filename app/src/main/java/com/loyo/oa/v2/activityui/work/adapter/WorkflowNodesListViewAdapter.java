package com.loyo.oa.v2.activityui.work.adapter;


import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.wfinstance.bean.WfNodes;

import java.util.ArrayList;

/**
 * 审批流程 展示 适配器
 * Created by Administrator on 2014/12/11 0011.
 */
public class WorkflowNodesListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<WfNodes> lstData;
    private int wfInstanceStatus;

    public WorkflowNodesListViewAdapter(int _wfInstanceStatus, ArrayList<WfNodes> lstData, LayoutInflater _mInflater) {
        mInflater = _mInflater;
        wfInstanceStatus = _wfInstanceStatus;
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
        if (convertView == null) {
            item_info = new Item_info();
            convertView = mInflater.inflate(R.layout.item_workflownodes_new, null);
            item_info.img_left = (ImageView) convertView.findViewById(R.id.img_left);
            item_info.tv_set_time = (TextView) convertView.findViewById(R.id.tv_set_time);
            item_info.tv_overtime = (TextView) convertView.findViewById(R.id.tv_overtime);
            item_info.tv_creator_title = (TextView) convertView.findViewById(R.id.tv_creator_title);
            item_info.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            item_info.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(item_info);
        } else {
            item_info = (Item_info) convertView.getTag();
        }

        WfNodes wfNodes = lstData.get(position);
        if (wfNodes != null) {
            User executor = wfNodes.getExecutorUser();
            String actionName = executor!=null? executor.getRealname(): "无";//节点人的名字
            String actionInfo = wfNodes.getComment();//处理意见

            if (TextUtils.isEmpty(wfNodes.title)) {
                if (wfNodes.getExecutorUser() != null) {
                    item_info.tv_creator_title.setText(actionName + (wfNodes.isNeedApprove() ? "审批意见" : "办结意见"));
                }
            } else {
                item_info.tv_creator_title.setText(wfNodes.title);
            }

            /**是否限时，是否超时*/
            if (wfNodes.getRemindAt() != 0) {
                item_info.tv_set_time.setVisibility(View.VISIBLE);
                item_info.tv_set_time.setText("限" + wfNodes.getRemindAt() + "小时");
                if (wfNodes.isOverTime()) {
                    item_info.tv_overtime.setVisibility(View.VISIBLE);
                } else {
                    item_info.tv_overtime.setVisibility(View.GONE);
                }
            } else {
                item_info.tv_set_time.setVisibility(View.GONE);
            }

            /*已通过*/
            if (wfInstanceStatus == 4) {
                item_info.img_left.setImageResource(R.drawable.img_wfinstance_agree_new);
                item_info.tv_content.setTextColor(Color.parseColor("#333333"));
                item_info.tv_content.setText(actionName + "：" + (TextUtils.isEmpty(actionInfo) ? "同意" : actionInfo));
                item_info.tv_time.setVisibility(View.VISIBLE);
//                item_info.tv_time.setText(DateTool.timet(wfNodes.getUpdateAt() + "", DateTool.DATE_FORMATE_SPLITE_BY_POINT));
                item_info.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(wfNodes.getUpdateAt()));
            } else {
                if (wfNodes.getActive() == 1) {
                    item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait_new);
                    item_info.tv_content.setTextColor(Color.parseColor("#999999"));
                    item_info.tv_content.setText(actionName + "(待处理)");
                } else if (wfNodes.getActive() == 2) {
                    item_info.img_left.setImageResource(R.drawable.img_wfinstance_wait_new);
                    item_info.tv_content.setTextColor(Color.parseColor("#999999"));
                    item_info.tv_content.setText(actionName + "(待处理)");
                } else if (wfNodes.getActive() == 3) {
                    if (wfNodes.isApproveFlag()) {
                        item_info.img_left.setImageResource(wfNodes.isNeedApprove() ? R.drawable.img_wfinstance_agree_new : R.drawable.img_wfinstance_complete_new);
                        item_info.tv_content.setTextColor(Color.parseColor("#333333"));
                        item_info.tv_content.setText(actionName + "：" + (TextUtils.isEmpty(actionInfo) ? (wfNodes.isNeedApprove() ? "同意" : "已办结") :
                                actionInfo));
                        item_info.tv_time.setVisibility(View.VISIBLE);
//                        item_info.tv_time.setText(DateTool.timet(wfNodes.getUpdateAt() + "", DateTool.DATE_FORMATE_SPLITE_BY_POINT));
                        item_info.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(wfNodes.getUpdateAt()));
                    } else {
                        item_info.img_left.setImageResource(R.drawable.img_wfinstance_notagree_new);
                        item_info.tv_content.setTextColor(Color.parseColor("#333333"));
                        item_info.tv_content.setText(actionName + "：" + actionInfo);
                        item_info.tv_time.setVisibility(View.VISIBLE);
//                        item_info.tv_time.setText(DateTool.timet(wfNodes.getUpdateAt() + "", DateTool.DATE_FORMATE_SPLITE_BY_POINT));
                        item_info.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(wfNodes.getUpdateAt()));
                        item_info.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(wfNodes.getUpdateAt()));
                    }
                }
            }
        }
        return convertView;
    }

    public class Item_info {
        public ImageView img_left;
        public TextView tv_overtime;//超时
        public TextView tv_creator_title;
        public TextView tv_content;//处理意见
        public TextView tv_set_time;//限时
        public TextView tv_time;
    }
}
