package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorkSheetListNestingAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetInfo;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【工单详情】  页面
 * Created by yyy on 16/8/30.
 */

public class WorksheetInfoActivity extends BaseActivity implements View.OnClickListener {

    /** UI */
    private ListView lv_listview;
    private LinearLayout img_title_left;
    private RelativeLayout img_title_right;
    private TextView tv_title_1, /*标题*/
            tv_title,            /*工单标题*/
            tv_status,           /*工单状态*/
            tv_commit_info,      /*提交信息*/
            tv_track_content,    /*内容*/
            tv_Assignment_name,  /*分派人*/
            tv_boom,             /*触发方式*/
            tv_related_order,    /*关联订单*/
            tv_responsible_name, /*负责人*/
            tv_assignment_time,  /*分派时间*/
            tv_finish_time,      /*完成时间*/
            tv_termination_time; /*终止时间*/


    /** Data*/
    private String id;

    /** Other*/
    private WorkSheetListNestingAdapter mAdapter;
    private BaseBeanT<WorksheetInfo> mWorksheetInfo;
    private Intent mIntent;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_info);
        initView();
    }

    private void initView() {
        mIntent = getIntent();
        id = mIntent.getStringExtra(ExtraAndResult.CC_USER_ID);
        showLoading("");
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title_1          = (TextView) findViewById(R.id.tv_title_1);
        tv_Assignment_name  = (TextView) findViewById(R.id.tv_Assignment_name);
        tv_boom             = (TextView) findViewById(R.id.tv_boom);
        tv_related_order    = (TextView) findViewById(R.id.tv_related_order);
        tv_responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        tv_assignment_time  = (TextView) findViewById(R.id.tv_assignment_time);
        tv_finish_time      = (TextView) findViewById(R.id.tv_finish_time);
        tv_termination_time = (TextView) findViewById(R.id.tv_termination_time);
        tv_commit_info      = (TextView) findViewById(R.id.tv_commit_info);
        tv_track_content    = (TextView) findViewById(R.id.tv_track_content);
        tv_title            = (TextView) findViewById(R.id.tv_title);
        tv_status           = (TextView) findViewById(R.id.tv_status);
        img_title_left.setOnClickListener(this);
        tv_related_order.setOnClickListener(this);
        tv_title_1.setText("工单信息");
        requestData();
    }

    private void bindData(){

                tv_title.setText(mWorksheetInfo.data.title);
                tv_Assignment_name.setText(mWorksheetInfo.data.dispatcherName);
                if(mWorksheetInfo.data.triggerMode == 1){
                    tv_boom.setText("流程触发");
                }else{
                    tv_boom.setText("定时触发");
                }
                tv_commit_info.setText(mWorksheetInfo.data.creatorName+" "+DateTool.timet(mWorksheetInfo.data.createdAt+"",DateTool.DATE_FORMATE_AT_MINUTES)+" 提交");
                tv_track_content.setText(mWorksheetInfo.data.content);
                tv_related_order.setText(mWorksheetInfo.data.orderName);
                tv_responsible_name.setText(mWorksheetInfo.data.responsorNames);

                if(mWorksheetInfo.data.confirmedAt != 0){
                    tv_assignment_time.setText(DateTool.timet(mWorksheetInfo.data.confirmedAt+"",DateTool.DATE_FORMATE_AT_MINUTES));
                }else{
                    tv_assignment_time.setText("--");
                }

                if (mWorksheetInfo.data.completedAt != 0) {
                    tv_finish_time.setText(DateTool.timet(mWorksheetInfo.data.completedAt + "", DateTool.DATE_FORMATE_AT_MINUTES));
                } else {
                    tv_finish_time.setText("--");
                }

                if (mWorksheetInfo.data.completedAt != 0) {
                    tv_termination_time.setText(DateTool.timet(mWorksheetInfo.data.completedAt + "", DateTool.DATE_FORMATE_AT_MINUTES));
                } else {
                    tv_termination_time.setText("--");
                }
        WorksheetCommon.setStatus(tv_status, mWorksheetInfo.data.status);
        if(null != mWorksheetInfo.data.attachment && mWorksheetInfo.data.attachment.size() > 0){
            mAdapter = new WorkSheetListNestingAdapter(mWorksheetInfo.data.attachment,this);
            lv_listview.setAdapter(mAdapter);
        }
    }

    /**
     * 获取工单信息
     * */
    private void requestData(){
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getWorksheetInfo(id, new Callback<BaseBeanT<WorksheetInfo>>() {
                    @Override
                    public void success(BaseBeanT<WorksheetInfo> worksheetInfo, Response response) {
                        HttpErrorCheck.checkResponse("获取工单信息：", response);
                        mWorksheetInfo = worksheetInfo;
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //跳转订单
            case R.id.tv_related_order:
                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_ID,mWorksheetInfo.data.orderId);
                app.startActivity(this, OrderDetailActivity.class, MainApp.ENTER_TYPE_RIGHT,false,mBundle);
                break;

            //返回
            case R.id.img_title_left:
                onBackPressed();
                break;

            default:
                break;
        }
    }
}
