package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetDetial;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetCommon;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventLayout;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【 工单详情 】  页面
 * Created by xeq on 16/8/27.
 */
public class WorksheetDetailActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout img_title_left;
    private LinearLayout ll_worksheet_info;
    private LinearLayout ll_events;
    private TextView tv_title_1, tv_title, tv_status, tv_assignment, tv_complete_number, tv_setting;
    private RelativeLayout img_title_right;
    private Button bt_confirm;
    private String worksheetId;
    private BaseBeanT<WorksheetDetial> mData;
    //处理事件
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case ExtraAndResult.REQUEST_CODE_CUSTOMER://到事件详情
                    Bundle bundle = new Bundle();
                    app.startActivityForResult(WorksheetDetailActivity.this, EventDetialActivity.class, MainApp.ENTER_TYPE_RIGHT, 1, bundle);
                    break;
                case ExtraAndResult.REQUEST_CODE_STAGE://设置负责人
                    SelectDetUserActivity2.startThisForOnly(WorksheetDetailActivity.this, null);
                    overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_detial);
        getIntentData();
        initView();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        worksheetId = intent.getStringExtra(ExtraAndResult.EXTRA_ID);
//        if (TextUtils.isEmpty(worksheetId)) {
//            Toast("参数不全");
//            onBackPressed();
//        }
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        ll_worksheet_info = (LinearLayout) findViewById(R.id.ll_worksheet_info);
        ll_events = (LinearLayout) findViewById(R.id.ll_events);
        ll_worksheet_info.setOnClickListener(this);
        tv_title_1.setText("工单详情");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_assignment = (TextView) findViewById(R.id.tv_assignment);
        tv_complete_number = (TextView) findViewById(R.id.tv_complete_number);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        tv_setting.setOnClickListener(this);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);
        getData();
    }

    private void getData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getWorksheetDetail("57c52813b0207a0615000001", new Callback<BaseBeanT<WorksheetDetial>>() {
                    @Override
                    public void success(BaseBeanT<WorksheetDetial> result, Response response) {
                        HttpErrorCheck.checkResponse("工单详情：", response);
                        mData = result;
                        loadData();
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
            case R.id.img_title_right:
                functionButton();
                break;
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.ll_worksheet_info:
                Bundle bundle = new Bundle();
                app.startActivityForResult(this, WorksheetInfoActivity.class, 0, this.RESULT_FIRST_USER, bundle);
                break;
            case R.id.tv_setting://批量设置
                break;
            case R.id.bt_confirm://提交完成
                break;
        }
    }

    private void loadData() {
        tv_title.setText(mData.data.title);
        tv_assignment.setText("分派人：" + mData.data.dispatcher.getName());
        WorksheetCommon.setStatus(tv_status, mData.data.status);
        for (int i = 0; i < mData.data.sheetEventsSupporter.size(); i++) {
            WorksheetEventLayout eventView = new WorksheetEventLayout(this, handler, mData.data.sheetEventsSupporter.get(i));
            ll_events.addView(eventView);
        }
    }

    /**
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(WorksheetDetailActivity.this).builder();
        dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, data.data.sales);
//                app.startActivityForResult(ClueDetailActivity.this, ClueTransferActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUSET_COMMENT, mBundle);
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
             /*用户单选, 负责人*/
            case SelectDetUserActivity2.REQUEST_ONLY:
                NewUser u = (NewUser) data.getSerializableExtra("data");
//                newUser = u;
//                tv_responsiblePerson.setText(newUser.getName());
                break;
        }
    }

}
