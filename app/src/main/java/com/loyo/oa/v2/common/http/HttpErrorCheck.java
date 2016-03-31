package com.loyo.oa.v2.common.http;

import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 网络回调 的检查
 * Created xnq 15/12/18.
 */
public class HttpErrorCheck {
    private static Toast mCurrentToast;
    private static String TAG = "LogoServerV2";
    private static String MSG = "msg";

    protected HttpErrorCheck() {
        throw new UnsupportedOperationException(); // 防止子类调用
    }

    private static void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }
        mCurrentToast = Toast.makeText(MainApp.getMainApp().getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }

    public static void checkError(RetrofitError error) {
        DialogHelp.cancelLoading();
        LogUtil.d("网络异常" + error.getMessage());
        LogUtil.d("error接口URL：" + error.getUrl());

        try {
            String msg = Utils.convertStreamToString(error.getResponse().getBody().in());
            LogUtil.d("error获得的：", msg);
            JSONObject job = new JSONObject(msg);
            if (500 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (401 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (404 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                Toast("请检查您的网络连接");
            } else {
                Toast(job.getString("error"));
            }
            LogUtil.d(error.getMessage() + " 失败的错误信息：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
            // Toast(error.getMessage());
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            Toast("服务端数据异常");
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            e.printStackTrace();
            Toast("连接服务器失败");
        }


        // LogUtil.d("cu123ow :" + error.getResponse().getBody().toString());
        //Object obj = error.getBody();
//        String msg = null == obj
//                ? error.getMessage() :
//                obj.toString();//error.getMessage();
    }

    public static void checkResponse(String tag, Response response) {
        DialogHelp.cancelLoading();
        MSG = tag;
        checkResponse(response);
    }

    public static void checkResponse(Response response) {
        DialogHelp.cancelLoading();
        try {
            String result = Utils.convertStreamToString(response.getBody().in());
            LogUtil.d(MSG + " 接口成功result：" + result);
            LogUtil.d(MSG + " 接口成功URL：" + response.getUrl());
        } catch (IOException e) {

        } catch (NullPointerException e) {
            LogUtil.d("Body空response:" + response.getUrl());
            e.printStackTrace();
        }
    }
}
