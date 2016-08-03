package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

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

    private TextView tv_title;
    private ImageView iv_submit;


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

            //提价
            case R.id.iv_submit:
                onBackPressed();
                break;

        }

    }
}
