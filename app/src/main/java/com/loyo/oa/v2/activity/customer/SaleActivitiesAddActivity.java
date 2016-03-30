package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CommonTag;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.DateTimePickDialog;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 新建跟进动态
 * */
public class SaleActivitiesAddActivity extends BaseActivity implements View.OnClickListener {

    private ViewGroup img_title_left, img_title_right, layout_remain_time, layout_sale_action;
    private EditText edt;
    private TextView tv_sale_action, tv_remain_time;
    private Customer mCustomer;
    private String tagItemIds;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_activities_add);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mCustomer = (Customer) bundle.getSerializable(Customer.class.getName());
        }

        initUI();
        getTempSaleActivity();
    }

    void getTempSaleActivity() {
        //        mSaleActivity = DBManager.Instance().getSaleActivity(mCustomer.getId());
        if (mSaleActivity == null) {
            return;
        }

        edt.setText(mSaleActivity.getContent());
    }

    void initUI() {
        super.setTitle("新建跟进动态");

        edt = (EditText) findViewById(R.id.edt);
        tv_remain_time = (TextView) findViewById(R.id.tv_remain_time);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_sale_action.setOnClickListener(this);
        layout_sale_action.setOnTouchListener(touch);

        layout_remain_time = (ViewGroup) findViewById(R.id.layout_remain_time);
        layout_remain_time.setOnClickListener(this);
        layout_remain_time.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);
    }

    /**
     * 选择下次跟进时间
     */
    private void selectRemainTime() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                String str = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day) + String.format(" %02d", hour) + String.format(":%02d", min);
                tv_remain_time.setText(str);
            }
        },false);
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;
            case R.id.layout_remain_time:
                selectRemainTime();
                break;
            case R.id.layout_sale_action:
                Bundle loseBundle = new Bundle();
                loseBundle.putString("title", "跟进方式");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_SINGLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_SALE_ACTIVE_ACTION);
                app.startActivityForResult(this, CommonTagSelectActivity_.class, 0, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                break;

            case R.id.img_title_right:
                String content = edt.getText().toString().trim();

                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                }

                if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("customerId", mCustomer.getId());
                map.put("content", content);
                map.put("typeId", tagItemIds);
                map.put("remindAt", DateTool.getDateToTimestamp(tv_remain_time.getText().toString().trim(), app.df2) / 1000);
                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addSaleactivity(map, new RCallback<SaleActivity>() {
                    @Override
                    public void success(final SaleActivity saleActivity, final Response response) {
                        onBackOk(saleActivity);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });

                break;
            default:

                break;
        }

    }

    /**
     * 返回创建的数据
     *
     * @param activity
     */
    public void onBackOk(final SaleActivity activity) {
        isSave = false;
        Intent intent = new Intent();
        intent.putExtra("data", activity);
        app.finishActivity(SaleActivitiesAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }


    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.Instance().deleteSaleActivity(mCustomer.getId());
        if (isSave) {
            mSaleActivity = new SaleActivity();
            mSaleActivity.setContent(edt.getText().toString());

            mSaleActivity.setType(null);
            mSaleActivity.setCreator(null);
            mSaleActivity.setAttachments(null);
            DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), mCustomer.getId());
        }
    }

    /**
     * 获取跟进方式
     *
     * @param tags
     * @return
     */
    private String getSaleTypes(final ArrayList<CommonTag> tags) {
        if (null == tags || tags.isEmpty()) {
            return "";
        }
        StringBuilder reasons = new StringBuilder();
        int index = 0;
        for (CommonTag reson : tags) {
            reasons.append(reson.getName());
            if (index < tags.size() - 1) {
                reasons.append(",");
            }
            index++;
        }
        return reasons.toString();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CommonTagSelectActivity.REQUEST_TAGS:
                ArrayList<CommonTag> tags = (ArrayList<CommonTag>) data.getSerializableExtra("data");
                tv_sale_action.setText(getSaleTypes(tags));
                tagItemIds = tags.get(0).getId();
                break;
            default:

                break;
        }
    }
}
