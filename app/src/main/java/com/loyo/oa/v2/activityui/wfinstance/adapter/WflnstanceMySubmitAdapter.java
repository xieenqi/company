package com.loyo.oa.v2.activityui.wfinstance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceItemData;
import com.loyo.oa.v2.activityui.wfinstance.bean.WflnstanceListItem;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xeq on 16/7/21.
 */
public class WflnstanceMySubmitAdapter extends BaseExpandableListAdapter {

    ArrayList<WflnstanceItemData> datas;
    protected Context mContext;

    public WflnstanceMySubmitAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(ArrayList<WflnstanceItemData> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return null == datas ? 0 : datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (null == datas) {
            return 0;
        }
        return null == datas.get(groupPosition).records ? 0 : datas.get(groupPosition).records.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return datas.get(groupPosition).records.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
        }
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        ImageView img_status = ViewHolder.get(convertView, R.id.img_time_point);
        WflnstanceItemData data = null;
        if (groupPosition >= 0)
            data = datas.get(groupPosition);

        /*列表状态条 颜色设置*/
        if (data != null && data.orderStr != null) {
            if (data.orderStr.contains("待审批") || data.orderStr.contains("未到我审批的")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.isteston));
                img_status.setImageResource(R.drawable.bg_view_purple_circle);
            } else if (data.orderStr.contains("审批中")||data.orderStr.contains("待我审批的")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.title_bg1));
                img_status.setImageResource(R.drawable.bg_view_blue_circle);
            } else if (data.orderStr.contains("未通过") || data.orderStr.contains("我驳回的")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.wfinstance_notagree));
                img_status.setImageResource(R.drawable.bg_view_red_circle);
            } else if (data.orderStr.contains("已通过") || data.orderStr.contains("我同意的")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.isfinish));
                img_status.setImageResource(R.drawable.bg_view_green_circle);
            }
            //暂时不要数量
            //tv_title.setText(pagingGroupDatas.get(groupPosition).getRecords().size()+data.getOrderStr());
            tv_title.setText(data.orderStr);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
        }
        WflnstanceListItem wfInstance = (WflnstanceListItem) getChild(groupPosition, childPosition);
        TextView title = ViewHolder.get(convertView, R.id.tv_title);
        TextView content = ViewHolder.get(convertView, R.id.tv_content);
        TextView time = ViewHolder.get(convertView, R.id.tv_time);
        TextView timeOut = ViewHolder.get(convertView, R.id.tv_timeOut);
        ImageView iv_repeattask = ViewHolder.get(convertView, R.id.iv_repeattask);
        View ack = ViewHolder.get(convertView, R.id.view_ack);
        ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);

        if (wfInstance.title != null) {
            title.setText(wfInstance.title);
        }

//        time.setText("提交时间: " + MainApp.getMainApp().df3.format(new Date(wfInstance.createdAt * 1000)));
        time.setText("提交时间：" + DateTool.getDateTimeFriendly(wfInstance.createdAt));
        if (wfInstance.nextExecutorName != null) {
            content.setText("审批人：" + wfInstance.nextExecutorName);
        }
        ack.setVisibility(wfInstance.viewed ? View.GONE : View.VISIBLE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
