package com.loyo.oa.v2.activityui.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.work.adapter.workReportAddgridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.discuss.DiscussDetialActivity;
import com.loyo.oa.v2.activityui.discuss.bean.Discussion;
import com.loyo.oa.v2.activityui.other.SelectEditDeleteActivity;
import com.loyo.oa.v2.activityui.work.api.WorkReportService;
import com.loyo.oa.v2.activityui.work.bean.WorkReportDyn;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * 【报告详情】
 */

@EActivity(R.layout.activity_workreports_info_new)
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
    TextView tv_review_time;
    @ViewById
    TextView tv_status;
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
    TextView tv_workContent;
    @ViewById
    EditText edt_workReport_title;
    @ViewById
    EditText edt_content;
    @ViewById
    ViewGroup no_dysndata_workreports;
    @ViewById
    TextView tv_crm, tv_work_score;
    @ViewById
    GridView info_gridview_workreports;
    @ViewById
    LoadingLayout ll_loading;

    @Extra(ExtraAndResult.EXTRA_ID)
    String workReportId;//推送的id
    @Extra(ExtraAndResult.EXTRA_TYPE)
    String keyType;//推送的id
    @Extra(ExtraAndResult.IS_UPDATE)
    boolean isUpdate;//是否需要刷新列表

    public WorkReport mWorkReport;
    public PaginationX<Discussion> mPageDiscussion;
    private boolean isOver = false;

    private ArrayList<WorkReportDyn> dynList;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_SUCCESS) {
                if (null == dynList || dynList.size() == 0) {
                    no_dysndata_workreports.setVisibility(View.VISIBLE);
                    info_gridview_workreports.setVisibility(View.GONE);
                } else {
                    no_dysndata_workreports.setVisibility(View.GONE);
                    info_gridview_workreports.setVisibility(View.VISIBLE);
                    workReportAddgridViewAdapter workGridViewAdapter = new workReportAddgridViewAdapter(getApplicationContext(), dynList);
                    info_gridview_workreports.setAdapter(workGridViewAdapter);
                }
            }
        }
    };

    @AfterViews
    void init() {
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getDataWorkReport();
            }
        });
        initUI();
        getDataWorkReport();
    }

    /**
     * 获取报告详情
     */
    void getDataWorkReport() {
        if (TextUtils.isEmpty(workReportId)) {
            Toast("参数不完整");
            finish();
            return;
        }

        WorkReportService.getWorkReportDetail(workReportId, keyType).subscribe(new DefaultLoyoSubscriber<WorkReport>(ll_loading) {
            @Override
            public void onNext(WorkReport _workReport) {
                mWorkReport = _workReport;
                updateUI(mWorkReport);
            }
        });
    }

    /**
     * 报告删除
     */
    void delete_WorkReport() {
        WorkReportService.deleteWorkReport(workReportId).subscribe(new DefaultLoyoSubscriber<WorkReport>() {
            @Override
            public void onNext(WorkReport workReport) {
                Intent intent = new Intent();
                intent.putExtra("delete", mWorkReport);
                app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_RIGHT, 0x09, intent);
            }
        });
    }

    void initUI() {
        super.setTitle("报告详情");
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(ViewUtil.OnTouchListener_softInput_hide.Instance());
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        layout_attachment.setOnTouchListener(Global.GetTouch());
        layout_discussion.setOnTouchListener(Global.GetTouch());
        img_title_right.setVisibility(View.GONE);
    }

    void updateUI(final WorkReport mWorkReport) {
        if (mWorkReport == null) {
            ll_loading.setStatus(LoadingLayout.Success);
            finish();
            Toast("服务端异常没有返回数据");
            return;
        }
        if (!mWorkReport.isRelevant()) {//和本报告无关的人
            layout_attachment.setVisibility(View.GONE);
        }
        if (null == mWorkReport.creator) {//没有创建人的时候
            Toast("没有创建人");
            onBackPressed();
        }
        StringBuilder title = new StringBuilder(mWorkReport.creator.name + "提交 ");
        String reportDate = "";
        String date = DateTool.getDateTimeFriendly(mWorkReport.createdAt);
        String reportType = "";
        String crmName = "";
        switch (mWorkReport.type) {
            case WorkReport.DAY:
                reportType = " 日报";
                crmName = "本日工作动态统计";
                reportDate = DateTool.getDateFriendly(mWorkReport.beginAt);
                break;
            case WorkReport.WEEK:
                reportType = " 周报";
                crmName = "本周工作动态统计";
                reportDate = DateTool.getMonthDay(mWorkReport.beginAt) + "-" + DateTool.getMonthDay(mWorkReport.endAt);
                break;
            case WorkReport.MONTH:
                reportType = " 月报";
                crmName = "本月工作动态统计";
                reportDate = DateTool.getYearMonth(mWorkReport.beginAt);
                break;
            default:
                break;

        }
        title.append(reportDate + reportType);
        if (mWorkReport.isDelayed) {
            title.append("(补签)");
        }

        tv_crm.setText(crmName);
        tv_discussion_count.setText("讨论 (" + mWorkReport.bizExtData.getDiscussCount() + ")");

        edt_workReport_title.setText(title.toString());
        /**
         * 工作动态统计
         */
        if (null != mWorkReport.crmDatas) {
            dynList = mWorkReport.crmDatas;
            mHandler.sendEmptyMessage(UPDATE_SUCCESS);
        } else {
            mHandler.sendEmptyMessage(UPDATE_SUCCESS);
        }
        OrganizationalMember reviewer = null != mWorkReport.reviewer && null != mWorkReport.reviewer.user ? mWorkReport.reviewer.user : null;
        tv_workContent.setText(TextUtils.isEmpty(mWorkReport.content) ? "无" : (mWorkReport.content.toString().contains("<") ? Html.fromHtml(mWorkReport.content) : mWorkReport.content));
        tv_reviewer.setText(mWorkReport.reviewer.user.getName());
        tv_toUser.setText(getJoinUserNames().isEmpty() ? "无" : getJoinUserNames());
        tv_workReport_time.setText("提交时间：" + date);

        if (null != mWorkReport.ProjectInfo && mWorkReport.ProjectInfo.title.length() != 0) {
            tv_ptoject.setText(mWorkReport.ProjectInfo.title);
        } else {
            tv_ptoject.setText("无");
        }

        showAttachment();
        if (mWorkReport.isReviewed()) {
            layout_score.setVisibility(View.VISIBLE);
            tv_status.setBackgroundResource(R.drawable.common_lable_green);
            tv_status.setText("已点评");
            tv_reviewer_.setText("点评人：" + mWorkReport.reviewer.user.getName());
            tv_review_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(mWorkReport.reviewer.reviewedAt));
            btn_workreport_review.setVisibility(View.GONE);
            tv_work_score.setVisibility(mWorkReport.reviewer.newScore.contains("-") ? View.GONE : View.VISIBLE);
            tv_work_score.setText(mWorkReport.reviewer.newScore + "分");
            if (!StringUtil.isEmpty(mWorkReport.reviewer.comment)) {
                edt_content.setText(mWorkReport.reviewer.comment);
                edt_content.setEnabled(false);
            }
            if (mWorkReport.creator.id.equals(MainApp.user.id)) {
                //显示编辑、删除按钮
                img_title_right.setVisibility(View.VISIBLE);
            }
        } else {
            layout_score.setVisibility(View.GONE);
            tv_status.setBackgroundResource(R.drawable.common_lable_blue);
            tv_status.setText("待点评");

            if (reviewer != null && reviewer.isCurrentUser()) {
                btn_workreport_review.setVisibility(View.VISIBLE);
                btn_workreport_review.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
            } else {
                btn_workreport_review.setVisibility(View.GONE);
            }

            if (mWorkReport.creator.id.equals(MainApp.user.id)) {
                //显示编辑、删除按钮
                img_title_right.setVisibility(View.VISIBLE);
            }

        }
        ll_loading.setStatus(LoadingLayout.Success);
    }


    void showAttachment() {
        if (null != mWorkReport.bizExtData) {
            tv_attachment_count.setText("附件 (" + mWorkReport.bizExtData.getAttachmentCount() + ")");
        }
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
        if (null != mWorkReport) {
            mWorkReport.setViewed(true);
            intent.putExtra("review", mWorkReport);
        }
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isUpdate ? 0x09 : RESULT_OK, intent);
    }

    /**
     * 附件上传
     */
    @Click(R.id.layout_attachment)
    void clickAttachment() {
        if (mWorkReport.reviewer.status == 1) {
            isOver = true;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", mWorkReport.attachments);
        bundle.putSerializable("uuid", mWorkReport.attachmentUUId);
        bundle.putInt("bizType", 1);
        bundle.putBoolean("isOver", isOver);
        app.startActivityForResult(this, AttachmentActivity_.class, MainApp.ENTER_TYPE_RIGHT, MSG_ATTACHMENT, bundle);

    }

    /**
     * 讨论
     */
    @Click(R.id.layout_discussion)
    void clickDiscussion() {
        Bundle bundle = new Bundle();
        bundle.putString("attachmentUUId", mWorkReport.attachmentUUId);
        bundle.putInt("status", mWorkReport.reviewer.status);
        bundle.putBoolean("isMyUser", isCreater());
        bundle.putInt("bizType", 1);
        int status = mWorkReport.reviewer.status;
        DiscussDetialActivity.startThisActivity(this, 1, mWorkReport.attachmentUUId, status, MSG_DISCUSSION);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
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
//                if (mWorkReport.isReviewed()) {
//                    Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
//                    intent.putExtra("extra", "复制报告");
//                    startActivityForResult(intent, MSG_DELETE_WORKREPORT);
//                } else {//只有创建者才可以复制报告
//                    LogUtil.dll("报告详情，右上角按钮 else");
//                    Intent intent = new Intent(mContext, SelectEditDeleteActivity.class);
//                    intent.putExtra("delete", true);
//                    intent.putExtra("edit", true);
//                    intent.putExtra("extra", "复制报告");
//                    startActivityForResult(intent, MSG_DELETE_WORKREPORT);
//                }
                functionButton();
                break;
            case R.id.btn_workreport_review:
                reviewWorkreport();
                break;

            default:
                break;
        }
    }

    /**
     * 详见
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(WorkReportsInfoActivity.this).builder();
        dialog.addSheetItem("复制报告", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("mWorkReport", mWorkReport);
                bundle.putInt("type", WorkReportAddActivity.TYPE_CREATE_FROM_COPY);
                app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
            }
        });
        if (!mWorkReport.isReviewed())
            dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mWorkReport", mWorkReport);
                    bundle.putInt("type", WorkReportAddActivity.TYPE_EDIT);
                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
                    isUpdate = true;
                }
            });
        if (!mWorkReport.isReviewed())
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    delete_WorkReport();
                }
            });
        dialog.show();
    }

    /**
     * 点评报告
     */
    private void reviewWorkreport() {
        if (null == mWorkReport) {
            return;
        }
        Intent intent = new Intent(this, WorkReportReviewActivity_.class);
        intent.putExtra("mWorkReportId", mWorkReport.getId());
        startActivityForResult(intent, MSG_REVIEW);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }


    /**
     * 获取抄送人
     *
     * @return
     */
    public String getJoinUserNames() {
        StringBuilder result = new StringBuilder();
        if (mWorkReport.members.users != null || mWorkReport.members.depts != null) {
            if (mWorkReport.members.users != null) {
                for (int i = 0; i < mWorkReport.members.users.size(); i++) {
                    if (null != mWorkReport && null != mWorkReport.members && null != mWorkReport.members.users && null != mWorkReport.members.users.get(i)) {
                        result.append(mWorkReport.members.users.get(i).getName() + " ");
                    }
                }
            }

            if (mWorkReport.members.depts != null) {
                for (int i = 0; i < mWorkReport.members.depts.size(); i++) {
                    result.append(mWorkReport.members.depts.get(i).getName() + " ");
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
        return mWorkReport.creator.getId().equals(MainApp.user.getId());
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            case MSG_REVIEW:
                getDataWorkReport();
                isUpdate = true;
                break;

//            case MSG_DELETE_WORKREPORT:
//                     /*编辑回调*/
//                if (data.getBooleanExtra("edit", false)) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("mWorkReport", mWorkReport);
//                    bundle.putInt("type", WorkReportAddActivity.TYPE_EDIT);
//                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
//                    isUpdate = true;
//                    /*复制回调*/
//                } else if ((data.getBooleanExtra("extra", false))) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("mWorkReport", mWorkReport);
//                    bundle.putInt("type", WorkReportAddActivity.TYPE_CREATE_FROM_COPY);
//                    app.startActivity((Activity) mContext, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, bundle, true);
//                    /*删除回调*/
//                } else if (data.getBooleanExtra("delete", false)) {
//                    delete_WorkReport();
//                }
//                break;

            case MSG_ATTACHMENT:
                try {
                    LogUtil.dll("MSG_ATTACHMENT");
                    if (null == data || null == data.getExtras()) {
                        LogUtil.dll("MSG_ATTACHMENT return");
                        return;
                    }
                    ArrayList<Attachment> attachments = (ArrayList<Attachment>) data.getSerializableExtra("data");
                    mWorkReport.bizExtData.setAttachmentCount(attachments.size());
                    showAttachment();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;

            case MSG_DISCUSSION:
                LogUtil.dll("MSG_DISCUSSION");
                if (null == data || null == data.getExtras()) {
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
