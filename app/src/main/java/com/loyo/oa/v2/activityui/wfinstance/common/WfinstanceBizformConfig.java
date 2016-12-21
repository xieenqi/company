package com.loyo.oa.v2.activityui.wfinstance.common;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.activityui.wfinstance.bean.BizForm;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.point.IWfInstance;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SharedUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class WfinstanceBizformConfig {

    /* 从网络获取 */
    public static void getBizform() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", 1);
        params.put("pageSize", 2000);
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWfInstance.class).getWfBizForms(params, new RCallback<PaginationX<BizForm>>() {
            @Override
            public void success(PaginationX<BizForm> result, Response response) {
                HttpErrorCheck.checkResponse("审批 config 自定义字段", response);
                String json = MainApp.gson.toJson(result);
                SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM);
                SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.WFINSTANCE_BIZFORM, json);
            }
        });
    }

    /* 读取缓存 */
    public static ArrayList<Tag> getBizformCache() {
        ArrayList<Tag> result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE),
                        new TypeToken<ArrayList<Tag>>() {
                        }.getType());

        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<Tag> getBizform(boolean fetchIfEmpty) {
        ArrayList<Tag> result = getBizformCache();

        if (fetchIfEmpty && result == null) {
            getBizform();
        }

        return result;
    }

    /* 清除缓存 */
    private static void clearBizform() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.CUSTOMER_TAGE);
    }
}
