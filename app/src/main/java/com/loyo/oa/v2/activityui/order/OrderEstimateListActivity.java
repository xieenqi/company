package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.adapter.OrderEstimateListAdapter;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.EstimateList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomTextView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 订单【回款记录】
 * Created by yyy on 16/8/2.
 */
public class OrderEstimateListActivity extends BaseLoadingActivity implements View.OnClickListener {

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
     * *******************来自【回款计划】***********************
     */

    public final static int ORDER_PLAN = 0x31;

    public final static String RET_HAS_CHANGED_DATA = "com.loyo.OrderEstimateListActivity.RET_HAS_CHANGED_DATA";
    public final static String KEY_COMMIT_CHANGE = "com.loyo.OrderEstimateListActivity.KEY_COMMIT_CHANGE";
    public final static String KEY_GET_DATA = "com.loyo.OrderEstimateListActivity.KEY_GET_DATA";

    private LinearLayout ll_back;
    private LinearLayout ll_add;
    private TextView tv_title, tv_rate_payment;
    private CustomTextView tv_dealprice, tv_totalprice, tv_aleryprice, tv_faileprice;  //成交金额、已回款、开票总金额、未回款
    private ListView lv_listview;
    private EstimateAdd mEstimateAdd;
    private EstimateList mEstimateList;

    private ArrayList<EstimateAdd> capitalReturningList = new ArrayList<>();
    private boolean hasChangedData = false;
    private boolean commitChanges = false;

    private OrderEstimateListAdapter mAdapter;
    private Intent mIntent;
    private Bundle mBundle;
    private String orderId;
    private String dealPrice;
    private int fromPage;
    private int requestPage;
    private int position;
    private boolean isAdd;
    private int backMoney = 0;
    private double ratePayment = 0.0;
    private int orderStatus;
    private boolean needFetchData = false;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //订单删除操作
                case ExtraAndResult.MSG_WHAT_GONG:
                    mBundle = msg.getData();
                    position = mBundle.getInt("posi");

                    if (commitChanges) {
                        deleteData(capitalReturningList.get(position));
                    }
                    else {
                        capitalReturningList.remove(position);
                        hasChangedData = true;
                        reloadList();
                    }

                    break;

                //刷新回款记录顶部数据
                case ExtraAndResult.MSG_SEND:
                    tv_rate_payment.setText("已回款|回款率" + mEstimateList.total.backMoneyRate + "%");
                    tv_dealprice.setText("￥" + Utils.setValueDouble(mEstimateList.total.dealMoney));
                    tv_totalprice.setText("￥" + Utils.setValueDouble(mEstimateList.total.backMoney));
                    tv_aleryprice.setText("￥" + Utils.setValueDouble(mEstimateList.total.billingMoney));
                    tv_faileprice.setText("￥" + Utils.setValueDouble(mEstimateList.total.notBackMoney));
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        if (needFetchData) {
            getPageData();
        }
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_orderestimate);
    }

    @Override
    public void getPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        getData();
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
            commitChanges = mIntent.getBooleanExtra(KEY_COMMIT_CHANGE, false);
            if (null != (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data")) {
                capitalReturningList = (ArrayList<EstimateAdd>) mIntent.getSerializableExtra("data");
            }
            needFetchData = mIntent.getBooleanExtra(KEY_GET_DATA, false);
            if (orderId != null) {
                needFetchData = true;/* fix bug */
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
        if (null != dealPrice) {
            tv_dealprice.setText("￥" + Utils.setValueDouble(dealPrice));
        }
        ll_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());

        //详情页面传过来的
/*      tv_totalprice.setText("￥" + Utils.setValueDouble(backMoney));
        tv_rate_payment.setText("已回款|回款率" + ratePayment + "%");*/

        tv_totalprice.setText("￥" + backMoney);
        tv_rate_payment.setText("已回款|回款率" + ratePayment + "%");

        //如果来自详情，则请求回款记录
        ll_add.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        mAdapter = new OrderEstimateListAdapter(this, capitalReturningList, mHandler, orderId, fromPage, isAdd);
        mAdapter.setOrderStatus(orderStatus);
        lv_listview.setAdapter(mAdapter);
        reloadList();
        ll_loading.setStatus(LoadingLayout.Success);
    }

    public void reloadList() {
        mAdapter.notifyDataSetChanged();
    }


    /**
     * 删除订单
     */
    public void deleteData(EstimateAdd item) {
        if (item == null) {
            return;
        }
        showLoading2("");
        OrderService.deletePayEstimate(item.id)
                .subscribe(new DefaultLoyoSubscriber<EstimateAdd>(hud) {
                    @Override
                    public void onNext(EstimateAdd add) {
                        getData();
                    }
                });
    }

    public void addData(EstimateAdd item) {
        if (item == null) {
            return;
        }
        showCommitLoading();
        HashMap<String, Object> map = new HashMap<>();
        map.put("attachmentUUId", item.attachmentUUId);
        map.put("attachmentCount", item.attachmentCount);
        map.put("payeeMethod", item.payeeMethod);
        map.put("orderId", orderId);
        map.put("attachmentsName", "");
        map.put("receivedAt", item.receivedAt);
        map.put("receivedMoney", item.receivedMoney);
        map.put("billingMoney", item.billingMoney);
        map.put("remark", item.remark);
        map.put("payMethodString", getPayeeMethod(item.payeeMethod));
        map.put("payeeUser", item.payeeUser);
        OrderService.addPayEstimate(map)
                .subscribe(new DefaultLoyoSubscriber<EstimateAdd>(hud) {
                    @Override
                    public void onNext(EstimateAdd add) {
                        getData();
                    }
                });
    }

    public void editData(EstimateAdd item) {
        if (item == null) {
            return;
        }
        showCommitLoading();
        HashMap<String, Object> map = new HashMap<>();
        map.put("attachmentUUId", item.attachmentUUId);
        map.put("attachmentCount", item.attachmentCount);
        map.put("payeeMethod", item.payeeMethod);
        map.put("orderId", orderId);
        map.put("attachmentsName", "");
        map.put("receivedAt", item.receivedAt);
        map.put("receivedMoney", item.receivedMoney);
        map.put("billingMoney", item.billingMoney);
        map.put("remark", item.remark);
        map.put("payMethodString", getPayeeMethod(item.payeeMethod));
        map.put("payeeUser", item.payeeUser);
        OrderService.editPayEstimate(item.id, map)
                .subscribe(new DefaultLoyoSubscriber<EstimateAdd>(hud) {
                    @Override
                    public void onNext(EstimateAdd add) {
                        getData();
                    }
                });
    }

    private String getPayeeMethod(int payeeMethod) {
        String result = "其他";
        switch (payeeMethod) {

            case 1:
                result= "现金";
                break;

            case 2:
                result= "支票";
                break;

            case 3:
                result= "银行转账";
                break;

            case 4:
                result= "其它";
                break;
        }
        return result;
    }


    /**
     * 获取收款记录列表
     */
    public void getData() {
        OrderService.getPayEstimate(orderId)
                .subscribe(new DefaultLoyoSubscriber<EstimateList>(ll_loading) {
                    @Override
                    public void onNext(EstimateList list) {
                        ll_loading.setStatus(LoadingLayout.Success);
                        if (null != list) {
                            mEstimateList = list;
                            if (null != list.records) {
                                capitalReturningList.clear();
                                capitalReturningList.addAll(list.records);
                                reloadList();
                                mHandler.sendEmptyMessage(ExtraAndResult.MSG_SEND);
                                if (capitalReturningList.size() == 0)
                                    ll_loading.setStatus(LoadingLayout.Empty);
                            }
                        } else {
                            ll_loading.setStatus(LoadingLayout.No_Network);
                            ll_loading.setNoNetworkText("没有获取到数据");
                        }
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
                app.startActivityForResult(this, OrderAddEstimateActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, requestPage, mBundle);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("data", capitalReturningList);
        mIntent.putExtra(RET_HAS_CHANGED_DATA, hasChangedData);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || null == data) {
            return;
        }

        if (commitChanges) {
            boolean isEdit = data.getBooleanExtra(OrderAddEstimateActivity.RET_IS_DATA_EDITED, false);
            mEstimateAdd = (EstimateAdd) data.getSerializableExtra("data");
            if (isEdit) {
                editData(mEstimateAdd);
            }
            else {
                addData(mEstimateAdd);
            }
        }
        else {
            boolean isEdit = data.getBooleanExtra(OrderAddEstimateActivity.RET_IS_DATA_EDITED, false);
            Serializable userInfo = data.getSerializableExtra(OrderAddEstimateActivity.KEY_USER_INFO);
            mEstimateAdd = (EstimateAdd) data.getSerializableExtra("data");
            if (mEstimateAdd != null) {
                if (isEdit && userInfo != null) {
                    Integer index = (Integer)((HashMap<String, Object>) userInfo).get("edit_index");
                    if (index < capitalReturningList.size() && index >= 0) {
                        capitalReturningList.remove(capitalReturningList.get(index));
                        capitalReturningList.add(index, mEstimateAdd);
                        hasChangedData = true;
                        reloadList();
                    }

                }
                else
                {
                    capitalReturningList.add(0, mEstimateAdd);
                    hasChangedData = true;
                    reloadList();
                }
            }
        }
    }
}
