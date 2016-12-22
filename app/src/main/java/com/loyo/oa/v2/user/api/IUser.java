package com.loyo.oa.v2.user.api;

import com.loyo.oa.v2.activityui.commonview.bean.NewUser;
import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.activityui.other.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by EthanGong on 2016/12/21.
 */

public interface IUser {
    @GET("/")
    ArrayList<Department> getOrganization();

    @GET("/")
    Observable<ArrayList<Department>> asynGetOrganization();

    @PUT("/api/v2/user/{userId}/profile")
    Observable<User> updateProfile(@Path("userId") String id, @Body HashMap<String, Object> map);

    /**
     * 以id获取人多信息
     * "/user/{id}/profile"  改成"/user/{id}/newprofile"
     *
     * @param id
     */
    @GET("/user/{id}/newprofile")
    Observable<NewUser> getUserById(@Path("id") String id);

    /**
     * 获取个人信息 "/user/profile"改为 "/user/newprofile" 权限管理换接口
     */
    @GET("/user/newprofile")
    Observable<NewUser> getProfile();

    @PUT("/")
    Observable<Object> updatePassword(@Body HashMap<String, Object> map);

    @PUT("/")
    Observable<User> rushHomeDate();

}
