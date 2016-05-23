package com.loyo.oa.v2.activity.sale;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleFild;
import com.loyo.oa.v2.activity.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activity.sale.bean.SaleOpportunityAdd;
import com.loyo.oa.v2.activity.sale.bean.SaleStage;
import com.loyo.oa.v2.activity.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【创建销售机会】
 * Created by xeq on 16/5/17.
 */
public class ActivityAddMySale extends BaseActivity {
    private TextView tv_title, tv_customer, tv_stage, tv_type, tv_source, tv_product, tv_estimate;
    private ImageView iv_submit;
    private LinearLayout ll_back, ll_customer, ll_stage, ll_estimate, ll_poduct, ll_type, ll_source;
    private EditText et_name, et_money, et_remake;
    private String customerName, customerId, stageId;
    private ArrayList<SaleIntentionalProduct> intentionProductData = new ArrayList<>();//意向产品的数据
    private int estimatedTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_sale);
        init();
        getDynamicInfo();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("创建销售机会");
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        et_name = (EditText) findViewById(R.id.et_name);
        et_money = (EditText) findViewById(R.id.et_money);
        et_remake = (EditText) findViewById(R.id.et_remake);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(click);
        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_customer.setOnTouchListener(Global.GetTouch());
        ll_customer.setOnClickListener(click);
        ll_stage = (LinearLayout) findViewById(R.id.ll_stage);
        ll_stage.setOnTouchListener(Global.GetTouch());
        ll_stage.setOnClickListener(click);
        ll_estimate = (LinearLayout) findViewById(R.id.ll_estimate);
        ll_estimate.setOnTouchListener(Global.GetTouch());
        ll_estimate.setOnClickListener(click);
        ll_poduct = (LinearLayout) findViewById(R.id.ll_poduct);
        ll_poduct.setOnTouchListener(Global.GetTouch());
        ll_poduct.setOnClickListener(click);
        ll_type = (LinearLayout) findViewById(R.id.ll_type);
        ll_type.setOnTouchListener(Global.GetTouch());
        ll_type.setOnClickListener(click);
        ll_source = (LinearLayout) findViewById(R.id.ll_source);
        ll_source.setOnTouchListener(Global.GetTouch());
        ll_source.setOnClickListener(click);
        iv_submit.setOnClickListener(click);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_stage = (TextView) findViewById(R.id.tv_stage);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_source = (TextView) findViewById(R.id.tv_source);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_estimate = (TextView) findViewById(R.id.tv_estimate);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    finish();
                    break;
                case R.id.iv_submit:
                    addSaleOpportunitty();
                    break;
                case R.id.ll_customer://选择客户
                    Bundle b = new Bundle();
                    app.startActivityForResult(ActivityAddMySale.this, SigninSelectCustomer.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                    break;
                case R.id.ll_stage://选择销售阶段
                    Bundle stage = new Bundle();
                    stage.putInt(ExtraAndResult.EXTRA_TYPE, ActivitySaleStage.SALE_STAGE);
                    stage.putString(ExtraAndResult.EXTRA_NAME, "销售阶段");
                    app.startActivityForResult(ActivityAddMySale.this, ActivitySaleStage.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, stage);
                    break;
                case R.id.ll_estimate://选择预估成交时间
                    estimateTime();
                    break;
                case R.id.ll_poduct://选择意向产品
                    Bundle product = new Bundle();
                    product.putSerializable(ExtraAndResult.EXTRA_DATA, intentionProductData);
                    app.startActivityForResult(ActivityAddMySale.this, ActivityIntentionProduct.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                    break;
                case R.id.ll_type://选择机会类型
                    Bundle type = new Bundle();
                    type.putInt(ExtraAndResult.EXTRA_TYPE, ActivitySaleStage.SALE_TYPE);
                    type.putString(ExtraAndResult.EXTRA_NAME, "机会类型");
                    app.startActivityForResult(ActivityAddMySale.this, ActivitySaleStage.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_TYPE, type);
                    break;
                case R.id.ll_source://选择机会来源
                    Bundle source = new Bundle();
                    source.putInt(ExtraAndResult.EXTRA_TYPE, ActivitySaleStage.SALE_SOURCE);
                    source.putString(ExtraAndResult.EXTRA_NAME, "机会来源");
                    app.startActivityForResult(ActivityAddMySale.this, ActivitySaleStage.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, source);
                    break;
            }
        }
    };

    /**
     * 获取动态字段
     */
    private void getDynamicInfo() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).getSaleFild(new Callback<ArrayList<SaleFild>>() {
            @Override
            public void success(ArrayList<SaleFild> bulletinPaginationX, Response response) {
                HttpErrorCheck.checkResponse("销售机会动态字段：", response);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    public void estimateTime() {
        Calendar cal = Calendar.getInstance();
        final DatePickerDialog mDialog = new DatePickerDialog(this, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();

//                age = Utils.getAge(year + "");
//                if (age > 0) {
//                    birthStr = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
//                    mHandler.sendEmptyMessage(0x01);
//                } else {
//                    Toast("出生日期不能是未来时间，请重新设置");
//                }
                tv_estimate.setText(year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day));
                estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_estimate.getText().toString(), "yyyy.MM.dd"));
            }
        });

        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        mDialog.show();
    }

    /**
     * 创建销售机会 到服务器
     */
    private void addSaleOpportunitty() {
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            Toast("请填写机会名称");
            return;
        } else if (TextUtils.isEmpty(customerId)) {
            Toast("请选择客户");
            return;
        } else if (TextUtils.isEmpty(stageId)) {
            Toast("请选择销售阶段");
            return;
        } else if (-1 == estimatedTime) {
            Toast("请选择预估成交时间");
            return;
        } else if (!(intentionProductData.size() > 0)) {
            Toast("请添加意向产品");
            return;
        }
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerName", customerName);
        map.put("customerId", customerId);
        map.put("name", et_name.getText().toString());
        map.put("stageId", stageId);
        map.put("estimatedAmount", Float.valueOf(et_money.getText().toString()));
        map.put("estimatedTime", estimatedTime);
        map.put("proInfos", intentionProductData);
        map.put("changeSource", tv_source.getText().toString());
        map.put("changeType", tv_type.getText().toString());
        map.put("memo", et_remake.getText().toString());
        map.put("extensionDatas", new ArrayList<>());
        LogUtil.d("创建销售机会传递--》", app.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(ISale.class).addSaleOpportunity(map, new Callback<SaleOpportunityAdd>() {
            @Override
            public void success(SaleOpportunityAdd saleOpportunityAdd, Response response) {
                HttpErrorCheck.checkResponse("创建销售机会", response);
                Toast("创建成功");
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                    Customer customer = (Customer) data.getSerializableExtra("data");
                    if (null != customer) {
                        customerId = customer.getId();
                        customerName = customer.name;
                    }
                    tv_customer.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                    break;
                case ExtraAndResult.REQUEST_CODE_STAGE:
                    SaleStage stage = (SaleStage) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                    if (null != stage) {
                        stageId = stage.id;
                        tv_stage.setText(stage.name);
                    }
                    break;
                case ExtraAndResult.REQUEST_CODE_TYPE:
                    String saletype = data.getStringExtra(ExtraAndResult.EXTRA_DATA);
                    tv_type.setText(saletype);
                    break;
                case ExtraAndResult.REQUEST_CODE_SOURCE:
                    String salesource = data.getStringExtra(ExtraAndResult.EXTRA_DATA);
                    tv_source.setText(salesource);
                    break;
                case ExtraAndResult.REQUEST_CODE_PRODUCT:
                    ArrayList<SaleIntentionalProduct> resultData = (ArrayList<SaleIntentionalProduct>) data.getSerializableExtra(ExtraAndResult.RESULT_DATA);
                    if (null != resultData) {
                        intentionProductData = resultData;
                        String productName = "";
                        for (SaleIntentionalProduct ele : intentionProductData) {
                            productName += ele.name + "、";
                        }
                        tv_product.setText(productName);
                    }
                    break;
            }
        }
    }
}
