package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;

import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.tool.BaseSingleSelectActivity;

import java.util.ArrayList;

/**
 * 写跟进的时候，
 * Created by jie on 17/2/10.
 */

public class FollowContactSingleSelectActivity extends BaseSingleSelectActivity<Contact> {
    @Override
    protected void getData() {
        Intent intent=getIntent();
        if(null!=intent){
            listData = (ArrayList<Contact>) intent.getSerializableExtra(EXTRA_DATA);
        }
        success(listData);
    }

    @Override
    protected String getPageTitle() {
        return "选择客户联系人";
    }

    @Override
    protected boolean isDefault(Contact item) {
        return current.equals(item.getId());
    }
}
