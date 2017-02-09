package com.loyo.oa.v2.activityui.contact.api;

import com.loyo.oa.v2.activityui.contact.model.ContactsRoleModel;
import com.loyo.oa.v2.network.model.BaseResponse;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by jie on 17/2/8.
 */

public interface IContacts {
    /**
     * 新建线索 表单传输
     */
    @GET("/contactrole")
    Observable<BaseResponse<List<ContactsRoleModel>>> getContactsRole();
}
