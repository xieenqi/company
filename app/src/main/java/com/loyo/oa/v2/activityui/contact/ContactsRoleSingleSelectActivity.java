package com.loyo.oa.v2.activityui.contact;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.api.ContactsService;
import com.loyo.oa.v2.activityui.contact.model.ContactsRoleModel;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseSingleSelectActivity;

import java.util.List;

/**
 * 选择联系人角色
 * Created by jie on 17/2/8.
 */

public class ContactsRoleSingleSelectActivity extends BaseSingleSelectActivity<ContactsRoleModel>{

    @Override
    protected void getData() {
        ContactsService.getContactsRole().subscribe(new DefaultLoyoSubscriber<BaseResponse<List<ContactsRoleModel>>>() {
            @Override
            public void onNext(BaseResponse<List<ContactsRoleModel>> listBaseResponse) {
                success(listBaseResponse.data);
            }
            @Override
            public void onError(Throwable e) {
                fail(e);
            }
        });
    }

    @Override
    protected String getPageTitle() {
        return "联系人角色";
    }
}
