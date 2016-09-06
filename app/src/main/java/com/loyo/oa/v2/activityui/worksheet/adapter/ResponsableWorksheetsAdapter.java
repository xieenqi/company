package com.loyo.oa.v2.activityui.worksheet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventStatus;
import com.loyo.oa.v2.activityui.worksheet.event.WorksheetEventFinishAction;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.tool.DateTool;

/**
 * Created by EthanGong on 16/8/27.
 */
public class ResponsableWorksheetsAdapter extends BaseGroupsDataAdapter {

    private long nowTime;
    private int fromPage;

    public ResponsableWorksheetsAdapter(final Context context, final GroupsData data, int fromPage) {
        super();
        mContext = context;
        groupsData = data;
        this.fromPage = fromPage;
        nowTime = Long.parseLong(DateTool.getDataOne(DateTool.getNowTime("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_worksheet_event_wrapper, null, false);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_endtime_tag = (TextView) convertView.findViewById(R.id.tv_endtime_tag);
            holder.tv_worksheet = (TextView) convertView.findViewById(R.id.tv_worksheet);
            holder.tv_deadline = (TextView) convertView.findViewById(R.id.tv_deadline);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.iv_action = (ImageView) convertView.findViewById(R.id.iv_action);
            final ViewHolder holderFinal = holder;
            holder.iv_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final WorksheetEvent wse = holderFinal.wse;
                    if (wse.id == null) {
                        return;
                    }

                    WorksheetEventFinishAction action = new WorksheetEventFinishAction();
                    action.data = wse;
                    action.eventCode = fromPage;
                    AppBus.getInstance().post(action);

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
        TextView tv_endtime_tag;

        TextView tv_time;
        ImageView iv_action;

        WorksheetEvent wse;

        public void loadData(WorksheetEvent wse) {
            this.wse = wse;
            tv_content.setText(wse.content);
            tv_worksheet.setText(wse.title);


            tv_endtime_tag.setVisibility(View.GONE);
            tv_deadline.setTextColor(mContext.getResources().getColor(R.color.text99));
            if(wse.endTime != 0){
                /*是否超时判断*/
                if(nowTime > wse.endTime && wse.status != WorksheetEventStatus.FINISHED){
                    tv_deadline.setTextColor(mContext.getResources().getColor(R.color.red1));
                    tv_deadline.setText(DateTool.getDiffTime(wse.endTime));
                    tv_endtime_tag.setVisibility(View.VISIBLE);
                }else{
                    tv_deadline.setText(DateTool.getDiffTime(wse.endTime));
                }
            }else{
                tv_deadline.setText("--");
            }


            //TODO 我负责的 未触发不显示触发时间
            if(wse.status == WorksheetEventStatus.UNACTIVATED){
                tv_time.setText(""+wse.daysLater+"天以后触发");
                tv_time.setVisibility(View.INVISIBLE);
            }else{
                tv_time.setText(DateTool.getDiffTime(wse.startTime));
                tv_time.setVisibility(View.VISIBLE);
            }

            if (wse.status == WorksheetEventStatus.WAITPROCESS) {
                iv_action.setVisibility(View.VISIBLE);
            }
            else {
                iv_action.setVisibility(View.GONE);
            }
        }
    }
}
