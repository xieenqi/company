package com.loyo.oa.v2.contacts.api;

import com.loyo.oa.v2.contacts.model.ContactsRoleModel;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.List;
import java.util.Map;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by jie on 17/2/8.
 */

public interface IContacts {
    /**
     * 新建线索 表单传输
     */
    @POST("/contactrole")
    Observable<BaseResponse<List<ContactsRoleModel>>> getContactsRole();
}
