package com.loyo.oa.v2.network;

import com.library.module.widget.loading.LoadingLayout;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * Created by EthanGong on 2016/12/15.
 */

public abstract class DefaultLoyoSubscriber<T> extends Subscriber<T> {

    private WeakReference<LoadingLayout> mLoadingLayoutRef;
    private
    @LoyoErrorChecker.CheckType
    int type;

    public DefaultLoyoSubscriber() {
        type = LoyoErrorChecker.TOAST;
    }

    public DefaultLoyoSubscriber(@LoyoErrorChecker.CheckType int checktype) {
        type = checktype;
    }

    public DefaultLoyoSubscriber(LoadingLayout layout) {
        if (layout != null) {
            mLoadingLayoutRef = new WeakReference<LoadingLayout>(layout);
            type = LoyoErrorChecker.LOADING_LAYOUT;
        }
    }

    private LoadingLayout getLoadingLayout() {
        if (mLoadingLayoutRef == null) {
            return null;
        }
        return mLoadingLayoutRef.get();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        LoyoErrorChecker.checkLoyoError(e, type, getLoadingLayout());
    }

    @Override
    public abstract void onNext(T t);
}
