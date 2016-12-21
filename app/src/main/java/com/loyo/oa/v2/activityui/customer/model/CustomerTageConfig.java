package com.loyo.oa.v2.activityui.customer.model;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomerTageConfig {

    /* 从网络获取 */
    public static void getTage() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                GetTags(new RCallback<ArrayList<Tag>>() {
                    @Override
                    public void success(ArrayList<Tag> result, Response response) {
                        HttpErrorCheck.checkResponse("客户 config标签:", response);
                        String json = MainApp.gson.toJson(result);
                        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE);
                        SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE, json);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<Tag> getTage(boolean fetchIfEmpty) {
        ArrayList<Tag> result = getTageCache();

        if (fetchIfEmpty && result == null) {
            getTage();
        }

        return result;
    }

    /* 读取缓存 */
    public static ArrayList<Tag> getTageCache() {
        ArrayList<Tag> result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE),
                        new TypeToken<ArrayList<Tag>>() {
                        }.getType());

        return result;
    }


    /* 清除缓存 */
    private static void clearTage() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE);
    }
}
