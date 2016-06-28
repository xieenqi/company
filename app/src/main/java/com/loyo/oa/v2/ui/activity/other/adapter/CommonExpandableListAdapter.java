package com.loyo.oa.v2.ui.activity.other.adapter;

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
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.ui.activity.commonview.DiscussionActivity;
import com.loyo.oa.v2.ui.activity.commonview.DiscussionActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.ui.activity.project.adapter.BasePagingGroupDataAdapter_;
import java.util.ArrayList;
import java.util.Date;

/**
 * 【公用适配器】 任务 审批 报告
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
        ImageView iv_repeattask = ViewHolder.get(convertView, R.id.iv_repeattask);
        View ack = ViewHolder.get(convertView, R.id.view_ack);
        ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
        TextView tv_discuss_num = ViewHolder.get(convertView, R.id.tv_disscuss_num);
        ImageView iv_disscuss_status = ViewHolder.get(convertView, R.id.img_discuss_status);

        /**审批*/
        if (obj instanceof WfInstanceRecord) {
            WfInstanceRecord wfInstance = (WfInstanceRecord) obj;
            if (wfInstance.title != null) {
                title.setText(wfInstance.title);
            }

            time.setText("提交时间: " + app.df3.format(new Date(wfInstance.createdAt * 1000)));
            if (wfInstance.nextExecutorName != null) {
                content.setText("审批人: " + wfInstance.nextExecutorName);
            }
            ack.setVisibility(wfInstance.viewed ? View.GONE : View.VISIBLE);

            /**任务*/
        } else if (obj instanceof TaskRecord) {
            //layout_discuss.setVisibility(View.VISIBLE); //右侧讨论暂时隐藏
            TaskRecord task = (TaskRecord) obj;
            /*任务超时判断*/
            try {
                /**重复任务赋值*/
                if (null != task.cornBody && task.cornBody.getType() != 0) {
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
                    switch (task.cornBody.getType()) {
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
                    hour = task.cornBody.getHour() + "";
                    mins = task.cornBody.getMinute() + "";

            /*如果小时分钟为单数，则前面拼上0*/
                    if (hour.length() == 1) {
                        hour = "0" + hour;
                    }

                    if (mins.length() == 1) {
                        mins = "0" + mins;
                    }
                    hourMins = hour + ":" + mins;

                    //每天
                    if (task.cornBody.getType() == 1) {
                        time.setText(caseName + " " + hourMins + " 重复");
                        //每周
                    } else if (task.cornBody.getType() == 2) {
                        switch (task.cornBody.getWeekDay()) {
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
                    } else if (task.cornBody.getType() == 3) {
                        dayName = task.cornBody.getDay() + "号";
                        time.setText(caseName + " " + dayName + " " + hourMins + " 重复");
                    }
                } else {
                    isOk = false;
                    textColor = R.color.tasklist_gray;
                    Long nowTime = Long.parseLong(DateTool.getDataOne(DateTool.getNowTime(), "yyyy.MM.dd HH:mm"));
                    if (nowTime > task.planendAt && task.status == Task.STATUS_PROCESSING) {
                        timeOut.setVisibility(View.VISIBLE);
                    } else {
                        timeOut.setVisibility(View.GONE);
                    }
                    time.setText("任务截止时间: " + MainApp.getMainApp().df3.format(new Date(task.planendAt * 1000)) + "");
                }
            } catch (Exception e) {
                Global.ProcException(e);
            }
            ack.setVisibility(task.viewed ? View.GONE : View.VISIBLE);
            if (null != task.responsibleName) {
                content.setText("负责人: " + task.responsibleName);
            }
            if (!TextUtils.isEmpty(task.title)) {
                title.setText(task.title);
            }
            time.setTextColor(mContext.getResources().getColor(textColor));
            iv_repeattask.setVisibility(isOk ? View.VISIBLE : View.GONE);

            /**报告*/
        } else if (obj instanceof WorkReportRecord) {
            //layout_discuss.setVisibility(View.VISIBLE);//右侧讨论暂时隐藏
            //iv_disscuss_status.setImageResource(discussCounter.isViewed() ? R.drawable.icon_discuss_reviewed : R.drawable.icon_disscuss_unreviewed);
            //tv_discuss_num.setText(discussCounter.getTotal() + "");
            final WorkReportRecord workReport = (WorkReportRecord) obj;
            layout_discuss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    previewDiscuss(workReport.attachmentUUId);
                }
            });

            if (null != workReport.reviewerName) {
                content.setText("点评: " + workReport.reviewerName);
            }
            StringBuilder reportTitle = new StringBuilder(workReport.title);
            String reportType = "";
            switch (workReport.type) {
                case 1:
                    reportType = " 日报";
                    break;
                case 2:
                    reportType = " 周报";
                    break;
                case 3:
                    reportType = " 月报";
                    break;
                default:
                    break;
            }
            reportTitle.append(reportType);
            if (workReport.isDelayed) {
                reportTitle.append(" (补签)");
            }

            String isDelayedTitle = workReport.isDelayed ? "(补签)" : " ";
            title.setText(workReport.title + " " + isDelayedTitle);

            String end = "提交时间: " + app.df3.format(new Date(workReport.createdAt * 1000));
            time.setText(end);
            ack.setVisibility(workReport.viewed ? View.GONE : View.VISIBLE);

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
