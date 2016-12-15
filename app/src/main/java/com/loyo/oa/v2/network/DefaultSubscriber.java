package com.loyo.oa.v2.network;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by EthanGong on 2016/12/15.
 */

public class DefaultSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {
        Log.d("", "");
    }

    @Override
    public void onError(Throwable e) {
        Log.d("", "");
    }

    @Override
    public void onNext(T t) {
        Log.d("", "");
    }
}
