package com.loyo.oa.v2.activity.sale;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CellInfo;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.Utils;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by xeq on 16/5/18.
 */
public class HttpSaleBuild {

    static CellInfo cellInfo;

    public static RestAdapter buildSale() {
        if (cellInfo == null) {
            cellInfo = Utils.getCellInfo();
        }
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", String.format("Bearer %s", MainApp.getToken()));
                request.addHeader("LoyoPlatform", cellInfo.getLoyoPlatform());
                request.addHeader("LoyoAgent", cellInfo.getLoyoAgent());
                request.addHeader("LoyoOSVersion", cellInfo.getLoyoOSVersion());
                request.addHeader("LoyoVersionName", Global.getVersionName());
                request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));
            }
        };
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(Config_project.API_URL_CUSTOMER()) //URL
                .setLogLevel(RestAdapter.LogLevel.FULL) //是否Debug
                .setRequestInterceptor(requestInterceptor).build();
        return adapter;
    }
}
