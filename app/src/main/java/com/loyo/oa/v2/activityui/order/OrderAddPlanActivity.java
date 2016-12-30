package com.loyo.oa.v2.activityui.order;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.activityui.order.bean.PlanEstimateList;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * 【新增\编辑】计划回款
 * Created by xeq on 16/8/4.
 */
public class OrderAddPlanActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title, tv_time, tv_tx, tv_kind;
    private LinearLayout ll_back, ll_time, ll_tx, ll_kind;
    private ImageView iv_submit;
    private EditText et_remake, et_estprice;

    private long estimatedTime;
    private int payeeMethod;
    private int remindType = 5;
    private int formPage;

    private String orderId;
    private Intent mIntent;

    private PlanEstimateList planEstimateList;

    /**
     * 计划回新增
     */
    public static final int PLAN_ADD = 0x01;

    /**
     * 计划编辑
     */
    public static final int PLAN_EDIT = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initUI();
    }

    public void initUI() {

        mIntent = getIntent();
        if (null != mIntent) {
            formPage = mIntent.getIntExtra("fromPage", PLAN_ADD);
            orderId = mIntent.getStringExtra("orderId");
            planEstimateList = (PlanEstimateList) mIntent.getSerializableExtra("data");
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("新增回款计划");
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(this);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        iv_submit.setOnClickListener(this);
        iv_submit.setOnTouchListener(Global.GetTouch());
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_time.setOnClickListener(this);
        ll_time.setOnTouchListener(Global.GetTouch());
        ll_tx = (LinearLayout) findViewById(R.id.ll_tx);
        ll_tx.setOnClickListener(this);
        ll_tx.setOnTouchListener(Global.GetTouch());
        ll_kind = (LinearLayout) findViewById(R.id.ll_kind);
        ll_kind.setOnClickListener(this);
        ll_kind.setOnTouchListener(Global.GetTouch());
        et_remake = (EditText) findViewById(R.id.et_remake);
        tv_time = (TextView) findViewById(R.id.tv_time);
        et_estprice = (EditText) findViewById(R.id.et_estprice);
        et_estprice.addTextChangedListener(OrderCommon.getTextWatcher());
        et_estprice.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        tv_tx = (TextView) findViewById(R.id.tv_tx);
        tv_kind = (TextView) findViewById(R.id.tv_kind);

        //是否编辑
        if (null != planEstimateList) {
            editData();
        } else {
//            tv_time.setText(DateTool.getNowTime("yyyy.MM.dd"));
//            estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_time.getText().toString(), "yyyy.MM.dd"));
            estimatedTime= com.loyo.oa.common.utils.DateTool.getStamp(false);
            tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateFriendly(estimatedTime));
        }
    }

    /**
     * 编辑参数设置
     */
    public void editData() {

        estimatedTime = planEstimateList.planAt;
//        tv_time.setText(app.df4.format(new Date(Long.valueOf(planEstimateList.planAt + "") * 1000)));
        tv_time.setText(DateTool.getDateFriendly(planEstimateList.planAt));
        et_estprice.setText(planEstimateList.planMoney + "");
        payeeMethod = planEstimateList.payeeMethod;
        remindType = planEstimateList.remindType;
        et_remake.setText(planEstimateList.remark);

        switch (planEstimateList.payeeMethod) {

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
                tv_kind.setText("其他");
                break;
        }

        switch (planEstimateList.remindType) {

            case 1:
                tv_tx.setText("计划前1天提醒");
                break;

            case 2:
                tv_tx.setText("计划前2天提醒");
                break;

            case 3:
                tv_tx.setText("计划前3天提醒");
                break;

            case 4:
                tv_tx.setText("计划前1周提醒");
                break;

            case 5:
                tv_tx.setText("不提醒");
                break;
        }

    }


    /**
     * 提交新建、编辑计划回款
     */
    public void commitPlan() {
        HashMap<String, Object> map = new HashMap<>();

        if (TextUtils.isEmpty(et_estprice.getText().toString())) {
            Toast("请选择计划回款金额！");
            return;
        }
        showCommitLoading();
        map.put("orderId", orderId);
        map.put("planAt", estimatedTime);

        map.put("planMoney", Float.parseFloat(et_estprice.getText().toString()));
        map.put("payeeMethod", payeeMethod);
        map.put("remindType", remindType);
        map.put("remark", et_remake.getText().toString());

        LogUtil.dee("创建计划:" + MainApp.gson.toJson(map));
        if (null == planEstimateList) {   //新建
            OrderService.addPlanEstimate(map)
                    .subscribe(new DefaultLoyoSubscriber<EstimatePlanAdd>(hud) {
                        @Override
                        public void onNext(EstimatePlanAdd add) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    app.finishActivity(OrderAddPlanActivity.this,
                                            MainApp.ENTER_TYPE_LEFT,
                                            RESULT_OK,
                                            new Intent());
                                }
                            },2000);
                        }
                    });

        } else {    //编辑
            map.put("id", planEstimateList.id);
            OrderService.editPlanEsstimate(planEstimateList.id, map)
                    .subscribe(new DefaultLoyoSubscriber<EstimatePlanAdd>(hud) {
                        @Override
                        public void onNext(EstimatePlanAdd add) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    app.finishActivity(OrderAddPlanActivity.this,
                                            MainApp.ENTER_TYPE_LEFT,
                                            RESULT_OK,
                                            new Intent());
                                }
                            },2000);
                        }
                    });
        }
    }


    /**
     * 时间选择
     */
    public void estimateTime() {
        Calendar cal = Calendar.getInstance();
        Locale.setDefault(Locale.CHINA);//设置语言
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
//                tv_time.setText(year + "." + String.format("%02d", (month + 1)) + "." + String.format("%02d", day));
//                estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_time.getText().toString(), "yyyy.MM.dd"));
                estimatedTime= com.loyo.oa.common.utils.DateTool.getStamp(year,month,day);
                tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateFriendly(estimatedTime));


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
    public void onClick(View v) {
        switch (v.getId()) {

            //返回
            case R.id.ll_back:
                onBackPressed();
                break;
            //提交
            case R.id.iv_submit:
                commitPlan();
                break;
            //计划回款日期
            case R.id.ll_time:
                estimateTime();
                break;
            //提醒
            case R.id.ll_tx:
                String[] dataTx = {"计划前1天提醒", "计划前2天提醒", "计划前3天提醒", "计划前1周提醒", "不提醒"};
                final PaymentPopView popViewTx = new PaymentPopView(this, dataTx, "设置提醒");
                popViewTx.show();
                popViewTx.setCanceledOnTouchOutside(true);
                popViewTx.setCallback(new PaymentPopView.VaiueCallback() {
                    @Override
                    public void setValue(String value, int index) {
                        remindType = index;
                        tv_tx.setText(value);
                    }
                });
                break;
            //付款方式
            case R.id.ll_kind:
                String[] dataKind = {"现金", "支票", "银行转账", "其它"};
                final PaymentPopView popViewKind = new PaymentPopView(this, dataKind, "付款方式");
                popViewKind.show();
                popViewKind.setCanceledOnTouchOutside(true);
                popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
                    @Override
                    public void setValue(String value, int index) {
                        payeeMethod = index;
                        tv_kind.setText(value);
                    }
                });
                break;
        }
    }
}
