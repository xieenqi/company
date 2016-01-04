package com.loyo.oa.v2.common.http;

import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 网络回调 的检查
 * Created xnq 15/12/18.
 */
public class HttpErrorCheck {
    private static Toast mCurrentToast;
    private static String TAG = "";

    private static void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(MainApp.getMainApp().getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }

    public static void checkError(RetrofitError error) {
        String msg = error.getMessage();
        if (msg.contains("500")) {
            Toast("失败：" + error.getMessage());
        } else if (msg.contains("401")) {
            Toast("失败：" + error.getMessage());
        } else if (msg.contains("404")) {
            Toast("失败：" + error.getMessage());
        } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
            Toast("请检查您的网络连接");
        } else {
            Toast(error.getMessage());
        }
        LogUtil.d("失败的错误信息：" + error.getMessage());
        LogUtil.d("error接口URL：" + error.getUrl());
    }

    public static void checkResponse(String tag, Response response) {
        TAG = tag;
        checkResponse(response);
    }

    public static void checkResponse(Response response) {
        try {
            String result = Utils.convertStreamToString(response.getBody().in());
            LogUtil.d(TAG + "接口成功result：" + result);
            LogUtil.d(TAG + "接口成功URL：" + response.getUrl());
        } catch (Exception e) {

        }
    }
}
