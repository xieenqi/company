package com.loyo.oa.v2.activityui.order;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.AttenDancePopView;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.DateTool;

import java.util.Calendar;

/**
 * 【新增回款】
 * Created by yyy on 16/8/3.
 */
public class OrderEstimateAddActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout ll_time;        //回款日期
    private LinearLayout ll_priecer;     //收款人
    private LinearLayout ll_kind;        //付款方式
    private LinearLayout ll_attachment;  //附件
    private LinearLayout ll_back;

    private TextView     tv_time;        //回款日期
    private TextView     tv_priceer;     //收款人
    private TextView     tv_kind;        //付款方式
    private TextView     tv_attachment;  //附件

    private EditText     et_estprice; //回款金额
    private EditText     et_kaiprice; //开票金额

    private TextView  tv_title;
    private ImageView iv_submit;

    private int estimatedTime = 0;
    private NewUser newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimateadd);
        initUI();
    }

    public void initUI(){

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


        tv_title.setText("新增回款");
        ll_back.setOnClickListener(this);
        iv_submit.setOnClickListener(this);
        ll_time.setOnClickListener(this);
        ll_priecer.setOnClickListener(this);
        ll_kind.setOnClickListener(this);
        ll_attachment.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnTouchListener(Global.GetTouch());

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            //回退
            case R.id.ll_back:
                onBackPressed();
                break;

            //提交
            case R.id.iv_submit:
                onBackPressed();
                break;

            //回款日期
            case R.id.ll_time:
                estimateTime();
                break;

            //收款人
            case R.id.ll_priecer:
                SelectDetUserActivity2.startThisForOnly(OrderEstimateAddActivity.this, null);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;

            //附件
            case R.id.ll_attachment:
                Intent intent = new Intent(OrderEstimateAddActivity.this,OrderAttachmentActivity.class);
                startActivity(intent);
                break;

            //付款方式
            case R.id.ll_kind:
                paymentSet();
                break;

        }
    }


    public void paymentSet(){
        final PaymentPopView popView = new PaymentPopView(this);
        popView.show();
        popView.setCanceledOnTouchOutside(true);

        //现金
        popView.onClickCase1(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_kind.setText("现金");
                popView.dismiss();
            }
        });
        //支票
        popView.onClickCase2(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_kind.setText("支票");
                popView.dismiss();
            }
        });
        //银行转账
        popView.onClickCase3(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_kind.setText("银行转账");
                popView.dismiss();
            }
        });
        //其他
        popView.onClickCase4(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_kind.setText("其他");
                popView.dismiss();
            }
        });

    }

    /**
     * 时间选择
     * */
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

        switch (requestCode){

            //用户单选, 负责人
            case SelectDetUserActivity2.REQUEST_ONLY:
                NewUser u = (NewUser) data.getSerializableExtra("data");
                newUser = u;
                tv_priceer.setText(newUser.getName());
                break;

        }
    }
}
