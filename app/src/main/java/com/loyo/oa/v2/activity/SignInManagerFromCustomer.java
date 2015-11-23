package com.loyo.oa.v2.activity;

import android.os.Bundle;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

public class SignInManagerFromCustomer extends BaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_manager_from_customer);

        if (savedInstanceState == null) {

//            Customer customer = (Customer) getIntent().getSerializableExtra(Customer.class.getName());
//
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new SignInManagerFragment(customer))
//                    .commit();
        }
    }
}
