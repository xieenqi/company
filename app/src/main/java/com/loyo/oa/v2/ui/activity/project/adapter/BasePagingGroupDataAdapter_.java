package com.loyo.oa.v2.ui.activity.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * com.loyo.oa.v2.adapter
 * 描述 :项目下子内容适配器（排序需要），任务计划Listview，X个任务进行中，适配器
 * 作者 : ykb
 * 时间 : 15/9/10.
 */
public abstract class BasePagingGroupDataAdapter_<T extends BaseBeans> extends BaseExpandableListAdapter {

    protected ArrayList<PagingGroupData_<T>> pagingGroupDatas;
    protected Context mContext;
    protected MainApp app;

    public BasePagingGroupDataAdapter_() {
        app = MainApp.getMainApp();
    }

    public void setData(final ArrayList<PagingGroupData_<T>> data) {
        pagingGroupDatas = data;
    }

    //上拉加载时不会滑到第一页需要获取数据源 ykb 07-15
    public List<PagingGroupData_<T>> getData() {
        return pagingGroupDatas;
    }

    @Override
    public int getGroupCount() {
        return pagingGroupDatas.size();
    }

    @Override
    public int getChildrenCount(final int groupPosition) {
        return pagingGroupDatas.get(groupPosition).getRecords().size();
    }

    @Override
    public Object getGroup(final int groupPosition) {
        return pagingGroupDatas.get(groupPosition);
    }

    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return pagingGroupDatas.get(groupPosition).getRecords().get(childPosition);
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sign_show_group, null);
        }
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        ImageView img_status = ViewHolder.get(convertView, R.id.img_time_point);

        PagingGroupData_ data = pagingGroupDatas.get(groupPosition);

        LogUtil.dll("status:" + data.getOrderStr());

        /*列表状态条 颜色设置*/
        if (data != null && data.getOrderStr() != null) {
            if (data.getOrderStr().contains("已")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.isfinish));
                img_status.setImageResource(R.drawable.bg_view_green_circle);
            } else if (data.getOrderStr().contains("未完成") || data.getOrderStr().contains("待点评")
                    || data.getOrderStr().contains("待审批") || data.getOrderStr().contains("进行中")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.isteston));
                img_status.setImageResource(R.drawable.bg_view_purple_circle);
            } else if (data.getOrderStr().contains("待审核") || data.getOrderStr().contains("审")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.title_bg1));
                img_status.setImageResource(R.drawable.bg_view_blue_circle);
            } else if (data.getOrderStr().contains("未")) {
                tv_title.setTextColor(mContext.getResources().getColor(R.color.wfinstance_notagree));
                img_status.setImageResource(R.drawable.bg_view_red_circle);
            }
            //暂时不要数量
            //tv_title.setText(pagingGroupDatas.get(groupPosition).getRecords().size()+data.getOrderStr());
            tv_title.setText(data.getOrderStr());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }

    public abstract View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent);
}
