package com.loyo.oa.v2.network;

import android.os.Build;

import com.loyo.oa.v2.activityui.other.model.CellInfo;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.network.model.APIException;
import com.loyo.oa.v2.network.model.BaseResponse;
import com.loyo.oa.v2.network.model.CompatBaseResponse;
import com.loyo.oa.v2.tool.GsonUtils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RetrofitAdapterFactory {

    private HashMap<String, SoftReference<RestAdapter>> adapters;
    private static volatile RetrofitAdapterFactory maker;


    private RetrofitAdapterFactory() {
        adapters = new HashMap<>();
    }

    /**
     * 获取创建RestAdapter的工厂
     *
     * @return
     */
    public static synchronized RetrofitAdapterFactory

    getInstance() {
        synchronized (RetrofitAdapterFactory.class) {
            if (null == maker) {
                synchronized (RetrofitAdapterFactory.class) {
                    maker = new RetrofitAdapterFactory();
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
    public RestAdapter build(final String url) {
        RestAdapter adapter = null == adapters.get(url) ? null : adapters.get(url).get();
        if (null == adapter) {

            final CellInfo cellInfo;
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
                    request.addHeader("LoyoVersionCode", String.valueOf(Global.getVersion()));
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

    static final Observable.Transformer transformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return flatResponse((BaseResponse<Object>)response);
                        }
                    });
        }
    };

    static final Observable.Transformer compatCanBeEmptyTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return compatFlatCanBeEmptyResponse(response);
                        }
                    })
                    ;
        }
    };
    static final Observable.Transformer compatTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return compatFlatResponse(response);
                        }
                    })
                    ;
        }
    };

    public static <T> Observable.Transformer<BaseResponse<T>, T> applySchedulers() {
        return (Observable.Transformer<BaseResponse<T>, T>) transformer;
    }

    public static <T> Observable.Transformer<T, T> compatApplyCanBeEmptySchedulers() {
        return (Observable.Transformer<T, T>) compatCanBeEmptyTransformer;
    }

    public static <T> Observable.Transformer<T, T> compatApplySchedulers() {
        return (Observable.Transformer<T, T>) compatTransformer;
    }

    public static <T> Observable<T> flatResponse(final BaseResponse<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response.isSuccess()) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.data);
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException(response.errcode, response.errmsg,
                                response.data));
                    }
                    return;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }

    public static <T> Observable<T> compatFlatResponse(final T response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response != null) {
                    if (!subscriber.isUnsubscribed()) {
                        if (response instanceof CompatBaseResponse
                                && ((CompatBaseResponse) response).errcode != 0) {
                            subscriber.onError(
                                    new APIException(
                                            ((CompatBaseResponse) response).errcode,
                                            ((CompatBaseResponse) response).errmsg, response));
                        }
                        else {
                            subscriber.onNext(response);
                        }
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException(-1000/* TODO: */, "请求出错"/* TODO: */,
                                response));
                    }
                    return;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }

    public static <T> Observable<T> compatFlatCanBeEmptyResponse(final T response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    if (response instanceof CompatBaseResponse
                            && ((CompatBaseResponse) response).errcode != 0) {
                        subscriber.onError(new APIException(((CompatBaseResponse) response).errcode,
                                ((CompatBaseResponse) response).errmsg,
                                response));
                    }
                    else {
                        subscriber.onNext(response);
                    }
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }
}
