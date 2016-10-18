package com.loyo.oa.v2.point;

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
     *
     * @param id
     * @param callback
     */
    @GET("/user/{id}/profile")
    void getUserById(@Path("id") String id, Callback<User> callback);

    @GET("/user/profile")
    void getProfile(retrofit.Callback<User> cb);

    @PUT("/")
    void updatePassword(@Body HashMap<String, Object> map, retrofit.Callback<Object> cb);

    @PUT("/")
    void rushHomeDate(retrofit.Callback<User> cb);

}
