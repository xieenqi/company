package com.loyo.oa.v2.activityui.sale.model;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.other.model.SaleStage;
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

public class SaleStageConfig {

    /* 从网络获取 */
    public static void getSaleStage() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSaleStges(new RCallback<ArrayList<SaleStage>>() {
            @Override
            public void success(ArrayList<SaleStage> result, Response response) {
                HttpErrorCheck.checkResponse("销售机会 config销售阶段:", response);
                    String json = MainApp.gson.toJson(result);
                    SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE);
                    SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE, json);
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    /* 读取缓存 */
    public static ArrayList<SaleStage>  getSaleStageCache() {
        ArrayList<SaleStage>  result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE),
                        new TypeToken<ArrayList<SaleStage>>() {
                        }.getType());

        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<SaleStage>  getSaleStage(boolean fetchIfEmpty) {
        ArrayList<SaleStage>  result = null;
        String json = SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE);
        result = MainApp.getMainApp().gson.fromJson(json, new TypeToken<ArrayList<SaleStage> >() {
        }.getType());

        if (fetchIfEmpty && result == null) {
            getSaleStage();
        }

        return result;
    }

    /* 清除缓存 */
    private static void clearSaleStage() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE);
    }
}
