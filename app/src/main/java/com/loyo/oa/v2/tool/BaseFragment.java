package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import com.loyo.oa.v2.activityui.project.ProjectInfoActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.GeneralPopView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;

public abstract class BaseFragment extends Fragment implements ProjectInfoActivity.OnProjectChangeCallback {

    protected MainApp app = MainApp.getMainApp();
    protected Activity mActivity;
    protected OnLoadSuccessCallback callback;
    protected int mId;
    public GeneralPopView generalPopView;
    public SweetAlertDialogView sweetAlertDialogView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MainApp.getMainApp();
        AppBus.getInstance().register(this);
        sweetAlertDialogView = new SweetAlertDialogView(getActivity());
    }

    public void onDestroy() {
        super.onDestroy();
        AppBus.getInstance().unregister(this);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private Toast mCurrentToast;

    protected void Toast(String msg) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(app.getBaseContext(), msg, Toast.LENGTH_SHORT);
        mCurrentToast.setGravity(Gravity.CENTER, 0, 0);
        mCurrentToast.show();
    }

    protected void Toast(int resId) {
        Toast(app.getString(resId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onProjectChange(int status) {

    }

    public void setCallback(int id, OnLoadSuccessCallback callback) {
        this.mId = id;
        this.callback = callback;
    }

    protected void onLoadSuccess(int size) {
        if (null != callback) {
            callback.onLoadSuccess(mId, size);
        }
    }

    /**
     * 加载loading的方法
     */
    public void showLoading(String msg) {
        DialogHelp.showLoading(getActivity(), msg, true);
    }

    public void showLoading(String msg, boolean Cancelable) {
        DialogHelp.showLoading(getActivity(), msg, Cancelable);
    }

    public static void cancelLoading() {
        DialogHelp.cancelLoading();
    }

    /**
     * 通用提示弹出框init
     */
    public GeneralPopView showGeneralDialog(boolean isOut, boolean isKind, String message) {
        generalPopView = new GeneralPopView(getActivity(), isKind);
        generalPopView.show();
        generalPopView.setMessage(message);
        generalPopView.setCanceledOnTouchOutside(isOut);
        return generalPopView;
    }

}
