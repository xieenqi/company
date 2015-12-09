package com.loyo.oa.v2.activity.customer;

import android.os.Bundle;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 客户管理，查重
 * Created by yyy on 15/12/9.
 */
public class CustomerRepeat extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_repeat);
        init();
    }

    void init() {
        setTitle("新建查重");
    }
}
