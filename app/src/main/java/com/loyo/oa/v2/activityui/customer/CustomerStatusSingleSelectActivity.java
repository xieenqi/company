package com.loyo.oa.v2.activityui.customer;

import android.os.Bundle;
import com.loyo.oa.v2.activityui.customer.model.CustomerStatusModel;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSingleSelectActivity;

import java.util.List;

/**
 * 选择客户状态
 * Created by jie on 17/2/8.
 */

public class CustomerStatusSingleSelectActivity extends BaseSingleSelectActivity<CustomerStatusModel.CustomerStatusItemModel>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current=getIntent().getStringExtra(EXTRA_CURRENT);
    }

    @Override
    protected void getData() {
        CustomerService.getCustomerStatus("1").subscribe(new DefaultLoyoSubscriber<List<CustomerStatusModel>>() {
            @Override
            public void onNext(List<CustomerStatusModel> customerStatusModels) {
                if(customerStatusModels.size()>0){
                    success(customerStatusModels.get(0).items);
                }
            }
            @Override
            public void onError(Throwable e) {
                fail(e);
            }
        });
    }

    @Override
    protected String getPageTitle() {
        return "客户状态";
    }

    @Override
    protected boolean isDefault(CustomerStatusModel.CustomerStatusItemModel item) {
        return current.equals(item.id);
    }
}
