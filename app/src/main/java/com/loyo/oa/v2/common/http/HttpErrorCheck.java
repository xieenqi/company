package com.loyo.oa.v2.common.http;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.login.DialogTipActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBean;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.RemindWay;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UpdateTipActivity;
import com.loyo.oa.v2.tool.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;
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
//
//    /**
//     * loading 报错 error信息走Error返回的
//     *
//     * @param error
//     * @param loadingLayout
//     */
//    public static void checkError(RetrofitError error, LoadingLayout loadingLayout) {
//        checkError(error, loadingLayout, true);
//    }

    /**
     * loading 报错 error信息走Error返回的
     *
     * @param error
     * @param loadingLayout //     * @param isLoading     true 只显示loading  false Toast
     */
    public static void checkError(RetrofitError error, LoadingLayout loadingLayout) {
        DialogHelp.cancelLoading();
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
//            if (500 == error.getResponse().getStatus()) {
//                loadingLayout.setStatus(LoadingLayout.No_Network);
//                loadingLayout.setNoNetworkText(job.getString("error"));
//            } else if (401 == error.getResponse().getStatus()) {
//                loadingLayout.setStatus(LoadingLayout.No_Network);
//                loadingLayout.setNoNetworkText(job.getString("error"));
//            } else if (404 == error.getResponse().getStatus()) {
//                loadingLayout.setStatus(LoadingLayout.No_Network);
//                loadingLayout.setNoNetworkText(job.getString("error"));
//            } else
            if (406 == error.getResponse().getStatus()) {
                Toast(job.getString("error"));
                /*到侧边栏 退出系统到登录界面*/
                Intent in = new Intent();
                in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
                LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                errorStatus(loadingLayout, "请检查您的网络连接");
            } else {
                errorStatus(loadingLayout, job.getString("error"));
            }
            LogUtil.d(error.getMessage() + " 失败的错误信息：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            errorStatus(loadingLayout, "无网 或 没有获取到数据");
            e.printStackTrace();
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            errorStatus(loadingLayout, "服务端数据异常");
            e.printStackTrace();
        }
    }

    private static void errorStatus(LoadingLayout loadingLayout, String errorText) {
        if (loadingLayout.getStatus() == LoadingLayout.Loading) {
            loadingLayout.setStatus(LoadingLayout.No_Network);
            loadingLayout.setNoNetworkText(errorText);
            return;
        }
        Toast(errorText);
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

    /**
     * loading 报错 error信息走success返回的
     *
     * @param tag
     * @param response
     * @param loadingLayout
     */
    public static void checkResponse(String tag, Response response, LoadingLayout loadingLayout) {
        DialogHelp.cancelLoading();
        try {
            String result = Utils.convertStreamToString(response.getBody().in());
            LogUtil.d(tag + " 接口成功result：" + result);
            LogUtil.d(tag + " 接口成功URL：" + response.getUrl());
            checkResponseError(result, loadingLayout);
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
     * 所有提交,成功检查(2.7.4新版提交方案)
     */
    public static void checkCommitSus(String tag, Response response) {
        DialogHelp.successStatusLoad();
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

    /**
     * 所有提交,失败检查(2.7.4新版提交方案)
     */
    public static void checkCommitEro(RetrofitError error) {
        String errorMsg = "提交失败!";
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
                //Toast(job.getString("error"));
                errorMsg = job.getString("error");
            } else if (401 == error.getResponse().getStatus()) {
                //Toast(job.getString("error"));
                errorMsg = job.getString("error");
            } else if (404 == error.getResponse().getStatus()) {
                //Toast(job.getString("error"));
                errorMsg = job.getString("error");
            } else if (406 == error.getResponse().getStatus()) {
                //Toast(job.getString("error"));
                errorMsg = job.getString("error");
                //到侧边栏 退出系统到登录界面
                Intent in = new Intent();
                in.setAction(ExtraAndResult.ACTION_USER_VERSION);
                in.putExtra(ExtraAndResult.EXTRA_DATA, "exite");
                LocalBroadcastManager.getInstance(MainApp.getMainApp()).sendBroadcast(in);
            } else if (error.getKind() == RetrofitError.Kind.NETWORK) {
                //Toast("请检查您的网络连接");
                errorMsg = "请检查您的网络连接";
            } else {
                String errorInfo = job.getString("error");
                //Toast(errorInfo);
                errorMsg = errorInfo;
            }
            LogUtil.d(error.getMessage() + " 失败的错误信息：" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            LogUtil.d("Body空err:" + error.getUrl());
            e.printStackTrace();
            //Toast("连接服务器失败");
            errorMsg = "提交失败!";
        } catch (JSONException e) {
            LogUtil.d("JSON异常err:" + error.getUrl());
            //Toast("服务端数据异常");
            errorMsg = "服务端数据异常";
            e.printStackTrace();
        } finally {
            DialogHelp.errorStatusLoading(errorMsg);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    DialogHelp.cancelStatusLoading();
                }
            }, 1500);
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
                    if (data.errType == RemindWay.DILOG.type) {
                        showDilog(msg);
                    } else {
                        Toast(msg);
                    }
                }
            }
        }
    }

    private static void showDilog(String msg) {
        Intent intentUpdateTipActivity = new Intent(MainApp.getMainApp().getBaseContext(), DialogTipActivity.class);
        intentUpdateTipActivity.putExtra("msg", msg);
        intentUpdateTipActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApp.getMainApp().getBaseContext().startActivity(intentUpdateTipActivity);
    }

    /**
     * 检查response 的错误信息loading 报错 error信息走success返回的
     *
     * @param result
     */
    private static void checkResponseError(String result, LoadingLayout loadingLayout) throws JsonSyntaxException {
        BaseBean data = MainApp.gson.fromJson(result, BaseBean.class);
        switch (data.errcode) {
            case 0:
                loadingLayout.setStatus(LoadingLayout.Success);
                break;
            case 1:
                loadingLayout.setStatus(LoadingLayout.No_Network);
                loadingLayout.setNoNetworkText("非常抱歉,服务器错误");
                break;
            case 2:
                loadingLayout.setStatus(LoadingLayout.No_Network);
                loadingLayout.setNoNetworkText("请求参数错误");
                break;
            default: {
                String msg;
                if (!TextUtils.isEmpty(data.errmsg)) {
                    msg = data.errmsg;
                } else {
                    msg = "服务器出错";
                }
                if (data.errcode != 0) {
                    loadingLayout.setStatus(LoadingLayout.No_Network);
                    loadingLayout.setNoNetworkText(msg);
                }
            }
        }
    }
}
