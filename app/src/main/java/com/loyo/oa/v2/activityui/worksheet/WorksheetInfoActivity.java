package com.loyo.oa.v2.activityui.worksheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【 工单详情 】  页面
 * Created by xeq on 16/8/27.
 */
public class WorkSheetInfoActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout img_title_left;
    private TextView tv_title_1;
    private LinearLayout ll_attachment;
    private RelativeLayout img_title_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_info);
        initView();
        loadData();
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        ll_attachment = (LinearLayout) findViewById(R.id.ll_attachment);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_title_1.setText("工单信息");
    }

    private void loadData() {
        for (int i = 0; i < 3; i++) {
            View eventView =  LayoutInflater.from(mContext).inflate(R.layout.item_worksheet_event, null, false);
            ll_attachment.addView(eventView);
        }
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
        }
    }

    /**
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(WorkSheetInfoActivity.this).builder();
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
