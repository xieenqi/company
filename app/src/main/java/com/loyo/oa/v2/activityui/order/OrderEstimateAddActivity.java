package com.loyo.oa.v2.activityui.order;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【新增回款】
 * Created by yyy on 16/8/3.
 */
public class OrderEstimateAddActivity extends BaseActivity{

    private LinearLayout ll_time;        //回款日期
    private LinearLayout ll_priecer;     //收款人
    private LinearLayout ll_kind;        //付款方式
    private LinearLayout ll_attachment;  //附件

    private TextView     tv_time;        //回款日期
    private TextView     tv_priceer;     //收款人
    private TextView     tv_kind;        //付款方式
    private TextView     tv_attachment;  //附件
    private EditText     et_estprice; //回款金额
    private EditText     et_kaiprice; //开票金额



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimateadd);
    }

    public void initUI(){

    }
}
