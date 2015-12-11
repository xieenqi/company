package com.loyo.oa.v2.tool;

import android.os.Build;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CellInfo;
import com.loyo.oa.v2.common.Global;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

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
    public synchronized static RestAdapterFactory getInstance() {
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
    public synchronized RestAdapter build(final String url) {

        RestAdapter adapter = null==adapters.get(url)?null:adapters.get(url).get();
        if (null == adapter) {

            //final CellInfo cellInfo = Utils.getCellInfo();
            final CellInfo cellInfo = new CellInfo();

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
                    request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));

                    LogUtil.dll("Authorization:" + String.format("Bearer %s", MainApp.getToken()));
                    LogUtil.dll("LoyoPlatform:" + cellInfo.getLoyoPlatform());
                    LogUtil.dll("LoyoAgent:" +  cellInfo.getLoyoAgent());
                    LogUtil.dll("LoyoOSVersion:" +  cellInfo.getLoyoOSVersion());
                    LogUtil.dll("LoyoVersionName:" + Global.getVersionName());
                    LogUtil.dll("LoyoVersionCode:" + String.valueOf(Global.getVersion()));

                }
            };
            adapter = new RestAdapter.Builder().setEndpoint(url).setLogLevel(RestAdapter.LogLevel.FULL).setRequestInterceptor(requestInterceptor).build();
            adapters.put(url, new SoftReference<>(adapter));
        }
        return adapter;
    }
}
