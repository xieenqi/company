package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.commonview.DiscussionActivity;
import com.loyo.oa.v2.activity.commonview.DiscussionActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.DiscussCounter;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

/**
 * 任务管理列表的adapter
 * <p>
 * xnq
 */
public class CommonExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter_<T> {

    private int textColor = R.color.tasklist_gray;
    private boolean isOk;

    public CommonExpandableListAdapter(final Context context, final ArrayList<PagingGroupData_<T>> data) {
        super();
        mContext = context;
        pagingGroupDatas = data;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
        }

        Object obj = getChild(groupPosition, childPosition);
        TextView title = ViewHolder.get(convertView, R.id.tv_title);
        TextView content = ViewHolder.get(convertView, R.id.tv_content);
        TextView time = ViewHolder.get(convertView, R.id.tv_time);
        TextView timeOut = ViewHolder.get(convertView, R.id.tv_timeOut);
        ImageView iv_repeattask = ViewHolder.get(convertView,R.id.iv_repeattask);
        View ack = ViewHolder.get(convertView, R.id.view_ack);
        ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
        TextView tv_discuss_num = ViewHolder.get(convertView, R.id.tv_disscuss_num);
        ImageView iv_disscuss_status = ViewHolder.get(convertView, R.id.img_discuss_status);

        /**审批*/

        if (obj instanceof WfInstance) {
            WfInstance wfInstance = (WfInstance) obj;
            if (wfInstance.title != null) {
                title.setText(wfInstance.title);
            }
            time.setText("提交时间: " + app.df3.format(new Date(wfInstance.createdAt * 1000)));
            if (wfInstance.creator != null && wfInstance.nextExecutor != null) {
                content.setText("审批人: " + wfInstance.nextExecutor.getRealname());
            }
            //ack.setVisibility(wfInstance.ack ? View.GONE : View.VISIBLE);

            /**任务*/

        } else if (obj instanceof Task) {
            //layout_discuss.setVisibility(View.VISIBLE); //右侧讨论暂时隐藏
            Task task = (Task) obj;
            /*任务超时判断*/
            try {
                /**重复任务赋值*/
                if(null != task.getCornBody() && task.getCornBody().getType() != 0){
                    isOk = true;
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_repeattask);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

                    timeOut.setVisibility(View.GONE);
                    textColor = R.color.title_bg1;
                    String caseName = "";
                    String hourMins = "";
                    String weekName = "";
                    String dayName = "";

                    String hour = "";
                    String mins = "";
                    switch(task.getCornBody().getType()){
                        case 1:
                            caseName = "每天";
                            break;

                        case 2:
                            caseName = "每周";
                            break;

                        case 3:
                            caseName = "每月";
                            break;
                    }
                    hour = task.getCornBody().getHour()+"";
                    mins = task.getCornBody().getMinute()+"";

            /*如果小时分钟为单数，则前面拼上0*/
                    if(hour.length() == 1){
                        hour = "0"+hour;
                    }

                    if(mins.length() == 1){
                        mins = "0"+mins;
                    }
                    hourMins = hour+":"+mins;

                    //每天
                    if(task.getCornBody().getType() == 1){
                        time.setText(caseName + " " + hourMins + " 重复");
                        //每周
                    }else if(task.getCornBody().getType() == 2){
                        switch (task.getCornBody().getWeekDay()){
                            case 1:
                                weekName = "日";
                                break;

                            case 2:
                                weekName = "一";
                                break;

                            case 3:
                                weekName = "二";
                                break;

                            case 4:
                                weekName = "三";
                                break;

                            case 5:
                                weekName = "四";
                                break;

                            case 6:
                                weekName = "五";
                                break;

                            case 7:
                                weekName = "六";
                                break;

                            default:
                                break;
                        }
                        time.setText(caseName + weekName + " " + hourMins + " 重复");
                        //每月
                    }else if(task.getCornBody().getType() == 3){
                        dayName = task.getCornBody().getDay()+"号";
                        time.setText(caseName + " " + dayName + " " + hourMins + " 重复");
                    }
                }else{
                    isOk = false;
                    textColor = R.color.tasklist_gray;
                    Long nowTime = Long.parseLong(DateTool.getDataOne(DateTool.getNowTime(), "yyyy.MM.dd HH:mm"));
                    if (nowTime > task.getPlanEndAt() && task.getStatus() == Task.STATUS_PROCESSING) {
                        timeOut.setVisibility(View.VISIBLE);
                    } else {
                        timeOut.setVisibility(View.GONE);
                    }
                    time.setText("任务截止时间: " + MainApp.getMainApp().df3.format(new Date(task.getPlanEndAt() * 1000)) + "");
                }
            } catch (Exception e) {
                Global.ProcException(e);
            }
            //ack.setVisibility(task.isAck() ? View.GONE : View.VISIBLE);
            if (null != task.getResponsiblePerson() && !TextUtils.isEmpty(task.getResponsiblePerson().getRealname())) {
                content.setText("负责人: " + task.getResponsiblePerson().getRealname());
            }
            if (!TextUtils.isEmpty(task.getTitle())) {
                title.setText(task.getTitle());
            }
            time.setTextColor(mContext.getResources().getColor(textColor));
            iv_repeattask.setVisibility(isOk ? View.VISIBLE:View.GONE);

            /**报告*/
        } else if (obj instanceof WorkReport) {
            //layout_discuss.setVisibility(View.VISIBLE);//右侧讨论暂时隐藏
            final WorkReport workReport = (WorkReport) obj;
            LogUtil.d(" 加载 报告 的数据： " + MainApp.gson.toJson(workReport));
            DiscussCounter discussCounter = workReport.discuss;
            //iv_disscuss_status.setImageResource(discussCounter.isViewed() ? R.drawable.icon_discuss_reviewed : R.drawable.icon_disscuss_unreviewed);
            //tv_discuss_num.setText(discussCounter.getTotal() + "");
            layout_discuss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    previewDiscuss(workReport.attachmentUUId);
                }
            });

            if (null != workReport.reviewer && null != workReport.reviewer.getUser() && !TextUtils.isEmpty(workReport.reviewer.getUser().getName())) {
                content.setText("点评: " + workReport.reviewer.getUser().getName());
            }
            StringBuilder reportTitle = new StringBuilder(workReport.reviewer.name + "提交 ");
            String reportDate = "";
            String reportType = "";
            switch (workReport.type) {
                case WorkReport.DAY:
                    reportType = " 日报";
                    break;
                case WorkReport.WEEK:
                    reportType = " 周报";
                    break;
                case WorkReport.MONTH:
                    reportType = " 月报";
                    break;
                default:

                    break;
            }
            reportTitle.append(reportType);
            if (workReport.isDelayed) {
                reportTitle.append(" (补签)");
            }

            title.setText(workReport.title);

            String end = "提交时间: " + app.df3.format(new Date(workReport.createdAt * 1000));
            time.setText(end);
            //ack.setVisibility(workReport.isAck() ? View.GONE : View.VISIBLE);

        }
        return convertView;
    }

    /**
     * @param attachmentUUid
     */
    private void previewDiscuss(final String attachmentUUid) {
        Intent intent = new Intent((Activity) mContext, DiscussionActivity_.class);
        intent.putExtra("attachementUUid", attachmentUUid);
        ((Activity) mContext).startActivityForResult(intent, DiscussionActivity.REQUEST_PREVIEW_DISCUSS);
    }
}
