package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 新建跟进动态
 */
public class WorksheetAddStep1Activity extends BaseActivity implements View.OnClickListener {

    private ViewGroup img_title_left, img_title_right, layout_sale_action;
    private EditText edt;
    private TextView tv_sale_action, tv_contact_name;
    private String clueId;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_contact, ll_contactItem;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_add_step1);

//        if (getIntent() != null && getIntent().getExtras() != null) {
////            Bundle bundle = getIntent().getExtras();
////            clueId = bundle.getString(ExtraAndResult.EXTRA_ID);
////            contactName = bundle.getString(ExtraAndResult.EXTRA_NAME);
//        }

        initUI();
        getTempSaleActivity();
    }

    void getTempSaleActivity() {
    }

    void initUI() {
        super.setTitle("选择订单和工单类型");


        // ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        //img_title_left.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        //img_title_right.setOnTouchListener(touch);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;
            case R.id.layout_remain_time:
                //selectRemainTime();
                break;
            case R.id.layout_sale_action:
                Bundle loseBundle = new Bundle();
                loseBundle.putString("title", "跟进方式");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_SINGLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_SALE_ACTIVE_ACTION);
                app.startActivityForResult(this, CommonTagSelectActivity_.class, 0, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                break;

            case R.id.img_title_right:

                Intent mIntent = new Intent();
                mIntent.setClass(this, WorksheetAddStep2Activity.class);
                startActivityForResult(mIntent, this.RESULT_FIRST_USER);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

//                String content = edt.getText().toString().trim();
//
//                if (StringUtil.isEmpty(content)) {
//                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
//                    return;
//                } else if (TextUtils.isEmpty(tagItemIds)) {
//                    Toast("请选择跟进方式");
//                    return;
//                } else if (TextUtils.isEmpty(clueId)) {
//                    Toast("请选择跟进线索");
//                    return;
//                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CommonTagSelectActivity.REQUEST_TAGS:
//                ArrayList<CommonTag> tags = (ArrayList<CommonTag>) data.getSerializableExtra("data");
//                tv_sale_action.setText(getSaleTypes(tags));
//                tagItemIds = tags.get(0).getId();
                break;
        }
    }
}

