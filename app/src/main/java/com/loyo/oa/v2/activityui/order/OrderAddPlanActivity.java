package com.loyo.oa.v2.activityui.order;

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
import com.loyo.oa.v2.activityui.order.bean.EstimatePlanAdd;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.Calendar;
import java.util.HashMap;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 添加 回款计划
 * Created by xeq on 16/8/4.
 */
public class OrderAddPlanActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title, tv_time, tv_tx, tv_kind;
    private LinearLayout ll_back, ll_time, ll_tx, ll_kind;
    private ImageView iv_submit;
    private EditText et_remake, et_estprice;

    private int estimatedTime;
    private int payeeMethod;
    private int remindType;

    private String orderId;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        initUI();
    }

    public void initUI() {

        mIntent = getIntent();
        if(null != mIntent){
            orderId = mIntent.getStringExtra("orderId");
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
        tv_tx = (TextView) findViewById(R.id.tv_tx);
        tv_kind = (TextView) findViewById(R.id.tv_kind);

        tv_time.setText(DateTool.getNowTime("yyyy.MM.dd"));
        estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_time.getText().toString(), "yyyy.MM.dd"));

    }

    public void commitPlan(){
        HashMap<String,Object> map = new HashMap<>();

        if(TextUtils.isEmpty(et_estprice.getText().toString())){
            Toast("请选择计划回款金额！");
            return;
        }else if(TextUtils.isEmpty(tv_tx.getText().toString())){
            Toast("请选择提醒方式！");
            return;
        }else if(TextUtils.isEmpty(tv_kind.getText().toString())){
            Toast("请选择付款方式！");
            return;
        }
        showLoading("");
        map.put("orderId",orderId);
        map.put("planAt",estimatedTime);
        map.put("planMoney",Integer.parseInt(et_estprice.getText().toString()));
        map.put("payeeMethod",payeeMethod);
        map.put("remindType",remindType);
        map.put("remark",et_remake.getText().toString());


        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IOrder.class)
                .addPlanEstimate(map, new Callback<EstimatePlanAdd>() {
                    @Override
                    public void success(EstimatePlanAdd estimatePlanAdd, Response response) {
                        HttpErrorCheck.checkResponse("创建计划回款", response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
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
