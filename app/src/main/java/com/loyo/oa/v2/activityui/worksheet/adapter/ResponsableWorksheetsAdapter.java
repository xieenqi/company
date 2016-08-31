package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.common.GroupsData;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.customview.multi_image_selector.bean.Image;

import java.util.Date;

/**
 * Created by EthanGong on 16/8/27.
 */
public class ResponsableWorksheetsAdapter extends BaseGroupsDataAdapter {
    public ResponsableWorksheetsAdapter(final Context context, final GroupsData data) {
        super();
        mContext = context;
        groupsData = data;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_worksheet_event_wrapper, null, false);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_worksheet = (TextView) convertView.findViewById(R.id.tv_worksheet);
            holder.tv_deadline = (TextView) convertView.findViewById(R.id.tv_deadline);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.iv_action = (ImageView) convertView.findViewById(R.id.iv_action);
            final ViewHolder holderFinal = holder;
            holder.iv_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorksheetEvent wse = holderFinal.wse;

                }
            });
            convertView.setTag(holder);

        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        WorksheetEvent wse = (WorksheetEvent) getChild(groupPosition, childPosition);
        holder.loadData(wse);
        return convertView;
    }

    private class ViewHolder {

        TextView tv_content;
        TextView tv_worksheet;
        TextView tv_deadline;

        TextView tv_time;
        ImageView iv_action;

        WorksheetEvent wse;

        public void loadData(WorksheetEvent wse) {
            this.wse = wse;
            tv_content.setText(wse.content);
            tv_worksheet.setText(wse.title);
            tv_deadline.setText(wse.daysDeadline + "天");

            tv_time.setText(app.df3.format(new Date(wse.updatedAt*1000)));
            // if (wse.status == WorksheetEventStatus.WAITPROCESS) {
            if (wse.status == WorksheetEventStatus.UNACTIVATED) { //  测试
                iv_action.setVisibility(View.VISIBLE);
            }
            else {
                iv_action.setVisibility(View.GONE);
            }
        }
    }
}
