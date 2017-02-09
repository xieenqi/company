package com.loyo.oa.v2.activityui.contact.api;

import com.loyo.oa.v2.activityui.contact.model.ContactsRoleModel;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.tool.Config_project;

import java.util.List;

import rx.Observable;

/**
 * 联系人相关网络服务
 * Created by jie on 17/2/8.
 */

public class ContactsService {
    // 获取 联系人角色
    public static Observable<BaseResponse<List<ContactsRoleModel>>> getContactsRole() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(/*TODO:*/Config_project.API_URL_CUSTOMER())
                        .create(IContacts.class)
                        .getContactsRole()
                        .compose(RetrofitAdapterFactory.<BaseResponse<List<ContactsRoleModel>>>compatApplySchedulers());
    }
}
