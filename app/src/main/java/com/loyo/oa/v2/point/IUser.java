package com.loyo.oa.v2.point;

import com.loyo.oa.v2.activityui.commonview.bean.NewUser;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.activityui.other.model.User;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;


/**
 * Created by pj on 15/5/21.
 */
public interface IUser {

    @GET("/")
    ArrayList<Department> getOrganization();

    @GET("/user/subordinates")
    ArrayList<User> getSubordinates();

    @PUT("/api/v2/user/{userId}/profile")
    void updateProfile(@Path("userId") String id, @Body HashMap<String, Object> map, Callback<User> callback);

    /**
     * 以id获取人多信息
     * "/user/{id}/profile"  改成"/user/{id}/newprofile"
     *
     * @param id
     * @param callback
     */
    @GET("/user/{id}/newprofile")
    void getUserById(@Path("id") String id, Callback<NewUser> callback);

    /**
     * 获取个人信息 "/user/profile"改为 "/user/newprofile" 权限管理换接口
     *
     * @param cb
     */
    @GET("/user/newprofile")
    void getProfile(retrofit.Callback<NewUser> cb);

    @PUT("/")
    void updatePassword(@Body HashMap<String, Object> map, retrofit.Callback<Object> cb);

    @PUT("/")
    void rushHomeDate(retrofit.Callback<User> cb);

}
