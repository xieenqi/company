package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.DiscussionActivity;
import com.loyo.oa.v2.activity.DiscussionActivity_;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.DiscussCounter;
import com.loyo.oa.v2.beans.PagingGroupData_;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.ArrayList;
import java.util.Date;

/**
 * 任务管理列表的adapter
 *
 * xnq
 */
public class CommonExpandableListAdapter<T extends BaseBeans> extends BasePagingGroupDataAdapter_<T> {

    public CommonExpandableListAdapter(Context context, ArrayList<PagingGroupData_<T>> data) {
        super();
        mContext = context;
        pagingGroupDatas = data;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
        }

        Object o = getChild(groupPosition, childPosition);

        ImageView status = ViewHolder.get(convertView, R.id.img_status);
        TextView title = ViewHolder.get(convertView, R.id.tv_title);
        TextView content = ViewHolder.get(convertView, R.id.tv_content);
        TextView time = ViewHolder.get(convertView, R.id.tv_time);
        TextView timeOut = ViewHolder.get(convertView, R.id.tv_timeOut);
        View ack = ViewHolder.get(convertView, R.id.view_ack);
        ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
        TextView tv_discuss_num = ViewHolder.get(convertView, R.id.tv_disscuss_num);
        ImageView iv_disscuss_status = ViewHolder.get(convertView, R.id.img_discuss_status);
        status.setVisibility(View.GONE);
        //审批
        if (o instanceof WfInstance) {
            WfInstance wfInstance = (WfInstance) o;
            if (wfInstance.getTitle() != null) {
                title.setText(wfInstance.getTitle());
            }
            time.setText("提交时间: " + app.df3.format(new Date(wfInstance.getCreatedAt() * 1000)));
            if (wfInstance.getCreator() != null) {
                content.setText(String.format("审批人 %s", wfInstance.getNextExecutor().getRealname()));
            }
            ack.setVisibility(wfInstance.isAck() ? View.GONE : View.VISIBLE);

            switch (wfInstance.getStatus()) {
                case WfInstance.STATUS_NEW:
                    status.setImageResource(R.drawable.img_wfinstance_list_status1);
                    break;
                case WfInstance.STATUS_PROCESSING:
                    status.setImageResource(R.drawable.img_wfinstance_list_status2);
                    break;
                case WfInstance.STATUS_ABORT:
                    status.setImageResource(R.drawable.img_wfinstance_list_status3);
                    break;
                case WfInstance.STATUS_APPROVED:
                    status.setImageResource(R.drawable.img_wfinstance_list_status4);
                    break;
                case WfInstance.STATUS_FINISHED:
                    status.setImageResource(R.drawable.img_wfinstance_list_status5);
                    break;
            }
        } else if (o instanceof Task) {
            layout_discuss.setVisibility(View.VISIBLE);
            Task task = (Task) o;
            if (task.getStatus() == Task.STATUS_PROCESSING) {
                status.setImageResource(R.drawable.task_status_1);
            } else if (task.getStatus() == Task.STATUS_REVIEWING) {
                status.setImageResource(R.drawable.task_status_2);
            } else if (task.getStatus() == Task.STATUS_FINISHED) {
                status.setImageResource(R.drawable.task_status_3);
            }
          //  Log.d("Volley", " 任务管理啊返回: " + task.);
            try {
                if(System.currentTimeMillis()>task.getPlanEndAt()&&task.getStatus() == Task.STATUS_PROCESSING){
                    timeOut.setVisibility(View.VISIBLE);
                }else{
                    timeOut.setVisibility(View.GONE);
                }
                //                time.setText("任务截止时间: " + DateTool.formateServerDate(task.getCreatedAt(), app.df3));
                time.setText("任务截止时间: " + app.df3.format(new Date(task.getPlanEndAt())));
            } catch (Exception e) {
                Global.ProcException(e);
            }
                           ack.setVisibility(task.isAck() ? View.GONE : View.VISIBLE);
            if (null != task.getResponsiblePerson() && !TextUtils.isEmpty(task.getResponsiblePerson().getRealname())) {
                content.setText("负责人: " + task.getResponsiblePerson().getRealname());
            }
            if (!TextUtils.isEmpty(task.getTitle())) {
                title.setText(task.getTitle());
            }

        }
        //报告
        else if (o instanceof WorkReport) {
            layout_discuss.setVisibility(View.VISIBLE);
            final WorkReport workReport = (WorkReport) o;
            DiscussCounter discussCounter = workReport.getDiscuss();
            //iv_disscuss_status.setImageResource(discussCounter.isViewed() ? R.drawable.icon_discuss_reviewed : R.drawable.icon_disscuss_unreviewed);
            //tv_discuss_num.setText(discussCounter.getTotal() + "");
            layout_discuss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    previewDiscuss(workReport.getAttachmentUUId());
                }
            });

            if (null != workReport.getReviewer() && null != workReport.getReviewer().getUser() && !TextUtils.isEmpty(workReport.getReviewer().getUser().getName())) {
                content.setText("点评: " + workReport.getReviewer().getUser().getName());
            }
            StringBuilder reportTitle = new StringBuilder(workReport.getCreator().name + "提交 ");
            String reportDate = "";
            String reportType = "";
            switch (workReport.getType()) {
                case WorkReport.DAY:
                    reportType = " 日报";
                    reportDate = app.df4.format(new Date(workReport.getBeginAt() * 1000));
                    break;
                case WorkReport.WEEK:
                    reportType = " 周报";
                    reportDate = app.df4.format(new Date(workReport.getBeginAt() * 1000)) + "-" + app.df4.format(new Date(workReport.getEndAt() * 1000));
                    break;
                case WorkReport.MONTH:
                    reportType = " 月报";
                    reportDate = DateTool.toDateStr(workReport.getBeginAt() * 1000, "yyyy.MM");
                    ;
                    break;
            }
            reportTitle.append(reportDate + reportType);
            if (workReport.isDelayed()) {
                reportTitle.append(" (补签)");
            }

            title.setText(reportTitle);

            String end = "提交时间: " + app.df3.format(new Date(workReport.getCreatedAt() * 1000));
            time.setText(end);
            //                ack.setVisibility(workReport.isAck() ? View.GONE : View.VISIBLE);
            status.setImageResource(workReport.isReviewed() ? R.drawable.img_workreport_list_status2 : R.drawable.img_workreport_list_status1);

        }
        return convertView;
    }

    /**
     * @param attachmentUUid
     */
    private void previewDiscuss(String attachmentUUid) {
        Intent intent = new Intent((Activity) mContext, DiscussionActivity_.class);
        intent.putExtra("attachementUUid", attachmentUUid);
        ((Activity) mContext).startActivityForResult(intent, DiscussionActivity.REQUEST_PREVIEW_DISCUSS);
    }
}
