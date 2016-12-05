package com.loyo.oa.v2.common.http;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.ExtraAndResult;
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

    public static void checkErrorForAttendance(RetrofitError error) {
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
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            e.printStackTrace();
            Toast("没有更多数据了！");
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            Toast("服务端数据异常");
            e.printStackTrace();
        }
    }


    public static void checkError(RetrofitError error) {
        DialogHelp.cancelLoading();
        LogUtil.d("网络异常" + error.getMessage());
        LogUtil.d("error接口URL：" + error.getUrl());

        try {
            String msg = Utils.
                    convertStreamToString(error.
                            getResponse()
                            .getBody()
                            .in());
            LogUtil.d("error获得的：", msg);
            JSONObject job = new JSONObject(msg);
            if (500 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (401 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (404 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (406 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
                //到侧边栏 退出系统到登录界面
                Intent in = new Intent();
                in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
                LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                Toast("请检查您的网络连接");
            } else {
                String errorInfo = job.getString("error");
                Toast(errorInfo);
            }
            LogUtil.d(error.getMessage() + " 失败的错误信息：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            e.printStackTrace();
            Toast("连接服务器失败");
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            Toast("服务端数据异常");
            e.printStackTrace();
        }
    }

    public static void checkError(RetrofitError error, LoadingLayout loadingLayout) {
//        DialogHelp.cancelLoading();
        LogUtil.d("loading网络异常: " + error.getMessage());
        LogUtil.d("error接口URL：" + error.getUrl());

        try {
            String msg = Utils.
                    convertStreamToString(error.
                            getResponse()
                            .getBody()
                            .in());
            LogUtil.d("error获得的：", msg);
            JSONObject job = new JSONObject(msg);
            if (500 == error.getResponse().getStatus()) {
                loadingLayout.setStatus(LoadingLayout.No_Network);
                loadingLayout.setNoNetworkText(job.getString("error"));
            } else if (401 == error.getResponse().getStatus()) {
                loadingLayout.setStatus(LoadingLayout.No_Network);
                loadingLayout.setNoNetworkText(job.getString("error"));
            } else if (404 == error.getResponse().getStatus()) {
                loadingLayout.setStatus(LoadingLayout.No_Network);
                loadingLayout.setNoNetworkText(job.getString("error"));
            } else if (406 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
                //到侧边栏 退出系统到登录界面
                Intent in = new Intent();
                in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
                LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                Toast("请检查您的网络连接");
            } else {
                loadingLayout.setStatus(LoadingLayout.No_Network);
                loadingLayout.setNoNetworkText(job.getString("error"));
            }
            LogUtil.d(error.getMessage() + " 失败的错误信息：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            e.printStackTrace();
            loadingLayout.setStatus(LoadingLayout.No_Network);
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            loadingLayout.setStatus(LoadingLayout.Error);
            loadingLayout.setErrorText("服务端数据异常");
            e.printStackTrace();
        }
    }

    public static void checkResponse(String tag, Response response) {
        DialogHelp.cancelLoading();
        try {
            String result = Utils.convertStreamToString(response.getBody().in());
            LogUtil.d(tag + " 接口成功result：" + result);
            LogUtil.d(tag + " 接口成功URL：" + response.getUrl());
            checkResponseError(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空response:" + response.getUrl());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void checkResponse(Response response) {
        DialogHelp.cancelLoading();
        try {
            String result = Utils.convertStreamToString(response.getBody().in());
            LogUtil.d(" 接口成功result：" + result);
            LogUtil.d(" 接口成功URL：" + response.getUrl());
            checkResponseError(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空response:" + response.getUrl());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }




    /**
     * 所有提交,成功检查
     * */
    public static void checkCommitSus(Response response) {
        DialogHelp.successStatusLoad();
        try {
            String result = Utils.convertStreamToString(response.getBody().in());
            LogUtil.d(" 接口成功result：" + result);
            LogUtil.d(" 接口成功URL：" + response.getUrl());
            checkResponseError(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空response:" + response.getUrl());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 所有提交,失败检查
     * */
    public static void checkCommitEro(RetrofitError error) {
        DialogHelp.cancelLoading();
        LogUtil.d("网络异常" + error.getMessage());
        LogUtil.d("error接口URL：" + error.getUrl());

        try {
            String msg = Utils.
                    convertStreamToString(error.
                            getResponse()
                            .getBody()
                            .in());
            LogUtil.d("error获得的：", msg);
            JSONObject job = new JSONObject(msg);
            if (500 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (401 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (404 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
            } else if (406 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
                //到侧边栏 退出系统到登录界面
                Intent in = new Intent();
                in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
                LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                Toast("请检查您的网络连接");
            } else {
                String errorInfo = job.getString("error");
                Toast(errorInfo);
            }
            LogUtil.d(error.getMessage() + " 失败的错误信息：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            e.printStackTrace();
            Toast("连接服务器失败");
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            Toast("服务端数据异常");
            e.printStackTrace();
        }
    }


    /**
     * 检查response 的错误信息
     *
     * @param result
     */
    private static void checkResponseError(String result) throws JsonSyntaxException {
        BaseBean data = MainApp.gson.fromJson(result, BaseBean.class);
        switch (data.errcode) {
            case 1:
                Toast("非常抱歉,服务器错误");
                break;
            case 2:
                Toast("请求参数错误");
                break;
            default: {
                String msg;
                if (!TextUtils.isEmpty(data.errmsg)) {
                    msg = data.errmsg;
                } else {
                    msg = "服务器出错";
                }
                if (data.errcode != 0) {
                    Toast(msg);
                }
            }
        }

    }
}
