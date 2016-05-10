package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.API_error;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.customview.CustomProgressDialog;
import org.apache.http.Header;

/**
 * Created by pj on 15/3/26.
 */
public abstract class BaseAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    protected Boolean isTopAdd = false;
    protected Boolean isBottomAdd = false;
    protected Boolean isShowProgressDialog = true;

    CustomProgressDialog customProgressDialog;

    CustomProgressDialog getDialog() {

        if (getActivity() == null) {
            return null;
        }

        if (customProgressDialog == null) {
            this.customProgressDialog = new CustomProgressDialog(getActivity());
            customProgressDialog.setCancelable(false);
        }

        return customProgressDialog;
    }

    public abstract Activity getActivity();

    public void setIsTopAdd(Boolean isTopAdd) {
        this.isTopAdd = isTopAdd;
    }

    public void setIsBottomAdd(Boolean isBottomAdd) {
        this.isBottomAdd = isBottomAdd;
    }

    public void setIsShowProgressDialog(Boolean isShowProgressDialog) {
        this.isShowProgressDialog = isShowProgressDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            getDialog().iLoadingTypeCount_Add(R.string.app_dialog_progress_commit);
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (getActivity() != null) {
            getDialog().iLoadingTypeCount_subtract();
        }
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        switch (i) {
            case 401:
                Global.Toast(R.string.app_Session_invalidation);
//                MainApp.getMainApp().toActivity(LoginActivity.class);
                break;
            default:
                API_error api_error = null;

                try {
                    Log.i("onFailure:", getStr(bytes));
                    api_error = MainApp.gson.fromJson(getStr(bytes), API_error.class);
                } catch (Exception e) {
                    //这里不需要上报
                    e.printStackTrace();
                }

                if (api_error != null) {
                    Global.Toast(api_error.getError());
                } else {
                    if (Global.isConnected()) {
                        Global.Toast(R.string.app_connectServerDataException);
                    } else {
                        Global.Toast("网络错误");
                    }
                }

                Global.ProcException(throwable);
                break;
        }
    }

    @Override
    public abstract void onSuccess(int i, Header[] headers, byte[] bytes);

    public String getStr(byte[] arg2) {
        //修复空指针bug bugly1041类似的bug v3.1.1 ykb 07-16
        String getStr = "";
        try {
            getStr = new String(arg2);
        } catch (Exception e) {
            Global.ProcException(e);
        }
        Log.d("AsyncResponse", "getStr:" + getStr);
        if (getStr == null || "".equals(getStr)) {
            Toast.makeText(MainApp.getMainApp().getBaseContext(), MainApp.getMainApp().getString(R.string.app_getServerDataException), Toast.LENGTH_LONG).show();
        }
        return getStr;
    }
}
