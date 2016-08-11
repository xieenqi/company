package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.sale.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.OrderAddforExtraData;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新建订单】
 * Created by xeq on 16/8/1.
 */
public class OrderAddActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private ImageView iv_submit;
    private LinearLayout ll_back;
    private LinearLayout ll_customer; //对应客户
    private LinearLayout ll_stage;    //意向产品
    private LinearLayout ll_estimate; //回款
    private LinearLayout ll_source;   //附件
    private LinearLayout tv_custom;   //附件

    private String customerName, customerId;
    private ArrayList<ContactLeftExtras> fieldData;
    private ArrayList<ContactLeftExtras> mCusList;
    private ArrayList<SaleIntentionalProduct> productData;//意向产品的数据
    private ArrayList<EstimateAdd> estimateData;          //回款记录数据


    private EditText et_name;     //订单标题
    private TextView tv_customer; //对应客户
    private TextView tv_stage;    //购买产品
    private EditText et_money;    //成交金额
    private TextView tv_estimate; //添加回款
    private TextView tv_source;   //附件
    private EditText et_ordernum; //订单编号
    private EditText et_remake;   //备注

    private Intent mIntent;
    private Bundle mBundle;
    private int fromPage;
    private int attamentSize = 0;
    private String uuid;

    private OrderDetail mOrderDetail;
    private OrderAddforExtraData orderAddforExtra;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ExtraAndResult.MSG_WHAT_VISIBLE:
                    if (attamentSize != 0) {
                        tv_source.setText("附件(" + attamentSize + ")");
                    }
                    break;
                case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                    ll_customer.setVisibility(TextUtils.isEmpty(customerId) ? View.VISIBLE : View.GONE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        init();
    }

    private void init() {
        mIntent = getIntent();
        if (null != mIntent) {
            mOrderDetail = (OrderDetail) mIntent.getSerializableExtra("data");
            fromPage = mIntent.getIntExtra("fromPage", OrderDetailActivity.ORDER_ADD);
            //客户创建订单
            customerId = mIntent.getStringExtra(ExtraAndResult.EXTRA_ID);
            customerName = mIntent.getStringExtra(ExtraAndResult.EXTRA_NAME);
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_stage = (LinearLayout) findViewById(R.id.ll_stage);
        ll_estimate = (LinearLayout) findViewById(R.id.ll_estimate);
        ll_source = (LinearLayout) findViewById(R.id.ll_source);
        tv_custom = (LinearLayout) findViewById(R.id.tv_custom);

        et_name = (EditText) findViewById(R.id.et_name);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_stage = (TextView) findViewById(R.id.tv_stage);
        et_money = (EditText) findViewById(R.id.et_money);
        tv_estimate = (TextView) findViewById(R.id.tv_estimate);
        tv_source = (TextView) findViewById(R.id.tv_source);
        et_ordernum = (EditText) findViewById(R.id.et_ordernum);
        et_remake = (EditText) findViewById(R.id.et_remake);

        iv_submit.setOnTouchListener(Global.GetTouch());
        ll_back.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ll_customer.setOnClickListener(this);
        ll_stage.setOnClickListener(this);
        ll_estimate.setOnClickListener(this);
        ll_source.setOnClickListener(this);

        if (fromPage == OrderDetailActivity.ORDER_EDIT) {
            tv_title.setText("编辑订单");
            editData();
        } else if (fromPage == OrderDetailActivity.ORDER_ADD) {
            tv_title.setText("新建订单");
            getAddDynamic();
            mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_HIDEDIALOG);
        } else if (fromPage == OrderDetailActivity.ORDER_CREATE) {
            tv_title.setText("生成订单");
            getAddDynamic();
            createData();
        }

    }

    /**
     * 编辑数据绑定
     */

    public void editData() {

        customerId = mOrderDetail.customerId;
        customerName = mOrderDetail.customerName;
        productData = mOrderDetail.proInfo;
        estimateData = mOrderDetail.paymentRecords;
        mCusList = mOrderDetail.extensionDatas;

        et_name.setText(mOrderDetail.title);
        tv_customer.setText(mOrderDetail.customerName);
        tv_stage.setText(getIntentionProductName());
        tv_estimate.setText(getEstimateName());
        tv_estimate.setText(getEstimateName());
        et_money.setText(mOrderDetail.dealMoney + "");
        et_ordernum.setText(mOrderDetail.orderNum);
        et_remake.setText(mOrderDetail.remark);
        bindExtraView(mCusList);

    }

    /**
     * 生成订单
     */
    void createData() {
        customerId = mOrderDetail.customerId;
        customerName = mOrderDetail.customerName;
        productData = mOrderDetail.proInfo;
        et_money.setText(mOrderDetail.dealMoney + "");
        tv_customer.setText(mOrderDetail.customerName);
        tv_stage.setText(getIntentionProductName());
    }

    /**
     * 设置权限
     * 标题、客户、产品、金额都是必填，所以不判断
     */
    public void setAddDynamic(ArrayList<ContactLeftExtras> extras) {
        for (ContactLeftExtras customerJur : extras) {
            switch (customerJur.name) {

                case "paymentRecords":
                    if (customerJur.required) {
                        tv_estimate.setHint("必填,请输入");
                    }
                    break;

                case "orderNum":
                    if (customerJur.required) {
                        et_ordernum.setHint("必填,请输入");
                    }
                    break;

                case "attachment":
                    if (customerJur.required) {
                        tv_source.setHint("必填,请输入");
                    }
                    break;

                case "remark":
                    if (customerJur.required) {
                        et_remake.setHint("备注(必填)");
                    }
                    break;

            }
        }
    }

    /**
     * 动态字段绑定
     */
    public void bindExtraView(ArrayList<ContactLeftExtras> extrases) {
        orderAddforExtra = new OrderAddforExtraData(mContext, extrases, true, R.color.title_bg1, 0);
        tv_custom.addView(orderAddforExtra);
        setAddDynamic(extrases);
    }


    /**
     * 获取新建订单动态字段
     */
    public void getAddDynamic() {
        showLoading("", false);
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 104);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getAddCustomerJur(map, new RCallback<ArrayList<ContactLeftExtras>>() {
            @Override
            public void success(final ArrayList<ContactLeftExtras> cuslist, final Response response) {
                HttpErrorCheck.checkResponse("新建订单动态字段", response);
                mCusList = cuslist;
                bindExtraView(cuslist);
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 提交订单
     */
    public void commitOrder() {

        if (TextUtils.isEmpty(et_name.getText().toString())) {
            Toast("请填写订单标题!");
            return;
        } else if (TextUtils.isEmpty(tv_customer.getText().toString())) {
            Toast("请选择对应客户!");
            return;
        } else if (TextUtils.isEmpty(tv_stage.getText().toString())) {
            Toast("请选择购买产品!");
            return;
        } else if (TextUtils.isEmpty(et_money.getText().toString())) {
            Toast("请选择成交金额!");
            return;
        }
        fieldData = new ArrayList<>();
        for (ContactLeftExtras extra : orderAddforExtra.getExtras()) {
            if (!extra.isSystem && extra.required && TextUtils.isEmpty(extra.val)) {
                Toast("请填写必填项!");
                return;
            } else if (!extra.isSystem && extra.required && !TextUtils.isEmpty(extra.val)) {
                fieldData.add(extra);
            }
        }

        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        if (fromPage == OrderDetailActivity.ORDER_EDIT) {
            map.put("id", mOrderDetail.id);
        }
        map.put("attachmentCount", attamentSize);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        map.put("title", et_name.getText().toString());
        map.put("attachmentUUId", uuid);
        map.put("dealMoney", Float.parseFloat(et_money.getText().toString()));
        map.put("orderNum", et_ordernum.getText().toString());
        map.put("remark", et_remake.getText().toString());
        map.put("proInfo", productData);
        map.put("paymentRecords", estimateData);
        map.put("extensionDatas", fieldData);
        LogUtil.dee("提交参数:" + MainApp.gson.toJson(map));

        if (fromPage == OrderDetailActivity.ORDER_EDIT) {
            editOrderData(map);
        } else {
            addOrderData(map);
        }

    }

    /**
     * 编辑订单
     */
    public void editOrderData(HashMap<String, Object> map) {

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .editOrder(mOrderDetail.id, map, new Callback<OrderAdd>() {
                    @Override
                    public void success(OrderAdd orderAdd, Response response) {
                        HttpErrorCheck.checkResponse("编辑订单", response);
                        app.finishActivity(OrderAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });

    }

    /**
     * 新建订单
     */
    public void addOrderData(HashMap<String, Object> map) {

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .addOrder(map, new Callback<OrderAdd>() {
                    @Override
                    public void success(OrderAdd orderAdd, Response response) {
                        HttpErrorCheck.checkResponse("创建订单", response);
                        app.finishActivity(OrderAddActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
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

            //提交
            case R.id.iv_submit:
                commitOrder();
                break;

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //对应客户
            case R.id.ll_customer:
                app.startActivityForResult(OrderAddActivity.this, SigninSelectCustomer.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, null);
                break;

            //购买产品
            case R.id.ll_stage:
                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, productData);
                mBundle.putBoolean("boolean", true);
                mBundle.putInt("data", ActionCode.ORDER_DETAIL);
                app.startActivityForResult(OrderAddActivity.this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, mBundle);
                break;

            //回款
            case R.id.ll_estimate:
                mBundle = new Bundle();
                if (!TextUtils.isEmpty(et_money.getText().toString())) {
                    mBundle.putString("price", et_money.getText().toString());
                }
                if (null != estimateData) {
                    mBundle.putSerializable("data", estimateData);
                }
                mBundle.putInt("fromPage", OrderEstimateListActivity.PAGE_ORDER_ADD);
                app.startActivityForResult(this, OrderEstimateListActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
                break;

            //附件
            case R.id.ll_source:
                mBundle = new Bundle();
                mBundle.putString("uuid", uuid);
                app.startActivityForResult(this, OrderAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
                break;

        }
    }

    /**
     * 获取 意向产品的名字
     *
     * @return
     */
    private String getIntentionProductName() {
        String productName = "";
        if (null != productData) {
            for (SaleIntentionalProduct ele : productData) {
                productName += ele.name + "、";
            }
        } else {
            return "";
        }
        return productName.length() > 0 ? productName.substring(0, productName.length() - 1) : "";
    }

    /**
     * 获取 回款记录的成交金额
     */
    private String getEstimateName() {
        String estimateName = "";
        if (estimateData.size() > 0) {
            estimateName = "￥";
            for (EstimateAdd est : estimateData) {
                estimateName += est.receivedMoney + "、";
            }
        }
        return estimateName.length() > 0 ? estimateName.substring(0, estimateName.length() - 1) : "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != -1 && null == data) {
            return;
        }

        switch (requestCode) {

            //选择客户
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.getId();
                    customerName = customer.name;
                }
                tv_customer.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                break;

            //选择购买产品
            case ExtraAndResult.REQUEST_CODE_PRODUCT:
                productData = (ArrayList<SaleIntentionalProduct>) data.getSerializableExtra(ExtraAndResult.RESULT_DATA);
                tv_stage.setText(getIntentionProductName());
                break;

            //选择回款
            case ExtraAndResult.REQUEST_CODE_SOURCE:
                estimateData = (ArrayList<EstimateAdd>) data.getSerializableExtra("data");
                tv_estimate.setText(getEstimateName());
                break;

            //附件回调
            case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                uuid = data.getStringExtra("uuid");
                attamentSize = data.getIntExtra("size", 0);
                mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_VISIBLE);
                break;


        }
    }
}
