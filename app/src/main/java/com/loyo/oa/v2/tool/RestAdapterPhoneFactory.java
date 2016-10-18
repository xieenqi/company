package com.loyo.oa.v2.tool;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import com.loyo.oa.v2.activityui.other.model.CellInfo;

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
public class RestAdapterPhoneFactory {

    private HashMap<String, SoftReference<RestAdapter>> adapters;
    private static volatile RestAdapterPhoneFactory maker;
    private Context mContext;

    private RestAdapterPhoneFactory(Context cts) {
        adapters = new HashMap<>();
        mContext = cts;
    }

    /**
     * 获取创建RestAdapter的工厂
     *
     * @return
     */
    public static synchronized RestAdapterPhoneFactory

    getInstance(Context cts) {
        synchronized (RestAdapterPhoneFactory.class) {
            if (null == maker) {
                synchronized (RestAdapterPhoneFactory.class) {
                    maker = new RestAdapterPhoneFactory(cts);
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

                    String az = "e1d5b6a43b0bd8c47d13ec15af6671c4:" +  SharedUtil.get(mContext,"time");
                    String zation = new String(Base64.encode(az.getBytes(),Base64.DEFAULT));

                    request.addHeader("Authorization",zation);
/*                  request.addHeader("Accept","application/json");
                    request.addHeader("Content-Type","application/json;charset=utf-8");
                    request.addHeader("Content-Length","256");*/
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
