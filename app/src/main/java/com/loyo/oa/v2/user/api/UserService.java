package com.loyo.oa.v2.user.api;

import com.loyo.oa.v2.activityui.commonview.bean.NewUser;
import com.loyo.oa.v2.activityui.customer.model.Department;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.Config_project;

import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;

/**
 * Created by EthanGong on 2016/12/21.
 */

public class UserService {

    public static
    Observable<NewUser> getUserById(String id) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(FinalVariables.GET_PROFILE)
                        .create(IUser.class)
                        .getUserById(id)
                        .compose(RetrofitAdapterFactory.<NewUser>compatApplySchedulers());
    }

    public static
    Observable<NewUser> getProfile() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(FinalVariables.GET_PROFILE)
                        .create(IUser.class)
                        .getProfile()
                        .compose(RetrofitAdapterFactory.<NewUser>compatApplySchedulers());
    }

    public static
    Observable<User> updateProfile(String id, HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(Config_project.SERVER_URL_LOGIN())
                        .create(IUser.class)
                        .updateProfile(id, map)
                        .compose(RetrofitAdapterFactory.<User>compatApplySchedulers());
    }

    public static
    Observable<User> rushHomeDate() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(FinalVariables.RUSH_HOMEDATA)
                        .create(IUser.class)
                        .rushHomeDate()
                        .compose(RetrofitAdapterFactory.<User>compatApplySchedulers());
    }

    public static
    Observable<ArrayList<Department>> asynGetOrganization() {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(FinalVariables.GET_ORGANIZATION)
                        .create(IUser.class)
                        .asynGetOrganization()
                        .compose(RetrofitAdapterFactory.<ArrayList<Department>>compatApplySchedulers());
    }

    public static
    Observable<Object> updatePassword(HashMap<String, Object> map) {
        return
                RetrofitAdapterFactory.getInstance()
                        .build(FinalVariables.GET_ORGANIZATION)
                        .create(IUser.class)
                        .updatePassword(map)
                        .compose(RetrofitAdapterFactory.<Object>compatApplySchedulers());
    }
}
