package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.adapter.OrderEstimateListAdapter;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimateList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CustomTextView;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 订单【回款记录】
 * Created by yyy on 16/8/2.
 */
public class OrderEstimateListActivity extends BaseActivity implements View.OnClickListener {

    /**
     * *****************来自【订单详情】********************
     */
    public final static int ORDER_DETAILS = 0x11;


    /**
     * 新增回款
     */
    public final static int ODET_EST_ADD = 0x12;

    /**
     * 编辑回款
     */
    public final static int ODET_EST_EDIT = 0x13;

    /**
     * 编辑附件
     */
    public final static int ODET_EST_EDITATTAMENT = 0x14;

    /**
     * 删除回款
     */
    public final static int ODET_EST_DELETE = 0x15;


    /**
     * ****************来自【订单新建】**********************
     */
    public final static int ORDER_ADD = 0x21;

    /**
     * 新增回款
     */
    public final static int OADD_EST_ADD = 0x22;

    /**
     * 编辑回款
     */
    public final static int OADD_EST_EDIT = 0x23;

    /**
     * 编辑附件
     */
    public final static int OADD_EST_EDITATTAMENT = 0x24;

    /**
     * 删除回款
     */
    public final static int OADD_EST_DELETE = 0x25;


    /**
     * *******************来自【回款计划】***********************
     */

    public final static int ORDER_PLAN = 0x31;


    private LinearLayout ll_back;
    private LinearLayout ll_add;
    private TextView tv_title, tv_rate_payment;
    private CustomTextView tv_dealprice, tv_totalprice, tv_aleryprice, tv_faileprice;  //成交金额、已回款、开票总金额、未回款

    private ListView lv_listview;
    private EstimateAdd mEstimateAdd;
    private ArrayList<EstimateAdd> mData = new ArrayList<>();
    private OrderEstimateListAdapter mAdapter;
    private Intent mIntent;
    private Bundle mBundle;

    private String orderId;
    private String dealPrice;

    private int fromPage;
    private int requestPage;
    private int position;
    private boolean isAdd;
    int backMoney, orderStatus;
    double ratePayment;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ExtraAndResult.MSG_WHAT_DIALOG:
                    ll_add.setVisibility(View.GONE);
                    break;

                //订单删除操作
                case ExtraAndResult.MSG_WHAT_GONG:

                    if (fromPage == ORDER_ADD) {
                        mBundle = msg.getData();
                        position = mBundle.getInt("posi");
                        mData.remove(position);
                        rushAdapter();
                        ll_add.setVisibility(View.VISIBLE);
                    } else if (fromPage == ORDER_DETAILS) {
                        deleteData();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderestimate);
        initUI();
    }

    public void initUI() {
        mIntent = getIntent();
        if (null != mIntent) {
            isAdd = mIntent.getBooleanExtra(ExtraAndResult.EXTRA_ADD, false);
            orderId = mIntent.getStringExtra("orderId");
            fromPage = mIntent.getIntExtra("fromPage", OrderEstimateListActivity.ORDER_ADD);
            dealPrice = mIntent.getStringExtra("price");
            backMoney = mIntent.getIntExtra("已回款", 0);
            ratePayment = mIntent.getDoubleExtra("回款率", 0);
            orderStatus = mIntent.getIntExtra("订单待审核", 0);
            if (null != (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data")) {
                mData = (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data");
            }
        }

        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_add = (LinearLayout) findViewById(R.id.ll_add);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_rate_payment = (TextView) findViewById(R.id.tv_rate_payment);
        tv_totalprice = (CustomTextView) findViewById(R.id.tv_totalprice);
        tv_aleryprice = (CustomTextView) findViewById(R.id.tv_aleryprice);
        tv_faileprice = (CustomTextView) findViewById(R.id.tv_faileprice);
        tv_dealprice = (CustomTextView) findViewById(R.id.tv_dealprice);
        lv_listview = (ListView) findViewById(R.id.lv_listview);
        tv_title.setText("回款记录");
        if (null != dealPrice)
            tv_dealprice.setText("￥" + Utils.setValueDouble(dealPrice));
        if (null != mData && mData.size() > 0)
            ll_add.setVisibility(View.GONE);
        ll_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());

        //如果来自详情，则请求回款记录
        if (fromPage == ORDER_DETAILS) {
            getData();
            ll_add.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        }
        mAdapter = new OrderEstimateListAdapter(this, mData, mHandler, orderId, fromPage, isAdd);
        mAdapter.setOrderStatus(orderStatus);
        lv_listview.setAdapter(mAdapter);
        rushAdapter();
    }

    public void rushAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 计算 已回款、开票总金额、未回款
     */
    private void setTitleNumber(EstimateList.Total total) {
        tv_totalprice.setText("￥" + Utils.setValueDouble(total.backMoney));
        tv_rate_payment.setText("已回款|回款率" + total.backMoneyRate + "%");
        tv_aleryprice.setText("￥" + Utils.setValueDouble(total.billingMoney));
        tv_faileprice.setText("￥" + Utils.setValueDouble(total.notBackMoney));
    }

    ;

    /**
     * 删除订单
     */
    public void deleteData() {

        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .deletePayEstimate(mData.get(position).id, new Callback<EstimateAdd>() {
                    @Override
                    public void success(EstimateAdd estimateAdds, Response response) {
                        HttpErrorCheck.checkResponse("回款记录列表", response);
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    /**
     * 获取收款记录列表
     */
    public void getData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .getPayEstimate(orderId, new Callback<EstimateList>() {
                    @Override
                    public void success(EstimateList estimateList, Response response) {
                        HttpErrorCheck.checkResponse("回款记录列表", response);
                        if (null != estimateList.records) {
                            mData.clear();
                            mData.addAll(estimateList.records);
                            rushAdapter();
                            setTitleNumber(estimateList.total);
                        }
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

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //新建
            case R.id.ll_add:
                mBundle = new Bundle();
                mBundle.putString("orderId", orderId);
                if (fromPage == OrderEstimateListActivity.ORDER_ADD) {
                    requestPage = OrderEstimateListActivity.OADD_EST_ADD;
                } else if (fromPage == OrderEstimateListActivity.ORDER_DETAILS) {
                    requestPage = OrderEstimateListActivity.ODET_EST_ADD;
                }
                mBundle.putInt("fromPage", requestPage);
                app.startActivityForResult(this, OrderAddEstimateActivity.class, MainApp.ENTER_TYPE_RIGHT, requestPage, mBundle);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        mIntent = new Intent();
        mIntent.putExtra("data", mData);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            //新建订单 编辑与新建回调
            case OADD_EST_ADD:
            case OADD_EST_EDIT:
                if (null == data) {
                    return;
                }
                mEstimateAdd = (EstimateAdd) data.getSerializableExtra("data");
                mData.clear();
                mData.add(mEstimateAdd);
                rushAdapter();
                break;

            //订单详情 编辑与新建回调
            case ODET_EST_EDIT:
            case ODET_EST_ADD:
                getData();
                break;

        }
        mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_DIALOG);
    }
}
