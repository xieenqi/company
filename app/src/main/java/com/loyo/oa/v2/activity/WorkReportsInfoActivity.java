package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;

import retrofit.client.Response;

@EActivity(R.layout.activity_workreports_info)
public class WorkReportsInfoActivity extends BaseActivity {

    final int MSG_DELETE_WORKREPORT = 100;
    final int MSG_ATTACHMENT = 200;
    final int MSG_DISCUSSION = 300;
    final int MSG_REVIEW = 400;

    @ViewById ViewGroup img_title_left;
    @ViewById ViewGroup img_title_right;
    @ViewById ViewGroup layout_content;
    @ViewById ViewGroup layout_score;
    @ViewById ViewGroup layout_attachment;
    @ViewById ViewGroup layout_discussion;

    @ViewById ImageView img_workreport_status;
    @ViewById Button btn_workreport_review;
    @ViewById TextView tv_reviewer;
    @ViewById TextView tv_toUser;
    @ViewById TextView tv_workReport_time;
    @ViewById TextView tv_attachment_count;
    @ViewById TextView tv_discussion_count;
    @ViewById TextView tv_reviewer_;
    @ViewById TextView tv_ptoject;

    @ViewById EditText edt_workReport_title;
    @ViewById EditText edt_content;
    @ViewById WebView webView_content;
    @ViewById RatingBar ratingBar_workReport;

    //统计数据
    @ViewById TextView tv_crm;
    @ViewById TextView tv_new_customers_num;
    @ViewById TextView tv_new_visit_num;
    @ViewById TextView tv_visit_customers_num;

    @Extra("workreport") WorkReport mWorkReport;

    //信鸽透传过来的id
    @Extra("id") String mId;

    PaginationX<Discussion> mPageDiscussion;

    @AfterViews
    void init() {
        initUI();
        updateUI();
        setTouchView(R.id.layout_touch);
        getData_WorkReport();
        getDiscussion();
    }

    String getId() {
        return (mWorkReport != null) ? mWorkReport.getId() : mId;
    }

    @Background
    void getData_WorkReport() {
        app.getRestAdapter().create(IWorkReport.class).get(getId(), new RCallback<WorkReport>() {
            @Override
            public void success(WorkReport _workReport, Response response) {
                if (_workReport == null) {
                    return;
                }

                mWorkReport = _workReport;
                updateUI();
            }
        });
    }

    @Background
    void getDiscussion() {
        app.getRestAdapter().create(IWorkReport.class).getDiscussions(getId(), new RCallback<PaginationX<Discussion>>() {
            @Override
            public void success(PaginationX<Discussion> discussionPaginationX, Response response) {
                if (discussionPaginationX == null) {
                    return;
                }

                mPageDiscussion = discussionPaginationX;
                showDiscussion();
            }
        });
    }

    void initUI() {
        super.setTitle("报告详情");

        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        img_title_right.setVisibility(View.INVISIBLE);
        layout_attachment.setOnTouchListener(Global.GetTouch());
        layout_discussion.setOnTouchListener(Global.GetTouch());
    }

    @UiThread
    void updateUI() {
        if (mWorkReport == null) {
            return;
        }

        if (null != mWorkReport.getCrmDatas() && mWorkReport.getCrmDatas().size() > 2) {
            tv_new_customers_num.setText(mWorkReport.getCrmDatas().get(0).getContent());
            tv_new_visit_num.setText(mWorkReport.getCrmDatas().get(1).getContent());
            tv_visit_customers_num.setText(mWorkReport.getCrmDatas().get(2).getContent());
        }

        StringBuilder title = new StringBuilder(mWorkReport.getCreator().name + "提交 ");
        String reportDate = "";
        String date = app.df3.format(new Date(mWorkReport.getCreatedAt() * 1000));
        String reportType = "";
        String crmName = "";
        switch (mWorkReport.getType()) {
            case WorkReport.DAY:
                reportType = " 日报";
                crmName = "本日工作动态统计";
                reportDate = app.df4.format(new Date(mWorkReport.getBeginAt() * 1000));
                break;
            case WorkReport.WEEK:
                reportType = " 周报";
                crmName = "本周工作动态统计";
                reportDate = app.df4.format(new Date(mWorkReport.getBeginAt() * 1000)) + "-" + app.df4.format(new Date(mWorkReport.getEndAt() * 1000));
                break;
            case WorkReport.MONTH:
                reportType = " 月报";
                crmName = "本月工作动态统计";
                reportDate = DateTool.toDateStr(mWorkReport.getBeginAt() * 1000, "yyyy.MM");
                break;

        }
        title.append(reportDate + reportType);
        if (mWorkReport.isDelayed()) {
            title.append("(补签)");
        }

        tv_crm.setText(crmName);

        edt_workReport_title.setText(title.toString());

        webView_content.getSettings().setJavaScriptEnabled(true);
        webView_content.loadDataWithBaseURL(null, mWorkReport.getContent(), "text/html", "utf-8", null);

        NewUser reviewer = null != mWorkReport.getReviewer() && null != mWorkReport.getReviewer().getUser() ? mWorkReport.getReviewer().getUser() : null;

        if (reviewer != null) {
            tv_reviewer.setText(reviewer.getName());
        }

        tv_toUser.setText(mWorkReport.getJoinUserNames());
        tv_workReport_time.setText("提交时间：" + date);
        if(null!=mWorkReport.getProject()){
            tv_ptoject.setText(mWorkReport.getProject().getTitle());
        }

        showAttachment();

        if (mWorkReport.isReviewed()) {
            img_title_right.setVisibility(View.GONE);
            layout_score.setVisibility(View.VISIBLE);
            img_workreport_status.setImageResource(R.drawable.img_workreport_status2);
            tv_reviewer_.setText("点评人：" + mWorkReport.getReviewer().getUser().getName());
            btn_workreport_review.setVisibility(View.GONE);
            ratingBar_workReport.setProgress(Integer.valueOf(String.valueOf(mWorkReport.getReviewer().getScore())).intValue() / 20);

            if (!StringUtil.isEmpty(mWorkReport.getReviewer().getComment())) {
                edt_content.setText(mWorkReport.getReviewer().getComment());
                edt_content.setEnabled(false);
            } else {
                layout_content.setVisibility(View.GONE);
            }

        } else {
            layout_score.setVisibility(View.GONE);
            img_workreport_status.setImageResource(R.drawable.img_workreport_status1);

            if (reviewer != null && reviewer.isCurrentUser()) {
                btn_workreport_review.setVisibility(View.VISIBLE);
                btn_workreport_review.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());

                ratingBar_workReport.setIsIndicator(false);
            }else {
                btn_workreport_review.setVisibility(View.GONE);
            }

            if (mWorkReport.getCreator() != null && mWorkReport.getCreator().isCurrentUser()) {
                //显示编辑、删除按钮
                img_title_right.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case MSG_REVIEW:
                getData_WorkReport();
                break;

            case MSG_DELETE_WORKREPORT:
                if (data.getBooleanExtra("delete", false)) {
                    RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).deleteWorkReport(mWorkReport.getId(), new RCallback<WorkReport>() {
                        @Override
                        public void success(WorkReport workReport, Response response) {
                            Intent intent = new Intent();
                            intent.putExtra("delete", mWorkReport);
                            app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
                        }
                    });
                } else if (data.getBooleanExtra("edit", false)) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mWorkReport", mWorkReport);
                    bundle.putInt("type", WorkReportAddActivity.TYPE_EDIT);
                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
                } else if ((data.getBooleanExtra("extra", false))) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mWorkReport", mWorkReport);
                    bundle.putInt("type", WorkReportAddActivity.TYPE_CREATE_FROM_COPY);
                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
                }

                break;

            case MSG_ATTACHMENT:
                if (data == null || data.getExtras() == null) {
                    return;
                }
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                mWorkReport.setAttachments(attachments);
                showAttachment();
                break;

            case MSG_DISCUSSION:
                if (data == null || data.getExtras() == null) {
                    return;
                }

                mPageDiscussion = (PaginationX<Discussion>) data.getSerializableExtra("data");
                showDiscussion();

                break;
        }
    }

    void showAttachment() {
        if (mWorkReport.getAttachments() != null && mWorkReport.getAttachments().size() > 0) {
            tv_attachment_count.setHint("附件".concat("(" + String.valueOf(mWorkReport.getAttachments().size()) + ")"));
        }
    }

    @UiThread
    void showDiscussion() {
        if (mPageDiscussion.getTotalRecords() > 0) {
            tv_discussion_count.setHint("讨论".concat("(" +
                    String.valueOf(mPageDiscussion.getTotalRecords()) + ")"));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //fixes buugly1084 v3.1.1 ykb 07-15
        if (mWorkReport != null) {
            mWorkReport.setAck(true);
            intent.putExtra("review", mWorkReport);
        }
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Click(R.id.layout_attachment)
    void clickAttachment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mWorkReport.getAttachments());
        bundle.putSerializable("uuid", mWorkReport.getAttachmentUUId());
        app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);
    }

    @Click(R.id.layout_discussion)
    void clickDiscussion() {
        Bundle bundle = new Bundle();
        bundle.putString("attachmentUUId", mWorkReport.getAttachmentUUId());
        app.startActivityForResult(this, DiscussionActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_DISCUSSION, bundle);
    }

    /**
     * 标题左右监听
     * */
    @Click({R.id.img_title_left, R.id.img_title_right, R.id.btn_workreport_review})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
                intent.putExtra("delete", true);
                intent.putExtra("edit", true);
                intent.putExtra("extra", "复制报告");
                startActivityForResult(intent, MSG_DELETE_WORKREPORT);
                break;
            case R.id.btn_workreport_review:
                reviewWorkreport();
                break;
        }
    }

    /**
     * 点评报告
     */
    private void reviewWorkreport() {
        Intent intent = new Intent(this, WorkReportReviewActivity_.class);
        intent.putExtra("mWorkReportId", mWorkReport.getId());
        startActivityForResult(intent, MSG_REVIEW);
    }
}
