package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.loyo.oa.v2.activityui.project.ProjectInfoActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.SweetAlertDialogView;

import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseFragment extends Fragment implements ProjectInfoActivity.OnProjectChangeCallback {

    protected MainApp app = MainApp.getMainApp();
    protected Activity mActivity;
    protected OnLoadSuccessCallback callback;
    protected int mId;
    public SweetAlertDialogView sweetAlertDialogView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MainApp.getMainApp();
        AppBus.getInstance().register(this);
        sweetAlertDialogView = new SweetAlertDialogView(getActivity());
        getActivity();
    }

    public void onDestroy() {
        super.onDestroy();
        AppBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(Object object) {

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
     * 关闭软键盘
     */
    public void hideInputKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


    /**
     * 手动 显示软键盘
     */
    public void showInputKeyboard(final EditText view) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        final Handler handler = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imm != null) {
                            view.requestFocus();
                            imm.showSoftInput(view, 0);
                        }
                    }
                });
            }
        }, 100);
    }

    /**
     * 关闭SweetAlertDialog
     */
    public void cancelDialog() {
        sweetAlertDialogView.sweetAlertDialog.dismiss();
    }

    /**
     * 加载loading的方法
     */
    public void showLoading(String msg) {
        DialogHelp.showLoading(mActivity, msg, true);
    }

    public void showLoading(String msg, boolean Cancelable) {
        DialogHelp.showLoading(mActivity, msg, Cancelable);
    }

    public static void cancelLoading() {
        DialogHelp.cancelLoading();
    }

}
