package com.loyo.oa.v2.activityui.sale.model;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.SharedUtil;
import java.util.ArrayList;

public class SaleStageConfig {

    /* 从网络获取 */
    public static void getSaleStage() {
        CustomerService.getSaleStges()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<SaleStage>>(LoyoErrorChecker.SILENCE) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(ArrayList<SaleStage> saleStage) {
                        String json = MainApp.gson.toJson(saleStage);
                        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE);
                        SharedUtil.put(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE, json);
                    }
                });
    }

    /* 读取缓存 */
    public static ArrayList<SaleStage> getSaleStageCache() {
        ArrayList<SaleStage> result =
                MainApp.getMainApp().gson.fromJson(
                        SharedUtil.get(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE),
                        new TypeToken<ArrayList<SaleStage>>() {
                        }.getType());
        if (result == null) {
            Global.Toast("销售阶段数据在准备中,请退出重试");
            return new ArrayList<SaleStage>();
        }
        return result;
    }

    // TODO:  增加网络获取的回调
    /* 读取缓存，如果没有缓冲，从网络获取 */
    public static ArrayList<SaleStage> getSaleStage(boolean fetchIfEmpty) {
        ArrayList<SaleStage> result = getSaleStageCache();

        if (fetchIfEmpty && (result == null || result.size()==0)) {
            getSaleStage();
        }

        return result;
    }

    /* 清除缓存 */
    private static void clearSaleStage() {
        SharedUtil.remove(MainApp.getMainApp(), ExtraAndResult.SALE_STAGE);
    }
}
