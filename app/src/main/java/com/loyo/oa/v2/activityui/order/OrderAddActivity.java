package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.activityui.order.event.OrderAddWorkSheetFinish;
import com.loyo.oa.v2.activityui.sale.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomerSearch;
import com.loyo.oa.v2.activityui.worksheet.OrderWorksheetListActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.OrderAddforExtraData;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
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
    private LinearLayout ll_addorder; //工单
    private LinearLayout ll_source;   //附件
    private LinearLayout tv_custom;   //附件

    private String customerName, customerId;
    private ArrayList<ContactLeftExtras> fieldData;
    private ArrayList<ContactLeftExtras> mCusList;
    private ArrayList<SaleIntentionalProduct> productData;//意向产品的数据
    private ArrayList<EstimateAdd> estimateData;          //回款记录数据
    private ArrayList<OrderWorksheetListModel> reWorkSheet = new ArrayList<>();

    private EditText et_name;     //订单标题
    private TextView tv_customer; //对应客户
    private TextView tv_stage;    //购买产品
    private EditText et_money;    //成交金额
    private TextView tv_estimate; //添加回款
    private TextView tv_source;   //附件
    private TextView tv_addorder; //工单
    private EditText et_ordernum; //订单编号
    private EditText et_remake;   //备注

    private Intent mIntent;
    private Bundle mBundle;
    private int fromPage;
    private int attamentSize = 0;
    private String uuid;
    public  static String orderTitle;
    private OrderDetail mOrderDetail;
    private OrderAddforExtraData orderAddforExtra;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case ExtraAndResult.MSG_WHAT_VISIBLE:
                     if (attamentSize != 0) {

                    tv_source.setText("附件（" + attamentSize + "）");
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
        tv_addorder = (TextView) findViewById(R.id.tv_addorder);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_stage = (LinearLayout) findViewById(R.id.ll_stage);
        ll_estimate = (LinearLayout) findViewById(R.id.ll_estimate);
        ll_source = (LinearLayout) findViewById(R.id.ll_source);
        tv_custom = (LinearLayout) findViewById(R.id.tv_custom);
        ll_addorder = (LinearLayout) findViewById(R.id.ll_addorder);

        et_name = (EditText) findViewById(R.id.et_name);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_stage = (TextView) findViewById(R.id.tv_stage);
        et_money = (EditText) findViewById(R.id.et_money);
        et_money.addTextChangedListener(OrderCommon.getTextWatcher());
        et_money.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
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
        ll_addorder.setOnClickListener(this);

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

        uuid = mOrderDetail.attachmentUUId;
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
        uuid = mOrderDetail.attachmentUUId;
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
        CustomerService.getAddCustomerJur(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>() {
                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        mCusList = contactLeftExtrasArrayList;
                        bindExtraView(contactLeftExtrasArrayList);
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
        } else if (TextUtils.isEmpty(customerId)) {
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
            } else if (!extra.isSystem && !TextUtils.isEmpty(extra.val)) {
                fieldData.add(extra);
            }
        }

        showStatusLoading(false);
        HashMap<String, Object> map = new HashMap<>();
        if (fromPage == OrderDetailActivity.ORDER_EDIT) {
            map.put("id", mOrderDetail.id);
        }
        map.put("attachmentCount", attamentSize);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        map.put("title", et_name.getText().toString());
        if (null == uuid || TextUtils.isEmpty(uuid)) {
            map.put("attachmentUUId", StringUtil.getUUID());
        } else {
            map.put("attachmentUUId", uuid);
        }
        map.put("dealMoney", Float.parseFloat(et_money.getText().toString()));
        map.put("orderNum", et_ordernum.getText().toString());
        map.put("remark", et_remake.getText().toString());
        map.put("proInfo", productData);
        map.put("paymentRecords", estimateData);
        map.put("extensionDatas", fieldData);
        map.put("reWorkSheet", reWorkSheet);
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
                        HttpErrorCheck.checkCommitSus("编辑订单",response);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cancelStatusLoading();
                                app.finishActivity(OrderAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                            }
                        },1000);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkCommitEro(error);
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
                        HttpErrorCheck.checkCommitSus("创建订单",response);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cancelStatusLoading();
                                app.finishActivity(OrderAddActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
                            }
                        },1000);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkCommitEro(error);
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //提交
            case R.id.iv_submit:
                if(Utils.isNetworkAvailable(mContext)){
                    commitOrder();
                }else{
                    Toast("请检查您的网路连接");
                }
                break;

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //对应客户
            case R.id.ll_customer:
                app.startActivityForResult(OrderAddActivity.this, SigninSelectCustomerSearch.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, null);
                break;

            //购买产品
            case R.id.ll_stage:
                mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, productData);
                mBundle.putBoolean("boolean", true);
                mBundle.putBoolean(IntentionProductActivity.KEY_CAN_EDIT, true);
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
                mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, true);
                mBundle.putInt("fromPage", OrderEstimateListActivity.ORDER_ADD);
                app.startActivityForResult(this, OrderEstimateListActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
                break;

            //工单
            case R.id.ll_addorder:
                Intent mIntent = new Intent(OrderAddActivity.this, OrderWorksheetListActivity.class);
                if(!TextUtils.isEmpty(et_name.getText().toString())){
                    orderTitle = et_name.getText().toString();
                }else{
                    orderTitle = "";
                }
                mIntent.putExtra(ExtraAndResult.EXTRA_NAME,reWorkSheet);
                startActivity(mIntent);
                break;

            //附件
            case R.id.ll_source:
                mBundle = new Bundle();
                mBundle.putInt("bizType", 25);
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

    /**
     * 添加工单数据回调
     * */
    @Subscribe
    public void onOrderAddWorkSheetFinish(OrderAddWorkSheetFinish event){
        reWorkSheet.clear();
        reWorkSheet.addAll((Collection<? extends OrderWorksheetListModel>) event.bundle.getSerializable(ExtraAndResult.EXTRA_ID));
        StringBuffer sBuffer = new StringBuffer();
        for(OrderWorksheetListModel orderWorksheetListModel:reWorkSheet){
            if(reWorkSheet.size() > 1){
                sBuffer.append(orderWorksheetListModel.title+",");
            }else{
                sBuffer.append(orderWorksheetListModel.title);
            }
        }
        tv_addorder.setText(sBuffer.toString());
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
                if (TextUtils.isEmpty(et_money.getText().toString())) {//成交金额  返显产品的销售总价
                    String priceInfo = data.getStringExtra("salePrice");
                    String price = priceInfo.substring(1, priceInfo.length());
                    et_money.setText("0".equals(price) ? "" : price);
                }
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
