package com.loyo.oa.v2.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.loyo.oa.v2.R;

/**
 * 【付款方式】
 * Created by yyy on 16/08/03.
 */
public class PaymentPopView extends Dialog{


    public PaymentPopView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.paymentpopview);

    }

    public void onClickCase1(View.OnClickListener cusOnclick){
        findViewById(R.id.payment_case1).setOnClickListener(cusOnclick);
    }

    public void onClickCase2(View.OnClickListener cusOnclick){
        findViewById(R.id.payment_case2).setOnClickListener(cusOnclick);
    }

    public void onClickCase3(View.OnClickListener cusOnclick){
        findViewById(R.id.payment_case3).setOnClickListener(cusOnclick);
    }

    public void onClickCase4(View.OnClickListener cusOnclick){
        findViewById(R.id.payment_case4).setOnClickListener(cusOnclick);
    }
}