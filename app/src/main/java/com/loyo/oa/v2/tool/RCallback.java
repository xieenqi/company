package com.loyo.oa.v2.tool;

import com.loyo.oa.v2.common.Global;

import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class RCallback<T> implements retrofit.Callback<T> {
    @Override
    public abstract void success(T t, Response response);

    @Override
    public void failure(RetrofitError error) {
        Global.ProcException(error);
    }
}