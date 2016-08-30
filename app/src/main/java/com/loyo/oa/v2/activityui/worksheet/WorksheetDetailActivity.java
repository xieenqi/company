package com.loyo.oa.v2.activityui.worksheet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetEventLayout;
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
    private TextView tv_title_1;
    private RelativeLayout img_title_right;
    //处理事件
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_detial);
        initView();

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
        getData();
        loadData();
    }

    private void getData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(IWorksheet.class).
                getWorksheetDetail("", new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

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
        }
    }

    private void loadData() {
        for (int i = 0; i < 13; i++) {
            WorksheetEventLayout eventView = new WorksheetEventLayout(this, handler);
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

}
