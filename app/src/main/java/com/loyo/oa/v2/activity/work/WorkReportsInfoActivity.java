package com.loyo.oa.v2.activity.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.SelectEditDeleteActivity;
import com.loyo.oa.v2.activity.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activity.commonview.DiscussionActivity_;
import com.loyo.oa.v2.adapter.workReportAddgridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Discussion;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.beans.WorkReportDyn;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Date;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 报告详情
 */

@EActivity(R.layout.activity_workreports_info)
public class WorkReportsInfoActivity extends BaseActivity {

    public final int MSG_DELETE_WORKREPORT = 100;
    public final int MSG_ATTACHMENT = 200;
    public final int MSG_DISCUSSION = 300;
    public final int MSG_REVIEW = 400;
    public static final int UPDATE_SUCCESS = 0x01;

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_right;
    @ViewById
    ViewGroup layout_content;
    @ViewById
    ViewGroup layout_score;
    @ViewById
    ViewGroup layout_attachment;
    @ViewById
    ViewGroup layout_discussion;

    @ViewById
    ImageView img_workreport_status;
    @ViewById
    Button btn_workreport_review;
    @ViewById
    TextView tv_reviewer;
    @ViewById
    TextView tv_toUser;
    @ViewById
    TextView tv_workReport_time;
    @ViewById
    TextView tv_attachment_count;
    @ViewById
    TextView tv_discussion_count;
    @ViewById
    TextView tv_reviewer_;
    @ViewById
    TextView tv_ptoject;

    @ViewById
    EditText edt_workReport_title;
    @ViewById
    EditText edt_content;
    @ViewById
    RatingBar ratingBar_workReport;
    @ViewById
    ViewGroup no_dysndata_workreports;
    @ViewById
    TextView tv_crm;
    @ViewById
    GridView info_gridview_workreports;

    @Extra(ExtraAndResult.EXTRA_ID)
    String workReportId;//推送的id

    public WorkReport mWorkReport;
    public PaginationX<Discussion> mPageDiscussion;
    private boolean isOver = false;
    private workReportAddgridViewAdapter workGridViewAdapter;

    private ArrayList<WorkReportDyn> dynList;
    private Handler mHandler = new Handler() {
        public void handleMessage(final Message msg) {
            if (msg.what == UPDATE_SUCCESS) {
                if (null == dynList || dynList.size() == 0) {
                    no_dysndata_workreports.setVisibility(View.VISIBLE);
                    info_gridview_workreports.setVisibility(View.GONE);
                } else {
                    no_dysndata_workreports.setVisibility(View.GONE);
                    info_gridview_workreports.setVisibility(View.VISIBLE);
                    workGridViewAdapter = new workReportAddgridViewAdapter(getApplicationContext(), dynList);
                    info_gridview_workreports.setAdapter(workGridViewAdapter);
                }
            }
        }
    };

    @AfterViews
    void init() {
        initUI();
        setTouchView(R.id.layout_touch);
        showLoading("");
        getData_WorkReport();
    }

    /**
     * 获取报告详情
     */
    @Background
    void getData_WorkReport() {
        if (TextUtils.isEmpty(workReportId)) {
            Toast("参数不完整");
            finish();
            return;
        }
        app.getRestAdapter().create(IWorkReport.class).get(workReportId, new RCallback<WorkReport>() {
            @Override
            public void success(final WorkReport _workReport,final Response response) {
                HttpErrorCheck.checkResponse(response);
                mWorkReport = _workReport;
                updateUI(mWorkReport);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 报告删除
     */
    void delete_WorkReport() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).deleteWorkReport(workReportId, new RCallback<WorkReport>() {
            @Override
            public void success(final WorkReport workReport,final Response response) {
                Intent intent = new Intent();
                intent.putExtra("delete", mWorkReport);
                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, RESULT_OK, intent);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 获取讨论内容，服务端已启用，暂注释
     */
/*    @Background
    void getDiscussion() {
        app.getRestAdapter().create(IWorkReport.class).getDiscussions(getId(), new RCallback<PaginationX<Discussion>>() {
            @Override
            public void success(PaginationX<Discussion> discussionPaginationX, Response response) {
                mPageDiscussion = discussionPaginationX;
                showDiscussion();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }*/

    void initUI() {
        super.setTitle("报告详情");
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        layout_attachment.setOnTouchListener(Global.GetTouch());
        layout_discussion.setOnTouchListener(Global.GetTouch());

    }

    void updateUI(final WorkReport mWorkReport) {
        if (mWorkReport == null) {
            return;
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
                reportDate = app.df8.format(new Date(mWorkReport.getBeginAt() * 1000));
                break;
            default:
                break;

        }
        title.append(reportDate + reportType);
        if (mWorkReport.isDelayed()) {
            title.append("(补签)");
        }

        tv_crm.setText(crmName);
        tv_discussion_count.setText("讨论 (" + mWorkReport.getBizExtData().getDiscussCount() + ")");
        tv_attachment_count.setText("附件 (" + mWorkReport.getBizExtData().getAttachmentCount() + ")");

        edt_workReport_title.setText(title.toString());
//        webView_content.getSettings().setJavaScriptEnabled(true);
//        webView_content.loadDataWithBaseURL(null, mWorkReport.getContent(), "text/html", "utf-8", null);
        /**
         * 工作动态统计
         */
        if (null != mWorkReport.getCrmDatas()) {
            dynList = mWorkReport.getCrmDatas();
            mHandler.sendEmptyMessage(UPDATE_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(UPDATE_SUCCESS);
        }
        NewUser reviewer = null != mWorkReport.getReviewer() && null != mWorkReport.getReviewer().getUser() ? mWorkReport.getReviewer().getUser() : null;

        tv_reviewer.setText(mWorkReport.getReviewer().getUser().getName());
        tv_toUser.setText(getJoinUserNames().isEmpty() ? "无抄送人" : getJoinUserNames());

        tv_workReport_time.setText("提交时间：" + date);

        if (null != mWorkReport.getProject() && mWorkReport.getProject().title.length() != 0) {
            tv_ptoject.setText(mWorkReport.getProject().title);
        } else {
            tv_ptoject.setText("无");
        }


        showAttachment();
        if (mWorkReport.isReviewed()) {
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
            } else {
                btn_workreport_review.setVisibility(View.GONE);
            }

            if (!mWorkReport.getCreator().id.equals(MainApp.user.id)) {
                //显示编辑、删除按钮
                img_title_right.setVisibility(View.GONE);
            }

        }
    }


    void showAttachment() {
        if (ListUtil.IsEmpty(mWorkReport.getAttachments())) {
            return;
        }
        tv_attachment_count.setText("附件 (" + (mWorkReport.getAttachments() == null ? 0 : mWorkReport.getAttachments().size()) + ")");
    }

    void showDiscussion() {
        if (!ListUtil.IsEmpty(mPageDiscussion.getRecords())) {
            int count = mPageDiscussion.getRecords().size();
            tv_discussion_count.setText("讨论 (" + count + ")");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mWorkReport != null) {
            mWorkReport.setAck(true);
            intent.putExtra("review", mWorkReport);
        }
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    /**
     * 附件上传
     */
    @Click(R.id.layout_attachment)
    void clickAttachment() {

        if(("1").equals(mWorkReport.getReviewer().getStatus())){
            isOver = true;
        }
        LogUtil.dll("status:"+mWorkReport.getReviewer().getStatus());
        LogUtil.dll("isOver:"+isOver);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mWorkReport.getAttachments());
        bundle.putSerializable("uuid", mWorkReport.getAttachmentUUId());
        bundle.putInt("bizType", 1);
        bundle.putBoolean("isOver",isOver);
        app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);

    }

    /**
     * 讨论
     */
    @Click(R.id.layout_discussion)
    void clickDiscussion() {
        Bundle bundle = new Bundle();
        bundle.putString("attachmentUUId", mWorkReport.getAttachmentUUId());
        bundle.putInt("status",Integer.parseInt(mWorkReport.getReviewer().getStatus()));
        bundle.putBoolean("isMyUser", isCreater());
        bundle.putInt("bizType", 1);
        app.startActivityForResult(this, DiscussionActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_DISCUSSION, bundle);
    }

    /**
     * 标题左右监听
     */
    @Click({R.id.img_title_left, R.id.img_title_right, R.id.btn_workreport_review})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:

                LogUtil.dll("报告详情，右上角按钮");
                if (mWorkReport.isReviewed()) {
                    Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
                    intent.putExtra("extra", "复制报告");
                    startActivityForResult(intent, MSG_DELETE_WORKREPORT);
                } else {
                    LogUtil.dll("报告详情，右上角按钮 else");
                    Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
                    intent.putExtra("delete", true);
                    intent.putExtra("edit", true);
                    intent.putExtra("extra", "复制报告");
                    startActivityForResult(intent, MSG_DELETE_WORKREPORT);
                }

                break;
            case R.id.btn_workreport_review:
                reviewWorkreport();
                break;

            default:
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


    /**
     * 获取抄送人
     *
     * @return
     */
    public String getJoinUserNames() {
        StringBuilder result = new StringBuilder();
        if (mWorkReport.getMembers().users != null || mWorkReport.getMembers().depts != null) {
            if (mWorkReport.getMembers().users != null) {
                for (int i = 0; i < mWorkReport.getMembers().users.size(); i++) {
                    result.append(mWorkReport.getMembers().users.get(i).getName() + ",");
                }
            }

            if (mWorkReport.getMembers().depts != null) {
                for (int i = 0; i < mWorkReport.getMembers().depts.size(); i++) {
                    result.append(mWorkReport.getMembers().depts.get(i).getName() + ",");
                }
            }
            return result.toString();
        } else {
            return result.append("无抄送人").toString();
        }
    }

    /**
     * 判断是否是创建人
     */
    public boolean isCreater() {
        return mWorkReport.getCreator().getId().equals(MainApp.user.getId()) ? true : false;
    }


    @Override
    protected void onActivityResult(final int requestCode,final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case MSG_REVIEW:
                getData_WorkReport();
                break;

            case MSG_DELETE_WORKREPORT:

                  /*编辑回调*/
                if (data.getBooleanExtra("edit", false)) {
                    LogUtil.dll("进入回调：编辑");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mWorkReport", mWorkReport);
                    bundle.putInt("type", WorkReportAddActivity.TYPE_EDIT);
                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
                }
                /*复制回调*/
                else if ((data.getBooleanExtra("extra", false))) {
                    LogUtil.dll("进入回调：复制");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mWorkReport", mWorkReport);
                    bundle.putInt("type", WorkReportAddActivity.TYPE_CREATE_FROM_COPY);
                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
                }

                /*删除回调*/
                else if (data.getBooleanExtra("delete", false)) {
                    delete_WorkReport();
                    LogUtil.dll("进入回调：删除");
                }

                break;

            case MSG_ATTACHMENT:
                LogUtil.dll("MSG_ATTACHMENT");
                if (data == null || data.getExtras() == null) {
                    LogUtil.dll("MSG_ATTACHMENT return");
                    return;
                }
                ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                mWorkReport.setAttachments(attachments);
                showAttachment();
                break;

            case MSG_DISCUSSION:
                LogUtil.dll("MSG_DISCUSSION");
                if (data == null || data.getExtras() == null) {
                    LogUtil.dll("MSG_DISCUSSION return");
                    return;
                }
                mPageDiscussion = (PaginationX<Discussion>) data.getSerializableExtra("data");
                showDiscussion();
                break;

            default:
                break;
        }
    }
}
