package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.order.bean.ExtensionDatas;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.order.common.ViewOrderDetailsExtra;
import com.loyo.oa.v2.activityui.sale.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单详情】页面
 * Created by xeq on 16/8/2.
 */
public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout img_title_left, ll_extra, ll_product, ll_record, ll_enclosure, ll_plan, ll_wflayout;
    private RelativeLayout img_title_right;
    private TextView tv_title_1, tv_title, tv_status, tv_customer, tv_money, tv_product, tv_plan, tv_plan_value,
            tv_record, tv_record_value, tv_enclosure, tv_enclosure_value, tv_responsible_name, tv_creator_name,
            tv_creator_time, tv_wfname, tv_order_number, tv_memo;
    private OrderDetail mData;
    private String orderId;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        orderId = getIntent().getStringExtra(ExtraAndResult.EXTRA_ID);
        if (TextUtils.isEmpty(orderId)) {
            onBackPressed();
            Toast("参数不全");
        }
    }

    private void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("订单详情");
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        tv_plan_value = (TextView) findViewById(R.id.tv_plan_value);
        tv_record = (TextView) findViewById(R.id.tv_record);
        tv_record_value = (TextView) findViewById(R.id.tv_record_value);
        tv_enclosure = (TextView) findViewById(R.id.tv_enclosure);
        tv_enclosure_value = (TextView) findViewById(R.id.tv_enclosure_value);
        tv_responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        tv_creator_name = (TextView) findViewById(R.id.tv_creator_name);
        tv_creator_time = (TextView) findViewById(R.id.tv_creator_time);
        tv_wfname = (TextView) findViewById(R.id.tv_wfname);
        tv_order_number = (TextView) findViewById(R.id.tv_order_number);
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        ll_extra = (LinearLayout) findViewById(R.id.ll_extra);
        tv_customer.setOnClickListener(this);
        tv_wfname.setOnClickListener(this);
        ll_product = (LinearLayout) findViewById(R.id.ll_product);
        ll_product.setOnClickListener(this);
        ll_record = (LinearLayout) findViewById(R.id.ll_record);
        ll_record.setOnClickListener(this);
        ll_enclosure = (LinearLayout) findViewById(R.id.ll_enclosure);
        ll_enclosure.setOnClickListener(this);
        ll_plan = (LinearLayout) findViewById(R.id.ll_plan);
        ll_plan.setOnClickListener(this);
        ll_wflayout = (LinearLayout) findViewById(R.id.ll_wflayout);
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                functionBuuton();
                break;
            case R.id.tv_customer://跳转到相关客户
                Intent intent = new Intent();
                intent.putExtra("Id", mData.customerId);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                intent.setClass(OrderDetailActivity.this, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.tv_wfname://跳转到相关审批
                Intent intentWf = new Intent();
                intentWf.putExtra(ExtraAndResult.EXTRA_ID, mData.wfId);
                intentWf.setClass(OrderDetailActivity.this, WfinstanceInfoActivity_.class);
                startActivityForResult(intentWf, ExtraAndResult.REQUEST_CODE);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
            case R.id.ll_product://购买产品
                Bundle product = new Bundle();
                product.putInt("data", ActionCode.ORDER_DETAIL);
                product.putSerializable(ExtraAndResult.EXTRA_DATA, mData.proInfo);
                app.startActivityForResult(OrderDetailActivity.this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
            case R.id.ll_record://回款记录
                Bundle mBundle = new Bundle();
                mBundle.putString("price", tv_money.getText().toString());
                app.startActivityForResult(this, OrderEstimateListActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
                break;
            case R.id.ll_enclosure://附件
                app.startActivityForResult(this, OrderAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, 101, null);
                break;
            case R.id.ll_plan://回款计划
                mBundle = new Bundle();
                mBundle.putString("orderId", mData.id);
                app.startActivityForResult(this, OrderPlanListActivity.class, MainApp.ENTER_TYPE_RIGHT, 102, mBundle);
                break;
        }

    }

    private void getData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .getSaleDetails(orderId, new Callback<OrderDetail>() {
                    @Override
                    public void success(OrderDetail orderdetail, Response response) {
                        HttpErrorCheck.checkResponse("订单详情", response);
                        if (null == orderdetail) {
                            Toast("没有获取到数据");
                            onBackPressed();
                            return;
                        }
                        mData = orderdetail;
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        onBackPressed();
                    }
                });
    }

    private void bindData() {
        tv_title.setText(mData.title);
        tv_customer.setText(mData.customerName);
        tv_money.setText(mData.dealMoney + "");
        tv_product.setText(mData.proName);
        tv_responsible_name.setText(mData.directorName);
        tv_creator_name.setText(mData.creatorName);
        tv_plan.setText("回款计划（" + mData.planNum + "）");
        tv_record.setText("回款记录（" + mData.recordNum + "）");
        tv_record_value.setText("￥" + mData.backMoney + "(" + mData.ratePayment + "%)");
        tv_enclosure.setText("附件（" + mData.attachmentCount + "）");
        tv_creator_time.setText(app.df3.format(new Date(Long.valueOf(mData.createdAt + "") * 1000)));
        OrderCommon.getOrderStatus(tv_status, mData.status);
        if (!TextUtils.isEmpty(mData.wfName)) {//是否关联审批
            ll_wflayout.setVisibility(View.VISIBLE);
            tv_wfname.setText(mData.wfName);
        }
        tv_order_number.setText(mData.orderNum);
        tv_memo.setText("备注：" + mData.remark);
        if (ll_extra.getChildCount() != 0) {
            ll_extra.removeAllViews();
        }
        if (null != mData.extensionDatas) {
            for (ExtensionDatas ele : mData.extensionDatas) {
                ll_extra.addView(new ViewOrderDetailsExtra(mContext, ele));
            }
        }
    }

    private void functionBuuton() {
        ActionSheetDialog dialog = new ActionSheetDialog(OrderDetailActivity.this).builder();
        dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, mData);
                app.startActivityForResult(OrderDetailActivity.this, OrderAddActivity.class, MainApp.ENTER_TYPE_RIGHT, 0x01, mBundle);
            }
        });
        dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                terminationOrder();
            }
        });
        dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                deleteOrder();
            }
        });
        dialog.show();
    }

    private void deleteOrder() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class).
                deleteOrder(mData.id, new Callback<Object>() {
                    @Override
                    public void success(Object o, Response response) {
                        HttpErrorCheck.checkResponse("删除订单：", response);
                        onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    private void terminationOrder() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class).
                terminationOrder(mData.id, new Callback<Object>() {
                    @Override
                    public void success(Object estimatePlanAdd, Response response) {
                        HttpErrorCheck.checkResponse("意外终止订单：", response);
                        Toast("订单终止成功");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }
}
