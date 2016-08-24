package com.loyo.oa.v2.activityui.clue;

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
import com.loyo.oa.v2.activityui.customer.FollowContactSelectActivity;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.customer.bean.Contact;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.DateTimePickDialog;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 新建跟进动态
 */
public class ClueFollowupCreateActivity extends BaseActivity implements View.OnClickListener {

    private ViewGroup img_title_left, img_title_right, layout_sale_action;
    private EditText edt;
    private TextView tv_sale_action, tv_contact_name;
    private String clueId;
    private String tagItemIds, contactId, contactName = "无";
    private LinearLayout ll_contact, ll_contactItem;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_followup_create);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            clueId = bundle.getString(ExtraAndResult.EXTRA_ID);
            contactName = bundle.getString(ExtraAndResult.EXTRA_NAME);
        }

        initUI();
        getTempSaleActivity();
    }

    void getTempSaleActivity() {
        if (mSaleActivity == null) {
            return;
        }

        edt.setText(mSaleActivity.getContent());
    }

    void initUI() {
        super.setTitle("写跟进");

        edt = (EditText) findViewById(R.id.edt);
        tv_sale_action = (TextView) findViewById(R.id.tv_sale_action);

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        layout_sale_action = (ViewGroup) findViewById(R.id.layout_sale_action);
        layout_sale_action.setOnClickListener(this);
        layout_sale_action.setOnTouchListener(touch);


        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);


        ll_contactItem = (LinearLayout) findViewById(R.id.ll_contactItem);
        ll_contact = (LinearLayout) findViewById(R.id.ll_contact);
        ll_contact.setOnClickListener(this);
        ll_contact.setOnTouchListener(touch);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        ll_contactItem.setVisibility(null == clueId ? View.GONE : View.VISIBLE);
        tv_contact_name.setText(contactName);
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
                String content = edt.getText().toString().trim();

                if (StringUtil.isEmpty(content)) {
                    Toast(getString(R.string.app_content) + getString(R.string.app_no_null));
                    return;
                } else if (TextUtils.isEmpty(tagItemIds)) {
                    Toast("请选择跟进方式");
                    return;
                } else if (TextUtils.isEmpty(clueId)) {
                    Toast("请选择跟进线索");
                    return;
                }


                HashMap<String, Object> map = new HashMap<>();
                map.put("sealsleadId", clueId);
                map.put("content", content);
                map.put("typeId", tagItemIds);

                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).addSaleactivity(map, new RCallback<SaleActivity>() {
                    @Override
                    public void success(final SaleActivity saleActivity, final Response response) {
                        HttpErrorCheck.checkResponse("新建跟进动态", response);
                        app.finishActivity(ClueFollowupCreateActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });

                break;
        }
    }

    boolean isSave = true;
    SaleActivity mSaleActivity;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clueId) {
            DBManager.Instance().deleteSaleActivity("clue"+clueId);
            if (isSave) {
                mSaleActivity = new SaleActivity();
                mSaleActivity.setContent(edt.getText().toString());
                mSaleActivity.setType(null);
                mSaleActivity.setCreator(null);
                mSaleActivity.setAttachments(null);
                DBManager.Instance().putSaleActivity(MainApp.gson.toJson(mSaleActivity), "clue"+clueId);
            }
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
        }
    }
}

