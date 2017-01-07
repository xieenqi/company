package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorkSheetListNestingAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.worksheet.api.WorksheetService;

/**
 * 【工单详情】  页面
 * Created by yyy on 16/8/30.
 */

public class WorksheetInfoActivity extends BaseLoadingActivity implements View.OnClickListener {

    /**
     * UI
     */
    private ListView lv_listview;
    private LinearLayout img_title_left,
            ll_assignment_time,
            ll_finish_time,
            ll_termination_time,
            ll_times_container;
    private RelativeLayout img_title_right;
    private TextView tv_title_1, /*标题*/
            tv_title,            /*工单标题*/
            tv_status,           /*工单状态*/
            tv_commit_info,      /*提交信息*/
            tv_track_content,    /*内容*/
            tv_Assignment_name,  /*分派人*/
            tv_boom,             /*触发方式*/
            tv_related_order,    /*关联订单*/
            tv_related_customer,/*对应客户*/
            tv_responsible_name, /*负责人*/
            tv_assignment_time,  /*分派时间*/
            tv_finish_time,      /*完成时间*/
            tv_termination_time; /*终止时间*/
    /**
     * Data
     */
    private String id;

    /**
     * Other
     */
    private WorkSheetListNestingAdapter mAdapter;
    private WorksheetInfo mWorksheetInfo;
    private Intent mIntent;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_worksheet_info_new);
    }

    @Override
    public void getPageData() {
        requestData();
    }

    private void initView() {
        mIntent = getIntent();
        id = mIntent.getStringExtra(ExtraAndResult.CC_USER_ID);
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_Assignment_name = (TextView) findViewById(R.id.tv_Assignment_name);
        tv_boom = (TextView) findViewById(R.id.tv_boom);
        tv_related_order = (TextView) findViewById(R.id.tv_related_order);
        tv_related_customer = (TextView) findViewById(R.id.tv_related_customer);
        tv_responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        tv_assignment_time = (TextView) findViewById(R.id.tv_assignment_time);
        tv_finish_time = (TextView) findViewById(R.id.tv_finish_time);
        tv_termination_time = (TextView) findViewById(R.id.tv_termination_time);
        tv_commit_info = (TextView) findViewById(R.id.tv_commit_info);
        tv_track_content = (TextView) findViewById(R.id.tv_track_content);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);

        ll_assignment_time = (LinearLayout) findViewById(R.id.ll_assignment_time);
        ll_finish_time = (LinearLayout) findViewById(R.id.ll_finish_time);
        ll_termination_time = (LinearLayout) findViewById(R.id.ll_termination_time);
        ll_times_container = (LinearLayout) findViewById(R.id.ll_times_container);

        img_title_left.setOnClickListener(this);
        tv_related_order.setOnClickListener(this);
        tv_related_customer.setOnClickListener(this);
        tv_title_1.setText("工单信息");
        getPageData();
    }

    private void bindData() {
        ll_loading.setStatus(LoadingLayout.Success);
        if (mWorksheetInfo == null) {
            return;
        }

        tv_title.setText(mWorksheetInfo.title);
        tv_Assignment_name.setText(mWorksheetInfo.dispatcherName);
        if (mWorksheetInfo.triggerMode == 1) {
            tv_boom.setText("自动流转");
        } else {
            tv_boom.setText("定时触发");
        }
        tv_commit_info.setText(mWorksheetInfo.creatorName + " "
                + DateTool.getDateTimeFriendly(mWorksheetInfo.createdAt) + " 提交");
        tv_track_content.setText(mWorksheetInfo.content);
        tv_related_order.setText(mWorksheetInfo.orderName);
        tv_related_customer.setText(mWorksheetInfo.customerName);
        tv_responsible_name.setText(mWorksheetInfo.responsorNames);

        if (mWorksheetInfo.confirmedAt != 0) {
            ll_assignment_time.setVisibility(View.VISIBLE);
            tv_assignment_time.setText(DateTool.getDateTimeFriendly(mWorksheetInfo.confirmedAt));
        } else {
            tv_assignment_time.setText("--");
            ll_assignment_time.setVisibility(View.GONE);
        }

        if (mWorksheetInfo.completedAt != 0) {
            ll_finish_time.setVisibility(View.VISIBLE);
            tv_finish_time.setText(DateTool.getDateTimeFriendly(mWorksheetInfo.completedAt));
        } else {
            tv_finish_time.setText("--");
            ll_finish_time.setVisibility(View.GONE);
        }

        if (mWorksheetInfo.interruptAt != 0) {
            ll_termination_time.setVisibility(View.VISIBLE);
            tv_termination_time.setText(DateTool.getDateTimeFriendly(mWorksheetInfo.interruptAt));
        } else {
            tv_termination_time.setText("--");
            ll_termination_time.setVisibility(View.GONE);
        }

        if (mWorksheetInfo.confirmedAt == 0
                && mWorksheetInfo.completedAt == 0
                && mWorksheetInfo.interruptAt == 0) {
            ll_times_container.setVisibility(View.GONE);
        }

        WorksheetCommon.setStatus(tv_status, mWorksheetInfo.status);
        if (null != mWorksheetInfo.attachment && mWorksheetInfo.attachment.size() > 0) {
            mAdapter = new WorkSheetListNestingAdapter(mWorksheetInfo.attachment, this);
            lv_listview.setAdapter(mAdapter);
            mAdapter.refreshData();
        }else{
            //没有就隐藏，不然ui会有间距的小问题
            lv_listview.setVisibility(View.GONE);
        }
    }

    /**
     * 获取工单信息
     */
    private void requestData() {
        WorksheetService.getWorksheetInfo(id)
                .subscribe(new DefaultLoyoSubscriber<WorksheetInfo>(ll_loading) {
                    @Override
                    public void onNext(WorksheetInfo info) {
                        mWorksheetInfo = info;
                        bindData();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //跳转订单
            case R.id.tv_related_order:
                if (! PermissionManager.getInstance().hasPermission(BusinessOperation.ORDER_MANAGEMENT)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                    return;
                }
                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_ID, mWorksheetInfo.orderId);
                app.startActivity(this, OrderDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                break;

            //返回
            case R.id.img_title_left:
                onBackPressed();
                break;
            //跳转客户详情
            case R.id.tv_related_customer:
                if (!PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_MANAGEMENT)) {
                sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                return;
            }
                mBundle = new Bundle();
                mBundle.putString("Id", mWorksheetInfo.customerId);
                app.startActivity(this, CustomerDetailInfoActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                break;
        }
    }
}
