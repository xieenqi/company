package com.loyo.oa.v2.customermanagement.fragment;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * Created by EthanGong on 2017/2/10.
 */

public class CustomerChildFragment extends BaseFragment {
    int totalCount;
    String title = "";
    Customer customer;

    public String getTitle() {
        return String.format("%s %d", title, totalCount);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
