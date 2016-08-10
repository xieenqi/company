package com.loyo.oa.v2.activityui.order;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.Calendar;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【新增回款】
 * Created by yyy on 16/8/3.
 */
public class OrderAddEstimateActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_time;        //回款日期
    private LinearLayout ll_priecer;     //收款人
    private LinearLayout ll_kind;        //付款方式
    private LinearLayout ll_attachment;  //附件
    private LinearLayout ll_back;

    private TextView tv_time;        //回款日期
    private TextView tv_priceer;     //收款人
    private TextView tv_kind;        //付款方式
    private TextView tv_attachment;  //附件

    private EditText et_estprice; //回款金额
    private EditText et_kaiprice; //开票金额
    private EditText et_remake;   //备注

    private TextView tv_title;
    private ImageView iv_submit;

    private int fromPage;
    private int estimatedTime = 0;
    private int paymentState;

    private String uuid;
    private String id;
    private String orderId, planId;
    private int attamentSize = 0;
    private Intent mIntent;
    private Bundle mBundle;
    private NewUser newUser;
    private EstimateAdd mEstimateAdd;
    private HashMap<String, Object> map;

    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg){

            switch (msg.what){

                //附件数量刷新
                case ExtraAndResult.MSG_WHAT_VISIBLE:
                    if(attamentSize != 0){
                        tv_attachment.setText("附件("+attamentSize+")");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimateadd);
        initUI();
    }

    public void initUI() {

        mIntent = getIntent();
        if (mIntent != null) {
            orderId = mIntent.getStringExtra("orderId");
            planId = mIntent.getStringExtra("planId");
            fromPage = mIntent.getIntExtra("fromPage", OrderEstimateListActivity.PAGE_ORDER_ADD);
            mEstimateAdd = (EstimateAdd) mIntent.getSerializableExtra(ExtraAndResult.RESULT_DATA);
            if(mIntent.getIntExtra("size",0) != 0){
                attamentSize = mIntent.getIntExtra("size",0);
            }
        }

        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_priecer = (LinearLayout) findViewById(R.id.ll_priecer);
        ll_kind = (LinearLayout) findViewById(R.id.ll_kind);
        ll_attachment = (LinearLayout) findViewById(R.id.ll_attachment);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_priceer = (TextView) findViewById(R.id.tv_priceer);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        tv_attachment = (TextView) findViewById(R.id.tv_attachment);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);

        et_estprice = (EditText) findViewById(R.id.et_estprice);
        et_kaiprice = (EditText) findViewById(R.id.et_kaiprice);
        et_remake = (EditText) findViewById(R.id.et_remake);
        ll_back.setOnClickListener(this);
        iv_submit.setOnClickListener(this);
        ll_time.setOnClickListener(this);
        ll_priecer.setOnClickListener(this);
        ll_kind.setOnClickListener(this);
        ll_attachment.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnTouchListener(Global.GetTouch());

        if(attamentSize != 0){
            tv_attachment.setText("附件("+attamentSize+")");
        }
        tv_time.setText(DateTool.getNowTime("yyyy.MM.dd"));
        estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_time.getText().toString(), "yyyy.MM.dd"));

        if (fromPage == OrderEstimateListActivity.PAGE_EDIT) {
            tv_title.setText("编辑回款记录");
        } else if (fromPage == OrderEstimateListActivity.PAGE_GENERATE) {
            creatEstimate();
        } else {
            tv_title.setText("新增回款记录");
        }
        editEstimate();
    }

    /**
     * 编辑 记录
     */
    private void editEstimate() {
        if (null != mEstimateAdd) {
            newUser = new NewUser();
            newUser.setId(mEstimateAdd.payeeUser.id);
            newUser.setName(mEstimateAdd.payeeUser.name);
            newUser.setAvatar(mEstimateAdd.payeeUser.avatar);

            id = mEstimateAdd.id;
            uuid = mEstimateAdd.attachmentUUId;
            estimatedTime = mEstimateAdd.receivedAt;
            paymentState = mEstimateAdd.payeeMethod;
            tv_time.setText(DateTool.timet(mEstimateAdd.receivedAt + "", "yyyy.MM.dd"));
            et_estprice.setText(mEstimateAdd.receivedMoney + "");
            et_kaiprice.setText(mEstimateAdd.billingMoney + "");
            tv_priceer.setText(mEstimateAdd.payeeUser.name);
            et_remake.setText(mEstimateAdd.remark);
            if(mEstimateAdd.attachmentCount != 0){
                attamentSize = mEstimateAdd.attachmentCount;
                tv_attachment.setText("附件("+attamentSize+")");
            }
            setPayeeMethod(mEstimateAdd.payeeMethod);
        }
    }

    /**
     * 计划生成 记录
     */
    private void creatEstimate() {
        if (null != mEstimateAdd) {
            estimatedTime = mEstimateAdd.receivedAt;
            paymentState = mEstimateAdd.payeeMethod;
            tv_time.setText(DateTool.timet(mEstimateAdd.receivedAt + "", "yyyy.MM.dd"));
            et_estprice.setText(mEstimateAdd.receivedMoney + "");
            et_remake.setText(mEstimateAdd.remark);
            setPayeeMethod(mEstimateAdd.payeeMethod);
        }
    }

    private void setPayeeMethod(int payeeMethod) {
        switch (payeeMethod) {

            case 1:
                tv_kind.setText("现金");
                break;

            case 2:
                tv_kind.setText("支票");
                break;

            case 3:
                tv_kind.setText("银行转账");
                break;

            case 4:
                tv_kind.setText("其它");
                break;
        }
    }

    /**
     * 提交数据
     */
    public void commitData() {
        switch (fromPage) {

            //来自订单新建 新建回款
            case OrderEstimateListActivity.PAGE_ORDER_ADD:
                mIntent.putExtra("data", mEstimateAdd);
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
                break;

            //来自订单详情 新建
            case OrderEstimateListActivity.PAGE_DETAILS_ADD:

                showLoading("");
                map = new HashMap<>();
                map.put("UUID", "");
                map.put("payeeMethod", mEstimateAdd.payeeMethod);
                map.put("orderId", orderId);
                map.put("attachmentsName", "");
                map.put("receivedAt", mEstimateAdd.receivedAt);
                map.put("receivedMoney", mEstimateAdd.receivedMoney);
                map.put("billingMoney", mEstimateAdd.billingMoney);
                map.put("remark", mEstimateAdd.remark);
                map.put("payMethodString", tv_kind.getText().toString());
                map.put("payeeUser", mEstimateAdd.payeeUser);

                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                        .addPayEstimate(map, new Callback<EstimateAdd>() {
                            @Override
                            public void success(EstimateAdd orderAdd, Response response) {
                                HttpErrorCheck.checkResponse("新建回款记录", response);
                                app.finishActivity(OrderAddEstimateActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                HttpErrorCheck.checkError(error);
                            }
                        });

                break;

            //来自编辑订单
            case OrderEstimateListActivity.PAGE_EDIT:

                showLoading("");
                map = new HashMap<>();
                map.put("UUID", mEstimateAdd.attachmentUUId);
                map.put("payeeMethod", mEstimateAdd.payeeMethod);
                map.put("orderId", orderId);
                map.put("attachmentsName", "");
                map.put("receivedAt", mEstimateAdd.receivedAt);
                map.put("receivedMoney", mEstimateAdd.receivedMoney);
                map.put("billingMoney", mEstimateAdd.billingMoney);
                map.put("remark", mEstimateAdd.remark);
                map.put("payMethodString", tv_kind.getText().toString());
                map.put("payeeUser", mEstimateAdd.payeeUser);
                LogUtil.dee("编辑订单:" + MainApp.gson.toJson(map));
                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                        .editPayEstimate(id, map, new Callback<EstimateAdd>() {
                            @Override
                            public void success(EstimateAdd orderAdd, Response response) {
                                HttpErrorCheck.checkResponse("新建回款记录", response);
                                app.finishActivity(OrderAddEstimateActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                HttpErrorCheck.checkError(error);
                            }
                        });
                break;
            //来自计划生成 新建
            case OrderEstimateListActivity.PAGE_GENERATE:

                showLoading("");
                map = new HashMap<>();
                map.put("UUID", "");
                map.put("payeeMethod", mEstimateAdd.payeeMethod);
                map.put("orderId", orderId);
                map.put("attachmentsName", "");
                map.put("receivedAt", mEstimateAdd.receivedAt);
                map.put("receivedMoney", mEstimateAdd.receivedMoney);
                map.put("billingMoney", mEstimateAdd.billingMoney);
                map.put("remark", mEstimateAdd.remark);
                map.put("payMethodString", tv_kind.getText().toString());
                map.put("payeeUser", mEstimateAdd.payeeUser);
                map.put("planId", planId);

                RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                        .addPayEstimate(map, new Callback<EstimateAdd>() {
                            @Override
                            public void success(EstimateAdd orderAdd, Response response) {
                                HttpErrorCheck.checkResponse("新建回款记录", response);
                                app.finishActivity(OrderAddEstimateActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                HttpErrorCheck.checkError(error);
                            }
                        });

                break;

        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //回退
            case R.id.ll_back:
                onBackPressed();
                break;

            //提交
            case R.id.iv_submit:

                if (estimatedTime == 0) {
                    Toast("请选择回款时间!");
                    return;
                } else if (TextUtils.isEmpty(et_estprice.getText().toString())) {
                    Toast("请选择回款金额!");
                    return;
                } else if (TextUtils.isEmpty(tv_priceer.getText().toString())) {
                    Toast("请选择收款人!");
                    return;
                }

                mIntent = new Intent();
                mEstimateAdd = new EstimateAdd();
                mEstimateAdd.receivedAt = estimatedTime;
                mEstimateAdd.receivedMoney = Integer.parseInt(et_estprice.getText().toString().trim());
                //如果开票金额没写，则默认为0
                if (TextUtils.isEmpty(et_kaiprice.getText().toString())) {
                    mEstimateAdd.billingMoney = 0;
                } else {
                    mEstimateAdd.billingMoney = Integer.parseInt(et_kaiprice.getText().toString().trim());
                }
                mEstimateAdd.id = id;
                mEstimateAdd.attachmentCount = attamentSize;
                mEstimateAdd.attachmentUUId = uuid;
                mEstimateAdd.payeeUser.id = newUser.getId();
                mEstimateAdd.payeeUser.name = newUser.getName();
                mEstimateAdd.payeeUser.avatar = newUser.getAvatar();
                mEstimateAdd.payeeMethod = paymentState;
                mEstimateAdd.remark = et_remake.getText().toString();
                commitData();

                break;

            //回款日期
            case R.id.ll_time:
                estimateTime();
                break;

            //收款人
            case R.id.ll_priecer:
                SelectDetUserActivity2.startThisForOnly(OrderAddEstimateActivity.this, null);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;

            //附件
            case R.id.ll_attachment:
                mBundle = new Bundle();
                mBundle.putString("uuid",uuid);
                app.startActivityForResult(this, OrderAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
                break;

            //付款方式
            case R.id.ll_kind:
                paymentSet();
                break;

        }
    }


    public void paymentSet() {
        String[] data = {"现金", "支票", "银行转账", "其它"};
        final PaymentPopView popView = new PaymentPopView(this, data, "付款方式");
        popView.show();
        popView.setCanceledOnTouchOutside(true);
        popView.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                paymentState = index;
                tv_kind.setText(value);
            }
        });
    }

    /**
     * 时间选择
     */
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
                tv_time.setText(year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day));
                estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_time.getText().toString(), "yyyy.MM.dd"));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            //用户单选, 负责人
            case SelectDetUserActivity2.REQUEST_ONLY:
                NewUser u = (NewUser) data.getSerializableExtra("data");
                newUser = u;
                tv_priceer.setText(newUser.getName());
                break;

            //附件回调
            case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                uuid = data.getStringExtra("uuid");
                attamentSize = data.getIntExtra("size",0);
                mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_VISIBLE);
                break;

        }
    }
}
