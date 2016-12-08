package com.loyo.oa.v2.tool;

import android.os.Build;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.common.Global;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * com.loyo.oa.v2.tool
 * 描述 :创建RestAdapter的工厂类
 * 作者 : ykb
 * 时间 : 15/10/6.
 */
public class RestAdapterFactory {

    private HashMap<String, SoftReference<RestAdapter>> adapters;
    private static volatile RestAdapterFactory maker;


    private RestAdapterFactory() {
        adapters = new HashMap<>();
    }

    /**
     * 获取创建RestAdapter的工厂
     *
     * @return
     */
    public static synchronized RestAdapterFactory

    getInstance() {
        synchronized (RestAdapterFactory.class) {
            if (null == maker) {
                synchronized (RestAdapterFactory.class) {
                    maker = new RestAdapterFactory();
                }
            }
        }
        return maker;
    }


    /**
     * 创建RestAdapter
     *
     * @param url
     * @return
     */
    CellInfo cellInfo;

    public RestAdapter build(final String url) {

        RestAdapter adapter = null == adapters.get(url) ? null : adapters.get(url).get();
        if (null == adapter) {

            cellInfo = new CellInfo();

            cellInfo.setLoyoAgent(Build.BRAND + " " + Build.MODEL);

            cellInfo.setLoyoOSVersion(cellInfo.getLoyoPlatform() + Build.VERSION.RELEASE);

            cellInfo.setLoyoHVersion(cellInfo.getLoyoPlatform() + Build.HARDWARE);


            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {

                    request.addHeader("Authorization", String.format("Bearer %s", MainApp.getToken()));
                    request.addHeader("LoyoPlatform", cellInfo.getLoyoPlatform());
                    request.addHeader("LoyoAgent", cellInfo.getLoyoAgent());
                    request.addHeader("LoyoOSVersion", cellInfo.getLoyoOSVersion());
                    request.addHeader("LoyoVersionName", Global.getVersionName());
                    request.addHeader("LoyoVersionCode", "2016120501");
                    //request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));

                }
            };
            adapter = new RestAdapter.Builder()
                    .setEndpoint(url)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(requestInterceptor)
                    .setConverter(new GsonConverter(GsonUtils.newInstance()))
                    .build();
            adapters.put(url, new SoftReference<>(adapter));
        }
        return adapter;
    }
}
