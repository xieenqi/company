package com.loyo.oa.v2.activityui.customer;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.contacts.api.ContactsService;
import com.loyo.oa.v2.contacts.model.ContactsRoleModel;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseSingleSelectActivity;

import java.util.List;

/**
 * 选择联系人角色
 * Created by jie on 17/2/8.
 */

public class CustomerContactsRoleSelectActivity extends BaseSingleSelectActivity<ContactsRoleModel> {
    private void getData() {
        ContactsService.getContactsRole().subscribe(new DefaultLoyoSubscriber<BaseResponse<List<ContactsRoleModel>>>() {
            @Override
            public void onNext(BaseResponse<List<ContactsRoleModel>> listBaseResponse) {

            }
        });
    }

}
