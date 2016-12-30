package com.loyo.oa.v2.network;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.model.APIException;
import com.loyo.oa.v2.network.model.LoyoError;
import com.loyo.oa.v2.tool.Utils;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit.RetrofitError;

import static com.loyo.oa.v2.network.model.LoyoError.AuthFail;

/**
 * 网络回调 的检查
 * Created xnq 15/12/18.
 */

public class LoyoErrorChecker {


    public final static int SILENCE        = 1<<0;
    public final static int TOAST          = 1<<1;
    public final static int LOADING_LAYOUT = 1<<2;
    public final static int PROGRESS_HUD = 1<<3;

    @IntDef({SILENCE, TOAST, LOADING_LAYOUT, PROGRESS_HUD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CheckType {}

    private LoyoErrorChecker() {
    }

    public static LoyoError loyoError(Throwable e) {
        String message = "";
        @LoyoError.State int state = LoyoError.Success;

        if (e instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) e;
            if (error.getResponse() == null) {
                state = LoyoError.No_Network;
                message = "网络异常";
            }
            else if (406 == error.getResponse().getStatus()) {
                try {
                    String msg = Utils.convertStreamToString(
                            error.getResponse().getBody().in());
                    JSONObject jsonObject = new JSONObject(msg);

                    String errorMessage =  jsonObject.getString("error");
                    if (TextUtils.isEmpty(errorMessage)) {
                        errorMessage = "授权失败，请重新登录";
                    }
                    message = errorMessage;
                }
                catch (Exception exception) {
                    message = "授权失败，请重新登录";
                }
                state = AuthFail;
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                state = LoyoError.No_Network;
                message = "请检查您的网络连接";
            } else {
                try {
                    String msg = Utils.convertStreamToString(
                            error.getResponse().getBody().in());
                    JSONObject jsonObject = new JSONObject(msg);

                    String errorMessage =  jsonObject.getString("error");
                    if (TextUtils.isEmpty(errorMessage)) {
                        errorMessage = error.getMessage();
                    }
                    state = LoyoError.No_Network;
                    message = errorMessage;

                } catch (Exception exception) {
                    state = LoyoError.Error;
                    message = "服务端数据异常";
                }
            }
        }
        else if (e instanceof APIException) {
            message = e.getMessage();
            state = LoyoError.Error;
        }
        else {
            state = LoyoError.No_Network;
            message = "网络异常";
        }

        return new LoyoError(message, state, e);
    }

    public static void checkLoyoError(Throwable e) {
        checkLoyoError(e, TOAST, null);
    }

    public static void checkLoyoError(Throwable e, LoadingLayout layout) {
        checkLoyoError(e, LOADING_LAYOUT, layout);
    }

    public static void checkLoyoError(Throwable e, @CheckType int type) {
        checkLoyoError(e, type, null);
    }

    public static LoyoError checkLoyoError(Throwable e, @CheckType int type, LoadingLayout layout) {
        LoyoError error = LoyoErrorChecker.loyoError(e);
        if (type == LOADING_LAYOUT && layout == null) {
            type = TOAST;
        }

        if (error.loadingState == AuthFail) {
            LoyoToast.info(MainApp.getMainApp().getApplicationContext(), error.message);
            //到侧边栏 退出系统到登录界面
            Intent in = new Intent();
            in.setAction(ExtraAndResult.ACTION_USER_VERSION);
            in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
            LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
            return null;
        }
        switch (type) {
            case SILENCE:
            {
                // 静默处理，不提示
            }
            break;
            case LOADING_LAYOUT:
            {
                layout.setStatus(error.loadingState); // TODO:
                layout.setNoNetworkText(error.message);
            }
            break;
            case PROGRESS_HUD:
            {
                // onCompleted 处理提示
            }
            break;
            default:
            {
                LoyoToast.error(MainApp.getMainApp().getApplicationContext(), error.message);
            }
            break;
        }
        return error;

    }
}
