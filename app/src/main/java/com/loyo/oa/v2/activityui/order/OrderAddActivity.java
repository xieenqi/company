package com.loyo.oa.v2.activityui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.signin.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ContactAddforExtraData;
import com.loyo.oa.v2.customview.OrderAddforExtraData;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.ArrayList;
import java.util.HashMap;
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
    private  ArrayList<ContactLeftExtras> mCusList;
    private ArrayList<SaleIntentionalProduct> intentionProductData = new ArrayList<>();//意向产品的数据

    private EditText et_name;     //订单标题
    private TextView tv_customer; //对应客户
    private TextView tv_stage;    //购买产品
    private EditText et_money;    //成交金额
    private TextView tv_estimate; //添加回款
    private TextView tv_source;   //附件
    private EditText et_ordernum; //订单编号
    private EditText et_remake;   //备注


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("新建订单");
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

        getAddDynamic();
    }


    /**
     * 获取新建订单动态字段
     * */
    public void getAddDynamic(){
        showLoading("",false);
        HashMap<String,Object> map = new HashMap<>();
        map.put("bizType",104);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getAddCustomerJur(map, new RCallback< ArrayList<ContactLeftExtras>>() {
            @Override
            public void success(final ArrayList<ContactLeftExtras> cuslist, final Response response) {
                HttpErrorCheck.checkResponse("新建订单动态字段",response);
                mCusList = cuslist;
                setAddDynamic();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 设置权限
     * 标题、客户、产品、金额都是必填，所以不判断
     * */
    public void setAddDynamic(){
        tv_custom.addView(new OrderAddforExtraData(mContext, mCusList, true, R.color.title_bg1, 0));

        for(ContactLeftExtras customerJur : mCusList){
            switch (customerJur.name){

                case "paymentRecords":
                    if(customerJur.required){
                        tv_estimate.setHint("必填,请输入");
                    }
                    break;

                case "orderNum":
                    if(customerJur.required){
                        et_ordernum.setHint("必填,请输入");
                    }
                    break;

                case "attachment":
                    if(customerJur.required){
                        tv_source.setHint("必填,请输入");
                    }
                    break;

                case "remark":
                    if(customerJur.required){
                        et_remake.setHint("备注(必填)");
                    }
                    break;

            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //提交
            case R.id.iv_submit:
                Toast("提交");
                break;

            //后退
            case R.id.ll_back:
                onBackPressed();
                break;

            //对应客户
            case R.id.ll_customer:
                Bundle b = new Bundle();
                app.startActivityForResult(OrderAddActivity.this, SigninSelectCustomer.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                break;

            //意向产品
            case R.id.ll_stage:
                Bundle product = new Bundle();
                product.putSerializable(ExtraAndResult.EXTRA_DATA, intentionProductData);
                app.startActivityForResult(OrderAddActivity.this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;

            //回款
            case R.id.ll_estimate:
                Toast("回款");
                break;

            //附件
            case R.id.ll_source:
                Intent intent = new Intent(OrderAddActivity.this,OrderAttachmentActivity.class);
                startActivity(intent);
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
        if (null != intentionProductData) {
            for (SaleIntentionalProduct ele : intentionProductData) {
                productName += ele.name + "、";
            }
        } else {
            return "";
        }
        return productName.length() > 0 ? productName.substring(0, productName.length() - 1) : "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != -1){
            return;
        }

        switch (requestCode){

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
                ArrayList<SaleIntentionalProduct> resultData = (ArrayList<SaleIntentionalProduct>)
                        data.getSerializableExtra(ExtraAndResult.RESULT_DATA);
                if (null != resultData) {
                    intentionProductData = resultData;
                    tv_stage.setText(getIntentionProductName());
                }
                break;
        }
    }
}
