package com.loyo.oa.v2.network;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.network.model.LoyoError;

import java.lang.ref.WeakReference;

import rx.Subscriber;

/**
 * Created by EthanGong on 2016/12/15.
 */

public abstract class DefaultLoyoSubscriber<T> extends Subscriber<T> {

    private WeakReference<LoadingLayout> mLoadingLayoutRef;
    private LoyoProgressHUD mHUD;
    private LoyoError mError;
    private String successTip;
    private boolean dismissOnlyWhenError;
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

    public DefaultLoyoSubscriber(LoyoProgressHUD hud) {
        if (hud != null) {
            mHUD = hud;
            type = LoyoErrorChecker.PROGRESS_HUD;
        }
    }

    public DefaultLoyoSubscriber(LoyoProgressHUD hud, String successTip) {
        if (hud != null) {
            mHUD = hud;
            type = LoyoErrorChecker.PROGRESS_HUD;
            this.successTip = successTip;
        }
    }

    public DefaultLoyoSubscriber(LoyoProgressHUD hud, boolean dismissOnlyWhenError) {
        if (hud != null) {
            mHUD = hud;
            type = LoyoErrorChecker.PROGRESS_HUD;
            this.dismissOnlyWhenError = dismissOnlyWhenError;
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
        if (mHUD != null) {
            if (mError != null) {
                mHUD.dismissWithError(mError.message);
            }
            else if (!dismissOnlyWhenError){
                mHUD.dismissWithSuccess(this.successTip);
            }
            // TODO:
            if (mHUD.getStyle() != LoyoProgressHUD.Style.LOYO_COMMIT) {
                if (mError != null) {
                    LoyoToast.error(MainApp.getMainApp().getApplicationContext(), mError.message);
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        mError = LoyoErrorChecker.checkLoyoError(e, type, getLoadingLayout());
        onCompleted();
    }

    @Override
    public abstract void onNext(T t);
}
