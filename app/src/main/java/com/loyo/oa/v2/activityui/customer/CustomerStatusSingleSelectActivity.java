package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

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


    private String tid;
    /**
     * 当条目被点击的时候,如果行为不一样，可以直接覆盖本方法
     * @param data
     */
    protected void onItemClicked(CustomerStatusModel.CustomerStatusItemModel data){
        Intent intent=new Intent();
        intent.putExtra("data",data);
        intent.putExtra("tid",tid+"");
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    protected void getData() {
        CustomerService.getCustomerStatus("1").subscribe(new DefaultLoyoSubscriber<List<CustomerStatusModel>>() {
            @Override
            public void onNext(List<CustomerStatusModel> customerStatusModels) {
                if(customerStatusModels.size()>0){
                    tid=customerStatusModels.get(0).id;
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
